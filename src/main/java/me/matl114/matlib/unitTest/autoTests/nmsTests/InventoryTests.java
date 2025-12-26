package me.matl114.matlib.unitTest.autoTests.nmsTests;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.COMPOUND_TAG;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentEnum;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.inventory.ItemHashMap;
import me.matl114.matlib.nmsUtils.inventory.ItemStackKey;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class InventoryTests implements TestCase {
    BooleanConsumer bh;

    @OnlineTest(name = "ItemStackHelper Test")
    public void test_ItemStackHelper() {
        CompoundTagHelper compHelper = NMSCore.COMPOUND_TAG;
        ResourceLocationHelper keyHelper = NMSCore.NAMESPACE_KEY;
        RegistriesHelper regHelper = NMSCore.REGISTRIES;
        ItemStackHelper itemHelper = NMSItem.ITEMSTACK;
        Object itm = regHelper.getValue(BuiltInRegistryEnum.ITEM, keyHelper.newNSKey("minecraft", "diamond_pickaxe"));
        Debug.logger(itm);
        Object newItemStack = itemHelper.newItemStack(itm, 3);
        Object nbt = itemHelper.getCustomedNbtView(newItemStack, true).getView();
        ItemStack item = itemHelper.getBukkitStack(newItemStack);
        Assert(item.getAmount() == 3);
        Debug.logger(nbt);
        ItemStack itemStack = SlimefunItems.GOLD_18K;
        ItemStack cis = ItemUtils.copyStack(itemStack);
        Object handle = ItemUtils.getHandle(cis);
        Object emptyNbt = compHelper.newComp();
        Debug.logger(itemHelper.save(handle, emptyNbt));
        Debug.logger(emptyNbt);
        cis.lore(List.of(Component.text("你好")));
        // test eq
        var itemStack2 = cis.clone();
        var itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(List.of("c", "t", "m", "d"));
        itemStack2.setItemMeta(itemMeta);
        var itemStack3 = cis.clone();
        itemMeta.setDisplayName("nmd");
        itemStack3.setItemMeta(itemMeta);
        // "clone" a exact same itemStack to avoid copy-on-write mechanism, because most of our comparison happens
        // between unrelated ItemStack, not cloned or sth
        var itemStack4 = ItemUtils.cleanStack(CleanItemStack.ofBukkitClean(itemStack3));
        Debug.logger(cis);
        Debug.logger(itemStack2);
        Debug.logger(itemStack3);
        Assert(itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack2), false, true));
        Assert(!itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), true, true));
        Assert(itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), false, false));

        Assert(!itemHelper.matchItem(ItemUtils.getHandle(cis), ItemUtils.getHandle(itemStack3), false, true));
        ;
        Assert(itemHelper.matchItem(ItemUtils.getHandle(itemStack2), ItemUtils.getHandle(itemStack3), true, false));
        Assert(!itemHelper.matchItem(ItemUtils.getHandle(itemStack2), ItemUtils.getHandle(itemStack3), true, true));
        Assert(itemHelper.matchItem(ItemUtils.getHandle(itemStack3), ItemUtils.getHandle(itemStack4), true, true));
        //        Object handle1 =  ItemUtils.getHandle(itemStack3);
        //        Object handle2 = ItemUtils.getHandle(itemStack4);
        //        Assert(handle1 != handle2);
        bh = (b) -> {};
        long a = System.nanoTime();
        for (int i = 0; i < 1000; ++i) {
            bh.accept(ItemUtils.matchItemStack(itemStack3, itemStack4, false, true));
        }
        long b = System.nanoTime();
        Debug.logger("check nbt time", b - a);

        a = System.nanoTime();
        ItemMeta meta1 = itemStack3.getItemMeta();
        ItemMeta meta2 = itemStack4.getItemMeta();
        for (int i = 0; i < 1000; ++i) {
            //             itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
            bh.accept(meta1.equals(meta2));
            //            ItemUtils.matchItemMeta(meta1, meta2, true);
        }
        b = System.nanoTime();
        Debug.logger("meta match time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 1000; ++i) {
            bh.accept(itemStack3.isSimilar(itemStack4));
        }
        b = System.nanoTime();
        Debug.logger("itemSimilar time", b - a);
        a = System.nanoTime();
        ItemMeta meta3 = itemStack3.getItemMeta();
        ItemMeta meta4 = itemStack4.getItemMeta();
        for (int i = 0; i < 1000; ++i) {
            //             itemStack3.getItemMeta().equals(itemStack4.getItemMeta());
            //            meta1.equals(meta2);
            bh.accept(CraftUtils.matchItemMeta(meta3, meta4, true));
        }
        b = System.nanoTime();
        Debug.logger("utils match time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 1000; ++i) {
            itemStack3.getItemMeta();
        }
        b = System.nanoTime();
        Debug.logger("getItemMeta time", b - a);
        Debug.logger("Test shulker box item");
        ItemStack shulker = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
        InventoryHolder holder = (InventoryHolder) meta.getBlockState();
        Inventory inventory = holder.getInventory();
        inventory.setItem(0, new ItemStack(Material.DIAMOND));
        inventory.setItem(1, new ItemStack(Material.BOOK));
        meta.setBlockState((BlockState) holder);
        shulker.setItemMeta(meta);
        var nmsShulker = CraftBukkit.ITEMSTACK.unwrapToNMS(shulker);
        var nbtShulker = NMSItem.ITEMSTACK.save(nmsShulker, COMPOUND_TAG.newComp());
        Debug.logger(nbtShulker);
        if (NMSItem.ITEMSTACK instanceof ItemStackHelper_1_20_R4 v1204) {
            ItemStack val = ItemUtils.newStack(Material.DIAMOND, 0);
            Assert(v1204.getComponents(CraftBukkit.ITEMSTACK.unwrapToNMS(val))
                    == DataComponentEnum.COMPONENT_MAP_EMPTY);
            Debug.logger("1.20.5+ test pass");
        }
    }

    @OnlineTest(name = "inventory mech test")
    public void test_InventoryMech() {
        Inventory inventory = CraftBukkit.INVENTORYS.createCustomInventory(
                new InventoryHolder() {
                    @Override
                    public @NotNull Inventory getInventory() {
                        return null;
                    }
                },
                1,
                "shit");
        Object content = CraftBukkit.INVENTORYS.getInventory(inventory);
        List itemList = NMSItem.CONTAINER.getContents(content);
        Debug.logger(itemList);
        Assert(NMSItem.ITEMSTACK.equalsEmpty(itemList.get(0)));
        inventory.setItem(0, ItemUtils.copyStack(SlimefunItems.GOLD_18K));
        Debug.logger(itemList);
        Debug.logger(NMSItem.ITEMSTACK.save(itemList.get(0)));
        ItemStack item = inventory.getItem(0);
        item.setAmount(0);
        Debug.logger(itemList);
        Debug.logger(NMSItem.ITEMSTACK.save(itemList.get(0)));
        Assert(NMSItem.ITEMSTACK.isEmpty(itemList.get(0)));
        item.setAmount(33);
        Debug.logger(itemList);
        Debug.logger(NMSItem.ITEMSTACK.save(itemList.get(0)));
        item.setAmount(0);
        item.setType(Material.HOPPER);
        Debug.logger(itemList);
        Debug.logger(NMSItem.ITEMSTACK.save(itemList.get(0)));
        item.setAmount(33);
        Debug.logger(itemList);
        Debug.logger(NMSItem.ITEMSTACK.save(itemList.get(0)));
        // test 1.21

    }

    volatile int value;

    @OnlineTest(name = "item hash test")
    public void test_itemHash() {
        Object2IntOpenCustomHashMap<ItemStack> hashMapTest =
                new Object2IntOpenCustomHashMap<>(300, new Hash.Strategy<ItemStack>() {
                    @Override
                    public int hashCode(ItemStack itemStack) {
                        return ItemUtils.itemStackHashCode(itemStack);
                    }

                    @Override
                    public boolean equals(ItemStack itemStack, ItemStack k1) {
                        return ItemUtils.matchItemStack(itemStack, k1, true);
                    }
                });
        List<ItemStack> items = Slimefun.getRegistry().getAllSlimefunItems().stream()
                .map(SlimefunItem::getItem)
                .map(ItemUtils::copyStack)
                .toList();
        for (var it : items) {

            hashMapTest.put(it, 1);
        }
        Slimefun.getRegistry().getAllSlimefunItems().stream()
                .map(SlimefunItem::getItem)
                .forEach(i -> hashMapTest.computeInt(i, (j, k) -> k + 1));
        for (var re : hashMapTest.object2IntEntrySet()) {
            if (re.getIntValue() != 2) {
                Debug.logger("check multiple item", re.getKey());
            }
        }
        long a = System.nanoTime();
        for (var item : items) {
            value = ItemUtils.itemStackHashCode(item);
        }
        long b = System.nanoTime();
        Debug.logger("hashing items cost", b - a, "for", items.size(), "items");
        a = System.nanoTime();
        for (var item : items) {
            value = item.hashCode();
        }
        b = System.nanoTime();
        Debug.logger("hashing items cost", b - a, "for", items.size(), "items");
        Int2IntArrayMap hashMap = new Int2IntArrayMap();

        items.forEach(s -> hashMap.compute(ItemUtils.itemStackHashCode(s), (i, j) -> {
            return j == null ? 1 : j + 1;
        }));
        for (var entry : hashMap.int2IntEntrySet()) {
            if (entry.getIntValue() != 1) {
                Debug.logger("hash Conflict at", entry.getIntValue(), entry.getIntKey());
            }
        }
        // test no display hashcode
        a = System.nanoTime();
        for (var item : items) {
            value = ItemUtils.itemStackHashCodeWithoutLore(item);
        }
        b = System.nanoTime();
        Debug.logger("hashing items no display cost", b - a, "for", items.size(), "items");
        Object2IntOpenCustomHashMap<ItemStack> hashMapTestNoDisplay =
                new Object2IntOpenCustomHashMap<>(300, new Hash.Strategy<ItemStack>() {
                    @Override
                    public int hashCode(ItemStack itemStack) {
                        return ItemUtils.itemStackHashCodeWithoutLore(itemStack);
                    }

                    @Override
                    public boolean equals(ItemStack itemStack, ItemStack k1) {
                        return ItemUtils.matchItemStack(itemStack, k1, false);
                    }
                });
        for (var it : items) {
            hashMapTestNoDisplay.put(it, 1);
        }
        List<ItemStack> itemsWithLoreModify = Slimefun.getRegistry().getAllSlimefunItems().stream()
                .map(SlimefunItem::getItem)
                .map(i -> {
                    ItemStack i2 = i.clone();
                    var lore = i2.lore();
                    lore = lore == null ? new ArrayList<>() : lore;
                    lore.add(Component.text("shit lore check line fucking 1"));
                    i2.lore(lore);
                    return i2;
                })
                .peek(i -> hashMapTestNoDisplay.computeInt(i.clone(), (j, k) -> k == null ? 1 : k + 1))
                .map(i -> {
                    ItemStack i2 = i.clone();
                    var lore = i2.lore();
                    lore = lore == null ? new ArrayList<>() : lore;
                    lore.add(Component.text("shit lore check line fucking 2"));
                    i2.lore(lore);
                    int hash1 = ItemUtils.itemStackHashCodeWithoutLore(i);
                    int hash2 = ItemUtils.itemStackHashCodeWithoutLore(i2);
                    if (hash1 != hash2) {
                        Debug.logger("hash different at", i, i2, hash1, hash2);
                    }
                    if (!ItemUtils.matchItemStack(i, i2, false)) {
                        Debug.logger("equals different at ", i, i2, ItemUtils.cleanStack(i), ItemUtils.cleanStack(i2));
                        throw new RuntimeException();
                    }
                    return i2;
                })
                .peek(i2 -> {
                    hashMapTestNoDisplay.computeInt(i2, (j, k) -> k == null ? 1 : k + 1);
                })
                .toList();
        ;

        for (var re : hashMapTestNoDisplay.object2IntEntrySet()) {
            if (re.getIntValue() != 3) {
                // pass no-lore , because it is lore-distinct, but the one with no lore is different from haslore
                if (re.getIntValue() == 1 && re.getKey().lore() == null) {
                    continue;
                } else if (re.getIntValue() == 2 && re.getKey().lore().size() == 1) {
                    continue;
                }
                Debug.logger("check multiple item no display", ItemUtils.cleanStack(re.getKey()), re.getIntValue());
            }
        }
        Int2IntArrayMap hashMap2 = new Int2IntArrayMap();
        Multimap<Integer, ItemStack> hashToItemStack = LinkedListMultimap.create();
        items.forEach(s -> {
            int value = ItemUtils.itemStackHashCodeWithoutLore(s);
            hashMap2.compute(value, (i, j) -> {
                return j == null ? 1 : j + 1;
            });
            hashToItemStack.put(value, s);
        });
        for (var entry : hashMap2.int2IntEntrySet()) {
            if (entry.getIntValue() != 1) {
                Debug.logger(
                        "hash no display Conflict at",
                        entry.getIntValue(),
                        entry.getIntKey(),
                        hashToItemStack.get(entry.getIntKey()));
            }
        }

        ItemHashMap<Integer> itemMap = new ItemHashMap<>(12800, false);
        List<ItemStack> itemWithLoreAdded = itemsWithLoreModify.stream()
                .map(i -> {
                    ItemStack itemAddLore = ItemUtils.copyStack(CleanItemStack.ofBukkitClean(i));
                    var lore = itemAddLore.lore();
                    lore.add(Component.text("shit lore check line 3"));
                    itemAddLore.lore(lore);
                    return itemAddLore;
                })
                .toList();
        List<ItemStackKey> itemStackKeys = itemsWithLoreModify.stream()
                .map(ItemStackKey::of)
                .peek(ItemStackKey::getHashCodeNoLore)
                .toList();
        List<Integer> itemWithLoreInteger =
                itemsWithLoreModify.stream().map(s -> s.getType().ordinal()).toList();
        a = System.nanoTime();
        // itemMap = new ItemHashMap<>(itemsWithLoreModify.toArray(ItemStack[]::new),
        // itemWithLoreInteger.toArray(Integer[]::new),false);
        itemStackKeys.forEach(s -> itemMap.put(s, s.getType().ordinal()));
        b = System.nanoTime();
        Debug.logger("itemHashMap construct, using", b - a, "size", itemMap.size());
        a = System.nanoTime();
        for (var item : itemWithLoreAdded) {
            try {
                Assert(itemMap.get(item) == item.getType().ordinal());
            } catch (Throwable e) {
                Debug.logger("Assertion failed for ", item);
            }
        }
        b = System.nanoTime();
        Debug.logger("test pass for itemsHashMap, using", b - a);
        a = System.nanoTime();
        BiPredicate<ItemStack, ItemStack> matcher = ItemHashMap.NO_LORE_ITEM_STRATEGY::equals;
        for (var item : itemWithLoreAdded) {
            for (var entry : itemMap.entrySet()) {
                if (matcher.test(item, entry.getKey())) {
                    Assert(entry.getValue() == item.getType().ordinal());
                    break;
                }
            }
        }
        b = System.nanoTime();
        Debug.logger("test pass for for-loop find, using", b - a);
        //        a = System.nanoTime();
        //        for (var item: itemWithLoreAdded){
        //            for (var entry: itemMap.entrySet()){
        //                if(item.getType() == entry.getKey().getType() && ItemUtils.matchItemStack(item,
        // entry.getKey(), false)){
        //                    Assert(entry.getValue() == item.getType().ordinal());
        //                    break;
        //                }
        //            }
        //        }
        //        b = System.nanoTime();
        //        Debug.logger("test pass for for-loop find, using", b-a);
    }
}
