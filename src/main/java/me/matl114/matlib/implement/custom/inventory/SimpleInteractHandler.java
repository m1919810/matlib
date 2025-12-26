package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface SimpleInteractHandler extends InteractHandler {
    boolean onClick(Inventory inventory, Player player, InventoryClickEvent clickEvent);

    default boolean onClick(Inventory inventory, Player player, int slotIndex, ClickType clickType) {
        throw new UnsupportedOperationException("Do not call from here");
    }
}
