package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ScreenOpenHandler {
    public void handleOpen(Player player, Inventory inventory);
}
