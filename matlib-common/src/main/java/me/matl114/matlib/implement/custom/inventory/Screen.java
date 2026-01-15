package me.matl114.matlib.implement.custom.inventory;

import org.bukkit.entity.Player;

public interface Screen {
    public int getMaxPages();

    public int getPageSize();

    public SlotType getSlotType(int idx);

    public <T, W extends InventoryBuilder<T>> W createInventory(
            int page0, InventoryBuilder.InventoryFactory<T, W> fact);

    default <T, W extends InventoryBuilder<T>> void openPage(
            InventoryBuilder.InventoryFactory<T, W> screenType, Player player, int page) {
        var builder = createInventory(page, screenType);
        builder.open(player);
    }

    default <T, W extends InventoryBuilder<T>> void openPageWithHistory(
            InventoryBuilder.InventoryFactory<T, W> screenType, Player player, int page) {
        var builder = createInventory(page, screenType);
        builder.openWithHistory(player);
    }

    public <T extends Screen> T relateToHistory(ScreenHistoryStack stack);
}
