package me.matl114.matlib.nmsUtils.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.function.BiFunction;
import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ItemHashMap<T> extends Object2ObjectOpenCustomHashMap<ItemStack, T> {
    public static final StrategyItemHash DEFAULT_ITEM_STRATEGY = new StrategyItemHash();
    public static final StrategyItemNoLoreHash NO_LORE_ITEM_STRATEGY = new StrategyItemNoLoreHash();

    public ItemHashMap(Strategy<ItemStack> customStrategy) {
        super(customStrategy);
    }

    public ItemHashMap(int expected, Strategy<ItemStack> customStrategy) {
        super(expected, customStrategy);
    }

    public ItemHashMap(boolean considerLore) {
        super(considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemHashMap(int expected, boolean considerLore) {
        super(expected, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemHashMap(Object2ObjectMap<ItemStack, T> map, boolean considerLore) {
        super(map, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public ItemHashMap(ItemStack[] key, T[] value, boolean considerLore) {
        super(key, value, considerLore ? DEFAULT_ITEM_STRATEGY : NO_LORE_ITEM_STRATEGY);
    }

    public static ItemStack unwrapCommon(ItemStack stack) {
        if (stack instanceof ItemStackKey key) {
            return key;
        }
        return ItemUtils.cleanStack(stack);
    }

    private ItemStack unwrap(ItemStack stack) {
        return unwrapCommon(stack);
    }

    @Override
    public T put(ItemStack itemStack, T t) {
        return super.put(this.unwrap(itemStack), t);
    }

    @Override
    public boolean remove(Object k, Object v) {
        if (k instanceof ItemStack stack) {
            return super.remove(this.unwrap(stack), v);
        } else {
            return false;
        }
    }

    @Override
    public T get(Object val) {
        if (val instanceof ItemStack stack) {
            return super.get(this.unwrap(stack));
        }
        return null;
    }

    @Override
    public boolean containsKey(Object k) {
        if (k instanceof ItemStack stack) {
            return super.containsKey(this.unwrap(stack));
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(ItemStack itemStack, T oldValue, T t) {
        return super.replace(this.unwrap(itemStack), oldValue, t);
    }

    @Override
    public T compute(ItemStack itemStack, BiFunction<? super ItemStack, ? super T, ? extends T> remappingFunction) {

        return super.compute(this.unwrap(itemStack), remappingFunction);
    }

    @Override
    public T computeIfPresent(
            ItemStack itemStack, BiFunction<? super ItemStack, ? super T, ? extends T> remappingFunction) {
        return super.computeIfPresent(this.unwrap(itemStack), remappingFunction);
    }

    @Override
    public T getOrDefault(Object k, T defaultValue) {
        if (k instanceof ItemStack stack) {
            return super.getOrDefault(this.unwrap(stack), defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Override
    public T putIfAbsent(ItemStack itemStack, T t) {
        return super.putIfAbsent(this.unwrap(itemStack), t);
    }

    public static class StrategyItemHash implements Strategy<ItemStack> {
        @Override
        public int hashCode(ItemStack itemStack) {
            if (itemStack instanceof ItemStackKey key) {
                return key.getHashCode();
            }
            return ItemUtils.itemStackHashCode(itemStack);
        }

        @Override
        public boolean equals(ItemStack itemStack, ItemStack k1) {
            if (itemStack instanceof ItemStackKey key) {
                if (k1 instanceof ItemStackKey key1) {
                    return ItemUtils.matchItemStack(key.handle, key1.handle, true);
                }
                return ItemUtils.matchItemStack(key.handle, k1, true);
            }

            return ItemUtils.matchItemStack(itemStack, k1 instanceof ItemStackKey key2 ? key2.handle : k1, true);
        }
    }

    public static class StrategyItemNoLoreHash implements Strategy<ItemStack> {

        @Override
        public int hashCode(ItemStack itemStack) {
            if (itemStack instanceof ItemStackKey key) {
                return key.getHashCodeNoLore();
            }
            return ItemUtils.itemStackHashCodeWithoutLore(itemStack);
        }

        @Override
        public boolean equals(ItemStack itemStack, ItemStack k1) {
            if (itemStack instanceof ItemStackKey key) {
                if (k1 instanceof ItemStackKey key1) {
                    return ItemUtils.matchItemStack(key.handle, key1.handle, false);
                }
                return ItemUtils.matchItemStack(key.handle, k1, false);
            }

            return ItemUtils.matchItemStack(itemStack, k1 instanceof ItemStackKey key2 ? key2.handle : k1, false);
        }
    }
}
