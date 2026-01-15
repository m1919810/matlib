package me.matl114.matlib.core.bukkit.chat;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import lombok.Getter;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlibAdaptor.common.lang.enums.TaskRequest;
import me.matl114.matlibAdaptor.implement.bukkit.InputManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class ChatInputManager implements Manager, Listener, InputManager {
    private boolean registered = false;

    @Getter
    private static ChatInputManager manager;

    private Plugin pl;
    protected Map<UUID, Deque<InputCatcher>> inputCatchers;
    protected Set<InputListener> inputListeners;
    protected Set<ChatListener> chatListeners;

    public ChatInputManager() {
        this.inputCatchers = new ConcurrentHashMap<>();
        this.inputListeners = ConcurrentHashMap.newKeySet();
        this.chatListeners = ConcurrentHashMap.newKeySet();
        manager = this;
    }

    private ChatInputManager registerFunctional() {
        Preconditions.checkState(!registered, "ChatInputManager functional have already been registered!");
        this.pl.getServer().getPluginManager().registerEvents(this, pl);
        this.registered = true;
        return this;
    }

    private ChatInputManager unregisterFunctional() {
        Preconditions.checkState(registered, "ChatInputManager functional haven't been registered!");
        HandlerList.unregisterAll(this);
        this.registered = false;
        return this;
    }

    @Override
    public ChatInputManager init(Plugin pl, String... path) {
        this.pl = pl;
        registerFunctional();
        addToRegistry();
        return this;
    }

    @Override
    public ChatInputManager reload() {
        deconstruct();
        return init(pl);
    }

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    public void deconstruct() {
        manager = null;
        unregisterFunctional();
        removeFromRegistry();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.inputCatchers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.inputCatchers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        String msg = e.getMessage().replace('§', '&');
        this.checkInput(e, e.getPlayer(), msg, e.getRecipients());
        AtomicReference<String> reference = new AtomicReference<>(e.getMessage());
        this.listenInput(e, e.getPlayer(), reference, e.getRecipients());
        e.setMessage(reference.get());
        this.listenChat(e);
    }

    private static final Set<Player> NO_PLAYERS = ImmutableSet.of();

    public boolean isCommandRecipient(Set<Player> players) {
        return isCommandRecipients(players);
    }

    public static boolean isCommandRecipients(Set<Player> players) {
        return players == NO_PLAYERS;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        this.checkInput(e, e.getPlayer(), e.getMessage(), NO_PLAYERS);
        AtomicReference<String> reference = new AtomicReference<>(e.getMessage());
        this.listenInput(e, e.getPlayer(), reference, NO_PLAYERS);
        e.setMessage(reference.get());
    }

    /**
     * Executes a task based on the specified TaskRequest type.
     * This method provides a unified interface for executing tasks according to different
     * execution strategies defined by the TaskRequest enum.
     *
     * @param taskRequest The type of execution request (RUN_LATER_MAIN, RUN_ON_CURRENT, etc.)
     * @param runnable The task to execute according to the request type
     */
    public static void runWithRequest(TaskRequest taskRequest, Runnable runnable) {
        switch (taskRequest) {
            case RUN_LATER_MAIN:
                ThreadUtils.executeSyncSched(runnable);
                break;
            case RUN_ON_CURRENT:
                runnable.run();
                break;
            case RUN_ASYNC:
                ThreadUtils.executeAsync(runnable);
                break;
            case RUN_ON_CURRENT_OR_LATER_MAIN:
                ThreadUtils.executeSync(runnable);
                break;
        }
    }

    private void checkInput(Cancellable e, Player p, String msg, Set<Player> recipients) {
        Deque<InputCatcher> callbacks = this.inputCatchers.get(p.getUniqueId());
        if (callbacks != null && !callbacks.isEmpty()) {
            do {
                InputCatcher last = callbacks.pollFirst();
                if (last != null) {
                    e.setCancelled(true);
                    runWithRequest(last.getRunningRequest(), () -> last.onChat(p, msg, recipients));
                    return;
                }
            } while (!callbacks.isEmpty());
        }
    }

    private void listenInput(Cancellable e, Player p, AtomicReference<String> msg, Set<Player> recipients) {
        Iterator<InputListener> listeners = this.inputListeners.iterator();
        boolean isCancel = e.isCancelled();
        while (listeners.hasNext()) {
            InputListener listener = listeners.next();
            if (isCancel && listener.ignoreCancel()) {
                continue;
            }
            Result re = listener.onChat(p, msg, recipients, isCancel);
            if (re.isRemove()) {
                listeners.remove();
            }
            isCancel = !re.isAccept();
        }
        e.setCancelled(isCancel);
    }

    private void listenChat(AsyncPlayerChatEvent event) {
        Iterator<ChatListener> listeners = this.chatListeners.iterator();
        while (listeners.hasNext()) {
            ChatListener listener = listeners.next();
            if (event.isCancelled() && listener.ignoreCancel()) {
                continue;
            }
            boolean removal = listener.onChat(event);
            if (removal) {
                listeners.remove();
            }
        }
    }

    public void awatInputForPlayer(Player player, InputCatcher catcher) {
        this.inputCatchers
                .computeIfAbsent(player.getUniqueId(), (u) -> new ArrayDeque<>())
                .addLast(catcher);
    }

    @Override
    public void registerInputListener(
            BiFunction<Player, AtomicReference<String>, Integer> returnCode, boolean isCancel) {
        registerInputListener(new InputListener() {
            public Result onChat(
                    Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel) {
                try {
                    int returned = returnCode.apply(player, message);
                    return Result.values()[returned];
                } catch (Throwable e) {
                    Debug.logger(
                            e,
                            "Error while inputListener" + returnCode + ("(Class:") + returnCode.getClass()
                                    + ") handle the input message:");
                    return Result.REJECT_AND_REMOVE;
                }
            }
        });
    }

    public void registerInputListener(InputListener listener) {
        this.inputListeners.add(listener);
    }

    public void registerChatListener(ChatListener listener) {
        this.chatListeners.add(listener);
    }
}
