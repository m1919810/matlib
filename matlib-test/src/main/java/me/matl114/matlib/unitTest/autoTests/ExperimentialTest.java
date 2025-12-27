package me.matl114.matlib.unitTest.autoTests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.common.functions.reflect.MethodInvoker;
import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.UnsafeOperation;
import me.matl114.matlib.common.lang.enums.Flags;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.experimential.FakeSchedular;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import sun.misc.Unsafe;

public class ExperimentialTest implements TestCase {
    private static Constructor<? extends Thread> threadConstructor;
    // @OnlineTest(name = "Tick Thread Test")
    public void test_constructTickThread() throws Throwable {
        Thread taskThread = FakeSchedular.runSync(() -> {
            Debug.logger("Running on Thread", Thread.currentThread(), "is Primary?", Bukkit.isPrimaryThread());
            Location testLocation = new Location(testWorld(), 0, 130, 0);
            // NOT RECOMMENDED
            Debug.logger("test setType and checkEntity");
            int counter = 0;
            for (int i = 0; i < 24_000; ++i) {
                testLocation.getChunk().setForceLoaded(true);
                counter += testWorld()
                        .getNearbyEntities(new Location(testWorld(), 0, 0, 0), 128, 128, 128)
                        .size();
                //                var re= testWorld().spawnEntity(testLocation, EntityType.MARKER);
                //                re.remove();
                testLocation.getChunk().setForceLoaded(false);
                testLocation.getBlock().setType(randGenMaterial());
                testLocation.getBlock().getType();
            }
            Debug.logger(counter);
            // ThreadUtils.sleep(1_000);
            Debug.logger("test block NBT get");
            testLocation.getBlock().setType(Material.CHEST);
            Chest chestState = (Chest) testLocation.getBlock().getState(false);
            String value = "testvalue_" + new Random().nextInt(114);
            chestState
                    .getPersistentDataContainer()
                    .set(new NamespacedKey("minecraft", "testkey"), PersistentDataType.STRING, value);
            chestState.update();
            for (int i = 0; i < 24_000; ++i) {
                Chest chestState1 = (Chest) testLocation.getBlock().getState(false);
                Assert(chestState1
                        .getPersistentDataContainer()
                        .get(new NamespacedKey("minecraft", "testkey"), PersistentDataType.STRING)
                        .equals(value));
                String newValue = "testvalue_" + new Random().nextInt(114514);
                chestState
                        .getPersistentDataContainer()
                        .set(new NamespacedKey("minecraft", "testkey"), PersistentDataType.STRING, newValue);
                chestState.update();
                value = newValue;
            }
            Debug.logger("test Finish");
        });
        taskThread.join();
        Debug.logger("Test getType speed");
        Location testLocation = new Location(testWorld(), 0, 130, 0);
        Material mat = testLocation.getBlock().getType();
        Block block = testLocation.getBlock();
        long a = System.nanoTime();
        for (int i = 0; i < 100_000; ++i) {
            block.getType();
        }
        long b = System.nanoTime();
        Debug.logger("async getType speed", b - a);
        Thread thread = FakeSchedular.runSync(() -> {
            long at = System.nanoTime();
            for (int i = 0; i < 100_000; ++i) {
                block.getType();
            }
            long bt = System.nanoTime();
            Debug.logger("sync getType speed", bt - at);
        });
        thread.join();
        Debug.logger("test tickThread finish");
    }

    private Material randGenMaterial() {
        Random rand = new Random();
        Material mat;
        do {
            mat = Material.values()[rand.nextInt(Material.values().length)];
        } while (!(mat.isBlock() && !mat.isAir()));
        return mat;
    }
    // @OnlineTest(name = "Versioned ItemStack Method test")
    public void test_itemstackMethod() throws Throwable {
        MethodAccess<?> access = MethodAccess.reflect(
                "getPersistentDataContainer", ItemStack.class); // .printError(true).initWithNull();
        Debug.logger(access.getMethodOrDefault(() -> null));
        Debug.logger(access.getMethodOrDefault(() -> null).getReturnType());
        ItemStack testItemStack = new CleanItemStack(Material.BOOK, 1, (meta) -> {
            meta.setDisplayName("&6测试&c小踏马的物品");
            meta.setLore(List.of("&6666", "&7777"));
            meta.getPersistentDataContainer()
                    .set(new NamespacedKey("minecraft", "testkey"), PersistentDataType.STRING, "testvalue");
            return meta;
        });
        MethodInvoker<?> invoker = access.getInvoker();

        Object obj = invoker.invoke(testItemStack);
        Debug.logger(obj);
        Debug.logger(obj.getClass());
        Debug.logger(obj.getClass()
                .getMethod("get", NamespacedKey.class, PersistentDataType.class)
                .invoke(obj, new Object[] {new NamespacedKey("minecraft", "testkey"), PersistentDataType.STRING}));
        //        long start = System.nanoTime();
        //        for (int i=0; i< 100_000;++i){
        //            invoker.invoke(testItemStack);
        //        }
        //        long end = System.nanoTime();
        //        Debug.logger("time used ",end-start);
        //        long start1 = System.nanoTime();
        //        for (int i=0; i< 100_000;++i){
        //            testItemStack.getItemMeta().getPersistentDataContainer();
        //        }
        //        long end1 = System.nanoTime();
        //        Debug.logger("time used ",end1-start1);
    }

    // @OnlineTest(name = "Async Chunk Load Test")
    public void test_asyncChunkLoad() throws Throwable {
        AtomicInteger cnt = new AtomicInteger(new Random().nextInt(36667));
        int y = new Random().nextInt(114514);
        Thread loadThread = FakeSchedular.runSync(() -> {
            for (int i = 0; i < 1_000; ++i) {
                int x = cnt.incrementAndGet();
                Chunk a = testWorld().getChunkAt(x, y);
                ExecutorUtils.sleep(2_00);
                if (i % 10 == 0) {
                    Debug.logger("Task ", i, "complete");
                }
            }
        });
        loadThread.join();
    }

    @OnlineTest(name = "unsafe test")
    public void test_unsafe() throws Throwable {
        EnumMap<Flags, Boolean> enumMap = new EnumMap<>(Flags.class);
        for (var flag : Flags.values()) {
            enumMap.put(flag, Boolean.FALSE);
        }

        Flags newFlag = ReflectUtils.addEnumConst(Flags.class, "SHIT", (u, n) -> {}, true);
        Debug.logger(Flags.values()[17]);
        ;
        Debug.logger(Flags.valueOf("SHIT"));
        Debug.logger((Object[]) Flags.values());
        enumMap.put((Flags) newFlag, Boolean.TRUE);
        Debug.logger(enumMap);
    }

    // @OnlineTest(name = "unsafe test 2")
    @UnsafeOperation
    @NotRecommended
    public void test_unsafe2() throws Throwable {
        Class<?> enumClass = Flags.class;
        Unsafe unsafe = ReflectUtils.getUnsafe();
        Field valuesField = Flags.class.getDeclaredField("$VALUES");
        // ensure clinit
        Object[] valuesClone = (Object[]) enumClass.getMethod("values").invoke(null);
        Object[] valuesShared =
                (Object[]) unsafe.getObject(unsafe.staticFieldBase(valuesField), unsafe.staticFieldOffset(valuesField));
        long offset = unsafe.arrayBaseOffset(valuesShared.getClass());
        long value = unsafe.arrayIndexScale(valuesShared.getClass());
        Debug.logger(valuesClone);
        Debug.logger(offset, value);
        Object val = unsafe.getObject(valuesShared, offset + value);
        Debug.logger(val);
        Thread.sleep(2_000);
        System.out.println("start");
        Thread.sleep(2_000);
        for (long i = 0; i < 16; ++i) {
            try {
                Object val2 = unsafe.getObject(valuesShared, offset + i * value);
                System.out.println("fucking enum " + val2);
            } catch (Throwable e) {
                System.out.println("shit , it's bug");
                e.printStackTrace();
            }
        }
        System.out.println("shit ,I am here");
        Thread.sleep(2_000);
        ReflectUtils.resizeArray(valuesShared, 1700);
        for (long i = 16; i < 1680; ++i) {
            unsafe.putObject(valuesShared, offset + i * value, null);
        }
        System.out.println("shit, I pull shit in it");

        for (long i = 16; i < 1680; ++i) {
            try {
                Object val2 = valuesShared[(int) i]; // unsafe.getObject(valuesShared, offset + i*value);
                System.out.println("shit shit " + i + "" + val2);
            } catch (Throwable e) {
                System.out.println("shit , it's bug");
                e.printStackTrace();
            }
        }
        Debug.logger((Object[]) Flags.values());
    }
}
