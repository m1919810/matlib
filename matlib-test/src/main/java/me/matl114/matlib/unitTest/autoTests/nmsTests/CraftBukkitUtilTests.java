package me.matl114.matlib.unitTest.autoTests.nmsTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.nbt.TagCompoundView;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CraftBukkitUtilTests implements TestCase {
    @OnlineTest(name = "Tag compound view tests")
    public void test_tagcompoundView() throws Throwable {
        ItemStack itemStack = ItemUtils.copyStack(SlimefunItems.TRASH_CAN);
        PersistentDataContainer container = ItemUtils.getPersistentDataContainerView(itemStack, true);
        AssertEq(
                container.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING),
                SlimefunItems.TRASH_CAN.getItemId());
        Assert(container.getKeys().size() == 1);
        container.set(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING, "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        Debug.logger(itemStack);
        ItemStack stack2 = ItemUtils.newStack(Material.HOPPER, 3);
        // test new pdc api
        PersistentDataContainer container2 = ItemUtils.getPersistentDataContainerView(stack2, true);
        container2.set(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING, "SHIT_TRASH_2");
        // the cache of container2 s flushed , lets do sth to fill the cache
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH_2");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        container2.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING);
        // then we apply modification from other source
        ItemUtils.setPersistentDataContainer(stack2, container, true);
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        // this should be a cached value, and it is not flushed from the raw, it is concurrentModification, but the
        // itemStack holds the newest version of modification
        AssertEq(container2.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING), "SHIT_TRASH_2");
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        // flush the cache to get the newest data
        ((TagCompoundView) container2).flush();
        AssertEq(container2.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        container2.set(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING, "SHIT_TRASH_2");
        // write can flush the cache
        AssertEq(container2.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING), "SHIT_TRASH_2");
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH_2");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        ((TagCompoundView) container2).copyFrom(container, true);
        AssertEq(container2.get(Slimefun.getItemDataService().getKey(), PersistentDataType.STRING), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(stack2).orElse(null), "SHIT_TRASH");
        AssertEq(Slimefun.getItemDataService().getItemData(itemStack).orElse(null), "SHIT_TRASH");
        for (int i = 0; i < 300; ++i) {
            ItemUtils.getPersistentDataContainerView(itemStack, true);
        }
        for (int i = 0; i < 300; ++i) {
            itemStack.getItemMeta().getPersistentDataContainer();
        }
        ExecutorUtils.sleepNs(23333);
        Debug.logger("creating view");
        long a = System.nanoTime();
        for (int i = 0; i < 1_000; ++i) {
            ItemUtils.getPersistentDataContainerView(itemStack, true);
        }
        long b = System.nanoTime();
        Debug.logger("Using time ", b - a);
        Debug.logger("using meta");
        a = System.nanoTime();
        for (int i = 0; i < 1_000; ++i) {
            itemStack.getItemMeta().getPersistentDataContainer();
        }
        b = System.nanoTime();
        Debug.logger("using meta", b - a);
    }
}
