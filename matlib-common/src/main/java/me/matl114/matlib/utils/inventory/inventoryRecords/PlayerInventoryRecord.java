package me.matl114.matlib.utils.inventory.inventoryRecords;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record PlayerInventoryRecord(Player player, Inventory inventory) implements InventoryRecord {
    @Override
    public Location invLocation() {
        return player.getLocation();
    }

    @Override
    public InventoryHolder optionalHolder() {
        return player;
    }

    @Override
    public InventoryKind invKind() {
        return InventoryKind.ENTITY_INVENTORY;
    }

    @Override
    public boolean stillValid() {
        return Bukkit.getPlayer(player.getUniqueId()) == player;
    }

    @Override
    public void setChange() {}

    @Override
    public Inventory getInventorySync() {
        return player.getInventory();
    }

    @Override
    public boolean canPlayerOpen(Player p) {
        return true;
    }

    public static InventoryRecord ofPlayer(Player p) {
        return new PlayerInventoryRecord(p, p.getInventory());
    }
}
