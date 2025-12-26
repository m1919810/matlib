package me.matl114.matlib.slimefunUtils.blockInventory.records;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import javax.annotation.Nonnull;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.implement.slimefun.manager.BlockDataCache;
import me.matl114.matlib.utils.inventory.inventoryRecords.InventoryRecord;
import me.matl114.matlib.utils.inventory.inventoryRecords.SimpleInventoryRecord;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record SlimefunInventoryRecord(Inventory inventory, @Nonnull SlimefunBlockData data) implements InventoryRecord {
    @Override
    public Location invLocation() {
        return data.getLocation();
    }

    @Override
    public InventoryHolder optionalHolder() {
        return data.getBlockMenu();
    }

    @Override
    public InventoryKind invKind() {
        return InventoryKind.PLUGIN_BLOCKMENU;
    }

    @Override
    public boolean stillValid() {
        return inventory != null && !data.isPendingRemove();
    }

    @Override
    public void setChange() {}

    @Override
    public Inventory getInventorySync() {
        return inventory;
    }

    @Override
    public boolean hasData() {
        return true;
    }

    public boolean canPlayerOpen(Player p) {
        return data.getBlockMenu() != null
                && data.getBlockMenu().canOpen(data.getLocation().getBlock(), p);
    }

    @ForceOnMainThread(condition = "checkVanilla = true")
    public static InventoryRecord getInventoryRecord(Location loc, boolean checkVanilla) {
        SlimefunBlockData data = BlockDataCache.getManager().safeGetBlockDataFromCache(loc);
        if (data != null) {
            BlockMenu inv = data.getBlockMenu();
            if (inv != null) {
                return new SlimefunInventoryRecord(inv.toInventory(), data);
            }
            // also contains vanilla inventory with Slimefun Item
        }
        return checkVanilla
                ? SimpleInventoryRecord.getInventoryRecord(loc)
                : new SimpleInventoryRecord(null, null, loc);
    }
}
