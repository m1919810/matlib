package me.matl114.matlib.unitTest.manualTests;

import java.util.Objects;
import java.util.Random;
import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.LocationUtils;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.inventory.inventoryRecords.InventoryRecord;
import me.matl114.matlib.utils.inventory.inventoryRecords.SimpleInventoryRecord;
import me.matl114.matlib.utils.inventory.inventorys.InventoryRemoteOpenDaemon;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryPlayerTest implements TestCase, Listener {
    InventoryRemoteOpenDaemon daemon = new InventoryRemoteOpenDaemon();

    {
        Bukkit.getPluginManager().registerEvents(this, MatlibTest.getInstance());
        Bukkit.getPluginManager().registerEvents(daemon, MatlibTest.getInstance());
    }
    // 论证： 超链接是安全的, Daemon任务也是正常运行的
    // 1. 卸载区块中可能发生方块实体卸载，此时由于在卸载区块读取blockType导致报错
    // 2. 卸载区块后方块实体仍旧有效？ 不知为
    // 研究 setRemoved从何而来
    //  通过public void unload(LevelChunk chunk) 调用
    //  那为什么有些情况区块卸载了实体还在
    //      有些实体在卸载的区块里 这些区块并没有被丢弃而是缓存在ServerChunkCache中， 对象是相同的。？
    //      那这对吗
    // 为什么可以正确保存
    // setChanged
    //
    //

    Inventory holder;
    Block blockPos;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (holder != null && Objects.equals(event.getInventory(), holder)) {
            Debug.logger("Holder closed");
            onInvLog(holder);
            ThreadUtils.executeSync(this::checkInventoryPost, 100);
            holder = null;
        }
    }

    private void onInvLog(Inventory inv) {
        int size = inv.getSize();
        Debug.logger("Log inventory");
        for (int i = 0; i < size; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack != null) {
                Debug.logger(i, stack);
            }
        }
    }
    // MatlibTest.testRunner.manuallyExecutedCase.get("open_remote_chest_test").getB().onInvLog()

    private void checkInventoryPost() {
        Debug.logger("Log inventory post");
        InventoryHolder holder1 = (InventoryHolder) blockPos.getState(false);
        Inventory iinv = holder1.getInventory();
        onInvLog(iinv);
    }

    @OnlineTest(name = "open remote chest test", automatic = false)
    public void test_open_remote_chest(CommandSender sender) {
        Player player = (Player) sender;
        Random random = new Random();

        Block block =
                player.getWorld().getBlockAt(100000 + random.nextInt(-1000, 1000), 0, 100000 + random.nextInt(1000));
        blockPos = block;
        ThreadUtils.executeSync(() -> {
            Debug.logger("Select position", blockPos);
            Debug.logger("Start chunk load");
            blockPos.getChunk();
            // chun
            Debug.logger("chunk load finish");
            blockPos.setType(Material.CHEST);
            InventoryRecord record = SimpleInventoryRecord.getInventoryRecord(blockPos.getLocation(), true);
            holder = record.inventory();
            holder.setItem(3, new ItemStack(Material.DIAMOND));
            holder.setItem(4, new ItemStack(Material.COMMAND_BLOCK));
            daemon.openInventory(player, record);
            onInvLog(holder);
            Debug.logger("Simulate steal");
            holder.setItem(4, null);
            onInvLog(holder);
            long chunkKey = LocationUtils.toChunkPos(block);
            Chunk chunk = LocationUtils.getChunk(player.getWorld(), chunkKey, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Debug.logger("check chunk load");
                    if (holder != null) {
                        Debug.logger(LocationUtils.isChunkLoaded(player.getWorld(), chunkKey));
                        Debug.logger(chunk.getLoadLevel());
                        Debug.logger();
                    } else {
                        Debug.logger("Task finish");
                        cancel();

                        ThreadUtils.executeSync(InventoryPlayerTest.this::checkInventoryPost, 100);
                    }
                }
            }.runTaskTimer(MatlibTest.getInstance(), 1, 1);
        });
    }

    @OnlineTest(name = "open remote chest saving issues", automatic = false)
    public void test_open_remote_chest_saving(CommandSender sender) {
        Player player = (Player) sender;
        Random random = new Random();

        Block block =
                player.getWorld().getBlockAt(100000 + random.nextInt(-1000, 1000), 0, 100000 + random.nextInt(1000));
        blockPos = block;
        ThreadUtils.executeSync(() -> {
            Debug.logger("Select position", blockPos);
            Debug.logger("Start chunk load");
            blockPos.getChunk();
            // chun
            Debug.logger("chunk load finish");
            blockPos.setType(Material.CHEST);
            InventoryRecord record = SimpleInventoryRecord.getInventoryRecord(blockPos.getLocation(), true);
            holder = record.inventory();
            ThreadUtils.executeSync(
                    () -> {
                        holder.setItem(3, new ItemStack(Material.DIAMOND));
                        holder.setItem(4, new ItemStack(Material.COMMAND_BLOCK));
                        daemon.openInventory(player, record);
                        onInvLog(holder);
                        Debug.logger("Simulate steal");
                        holder.setItem(4, null);
                        onInvLog(holder);
                        long chunkKey = LocationUtils.toChunkPos(block);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Debug.logger("check chunk load");
                                if (holder != null) {
                                    Debug.logger(LocationUtils.isChunkLoaded(player.getWorld(), chunkKey));
                                    Debug.logger();
                                } else {
                                    Debug.logger("Task finish");
                                    cancel();

                                    ThreadUtils.executeSync(InventoryPlayerTest.this::checkInventoryPost, 100);
                                }
                            }
                        }.runTaskTimer(MatlibTest.getInstance(), 1, 20);
                    },
                    20);
        });
    }
}
