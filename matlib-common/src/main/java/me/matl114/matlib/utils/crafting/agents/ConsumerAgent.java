package me.matl114.matlib.utils.crafting.agents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.stackCache.StackBuffer;

@Accessors(chain = true)
public class ConsumerAgent extends StackAgent {
    private static final ConsumerAgent INSTANCE = new ConsumerAgent();

    public static ConsumerAgent get(@Nullable IConsumer consumer) {
        ConsumerAgent agent = (ConsumerAgent) INSTANCE.clone();
        agent.consumer = consumer == null ? IConsumer.EMPTY : consumer;
        return agent;
    }

    @Nonnull
    protected IConsumer consumer;

    @Nullable public IConsumer getConsumer() {
        return consumer == IConsumer.EMPTY ? null : consumer;
    }

    private ConsumerAgent() {}

    @Override
    public long getAmountPerStack() {
        return consumer.getRequiringAmount();
    }

    public void calculateAmountByStackAmount() {
        setMatchAmount(this.consumer.getTotalConsumeAmount(this.getRelated(), this.getMatchStackAmount()));
    }

    public void consumeSlot(@Nullable StackBuffer slot) {
        // 能溢出我吃屎
        if (slot != null) {
            setMatchAmount(slot.getAmount() + getMatchAmount());
            addRelated(slot);
        }
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
            s = target.consume(s);
            target.updateSource();
            if (s <= 0) break;
        }
        setMatchAmount(s);
        updateStackAmount();
    }
}
