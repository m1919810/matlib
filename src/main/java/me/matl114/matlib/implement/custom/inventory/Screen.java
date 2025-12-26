package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;

public interface Screen {
    public void openPage(InventoryBuilder.InventoryFactory screenType, Player player, int page);

    public void openPageWithHistory(InventoryBuilder.InventoryFactory screenType, Player player, int page);

    public <T extends Screen> T relateToHistory(ScreenHistoryStack stack);
}
