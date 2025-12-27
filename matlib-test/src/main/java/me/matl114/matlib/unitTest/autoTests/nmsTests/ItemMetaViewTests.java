package me.matl114.matlib.unitTest.autoTests.nmsTests;

import com.google.common.collect.*;
import java.util.*;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.nbt.ItemDataValue;
import me.matl114.matlib.nmsUtils.nbt.ItemMetaView;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionedAttribute;
import me.matl114.matlib.utils.version.VersionedRegistry;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMetaViewTests implements TestCase {
    @OnlineTest(name = "test codec values")
    public void test_codec() throws Throwable {}

    static ItemStack generateComplex() {
        ItemStack stack = ItemUtils.newStack(Material.CHEST, 33);
        ItemMeta meta0 = stack.getItemMeta();
        meta0.addEnchant(VersionedRegistry.enchantment("efficiency"), 36, true);
        meta0.setUnbreakable(true);
        meta0.addAttributeModifier(
                VersionedRegistry.attribute("max_health"),
                VersionedAttribute.getInstance()
                        .createAttributeModifier(
                                UUID.randomUUID(),
                                "shit-attribute",
                                336,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HEAD));
        stack.setItemMeta(meta0);
        try {
            meta0.setCanPlaceOn(Set.of(Material.STONE, Material.DIRT));
            stack.setItemMeta(meta0);
        } catch (Throwable e) {
            Object nms = ItemUtils.unwrapHandle(stack);
            ItemDataValue.primitive(
                            "minecraft:can_place_on",
                            Map.of("predicates", Map.of("blocks", List.of("minecraft:stone", "minecraft:dirt"))))
                    .applyToStack(nms);
        }
        return stack;
    }

    @OnlineTest(name = "test mmap values")
    public void test_mmap() throws Throwable {
        ItemStack stack = generateComplex();

        Object nms = ItemUtils.unwrapHandle(stack);
        Debug.logger(ItemUtils.dumpItemStack(nms));
        ItemMetaView view = ItemMetaView.of(nms);
        // ench tests
        Debug.logger("enchantment map tests");
        Assert(view.getEnchants().size() == 1);
        view.addEnchant(VersionedRegistry.enchantment("sharpness"), 64, true);
        Assert(stack.getItemMeta().getEnchants().size() == 2);
        Debug.logger(ItemUtils.dumpItemStack(nms));
        Assert(new HashMap<>(stack.getItemMeta().getEnchants())
                .equals(Map.of(
                        VersionedRegistry.enchantment("efficiency"),
                        36,
                        VersionedRegistry.enchantment("sharpness"),
                        64)));
        view.removeEnchantments();
        Assert(!view.hasEnchants());
        Assert(!stack.getItemMeta().hasEnchants());
        view.addEnchant(VersionedRegistry.enchantment("efficiency"), 36, true);
        Debug.logger(ItemUtils.dumpItemStack(nms));
        // itemflag tests
        Debug.logger("item flag tests");
        Assert(view.getItemFlags().isEmpty());
        Set<ItemFlag> sets = Set.of(
                ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON);
        view.addItemFlags(sets.toArray(ItemFlag[]::new));
        Assert(view.getItemFlags().size() == sets.size());
        Assert(Objects.equals(view.getItemFlags(), sets));
        Debug.logger(stack.getItemMeta().getItemFlags());
        if (Version.getVersionInstance().isAtLeast(Version.v1_20_R3)) {
            Assert(Objects.equals(stack.getItemMeta().getItemFlags(), sets));
        } else {
            ItemStack stack0 = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta testMeta = stack0.getItemMeta();
            testMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            stack0.setItemMeta(testMeta);
            Debug.logger(ItemUtils.dumpItemStack(ItemUtils.unwrapHandle(stack0)));
        }
        view.removeItemFlags(sets.toArray(ItemFlag[]::new));
        Assert(view.getItemFlags().isEmpty());
        Assert(stack.getItemMeta().getItemFlags().isEmpty());
        // test unbreakable
        Debug.logger("unbreakable tests");
        Assert(view.isUnbreakable());
        view.setUnbreakable(false);
        Assert(!stack.getItemMeta().isUnbreakable());
        Assert(!view.isUnbreakable());
        view.setUnbreakable(true);
        Assert(stack.getItemMeta().isUnbreakable());
        Assert(!stack.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
        Assert(view.isUnbreakable());
        Assert(!view.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
        view.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        // test attributes
        Debug.logger("attribute tests");
        Assert(view.hasAttributeModifiers());
        Assert(view.getAttributeModifiers().size() == 1);
        view.addAttributeModifier(
                VersionedRegistry.attribute("movement_speed"),
                VersionedAttribute.getInstance()
                        .createAttributeModifier(
                                UUID.randomUUID(),
                                "shit-attribute-2",
                                114,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HEAD));
        view.addAttributeModifier(
                VersionedRegistry.attribute("armor"),
                VersionedAttribute.getInstance()
                        .createAttributeModifier(
                                UUID.randomUUID(),
                                "shit-attribute-2",
                                191,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HEAD));
        Multimap<Attribute, AttributeModifier> modifierMultimap =
                LinkedHashMultimap.create(view.getAttributeModifiers());
        AssertEq(modifierMultimap, LinkedHashMultimap.create(modifierMultimap));
        Assert(modifierMultimap.size() == 3);
        Assert(view.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        Assert(stack.getItemMeta().getAttributeModifiers().size() == 3);
        AssertEq(modifierMultimap, LinkedHashMultimap.create(stack.getItemMeta().getAttributeModifiers()));
        view.setAttributeModifiers(null);
        Assert(!stack.getItemMeta().hasAttributeModifiers());
        Multimap<Attribute, AttributeModifier> newMap = LinkedHashMultimap.create();
        for (var entry : modifierMultimap.entries()) {
            view.addAttributeModifier(entry.getKey(), entry.getValue());
            newMap.put(entry.getKey(), entry.getValue());
            AssertEq(newMap, LinkedHashMultimap.create(stack.getItemMeta().getAttributeModifiers()));
        }
        view.setAttributeModifiers(null);
        Assert(!stack.getItemMeta().hasAttributeModifiers());
        view.setAttributeModifiers(newMap);
        AssertEq(LinkedHashMultimap.create(stack.getItemMeta().getAttributeModifiers()), newMap);
        Debug.logger(ItemUtils.dumpItemStack(nms));
        Debug.logger("All tests passed");
    }

    @OnlineTest(name = "test metaView serialization")
    public void test_serialization() throws Throwable {
        ItemStack stack = generateComplex();
        Object nms = ItemUtils.unwrapHandle(stack);
        ItemMetaView view = ItemMetaView.of(nms);
        ItemMeta meta = stack.getItemMeta();
        AssertEq(meta.getAsString(), view.getAsString());
        if (Version.getVersionInstance().isAtLeast(Version.v1_20_R4)) {
            // test comp string
            AssertEq(meta.getAsComponentString(), view.getAsComponentString());
        }
    }
}
