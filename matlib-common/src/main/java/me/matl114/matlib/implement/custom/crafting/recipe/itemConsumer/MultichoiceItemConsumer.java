package me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemCounter;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MultichoiceItemConsumer<T extends ItemStackCache<T>> extends AbstractItemConsumer<T> {
    List<T> itemStackCache;

    public MultichoiceItemConsumer(List<T> matches, int required) {
        this(matches, required, IntProvider.constInt(required));
    }

    public MultichoiceItemConsumer(List<T> matches, int required, IntProvider provider) {
        super(required, provider);
        Preconditions.checkArgument(!matches.isEmpty());
        this.itemStackCache = List.copyOf(matches);
    }

    @Override
    public boolean accept(@Nullable StackBuffer cache) {
        if (cache instanceof ItemCounter slot) {
            T itemCache = (T) slot.getCache();
            return itemStackCache.stream().anyMatch(i -> i.matchItem(itemCache, false));
        }
        return false;
    }

    @Override
    public IConsumer copy() {
        return new MultichoiceItemConsumer<>(this.itemStackCache, this.required, this.consumeProvider);
    }

    @Override
    public boolean similarConsumer(IConsumer consumer) {
        return consumer instanceof MultichoiceItemConsumer
                && super.equalsConsumer(consumer)
                && Set.of(itemStackCache).equals(Set.of(((MultichoiceItemConsumer) consumer).itemStackCache));
    }

    @Override
    public ItemStack getSample() {
        return itemStackCache.get(0).getItem();
    }

    @Override
    public Stream<ItemStack> getSamples() {
        return itemStackCache.stream().map(ItemStackCache::getItem);
    }

    @Override
    public Stream<T> getItems() {
        return itemStackCache.stream();
    }

    protected ItemStack processDisplay(T cache) {
        ItemStack stack = super.processDisplay(cache);
        if (stack == null) return stack;
        if (this.itemStackCache.size() > 1) {
            return TextUtils.appendLore(stack, "&e物品辞典匹配");
        }
        return stack;
    }
}
