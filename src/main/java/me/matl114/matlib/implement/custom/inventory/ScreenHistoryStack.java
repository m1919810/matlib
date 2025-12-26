package me.matl114.matlib.implement.custom.inventory;

import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public interface ScreenHistoryStack extends Listener {
    /**
     * clear the stack of this player
     * @param player
     */
    public void cleanPlayerHistory(Player player);

    /**
     * pop the top
     * @param player
     */
    public void popLast(Player player);

    /**
     * pop the top and open te last record
     * @param factory
     * @param player
     * @return
     */
    default boolean goBackToLast(InventoryBuilder.InventoryFactory factory, Player player) {
        popLast(player);
        return openLast(factory, player);
    }
    /**
     * push a new record to the record
     * @param screen
     * @param player
     * @param page
     */
    public void pushNew(Screen screen, Player player, int page);

    /**
     * switch the top history's page; make sure that the top matches the input screen
     * @param screen
     * @param player
     * @param page
     */
    public void switchTopPage(Screen screen, Player player, int page);

    /**
     * clear the history and openAndPush()
     * @param screenType
     * @param player
     * @param page
     * @param screen
     */
    default void openWithHistoryClear(
            InventoryBuilder.InventoryFactory screenType, Player player, int page, Screen screen) {
        cleanPlayerHistory(player);
        openAndPush(screenType, player, page, screen);
    }

    /**
     * open the last history if present ;return whether the last history presents
     * @param screenType
     * @param player
     * @return
     */
    public boolean openLast(InventoryBuilder.InventoryFactory screenType, Player player);

    /**
     * open a screen inventory or open the last history if present
     * @param screenType
     * @param player
     * @param screenSupplier
     */
    default void openLastOrCreate(
            InventoryBuilder.InventoryFactory screenType, Player player, Supplier<Screen> screenSupplier) {
        if (!openLast(screenType, player)) {
            Screen screen = screenSupplier.get().relateToHistory(this);
            screen.openPageWithHistory(screenType, player, 1);
        }
    }

    /**
     * open a screen inventory at this stack and push record to this stack
     * @param screenType
     * @param player
     * @param page
     * @param screen
     */
    default void openAndPush(InventoryBuilder.InventoryFactory screenType, Player player, int page, Screen screen) {
        screen.relateToHistory(this);
        screen.openPageWithHistory(screenType, player, page);
    }

    /**
     * open a screen inventory at this stack and do not push the record
     * @param screenType
     * @param player
     * @param page
     * @param screen
     */
    default void openNoHistory(InventoryBuilder.InventoryFactory screenType, Player player, int page, Screen screen) {
        screen.openPage(screenType, player, page);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    default void cleanHistoryWhenLeave(PlayerQuitEvent event) {
        cleanPlayerHistory(event.getPlayer());
    }

    static ScreenHistoryStack of() {
        return new ScreenHistoryStackImpl();
    }
}
