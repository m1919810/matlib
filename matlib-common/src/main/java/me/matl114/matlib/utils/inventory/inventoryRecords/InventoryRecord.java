package me.matl114.matlib.utils.inventory.inventoryRecords;

import me.matl114.matlib.common.lang.annotations.Experimental;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryRecord {
    /**
     * get the Location, nonnull
     * @return
     */
    public Location invLocation();

    /**
     * get the optional inventory ,it might be null, which means there is no inventory
     * @return
     */
    public Inventory inventory();

    /**
     * get the optional inventory holder,it might be null, which means there is no data
     * @return
     */
    public InventoryHolder optionalHolder();

    @Internal
    public InventoryKind invKind();
    /**
     * check if inventory belongs to a Slimefun Block
     * @return
     */
    default boolean isSlimefunInv() {
        return invKind() == InventoryKind.PLUGIN_BLOCKMENU;
    }

    /**
     * check if inventory belongs to a BlockState
     * @return
     */
    default boolean isVanillaInv() {
        return invKind() == InventoryKind.TILE_ENTITY_INVENTORY;
    }

    /**
     * check if inventory are made of multi BlockState(DoubleChest)
     * @return
     */
    default boolean isMultiBlockInv() {
        return false;
    }

    /**
     * check if inventory belongs to a Entity
     * @return
     */
    default boolean isEntityInv() {
        return invKind() == InventoryKind.ENTITY_INVENTORY;
    }
    /**
     * check if optionalHolder still presents,
     * @return
     */
    public boolean stillValid();

    @ForceOnMainThread
    @Note("call only when isVanillaInv = true,")
    public void setChange();
    /**
     * check if data present
     * @return
     */
    default boolean hasData() {
        return optionalHolder() != null;
    }

    default boolean hasInv() {
        return inventory() != null;
    }
    /**
     * get Inventory (if needed ,always Sync)
     * @return
     */
    @Experimental
    public Inventory getInventorySync();
    // todo I have a big big big idea about this: fast access cache,
    // first we need a ensureDelaySyncRunner class
    @ForceOnMainThread
    public boolean canPlayerOpen(Player p);

    @Internal
    static enum InventoryKind {
        TILE_ENTITY_INVENTORY,
        PLUGIN_BLOCKMENU,
        ENTITY_INVENTORY
    }
}
