package me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemCounter;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public class SimpleItemConsumer<T extends ItemStackCache<T>> extends AbstractItemConsumer<T> {
    T itemStackCache;

    public SimpleItemConsumer(T itemStackCache, int required) {
        this(itemStackCache, required, IntProvider.constInt(required));
    }

    public SimpleItemConsumer(T itemStackCache, int required, IntProvider consumeProvider) {
        super(required, consumeProvider);
        this.itemStackCache = itemStackCache;
    }

    @Override
    public boolean accept(StackBuffer buffer) {
        return buffer instanceof ItemCounter cache
                && !cache.isNull()
                && itemStackCache.matchItem((T) cache.getCache(), false);
    }

    @Override
    public long getRequiringAmount() {
        return required;
    }

    @Override
    public long getTotalConsumeAmount(List<StackBuffer> acceptedItems, long craftTime) {
        return consumeProvider.nSample(craftTime);
    }

    @Override
    public IConsumer copy() {
        return new SimpleItemConsumer<>(itemStackCache, required, consumeProvider);
    }

    @Override
    public boolean similarConsumer(IConsumer consumer) {
        return consumer instanceof SimpleItemConsumer<?> simple
                && Objects.equals(itemStackCache, ((SimpleItemConsumer<?>) consumer).itemStackCache);
    }

    @Override
    public ItemStack getSample() {
        return itemStackCache.getItem();
    }

    @Override
    public Stream<ItemStack> getSamples() {
        return Stream.of(itemStackCache).map(ItemStackCache::getItem);
    }

    @Override
    public Stream<T> getItems() {
        return Stream.of(itemStackCache);
    }
}
