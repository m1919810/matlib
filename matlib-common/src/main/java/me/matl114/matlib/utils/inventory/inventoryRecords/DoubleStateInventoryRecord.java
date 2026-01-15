package me.matl114.matlib.utils.inventory.inventoryRecords;

import com.google.common.base.Preconditions;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record DoubleStateInventoryRecord(DoubleChestInventory inventory, TileState left, TileState right)
        implements InventoryRecord {
    public static InventoryRecord ofDoubleChest(DoubleChestInventory inventory) {
        //        Location locationLeft = inventory.getLeftSide().getLocation();
        //        Location locationRight = inventory.getRightSide().getLocation();
        //        if(locationLeft==null || locationRight==null) return new
        // DoubleStateInventoryRecord(inventory,null,null);

        InventoryHolder holderLeft = inventory.getLeftSide().getHolder(false);
        InventoryHolder holderRight = inventory.getRightSide().getHolder(false);
        if (holderLeft instanceof TileState tileLeft && holderRight instanceof TileState tileRight) {
            return new DoubleStateInventoryRecord(inventory, tileLeft, tileRight);
        } else {
            return new DoubleStateInventoryRecord(inventory, null, null);
        }
    }

    @Override
    public Location invLocation() {
        return inventory.getLocation();
    }

    @Override
    public InventoryHolder optionalHolder() {
        return inventory.getHolder(false);
    }

    @Override
    public InventoryKind invKind() {
        return InventoryKind.TILE_ENTITY_INVENTORY;
    }

    @Override
    public boolean isMultiBlockInv() {
        return true;
    }

    @Override
    public boolean stillValid() {
        return left != null
                && WorldUtils.isTileEntityStillValid(left)
                && right != null
                && WorldUtils.isTileEntityStillValid(right);
    }

    @Override
    public void setChange() {
        WorldUtils.tileEntitySetChange(left);
        WorldUtils.tileEntitySetChange(right);
    }

    public boolean canPlayerOpen(Player p) {
        return stillValid();
    }

    public void ensureChunkLoad() {
        if (left != null) {
            left.getChunk();
        }
        if (right != null) {
            right.getChunk();
        }
    }

    @Override
    public Inventory getInventorySync() {
        if (inventory != null) {
            return inventory;
        } else {
            Preconditions.checkArgument(Bukkit.isPrimaryThread());
            return left instanceof InventoryHolder lholder
                    ? lholder.getInventory()
                    : (right instanceof InventoryHolder rholder ? rholder.getInventory() : null);
        }
    }
}
