package me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer;

import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.implement.custom.crafting.recipe.IFixedAmountConsumer;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemConsumer<T extends ItemStackCache<T>> extends IFixedAmountConsumer
        implements ItemConsumer<T> {
    protected AbstractItemConsumer(int required) {
        super(required);
    }

    protected AbstractItemConsumer(int required, IntProvider provider) {
        super(required, provider);
    }

    protected ItemStack processDisplay(T cache) {
        if (cache.isAir()) return null;
        ItemStack item = cache.getItem().clone();
        return processDisplay(item);
    }

    protected ItemStack processDisplay(ItemStack item) {
        if (item == null) return null;
        int maxStack = item.getMaxStackSize();
        item.setAmount(MathUtils.clamp(required, 0, maxStack));
        int min = consumeProvider.getMin();
        int max = consumeProvider.getMax();
        if (min == max) {
            if (min == required) {
                if (min <= maxStack && min > 0) {
                    return item;
                } else {
                    return TextUtils.appendLore(item, "&c输入数量: " + required);
                }
            } else {
                return TextUtils.appendLore(item, "&c条件数量: " + required, "&c消耗数量: " + min);
            }
        } else {
            return TextUtils.appendLore(
                    item, "&c条件数量: " + required, "&c消耗数量: " + min + "~" + max, "&c数量分布: " + consumeProvider.getType());
        }
    }

    @Override
    public Stream<ItemStack> getDisplays() {
        return getItems().map(this::processDisplay).filter(Objects::nonNull);
    }
}
