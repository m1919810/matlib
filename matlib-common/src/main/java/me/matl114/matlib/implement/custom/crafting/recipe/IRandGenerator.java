package me.matl114.matlib.implement.custom.crafting.recipe;

import java.util.stream.Stream;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public interface IRandGenerator<G extends IGenerator> extends IGenerator {
    Stream<ItemStack> getConditionalDisplay(double current);

    default Stream<ItemStack> getDisplays() {
        return getConditionalDisplay(1);
    }

    default boolean canStack(StackBuffer generated, StackBuffer slotItem) {
        throw new UnsupportedOperationException(
                "canStack() is not supported in Random Generator, use source.canStack()");
    }
}
