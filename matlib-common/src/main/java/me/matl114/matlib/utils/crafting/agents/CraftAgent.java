package me.matl114.matlib.utils.crafting.agents;

import com.google.common.base.Preconditions;
import java.util.List;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.utils.crafting.recipe.Recipe;
import me.matl114.matlib.utils.stackCache.StackBuffer;

public abstract class CraftAgent<T extends Recipe> {
    protected List<T> recipeChoices;
    protected int historyPointer = 0;
    public static final int F_DISABLE_HISTORY = -2;
    public static final int F_HISTORY_ABSENT = -1;
    public static final int F_ORDER_SEQUENTIAL = 1;
    public static final int F_ORDER_REVERSE = -1;

    public CraftAgent() {}

    public void disableHistory() {
        historyPointer = -2;
    }

    public int getHistoryPointer() {
        return MathUtils.clamp(historyPointer, -2, recipeChoices.size() - 1);
    }

    public void setHistoryPointer(int pointer) {
        historyPointer = MathUtils.clamp(pointer, 0, recipeChoices.size() - 1);
    }

    public boolean hasRecipe() {
        return historyPointer >= 0 && historyPointer < recipeChoices.size();
    }

    public abstract CraftingOperation<T> matchRecipe(List<StackBuffer> counters, T recipe, long craftLimit);

    public CraftingOperation<T> findRecipe(List<StackBuffer> inputSlots, int orderFlag, long craftLimit) {
        int recipeAmount = this.recipeChoices.size();
        if (recipeAmount <= 0) {
            return null;
        }
        Preconditions.checkArgument(orderFlag != 0);
        int delta = orderFlag;
        int size = inputSlots.size();
        int _index = getHistoryPointer();
        int index;
        if (_index < 0) {
            index = 0;
        } else {
            index = _index;
        }
        for (int i = 0; i < recipeAmount; ++i) {
            int nextIndex = (index + i) % recipeAmount;
            T recipe = this.recipeChoices.get(nextIndex);
            CraftingOperation<T> result = matchRecipe(inputSlots, recipe, craftLimit);
            if (result != null && result.getCraftLimit() >= 1) {
                if (_index != F_DISABLE_HISTORY) {
                    setHistoryPointer(nextIndex);
                }
                return result;
            }
        }
        return null;
    }
}
