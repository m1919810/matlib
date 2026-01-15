package me.matl114.matlib.implement.custom.crafting.agent;

import java.util.List;
import me.matl114.matlib.utils.crafting.agents.ConsumerAgent;
import me.matl114.matlib.utils.crafting.agents.CraftAgent;
import me.matl114.matlib.utils.crafting.agents.CraftingOperation;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.crafting.recipe.Recipe;
import me.matl114.matlib.utils.stackCache.StackBuffer;

public class OrderedCraftAgent<T extends Recipe> extends CraftAgent<T> {
    @Override
    public CraftingOperation<T> matchRecipe(List<StackBuffer> input, T recipe, long craftLimit) {
        IConsumer[] recipeInput = recipe.getInputs();
        int len = input.size();
        int len2 = recipeInput.length;
        if (len < len2) return null;
        long max = craftLimit;
        ConsumerAgent[] consumers = new ConsumerAgent[len];
        for (int i = 0; i < len2; ++i) {
            StackBuffer slot = input.get(i);
            IConsumer consumer = recipeInput[i];
            ConsumerAgent agent = ConsumerAgent.get(consumer);
            consumers[i] = agent;
            if (slot == null || slot.isNull()) {
                if (agent.getConsumer() == null) {
                    continue;
                }
            }
            if (agent.getConsumer() != null && agent.getConsumer().accept(slot)) {
                agent.consumeSlot(slot);
                max = Math.min(max, agent.getMatchStackAmount());
            } else {
                return null;
            }
        }
        return new CraftingOperation<>(consumers, null, recipe, max);
    }
}
