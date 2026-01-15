package me.matl114.matlib.implement.custom.crafting.agent;

import java.util.List;
import me.matl114.matlib.algorithms.dataStructures.frames.bits.BitList;
import me.matl114.matlib.utils.crafting.agents.ConsumerAgent;
import me.matl114.matlib.utils.crafting.agents.CraftAgent;
import me.matl114.matlib.utils.crafting.agents.CraftingOperation;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.crafting.recipe.Recipe;
import me.matl114.matlib.utils.stackCache.StackBuffer;

public class UnorderedCraftAgent<T extends Recipe> extends CraftAgent<T> {

    @Override
    public CraftingOperation<T> matchRecipe(List<StackBuffer> itemCounters, T recipe, long craftLimit) {
        int len2 = itemCounters.size();
        IConsumer[] recipeInput = recipe.getInputs();
        int cnt = recipeInput.length;
        if (cnt > len2) return null;
        ConsumerAgent[] result = new ConsumerAgent[cnt];

        final BitList visited = new BitList(len2);
        for (int i = 0; i < cnt; ++i) {

            ConsumerAgent agent = ConsumerAgent.get(recipeInput[i]);
            // in case some idiots! put 0 in recipe
            // maxAmount=Math.min( itemCounter.getAmount()*maxMatchCount,1);
            for (int j = 0; j < len2; ++j) {
                StackBuffer itemCounter2 = itemCounters.get(j);
                if (itemCounter2 == null) continue;
                // 增加过滤器 过滤已经被绑定的槽位
                if (!visited.get(j)) {
                    itemCounter2.syncData();
                    visited.setTrue(j); // = true;
                } else if (itemCounter2.isDirty()) {
                    continue;
                }
                if (agent.getConsumer() != null && agent.getConsumer().accept(itemCounter2)) {
                    agent.consumeSlot(itemCounter2);
                    if (agent.getMatchStackAmount() >= craftLimit) break;
                }
            }
            // 不够一份的量
            if (agent.getMatchStackAmount() < 1) {
                return null;
            }
            craftLimit = Math.min(agent.getMatchStackAmount(), craftLimit);
            result[i] = agent;
        }
        return new CraftingOperation<T>(result, null, recipe, craftLimit);
    }
    //    @Override
    //    public <W extends ItemStackCache<W>> void craft(CraftingOperation<W> operation, List<ItemWithSlot<W>>
    // inputSlots, List<ItemWithSlot<W>> outputSlots) {
    //
    //    }

}
