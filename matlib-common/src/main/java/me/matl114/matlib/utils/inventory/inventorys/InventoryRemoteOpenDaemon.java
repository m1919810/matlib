package me.matl114.matlib.utils.inventory.inventorys;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.inventory.inventoryRecords.InventoryRecord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryRemoteOpenDaemon implements Listener {
    private final Map<Inventory, InventoryRecord> openingInventories =
            new ConcurrentHashMap<Inventory, InventoryRecord>();

    @Nullable public CompletableFuture<Void> openInventory(Player player, InventoryRecord record) {
        Inventory inv = record.inventory();
        if (inv == null || !record.canPlayerOpen(player)) {
            return null;
        }
        player.openInventory(inv);
        openingInventories.put(inv, record);
        CompletableFuture<Void> future = new CompletableFuture<>();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!record.stillValid() || inv.getViewers().isEmpty()) {
                    // will trigger CloseEvent, openedInventory Map will be removed here
                    // inv close require run on Main Thread
                    inv.close();
                    this.cancel();
                    // ((SimpleInventoryRecord) record).optionalHolder.tileEntity ==
                    // LevelUtils.getBlockEntityAsync(((SimpleInventoryRecord) record).optionalHolder.getBlock(), false)
                    // x=100020, y=0, z=100295
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                future.complete(null);
            }
        };
        runnable.runTaskTimer(ThreadUtils.getMockPlugin(), 0L, 1L);
        return future;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent e) {
        this.openingInventories.remove(e.getInventory());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getWhoClicked().getOpenInventory().getTopInventory();
        if (onInventory(inv)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onInventoryDrag(InventoryDragEvent e) {
        Inventory inv = e.getWhoClicked().getOpenInventory().getTopInventory();
        if (onInventory(inv)) {
            e.setCancelled(true);
        }
    }

    public boolean onInventory(Inventory inv) {
        InventoryRecord tileState = openingInventories.get(inv);
        if (tileState != null) {
            if (!tileState.stillValid()) {
                // execute close at next tick
                ThreadUtils.executeSyncSched(inv::close);
                // Schedules.launchSchedules(inv::close, 0, true, 0);
                return true;
            }
            // valid interact remote inventory
            if (tileState.invKind() == InventoryRecord.InventoryKind.TILE_ENTITY_INVENTORY) {
                // load chunk to ensure save
                tileState.ensureChunkLoad();
            }
        }

        return false;
    }
}
