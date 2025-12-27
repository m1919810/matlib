package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import me.matl114.matlib.implement.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.*;
import me.matl114.matlib.utils.inventory.inventoryRecords.InventoryRecord;
import me.matl114.matlib.utils.inventory.inventoryRecords.SimpleInventoryRecord;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryUtilTests implements TestCase {

    @OnlineTest(name = "Inventory validation test")
    public void test_Inventory() {
        World thisWorld = Bukkit.getWorlds().get(0);
        Location testLocation = new Location(thisWorld, 0, 0, 0);
        Block testBlock = testLocation.getBlock();
        Debug.logger("Running test in world: " + thisWorld.getName());
        Debug.logger("Launch scheduled sync task");
        // test blockEntity field reflection
        FutureTask<Void> createChest = ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            testBlock.setType(Material.CHEST);
                            Debug.logger("Create a chest");
                            return null;
                        },
                        10,
                        true);
        AtomicReference<BlockState> blockStateHere = new AtomicReference<>(null);
        AtomicReference<Inventory> inventoryHere = new AtomicReference<>(null);
        FutureTask<Void> getChestBlockState = ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            blockStateHere.set(WorldUtils.getBlockStateNoSnapShot(testBlock));
                            Assert(blockStateHere.get() instanceof InventoryHolder);
                            inventoryHere.set(((InventoryHolder) blockStateHere.get()).getInventory());
                            AssertNN(inventoryHere.get());
                            Debug.logger("get chest block state: " + blockStateHere.get());
                            return null;
                        },
                        10,
                        true);
        try {
            createChest.get();
            getChestBlockState.get();
        } catch (Throwable e) {
            Debug.logger(e);
        }
        Debug.logger("Back to Async Thread");
        Debug.logger("check inventory here ");
        Inventory inventory = inventoryHere.get();
        AssertNN(inventory);
        Debug.logger("check inventory slot ");
        inventory.setItem(0, new ItemStack(Material.CHEST));
        Assert(inventory.getItem(0).getType() == Material.CHEST);
        Debug.logger("check async blockState function");
        TileState tileState = (TileState) blockStateHere.get();
        AssertNN(tileState.getPersistentDataContainer());
        Assert(tileState.isPlaced());
        Assert(WorldUtils.getCraftBlockEntityStateClass().isInstance(tileState));
        Object tileEntity = WorldUtils.getTileEntityHandle().get(tileState);
        Debug.logger("check tileEntity ");
        Assert(WorldUtils.getTileEntityClass().isInstance(tileEntity));
        Debug.logger("check tile Entity Removal");
        Assert(!((boolean) WorldUtils.getTileEntityRemovalHandle().get(tileEntity)));
        Debug.logger("check tile Entity setChange");
        WorldUtils.tileEntitySetChange(tileState);
        AtomicReference<BlockState> newState = new AtomicReference<>(null);
        FutureTask<Void> deleteChest = ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            testBlock.setType(Material.AIR);
                            newState.set(WorldUtils.getBlockStateNoSnapShot(testBlock));
                            Debug.logger("Delete a chest");
                            return null;
                        },
                        10,
                        true);
        try {
            deleteChest.get();
        } catch (Throwable e) {
        }
        Debug.logger("check new BlockState");
        Assert(!WorldUtils.getCraftBlockEntityStateClass().isInstance(newState.get()));
        Debug.logger("check inventory status after delete");
        Assert(inventory.getItem(0).getType() == Material.CHEST);
        inventory.setItem(0, new ItemStack(Material.DIAMOND));
        Debug.logger("check inventory update after delete");
        Assert(inventory.getItem(0).getType() == Material.DIAMOND);
        Debug.logger("check tile Entity Removal");
        AssertNN(WorldUtils.getTileEntityRemovalHandle().get(tileEntity));
        long start = System.nanoTime();
        for (int i = 0; i < 1_000; ++i) {
            // tileEntityRemovalHandle.get(tileEntityHandle.get(tileState));
            WorldUtils.isTileEntityStillValid(tileState);
            // 24112100
        }
        long end = System.nanoTime();
        Debug.logger("Test Accessing tileState removal flag: cost", end - start);
        Debug.logger("check safety of Async inventory operation");
        // test Async Inventory Operation
        var iteration = WorldUtils.getInventoryHolderTypes();
        while (iteration.hasNext()) {
            var mat = iteration.next();
            FutureTask<InventoryRecord> materialInventory = ScheduleManager.getManager()
                    .getScheduledFuture(
                            () -> {
                                testBlock.setType(Material.AIR);
                                testBlock.setType(mat);
                                BlockState state = WorldUtils.getBlockStateNoSnapShot(testBlock);
                                Assert(state instanceof InventoryHolder);
                                return SimpleInventoryRecord.getInventoryRecord(testBlock.getLocation(), true);
                            },
                            0,
                            true);
            try {
                doAsyncInventoryTest(materialInventory.get(), mat);
            } catch (Throwable e) {
                Debug.severe(e, "Assertion failed for", mat);
            }
        }
        // test no-copy method
        Inventory bukkitInventory = Bukkit.createInventory(null, 54, "testTitle");
        ItemStack item = CraftUtils.getCraftCopy(new ItemStack(Material.DIAMOND));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer()
                .set(new NamespacedKey("matlib", "test"), PersistentDataType.STRING, "SFID_BLABLABLA");
        meta.setDisplayName(AddUtils.resolveColor("&6&l ababababababab"));
        item.setItemMeta(meta);
        NMSInventoryUtils.setInvItemNoCopy(bukkitInventory, 1, item);
        item.setAmount(63);
        Assert(bukkitInventory.getItem(1).getAmount() == 63);
        NMSInventoryUtils.setInvItemNoCopy(bukkitInventory, 1, null);
        Assert(bukkitInventory.getItem(1) == null);
        Debug.logger("Passing No-Copy inventory set");
        long a = System.nanoTime();
        for (int i = 0; i < 1_000; ++i) {
            bukkitInventory.setItem(0, item);
        }
        long b = System.nanoTime();
        Debug.logger("original setItem time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 1_000; ++i) {
            NMSInventoryUtils.setInvItemNoCopy(bukkitInventory, 0, item);
        }
        b = System.nanoTime();
        Debug.logger("no-copy setItem time", b - a);
    }

    private Material randGenMaterial() {
        Random rand = new Random();
        Material mat;
        do {
            mat = Material.values()[rand.nextInt(Material.values().length)];
        } while (!(mat.isItem() && !mat.isAir()));
        return mat;
    }

    public void doAsyncInventoryTest(InventoryRecord testInventoryRecord, Material material) {
        Inventory testInventory = testInventoryRecord.inventory();
        try {
            if (testInventory.getSize() > 0) {
                // check set
                testInventory.setItem(0, new ItemStack(Material.DIAMOND));
                Assert(testInventory.getItem(0).getType() == Material.DIAMOND);
                // check update
                testInventory.setItem(0, new ItemStack(Material.CHEST));
                Assert(testInventory.getItem(0).getType() == Material.CHEST);
                for (int i = 0; i < testInventory.getSize(); ++i) {
                    Material material1 = randGenMaterial();
                    ItemStack testItemStack = new ItemStack(material1);
                    NMSInventoryUtils.setTileInvItemNoUpdate(testInventoryRecord, i, testItemStack);
                    ItemStack itemInInv = testInventory.getItem(i);
                    Preconditions.checkArgument(
                            Objects.equals(itemInInv, testItemStack),
                            "Not equal for two ItemStack {0} {1} in slot {2}",
                            itemInInv,
                            testItemStack,
                            i);
                }
            } else {
                Debug.logger("Zero size Inventory: ", testInventory);
            }
        } catch (Throwable e) {
            Debug.warn("Validate unsafe inventory type:", testInventory.getType());
            Assert(!InventoryUtils.isInventoryTypeAsyncSafe(testInventory.getType()));
        }
        Debug.logger("Material", material.toString(), "passes the async inventory test");
    }

    @OnlineTest(name = "Spawner type test")
    public void test_spawnertype() {
        ItemStack item = new ItemStack(Material.SPAWNER);
        if (item.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            BlockState state = blockStateMeta.getBlockState();
            if (state instanceof CreatureSpawner spawner) {
                for (EntityType typed : EntityType.values()) {
                    if (typed == EntityType.UNKNOWN) continue;
                    try {
                        spawner.setSpawnedType(typed);
                        blockStateMeta.setBlockState(spawner);
                        item.setItemMeta(blockStateMeta);
                        Assert(((CreatureSpawner) ((BlockStateMeta) item.getItemMeta()).getBlockState())
                                        .getSpawnedType()
                                == typed);

                    } catch (Throwable e) {
                        Debug.logger(e);
                    }
                }
            }
        }
    }
}
