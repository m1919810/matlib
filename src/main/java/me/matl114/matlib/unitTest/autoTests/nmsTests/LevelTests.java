package me.matl114.matlib.unitTest.autoTests.nmsTests;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.COMPOUND_TAG;
import static me.matl114.matlib.nmsMirror.impl.NMSLevel.BLOCK_ENTITY;
import static me.matl114.matlib.nmsMirror.impl.NMSLevel.LEVEL;

import java.util.concurrent.FutureTask;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.nmsUtils.LevelUtils;
import me.matl114.matlib.nmsUtils.ServerUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class LevelTests implements TestCase {
    @OnlineTest(name = "World and blockEntity tests")
    public void test_worldAndBlockEntity() throws Throwable {
        int x = 2;
        int y = 36;
        int z = 36;
        Block block = testWorld().getBlockAt(2, 36, 36);
        ServerUtils.executeFuture(() -> {
                    Assert(Bukkit.isPrimaryThread());
                    block.setType(Material.CHEST);
                    Assert(block.getState(false) instanceof TileState);
                    ((TileState) block.getState(false))
                            .getPersistentDataContainer()
                            .set(new NamespacedKey("matlibtest", "test"), PersistentDataType.STRING, "testvalue");
                    ((InventoryHolder) block.getState(false))
                            .getInventory()
                            .setItem(10, new ItemStack(Material.DIAMOND));
                    return null;
                })
                .get();
        int cx = x >> 4;
        int cz = z >> 4;
        var serverLevel = WorldUtils.getHandledWorld(testWorld());
        testWorld().getChunkAt(cx, cz);
        var chunk1 = LEVEL.getChunkCustom(serverLevel, cx, cz, true);
        AssertNN(chunk1);
        Debug.logger(chunk1);
        var entity1 = LevelUtils.getBlockEntityAsync(block, false);
        AssertNN(entity1);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));
        Debug.logger(BLOCK_ENTITY.saveWithId(entity1));
        Object pdc = BLOCK_ENTITY.getPersistentDataCompound(entity1);
        COMPOUND_TAG.clear(pdc);
        COMPOUND_TAG.putBoolean(pdc, "testBoolean", true);
        Debug.logger(BLOCK_ENTITY.saveWithFullMetadata(entity1));

        Debug.logger("test block getter");
        Debug.logger(block.getType());
        long a = System.nanoTime();
        for (int i = 0; i < 99_999; ++i) {
            block.getType();
        }
        long b = System.nanoTime();
        Debug.logger("using time ", b - a);
        Debug.logger(LevelUtils.getBlockTypeAsync(block, true));
        a = System.nanoTime();
        for (int i = 0; i < 99_999; ++i) {
            // LEVEL.getBlockStateCustom(serverLevel, block.getX(), block.getY(), block.getZ(),true);
            LevelUtils.getBlockTypeAsync(block, true);
        }
        b = System.nanoTime();
        Debug.logger("using time ", b - a);
    }

    @OnlineTest(name = "minecraft chunk schedular test")
    public void test_chunkschedular() throws Throwable {
        World world = testWorld();
        for (int i = 0; i < 10; ++i) {
            long c = System.nanoTime();
            FutureTask<Void> task = ExecutorUtils.getFutureTask(() -> {
                long b = System.nanoTime();
                Debug.logger("World Main Executor Response Time", b - c);
                Assert(Bukkit.isPrimaryThread());
            });
            ServerUtils.executeAsChunkTask(world, task);
            task.get();
        }
        ServerUtils.executeSync(() -> {
            Debug.logger("throw a fucking exception on main ");
            throw new RuntimeException();
        });
        ExecutorUtils.sleep(50);
    }
}
