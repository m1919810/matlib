package me.matl114.matlib.nmsUtils.inventory;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.function.BiFunction;
import me.matl114.matlib.nmsUtils.ItemUtils;

public class ItemCacheHashMap<K extends ItemHashCache, T> extends Object2ObjectOpenCustomHashMap<K, T> {
    public static final ItemCacheHashMap.StrategyItemHash DEFAULT_ITEM_STRATEGY =
            new ItemCacheHashMap.StrategyItemHash();
    public static final ItemCacheHashMap.StrategyItemNoLoreHash NO_LORE_ITEM_STRATEGY =
            new ItemCacheHashMap.StrategyItemNoLoreHash();

    public ItemCacheHashMap(Hash.Strategy<ItemHashCache> customStrategy) {
        super(customStrategy);
    }

    public ItemCacheHashMap(int expected, Hash.Strategy<ItemHashCache> customStrategy) {
        super(expected, customStrategy);
    }

    public ItemCacheHashMap(boolean considerLore) {
        super(considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemCacheHashMap(int expected, boolean considerLore) {
        super(expected, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemCacheHashMap(Object2ObjectMap<? extends K, ? extends T> map, boolean considerLore) {
        super(map, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemCacheHashMap(K[] key, T[] value, boolean considerLore) {
        super(key, value, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    @Override
    public boolean remove(Object k, Object v) {
        if (k instanceof ItemHashCache stack) {
            return super.remove((stack), v);
        } else {
            return false;
        }
    }

    @Override
    public T get(Object val) {
        if (val instanceof ItemHashCache stack) {
            return super.get((stack));
        }
        return null;
    }

    @Override
    public boolean containsKey(Object k) {
        if (k instanceof ItemHashCache stack) {
            return super.containsKey((stack));
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K itemStack, T oldValue, T t) {
        return super.replace((itemStack), oldValue, t);
    }

    @Override
    public T compute(K itemStack, BiFunction<? super K, ? super T, ? extends T> remappingFunction) {

        return super.compute((itemStack), remappingFunction);
    }

    @Override
    public T computeIfPresent(K itemStack, BiFunction<? super K, ? super T, ? extends T> remappingFunction) {
        return super.computeIfPresent((itemStack), remappingFunction);
    }

    @Override
    public T getOrDefault(Object k, T defaultValue) {
        if (k instanceof ItemHashCache stack) {
            return super.getOrDefault((stack), defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Override
    public T putIfAbsent(K itemStack, T t) {
        return super.putIfAbsent((itemStack), t);
    }

    public static class StrategyItemHash implements Hash.Strategy<ItemHashCache> {
        @Override
        public int hashCode(ItemHashCache itemStack) {
            return itemStack.getHashCode();
        }

        @Override
        public boolean equals(ItemHashCache itemStack, ItemHashCache k1) {
            return ItemUtils.matchItemStack(itemStack.getCraftStack(), k1.getCraftStack(), true);
        }
    }

    public static class StrategyItemNoLoreHash implements Hash.Strategy<ItemHashCache> {

        @Override
        public int hashCode(ItemHashCache itemStack) {
            return itemStack == null ? 0 : itemStack.getHashCodeNoLore();
        }

        @Override
        public boolean equals(ItemHashCache itemStack, ItemHashCache k1) {
            // null case
            return (itemStack == null || k1 == null)
                    ? (itemStack == k1)
                    : ItemUtils.matchItemStack(itemStack.getCraftStack(), k1.getCraftStack(), false);
        }
    }
}
