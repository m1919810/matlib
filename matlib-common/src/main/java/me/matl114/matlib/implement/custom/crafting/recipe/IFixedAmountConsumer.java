package me.matl114.matlib.implement.custom.crafting.recipe;

import java.util.List;
import java.util.Objects;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.StackBuffer;

public abstract class IFixedAmountConsumer implements IConsumer {

    protected IFixedAmountConsumer(int required) {
        this(required, IntProvider.constInt(required));
    }

    protected IFixedAmountConsumer(int required, IntProvider provider) {
        this.required = required;
        this.consumeProvider = provider;
    }

    protected int required;
    protected IntProvider consumeProvider;

    @Override
    public long getRequiringAmount() {
        return required;
    }

    @Override
    public long getTotalConsumeAmount(List<StackBuffer> acceptedItems, long craftTime) {
        return consumeProvider.nSample(craftTime);
    }

    @Override
    public final boolean equalsConsumer(IConsumer consumer) {
        return consumer instanceof IFixedAmountConsumer
                && ((IFixedAmountConsumer) consumer).required == required
                && Objects.equals(((IFixedAmountConsumer) consumer).consumeProvider, consumeProvider)
                && similarConsumer(consumer);
    }

    @Override
    public abstract boolean similarConsumer(IConsumer consumer);

    @Override
    public final IConsumer combine(IConsumer consumer) {
        IFixedAmountConsumer newConsumer = (IFixedAmountConsumer) this.copy();
        newConsumer.required = this.required + ((IFixedAmountConsumer) consumer).required;
        newConsumer.consumeProvider =
                IntProvider.combine(List.of(this.consumeProvider, ((IFixedAmountConsumer) consumer).consumeProvider));
        return newConsumer;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        else if (obj instanceof IConsumer consumer) {
            return equalsConsumer(consumer);
        } else return false;
    }
}
