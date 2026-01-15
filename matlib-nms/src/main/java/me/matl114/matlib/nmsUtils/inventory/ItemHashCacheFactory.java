package me.matl114.matlib.nmsUtils.inventory;

import me.matl114.matlib.utils.stackCache.ItemCacheFactory;
import me.matl114.matlib.utils.stackCache.ItemWithSlot;
import org.bukkit.inventory.ItemStack;

public class ItemHashCacheFactory implements ItemCacheFactory<ItemStackHashCache> {

    @Override
    public ItemStackHashCache getCache(ItemStack stack) {
        return ItemStackHashCache.get(stack);
    }

    @Override
    public ItemWithSlot<ItemStackHashCache> getWithSlot(ItemStack stack, int slot) {
        return new ItemWithSlot<>(getCache(stack), slot);
    }

    public static class PrecomputeHashLore extends ItemHashCacheFactory {
        @Override
        public ItemStackHashCache getCache(ItemStack stack) {
            ItemStackHashCache cache = super.getCache(stack);
            cache.getHashCodeNoLore();
            return cache;
        }
    }

    public static class PrecomputeHash extends ItemHashCacheFactory {
        @Override
        public ItemStackHashCache getCache(ItemStack stack) {
            ItemStackHashCache cache = super.getCache(stack);
            cache.getHashCode();
            return cache;
        }
    }

    public static class PrecomputeHashAll extends ItemHashCacheFactory {
        @Override
        public ItemStackHashCache getCache(ItemStack stack) {
            ItemStackHashCache cache = super.getCache(stack);
            cache.getHashCode();
            cache.getHashCodeNoLore();
            return cache;
        }
    }
}
