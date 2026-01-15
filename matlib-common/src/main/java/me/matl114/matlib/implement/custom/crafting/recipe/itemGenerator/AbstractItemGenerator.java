package me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator;

import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.implement.custom.crafting.recipe.IFixedAmountGenerator;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemGenerator<T extends ItemStackCache<T>> extends IFixedAmountGenerator
        implements ItemGenerator<T> {

    protected AbstractItemGenerator(int required) {
        super(required);
    }

    protected AbstractItemGenerator(IntProvider provider) {
        super(provider);
    }

    protected ItemStack processDisplay(T cache) {
        if (cache.isAir()) return null;
        ItemStack item = cache.getItem().clone();
        return processDisplay(item);
    }

    protected ItemStack processDisplay(ItemStack item) {
        if (item == null) return null;
        int maxStack = item.getMaxStackSize();
        item.setAmount(MathUtils.clamp(generatorProvider.sample(), 0, maxStack));
        int min = generatorProvider.getMin();
        int max = generatorProvider.getMax();
        if (min == max) {
            if (min <= maxStack && min > 0) {
                return item;
            } else {
                return TextUtils.appendLore(item, "&c输出数量: " + min);
            }

        } else {
            return TextUtils.appendLore(
                    item, "&c随机输出数量: %d~%d".formatted(min, max), "&c数量分布: " + generatorProvider.getType());
        }
    }

    @Override
    public Stream<ItemStack> getDisplays() {
        return getItems().map(this::processDisplay).filter(Objects::nonNull);
    }
}
