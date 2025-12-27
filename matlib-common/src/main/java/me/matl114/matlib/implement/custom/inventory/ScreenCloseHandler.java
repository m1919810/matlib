package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ScreenCloseHandler {
    public void handleClose(Player player, Inventory inventory);
}
