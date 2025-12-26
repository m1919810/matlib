package me.matl114.matlib.unitTest.autoTests.nmsTests;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.NAMESPACE_KEY;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.REGISTRIES;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.*;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.Material;

public class CoreTests implements TestCase {
    static TestReflection TEST = DescriptorImplBuilder.createMultiHelper(TestReflection.class);

    @OnlineTest(name = "Env and Enum tests")
    public void test_env() throws Throwable {
        Assert(CraftBukkit.MAGIC_NUMBERS.getBlock(Material.AIR) == EmptyEnum.BLOCK_AIR);
        Assert(CraftBukkit.MAGIC_NUMBERS.getItem(Material.AIR) == EmptyEnum.ITEM_AIR);
        var itemA = CraftBukkit.ITEMSTACK.unwrapToNMS(new CleanItemStack(Material.BOOK, 336));
        Debug.logger(itemA);
        Assert(NMSItem.ITEMSTACK.getCount(itemA) == 336);
        var cisA = CraftBukkit.ITEMSTACK.createCraftItemStack(Material.BOOK, 23, null);
        Debug.logger(cisA);
        Assert(CraftUtils.isCraftItemStack(cisA));
        AtomicInteger count = new AtomicInteger();
        REGISTRIES.stream(BuiltInRegistryEnum.BLOCK).peek(b -> {
            count.getAndIncrement();
            if (CraftBukkit.MAGIC_NUMBERS.getMaterialFromBlock(b) == null) {
                Debug.logger("check material from block, ", b);
            }
        });
        Debug.logger("check material complete", count.get());
    }

    @OnlineTest(name = "special test ", automatic = false)
    public void test_test() throws Throwable {
        if (REGISTRIES.containsKey(BuiltInRegistryEnum.ITEM, NAMESPACE_KEY.newNSKey("minecraft", "myitem"))) {
            Debug.logger("already registered");
            return;
        }
        TEST.frozenSetter(BuiltInRegistryEnum.ITEM, false);
        TEST.unregisteredIntrusiveHoldersSetter(BuiltInRegistryEnum.ITEM, new IdentityHashMap<>());
        Object newItem = TEST.newItem(TEST.newProperties());
        Object item = TEST.registerItem("myitem", newItem);
        TEST.freeze(BuiltInRegistryEnum.ITEM);
        Debug.logger(item);
        CraftBukkit.MAGIC_NUMBERS.ITEM_MATERIALGetter().put(item, Material.END_GATEWAY);
        CraftBukkit.MAGIC_NUMBERS.MATERIAL_ITEMGetter().put(Material.END_GATEWAY, item);
    }

    @OnlineTest(name = "package private access test")
    public void test_access() throws Throwable {
        Debug.logger(NMSItem.CONTAINER.newCustomContainer(null, 1, "byd").getClass());
    }

    @MultiDescriptive(targetDefault = "wtf")
    public interface TestReflection extends TargetDescriptor {
        @FieldTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void frozenSetter(Object obj, boolean f);

        @FieldTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void unregisteredIntrusiveHoldersSetter(Object obj, Map<?, ?> map);

        @MethodTarget
        @RedirectClass("net.minecraft.core.MappedRegistry")
        void freeze(Object reg);

        @MethodTarget(isStatic = true)
        @RedirectClass("net.minecraft.world.item.Items")
        Object registerItem(String id, @RedirectType("Lnet/minecraft/world/item/Item;") Object item);

        @ConstructorTarget
        @RedirectClass("net.minecraft.world.item.Item")
        Object newItem(@RedirectType("Lnet/minecraft/world/item/Item$Properties;") Object properties);

        @ConstructorTarget
        @RedirectClass("net.minecraft.world.item.Item$Properties")
        Object newProperties();
    }
}
