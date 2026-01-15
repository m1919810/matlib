package me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator;

import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemCounter;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public class SimpleItemGenerator<T extends ItemStackCache<T>> extends AbstractItemGenerator<T>
        implements ItemGenerator<T> {
    T cache;

    public SimpleItemGenerator(T stackCache) {
        this(stackCache, stackCache.getAmount());
    }

    public SimpleItemGenerator(T stackCache, int v) {
        this(stackCache, IntProvider.constInt(v));
    }

    public SimpleItemGenerator(T stackCache, IntProvider i) {
        super(i);
        this.cache = stackCache.deepcopy(CraftUtils::getCraftCopy);
    }

    @Override
    public ItemStack getSample() {
        return cache.getItem();
    }

    @Override
    public Stream<T> getItems() {
        return Stream.of(cache);
    }

    @Override
    public boolean canStack(StackBuffer generated, StackBuffer slotItem) {
        // 匹配空的槽位
        return slotItem instanceof ItemCounter slots
                && generated instanceof ItemCounter gen
                && (slots.isNull() || gen.getCache().matchItem(slots.getCache(), false));
    }

    @Override
    public Stream<Pair<StackBuffer, IGenerator>> generateOutput() {
        return Stream.of(new Pair<>(new ItemCounter<>(cache, generatorProvider.sample()), this));
    }

    @Override
    public IGenerator copy() {
        return new SimpleItemGenerator(cache, generatorProvider.sample());
    }

    @Override
    public boolean similarGenerator(IGenerator consumer) {
        return consumer instanceof SimpleItemGenerator simple && Objects.equals(cache, simple.cache);
    }
}
