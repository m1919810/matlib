package me.matl114.matlib.utils.crafting.agents;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.crafting.CraftingUtils;
import me.matl114.matlib.utils.crafting.recipe.Recipe;
import me.matl114.matlib.utils.stackCache.StackBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class CraftingOperation<T extends Recipe> {
    ConsumerAgent[] consumerAgent;
    GeneratorAgent[] generatorAgent;
    T recipe;
    long craftLimit;

    public boolean canCraft() {
        return craftLimit > 0;
    }

    public void consumeInput() {
        for (var consumer : consumerAgent) {
            consumer.updateRelatedSlots();
        }
    }

    public void pushOutput() {
        for (var generator : generatorAgent) {
            generator.updateRelatedSlots();
        }
    }

    public void resetInputMatching() {
        CraftingUtils.resetMatchingInfo(consumerAgent);
    }

    public void resetOutputMatching() {
        CraftingUtils.resetMatchingInfo(generatorAgent);
    }

    public void delayPush(List<StackBuffer> output) {
        resetOutputMatching();
        CraftingUtils.prepareCraftingOutput(this, output);
        finalizeOutput();
        pushOutput();
    }

    public void finalizeOperation() {
        finalizeInput();
        finalizeOutput();
    }

    public void finalizeInput() {
        for (var consumer : consumerAgent) {
            consumer.setStackAmount(craftLimit);
            consumer.calculateAmountByStackAmount();
        }
    }

    public void finalizeOutput() {
        for (var generator : generatorAgent) {
            generator.setStackAmount(craftLimit);
            generator.calculateAmountByStackAmount();
        }
    }
}
