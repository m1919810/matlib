package me.matl114.matlib.utils.inventory.inventoryRecords;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.InventoryUtils;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record SimpleInventoryRecord<T extends TileState & InventoryHolder>(
        Inventory inventory, T optionalHolder, Location invLocation) implements InventoryRecord {
    @Override
    public InventoryKind invKind() {
        return InventoryKind.TILE_ENTITY_INVENTORY;
    }

    // this class records a Location 's Inventory Info,whether it is sf or vanilla, and records sth about holder

    @Override
    public boolean stillValid() {
        return inventory != null && optionalHolder != null && WorldUtils.isTileEntityStillValid(optionalHolder);
        //        if( inventory == null ) return false;
        //        if( ){
        //            return WorldUtils.isTileEntityStillValid(tile);
        //        }else {
        //            //this is vanilla ,this is not a valid inventory
        //            return false;
        //        }
    }

    @Override
    public void setChange() {
        WorldUtils.tileEntitySetChange(optionalHolder);
    }

    @Override
    public Inventory getInventorySync() {
        if (inventory != null) {
            return inventory;
        } else {
            Preconditions.checkArgument(Bukkit.isPrimaryThread());
            return optionalHolder.getInventory();
        }
    }

    public boolean canPlayerOpen(Player p) {
        return optionalHolder != null && InventoryUtils.canBlockInventoryOpenToPlayer(optionalHolder);
    }
    // todo need check of double chest
    @Nonnull
    @ForceOnMainThread
    @Note(
            value =
                    "some InventoryType is totally async unsafe,we will seen then as null,others need catch unhandled exception when set(slot,item)")
    public static InventoryRecord getInventoryRecord(Location loc) {
        return getInventoryRecord(loc, false);
    }

    @Nonnull
    @ForceOnMainThread
    @Note(
            value =
                    "some InventoryType is totally async unsafe,others need catch unhandled exception when set(slot,item)")
    public static InventoryRecord getInventoryRecord(Location loc, boolean useOnMain) {
        // should force Sync
        Block b = loc.getBlock();
        if (WorldUtils.getBlockStateNoSnapShot(b) instanceof InventoryHolder holder
                && holder instanceof TileState state) {
            Inventory inventory = holder.getInventory();
            if ((useOnMain) || InventoryUtils.isInventoryTypeAsyncSafe(inventory.getType())) {
                return inventory instanceof DoubleChestInventory chestchest
                        ? DoubleStateInventoryRecord.ofDoubleChest(chestchest)
                        : new SimpleInventoryRecord(inventory, state, loc);
            } else {
                return new SimpleInventoryRecord(null, state, loc);
            }
        }
        return new SimpleInventoryRecord(null, null, loc);
    }

    public static InventoryRecord fromInventory(Inventory inventory, boolean useOnMain) {
        if ((useOnMain) || InventoryUtils.isInventoryTypeAsyncSafe(inventory.getType())) {
            return inventory instanceof DoubleChestInventory chestchest
                    ? DoubleStateInventoryRecord.ofDoubleChest(chestchest)
                    : (inventory.getHolder(false) instanceof TileState state
                            ? new SimpleInventoryRecord(inventory, state, inventory.getLocation())
                            : new SimpleInventoryRecord(null, null, inventory.getLocation()));
        } else {
            return new SimpleInventoryRecord(null, null, null);
        }
    }
}
