package me.matl114.matlib.implement.custom.crafting.recipe;

import java.util.List;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.serialization.simple.IntProvider;

public abstract class IFixedAmountGenerator implements IGenerator {
    protected IntProvider generatorProvider;

    public IFixedAmountGenerator(int i) {
        this(IntProvider.constInt(i));
    }

    public IFixedAmountGenerator(IntProvider generatorProvider) {
        this.generatorProvider = generatorProvider;
    }

    @Override
    public final boolean equalsGenerator(IGenerator consumer) {
        return consumer instanceof IFixedAmountGenerator
                && ((IFixedAmountGenerator) consumer).generatorProvider.equals(generatorProvider)
                && this.similarGenerator(consumer);
    }

    @Override
    public final IGenerator combine(IGenerator consumer) {
        IFixedAmountGenerator newConsumer = (IFixedAmountGenerator) this.copy();
        newConsumer.generatorProvider = IntProvider.combine(
                List.of(this.generatorProvider, ((IFixedAmountGenerator) consumer).generatorProvider));
        return newConsumer;
    }
}
