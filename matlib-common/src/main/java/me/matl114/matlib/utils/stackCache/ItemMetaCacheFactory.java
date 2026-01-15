package me.matl114.matlib.utils.stackCache;

import org.bukkit.inventory.ItemStack;

public class ItemMetaCacheFactory implements ItemCacheFactory<ItemStackMetaCache> {
    @Override
    public ItemStackMetaCache getCache(ItemStack stack) {
        return ItemStackMetaCache.get(stack);
    }

    @Override
    public ItemWithSlot<ItemStackMetaCache> getWithSlot(ItemStack stack, int slot) {
        return new ItemWithSlot<>(getCache(stack), slot);
    }
}
