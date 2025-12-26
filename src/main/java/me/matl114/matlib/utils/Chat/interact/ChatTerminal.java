package me.matl114.matlib.utils.chat.interact;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Setter;
import me.matl114.matlibAdaptor.implement.bukkit.InputManager;
import org.bukkit.entity.Player;

public abstract class ChatTerminal implements InputManager.InputListener {
    boolean enabled = false;
    Player owner;
    boolean ignoreCancelled = false;

    @Setter
    public Runnable onEnd;

    public ChatTerminal(Player owner) {
        this(owner, false);
    }

    public ChatTerminal(Player owner, boolean ignoreCancelled) {
        this.owner = owner;
        this.ignoreCancelled = ignoreCancelled;
    }

    public void onEnable(InputManager inputManager) {
        Preconditions.checkNotNull(owner);
        enabled = true;
        inputManager.registerInputListener(this);
    }

    public void onExit() {
        if (onEnd != null) {
            onEnd.run();
        }
    }

    public void onQuit() {
        if (onEnd != null) {
            onEnd.run();
        }
    }

    @Override
    public final InputManager.Result onChat(
            Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel) {
        if (!enabled) {
            return InputManager.Result.ACCEPT_AND_REMOVE;
        }

        String msg = message.get();
        if (msg.startsWith("/")) {
            // ignore command lines
            return InputManager.Result.ACCEPT_AND_NOT_REMOVE;
        }
        if ("exit".equals(msg)) {
            onExit();
            enabled = false;
            return InputManager.Result.REJECT_AND_REMOVE;
        } else if ("quit".equals(msg)) {
            onQuit();
            enabled = false;
            return InputManager.Result.REJECT_AND_REMOVE;
        } else {
            return onExecution(player, msg, recipients)
                    ? InputManager.Result.REJECT_AND_NOT_REMOVE
                    : InputManager.Result.ACCEPT_AND_NOT_REMOVE;
        }
    }

    public abstract boolean onExecution(Player player, String label, Set<Player> recipients);

    public boolean ignoreCancel() {
        return ignoreCancelled;
    }
}
