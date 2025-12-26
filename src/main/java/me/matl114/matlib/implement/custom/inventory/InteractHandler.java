package me.matl114.matlib.implement.custom.inventory;

import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface InteractHandler {
    @Internal
    public boolean onClick(Inventory inventory, Player player, int slotIndex, ClickType clickType);

    @Note("Caller should call this method")
    default boolean onClick(Inventory inventory, Player player, InventoryClickEvent clickEvent) {
        return onClick(inventory, player, clickEvent.getSlot(), clickEvent.getClick());
    }

    public static final InteractHandler EMPTY = (SimpleInteractHandler) ((inventory, player, event) -> false);

    public static final InteractHandler ACCEPT = (SimpleInteractHandler) ((inventory, player, event) -> true);

    public static InteractHandler test(Predicate<Player> p) {
        return (SimpleInteractHandler) ((inventory, player, event) -> p.test(player));
    }

    public static InteractHandler task(Consumer<Player> p) {
        return (SimpleInteractHandler) ((inventory, player, event) -> {
            p.accept(player);
            return false;
        });
    }
}
