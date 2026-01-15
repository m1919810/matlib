package me.matl114.matlib.utils.crafting.agents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.StackBuffer;

@SuppressWarnings("all")
public class GeneratorAgent extends StackAgent {
    private static final GeneratorAgent INSTANCE = new GeneratorAgent();

    @Nullable public IGenerator getGenerator() {
        return generator == IGenerator.EMPTY ? null : generator;
    }

    @Nonnull
    protected IGenerator generator;

    @Getter
    StackBuffer outputSample;

    public static GeneratorAgent get(StackBuffer gen, IGenerator sourceGenerator) {
        GeneratorAgent agent = (GeneratorAgent) INSTANCE.clone();
        agent.generator = sourceGenerator == null ? IGenerator.EMPTY : sourceGenerator;
        agent.outputSample = gen;
        return agent;
    }

    public boolean canStackOn(StackBuffer cache) {
        return generator.canStack(outputSample, cache);
    }

    public void pushSlot(@Nonnull StackBuffer slot, long amount) {
        // 能溢出我吃屎
        setMatchAmount(getMatchAmount() + amount);
        addRelated(slot);
        updateStackAmount();
    }

    public void updateRelatedSlots() {
        if (related == null) {
            return;
        }
        long s = getMatchAmount();
        int len = related.size();
        StackBuffer target;

        for (int i = 0; i < len; i++) {
            target = related.get(i);
            target.grab(s);
            target.updateSource();
            if (s <= 0) break;
        }
        setMatchAmount(s);
        updateStackAmount();
    }

    public void calculateAmountByStackAmount() {
        setMatchAmount(getAmountPerStack() * getMatchStackAmount());
    }

    @Override
    public long getAmountPerStack() {
        return outputSample.getAmount();
    }
}
