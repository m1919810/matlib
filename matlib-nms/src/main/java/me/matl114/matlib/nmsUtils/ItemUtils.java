package me.matl114.matlib.nmsUtils;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.nbt.TagCompoundView;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {

    public static TagCompoundView getPersistentDataContainerView(
            @Nonnull ItemStack craftItemStack, boolean forceCreate) {
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);

        var pdc = NMSItem.ITEMSTACK.getPersistentDataCompoundView(handle, forceCreate);

        if (pdc.getView() != null) {
            return new TagCompoundView(pdc);
        } else {
            return null;
        }
    }

    public static TagCompoundView getPersistentDataContainerView(Object nms, boolean forceCreate) {
        var pdc = NMSItem.ITEMSTACK.getPersistentDataCompoundView(nms, forceCreate);
        return new TagCompoundView(pdc);
    }

    public static void setPersistentDataContainer(
            @Note("please make sure that this is a CraftItemStack") @NotNull ItemStack craftItemStack,
            @NotNull PersistentDataContainer container,
            boolean deepcopy) {
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        Map<String, ?> val;
        if (container instanceof TagCompoundView view) {
            val = Objects.requireNonNull(
                    NMSCore.COMPOUND_TAG.tagsGetter(view.getCompoundTagViews().getView()));
        } else if (CraftBukkit.PERSISTENT_DATACONTAINER.isCraftContainer(container)) {
            val = CraftBukkit.PERSISTENT_DATACONTAINER.getRaw(container);
        } else {
            throw new UnsupportedOperationException(
                    "Persistent Data Container Class not supported: " + container.getClass());
        }
        Object newComp;
        Map<String, Object> newMap = new Object2ObjectOpenHashMap<>(val, 0.8f);
        if (deepcopy) {
            for (var entry : val.entrySet()) {
                newMap.put(entry.getKey(), NMSCore.TAGS.copy(entry.getValue()));
            }
        } else {
            newMap.putAll(val);
        }
        newComp = NMSCore.COMPOUND_TAG.newComp(newMap);
        NMSItem.ITEMSTACK.getPersistentDataCompoundView(handle, false).writeBack(newComp);
    }

    @Nonnull
    public static ItemStack asCraftMirror(Object val) {
        return CraftBukkit.ITEMSTACK.asCraftMirror(val);
    }

    @Nullable public static ItemStack asCraftMirrorOrNull(Object val) {
        ItemStack stack = CraftBukkit.ITEMSTACK.asCraftMirror(val);
        return stack == null ? null : (stack.getType().isAir() ? null : stack);
    }

    @Nonnull
    public static ItemStack asBukkitCopy(Object val) {
        return CraftBukkit.ITEMSTACK.asBukkitCopy(val);
    }

    @Nullable public static ItemStack asBukkitCopyOrNull(Object val) {
        ItemStack stack = asBukkitCopy(val);
        return stack == null ? null : (stack.getType().isAir() ? null : stack);
    }

    @Nonnull
    public static Object asNMSCopy(ItemStack val) {
        return CraftBukkit.ITEMSTACK.asNMSCopy(val);
    }

    @Nonnull
    public static ItemStack newStack(Material material) {
        return CraftBukkit.ITEMSTACK.createCraftItemStack(material, 1);
    }

    @Nonnull
    public static ItemStack newStack(Material material, int amount) {
        return CraftBukkit.ITEMSTACK.createCraftItemStack(material, amount);
    }

    @Nonnull
    public static ItemStack newStack(Material material, int amount, ItemMeta meta) {
        return CraftBukkit.ITEMSTACK.createCraftItemStack(material, amount, meta);
    }

    @Nullable public static ItemStack cleanStack(@Nullable ItemStack whatever) {
        return whatever == null ? null : CraftBukkit.ITEMSTACK.getCraftStack(whatever);
    }

    @Nullable public static ItemStack copyStack(@Nullable ItemStack whatever) {
        return whatever == null ? null : CraftBukkit.ITEMSTACK.asCraftCopy(whatever);
    }

    @Nonnull
    public static Object unwrapHandle(@Nonnull ItemStack it) {
        return CraftBukkit.ITEMSTACK.unwrapToNMS(it);
    }

    @Nonnull
    public static Object unwrapNullable(@Nullable ItemStack whatever) {
        return whatever == null ? EmptyEnum.EMPTY_ITEMSTACK : unwrapHandle(whatever);
    }

    @Nullable public static Object getHandle(@Nonnull ItemStack cis) {
        return CraftBukkit.ITEMSTACK.handleGetter(cis);
    }

    public static boolean matchItemStack(@Nullable ItemStack item1, @Nullable ItemStack item2, boolean distinctLore) {
        return matchItemStack(item1, item2, distinctLore, true);
    }

    public static boolean matchItemStack(
            @Nullable ItemStack item1, @Nullable ItemStack item2, boolean distinctLore, boolean distinctName) {
        if (item1 == null || item2 == null) {
            return item1 == item2;
        }
        var handle1 = CraftBukkit.ITEMSTACK.unwrapToNMS(item1);
        var handle2 = CraftBukkit.ITEMSTACK.unwrapToNMS(item2);
        return NMSItem.ITEMSTACK.matchItem(handle1, handle2, distinctLore, distinctName);
    }

    public static ItemStack pushItem(
            @Note("make sure this is made by craftbukkit, not self-implemented") Inventory craftInventory,
            ItemStack item,
            int... slots) {
        Preconditions.checkArgument(
                CraftBukkit.INVENTORYS.isCraftInventory(craftInventory), "Should pass a craft inventory");
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = NMSItem.CONTAINER.getContents(content);
        var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
        if (itemNMS == EmptyEnum.EMPTY_ITEMSTACK) {
            return null;
        }
        int amount = item.getAmount();
        int maxSize = item.getMaxStackSize();
        for (int slot : slots) {
            if (amount <= 0) {
                break;
            }
            var nmsItem = lst.get(slot);
            // is null or air
            if (NMSItem.ITEMSTACK.isEmpty(nmsItem)) {
                int received = Math.min(amount, maxSize);
                var re = NMSItem.ITEMSTACK.split(itemNMS, received);

                // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                amount -= received;
                NMSItem.CONTAINER.setItem(content, slot, re);
            } else {
                // nmsItem is not EMPTY
                int count = NMSItem.ITEMSTACK.getCount(nmsItem);
                if (count >= maxSize) {
                    continue;
                }
                if (!NMSItem.ITEMSTACK.matchItem(nmsItem, itemNMS, false, true)) {
                    continue;
                }
                int received = MathUtils.clamp(maxSize - count, 0, amount);
                amount -= received;
                NMSItem.ITEMSTACK.setCount(nmsItem, count + received);
            }
        }
        item.setAmount(amount);
        return amount > 0 ? item : null;
    }

    public static void pushItem(
            @Note("make sure this is made by craftbukkit, not self-implemented") Inventory craftInventory,
            int[] slots,
            ItemStack... items) {
        Preconditions.checkArgument(
                CraftBukkit.INVENTORYS.isCraftInventory(craftInventory), "Should pass a craft inventory");
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = NMSItem.CONTAINER.getContents(content);
        for (var item : items) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
            if (itemNMS == null || itemNMS == EmptyEnum.EMPTY_ITEMSTACK) {
                continue;
            }
            int amount = item.getAmount();
            int maxSize = item.getMaxStackSize();
            for (int slot : slots) {
                if (amount <= 0) {
                    break;
                }
                var nmsItem = lst.get(slot);
                // is null or air
                if (NMSItem.ITEMSTACK.isEmpty(nmsItem)) {
                    int received = Math.min(amount, maxSize);
                    var re = NMSItem.ITEMSTACK.split(itemNMS, received);

                    // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                    amount -= received;
                    NMSItem.CONTAINER.setItem(content, slot, re);
                } else {
                    // nmsItem is not EMPTY
                    int count = NMSItem.ITEMSTACK.getCount(nmsItem);
                    if (count >= maxSize) {
                        continue;
                    }
                    if (!NMSItem.ITEMSTACK.matchItem(nmsItem, itemNMS, false, true)) {
                        continue;
                    }
                    int received = MathUtils.clamp(maxSize - count, 0, amount);
                    amount -= received;
                    NMSItem.ITEMSTACK.setCount(nmsItem, count + received);
                }
            }
            item.setAmount(amount);
        }
    }

    public static ItemStack pushItemWithoutMatch(
            @Note("make sure this is made by craftbukkit, not self-implemented inv") Inventory craftInventory,
            ItemStack item,
            int... slots) {
        Preconditions.checkArgument(
                CraftBukkit.INVENTORYS.isCraftInventory(craftInventory), "Should pass a craft inventory");
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = NMSItem.CONTAINER.getContents(content);
        var itemNMS = CraftBukkit.ITEMSTACK.unwrapToNMS(item);
        if (itemNMS == EmptyEnum.EMPTY_ITEMSTACK) {
            return null;
        }
        int amount = item.getAmount();
        int maxSize = item.getMaxStackSize();
        for (int slot : slots) {
            if (amount <= 0) {
                break;
            }
            var nmsItem = lst.get(slot);
            // is null or air
            if (NMSItem.ITEMSTACK.isEmpty(nmsItem)) {
                int received = Math.min(amount, maxSize);
                var re = NMSItem.ITEMSTACK.split(itemNMS, received);
                // CraftBukkit.ITEMSTACK.asNMSCopy(item);
                amount -= received;
                NMSItem.CONTAINER.setItem(content, slot, re);
            } else {
                // nmsItem is not EMPTY
                int count = NMSItem.ITEMSTACK.getCount(nmsItem);
                if (count >= maxSize) {
                    continue;
                }
                int received = MathUtils.clamp(maxSize - count, 0, amount);
                amount -= received;
                NMSItem.ITEMSTACK.setCount(nmsItem, count + received);
            }
        }
        item.setAmount(amount);
        return amount > 0 ? item : null;
    }

    public static ItemStack grabItem(
            @Note("make sure this is made by craftbukkit, not self-implemented inv") Inventory craftInventory,
            ItemStack item,
            int requestedAmount,
            int... slots) {
        Preconditions.checkArgument(
                CraftBukkit.INVENTORYS.isCraftInventory(craftInventory), "Should pass a craft inventory");
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        var content = CraftBukkit.INVENTORYS.getInventory(craftInventory);
        List<?> lst = NMSItem.CONTAINER.getContents(content);
        Object stackToReturnNMS = null;
        int collected = 0;
        for (int slot : slots) {
            if (collected >= requestedAmount) {
                break;
            }
            var nms = lst.get(slot);
            if (nms == null || NMSItem.ITEMSTACK.isEmpty(nms)) {
                continue;
            } else {
                if (stackToReturnNMS == null) {
                    stackToReturnNMS = CraftBukkit.ITEMSTACK.asNMSCopy(item);
                }
                if (NMSItem.ITEMSTACK.matchItem(stackToReturnNMS, nms, false, true)) {
                    int count = NMSItem.ITEMSTACK.getCount(nms);
                    int withDraw = Math.min(count, requestedAmount - collected);
                    NMSItem.ITEMSTACK.setCount(nms, count - withDraw);
                    collected += withDraw;
                }
            }
        }
        if (stackToReturnNMS == null) {
            return null;
        }
        NMSItem.ITEMSTACK.setCount(stackToReturnNMS, collected);
        return CraftBukkit.ITEMSTACK.asCraftMirror(stackToReturnNMS);
    }

    public static int itemStackHashCode(@Note("should pass a CraftItemStack for the best") ItemStack craftItemStack) {
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        return NMSItem.ITEMSTACK.customHashcode(handle);
    }

    public static int itemStackHashCodeWithoutLore(
            @Note("should pass a CraftItemStack for the best") ItemStack craftItemStack) {
        var handle = CraftBukkit.ITEMSTACK.unwrapToNMS(craftItemStack);
        return NMSItem.ITEMSTACK.customHashWithoutDisplay(handle);
    }

    public static Object dumpItemStack(Object itemStack) {
        Object emptyNbt = NMSCore.COMPOUND_TAG.newComp();
        return NMSItem.ITEMSTACK.save(itemStack, emptyNbt);
    }
}
