package me.matl114.matlib.utils;

import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeSafeProvider;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Lectern;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryUtils {
    private static final InventoryType PAPER_DECORATED_POT_TYPE =
            new InitializeSafeProvider<>(() -> InventoryType.valueOf("DECORATED_POT"), null).v();

    /**
     * Checks if an inventory type is common and safe enough for setItem and getItem operations.
     * Some inventory types have special behaviors that make them unsuitable for standard
     * inventory manipulation operations.
     *
     * @param inventoryType The InventoryType to check
     * @return true if the inventory type is common and safe for standard operations, false otherwise
     */
    @Note(value = "check if Inventory type safe enough to setItem and getItem,some inventory are too weird")
    public static boolean isInventoryTypeCommon(InventoryType inventoryType) {
        return inventoryType != InventoryType.CHISELED_BOOKSHELF
                && inventoryType != InventoryType.JUKEBOX
                && inventoryType != InventoryType.COMPOSTER
                && inventoryType != PAPER_DECORATED_POT_TYPE;
    }

    /**
     * Checks if an inventory type is safe for asynchronous operations.
     * When this method returns false, the inventory type will 100% trigger block updates.
     * When it returns true, the inventory type will be safe in most cases, though it may still
     * cause block updates when redstone comparators are nearby, but inventory changes will be preserved.
     *
     * @param inventoryType The InventoryType to check
     * @return true if the inventory type is safe for async operations, false otherwise
     */
    @Note(
            value =
                    "check if Inventory type commonly async safe,when return false,this type of inventory will 100% trigger block update,others will be safe in most time(still cause block update when redstone comparator is near,but inventory changes will keep)")
    public static boolean isInventoryTypeAsyncSafe(InventoryType inventoryType) {
        return inventoryType != InventoryType.LECTERN && isInventoryTypeCommon(inventoryType);
    }

    /**
     * Checks if a block inventory can be opened by a player.
     * This method must be called on the main thread as it accesses block state information.
     *
     * @param inventory The inventory to check
     * @return true if the block inventory can be opened by a player, false otherwise
     */
    @ForceOnMainThread
    public static boolean canBlockInventoryOpenToPlayer(Inventory inventory) {
        // should run on Primary thread
        InventoryHolder holder = inventory.getHolder(false);
        return canBlockInventoryOpenToPlayer(holder);
    }

    /**
     * Checks if an InventoryHolder represents a block inventory.
     * Block inventories are those that are associated with physical blocks in the world.
     *
     * @param inventoryHolder The InventoryHolder to check
     * @return true if the holder represents a block inventory, false otherwise
     */
    public static boolean isBlockInventory(InventoryHolder inventoryHolder) {
        return inventoryHolder instanceof BlockInventoryHolder || inventoryHolder instanceof DoubleChest;
    }

    /**
     * Checks if a block inventory holder can be opened by a player.
     * This method determines whether the block inventory is accessible to players
     * based on its type and implementation.
     *
     * @param holder The InventoryHolder to check
     * @return true if the block inventory can be opened by a player, false otherwise
     */
    public static boolean canBlockInventoryOpenToPlayer(InventoryHolder holder) {
        return holder instanceof Container
                || holder instanceof Lectern
                || holder instanceof DoubleChest
                || !(holder instanceof BlockInventoryHolder);
    }
}
