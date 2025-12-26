package me.matl114.matlibAdaptor.implement.bukkit;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.UnsafeOperation;
import me.matl114.matlib.common.lang.enums.TaskRequest;
import me.matl114.matlibAdaptor.proxy.annotations.AdaptorInterface;
import me.matl114.matlibAdaptor.proxy.annotations.InternalMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@AdaptorInterface
public interface InputManager {
    public boolean isCommandRecipient(Set<Player> players);

    default void awaitInputForPlayer(Player player, Consumer<String> consumer) {
        awatInputForPlayer(player, (pla, str, set) -> {
            consumer.accept(str);
        });
    }

    default void awaitInputForPlayer(Player player, BiConsumer<Player, String> consumer) {
        awatInputForPlayer(player, (pla, str, set) -> {
            consumer.accept(pla, str);
        });
    }

    @InternalMethod
    public void awatInputForPlayer(Player player, InputCatcher catcher);

    default void awaitInputAny(BiConsumer<Player, String> consumer) {
        registerInputListener((p, str, rp, cl) -> {
            consumer.accept(p, str.get());
            return Result.REJECT_AND_REMOVE;
        });
    }

    default void awaitInputAny(BiPredicate<Player, String> shouldAccept) {
        registerInputListener((p, str, rp, cl) -> {
            return shouldAccept.test(p, str.get()) ? Result.ACCEPT_AND_REMOVE : Result.REJECT_AND_REMOVE;
        });
    }

    default void awaitInputModify(BiPredicate<Player, AtomicReference<String>> shouldAccept) {
        registerInputListener((p, str, rp, cl) -> {
            return shouldAccept.test(p, str) ? Result.ACCEPT_AND_REMOVE : Result.REJECT_AND_REMOVE;
        });
    }

    default void awaitInputAny(BiConsumer<Player, String> consumer, boolean ignoreCancel) {
        registerInputListener(new InputListener() {
            @Override
            public Result onChat(
                    Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel) {
                consumer.accept(player, message.get());
                return Result.REJECT_AND_REMOVE;
            }

            @Override
            public boolean ignoreCancel() {
                return ignoreCancel;
            }
        });
    }

    default void awaitInputAny(BiPredicate<Player, String> shouldAccept, boolean ignoreCancel) {
        registerInputListener(new InputListener() {
            @Override
            public Result onChat(
                    Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel) {
                return shouldAccept.test(player, message.get()) ? Result.ACCEPT_AND_REMOVE : Result.REJECT_AND_REMOVE;
            }

            @Override
            public boolean ignoreCancel() {
                return ignoreCancel;
            }
        });
    }

    default void awaitInputModify(BiPredicate<Player, AtomicReference<String>> shouldAccept, boolean ignoreCancel) {
        registerInputListener(new InputListener() {
            @Override
            public Result onChat(
                    Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel) {
                return shouldAccept.test(player, message) ? Result.ACCEPT_AND_REMOVE : Result.REJECT_AND_REMOVE;
            }

            @Override
            public boolean ignoreCancel() {
                return ignoreCancel;
            }
        });
    }

    /**
     * return Result.values()[returnedCode];
     * @param returnCode
     * @param isCancel
     */
    public void registerInputListener(
            BiFunction<Player, AtomicReference<String>, Integer> returnCode, boolean isCancel);

    @InternalMethod
    public void registerInputListener(InputListener listener);

    default void addChatListener(Predicate<AsyncPlayerChatEvent> shouldRemove) {
        registerChatListener(shouldRemove::test);
    }

    default void addChatListener(Predicate<AsyncPlayerChatEvent> shouldRemove, boolean ignoreCancel) {
        registerChatListener(new ChatListener() {
            @Override
            public boolean onChat(AsyncPlayerChatEvent event) {
                return shouldRemove.test(event);
            }

            @Override
            public boolean ignoreCancel() {
                return ignoreCancel;
            }
        });
    }

    @InternalMethod
    public void registerChatListener(ChatListener listener);

    public static interface InputCatcher {
        void onChat(Player player, String message, Set<Player> recipients);

        default TaskRequest getRunningRequest() {
            return TaskRequest.RUN_ON_CURRENT_OR_LATER_MAIN;
        }
    }

    @Getter
    public enum Result {
        REJECT_AND_REMOVE(false, true),
        REJECT_AND_NOT_REMOVE(false, false),
        ACCEPT_AND_REMOVE(true, true),
        ACCEPT_AND_NOT_REMOVE(true, false);

        Result(boolean acc, boolean remove) {
            this.accept = acc;
            this.remove = remove;
        }

        final boolean accept;
        final boolean remove;
    }
    /**
     * 可复用的监听器,监听chat和command
     */
    public static interface InputListener {
        Result onChat(Player player, AtomicReference<String> message, Set<Player> recipients, boolean isCancel);

        default boolean ignoreCancel() {
            return false;
        }

        @UnsafeOperation
        @Note("should make sure that this Input Listener do not use recipients and isCancel argument")
        default BiFunction<Player, AtomicReference<String>, Integer> toBiFunction() {
            return (p, str) -> {
                return onChat(p, str, null, false).ordinal();
            };
        }
    }

    /**
     * 可复用的监听器,只监听chat
     */
    public static interface ChatListener {
        /**
         * return if for removal
         * @param event
         * @return
         */
        boolean onChat(AsyncPlayerChatEvent event);

        default boolean ignoreCancel() {
            return false;
        }
    }
}
