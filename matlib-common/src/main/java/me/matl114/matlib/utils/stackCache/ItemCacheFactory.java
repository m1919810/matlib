package me.matl114.matlib.utils.stackCache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface ItemCacheFactory<T extends ItemStackCache<T>> {
    T getCache(ItemStack stack);

    default List<T> getCache(ItemStack[] stacks) {
        List<T> list = new ArrayList<T>(stacks.length);
        for (ItemStack stack : stacks) {
            list.add(getCache(stack));
        }
        return list;
    }

    ItemWithSlot<T> getWithSlot(ItemStack stack, int slot);

    default ItemWithSlot<T> getInputSlot(ItemStack stack) {
        return stack == null ? null : getWithSlot(stack, 0);
    }

    default ItemWithSlot<T> getInputSlot(ItemStack stack, int slot) {
        return stack == null ? null : getWithSlot(stack, slot);
    }

    default ItemWithSlot<T> getInputInventory(InventoryHolder inventory, int slot) {
        return getInputSlot(inventory.getInventory().getItem(slot), slot).setMenu(inventory);
    }

    default ItemWithSlot<T> getWithInventory(InventoryHolder inventory, int slot) {
        return getWithSlot(inventory.getInventory().getItem(slot), slot).setMenu(inventory);
    }

    default IntFunction<ItemWithSlot<T>> getInputIndex(InventoryHolder inventory, int[] slot) {
        return (i) -> getInputSlot(inventory.getInventory().getItem(slot[i])).setMenu(inventory);
    }

    default IntFunction<ItemWithSlot<T>> getOutputIndex(InventoryHolder inventory, int[] slot) {
        return (i) -> getWithInventory(inventory, slot[i]);
    }
}
