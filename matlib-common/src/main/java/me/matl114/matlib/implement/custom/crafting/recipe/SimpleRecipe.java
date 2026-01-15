package me.matl114.matlib.implement.custom.crafting.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer.ItemConsumer;
import me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator.ItemGenerator;
import me.matl114.matlib.utils.crafting.recipe.Recipe;
import me.matl114.matlib.utils.stackCache.ItemStackCache;

@Getter
@AllArgsConstructor
@Builder
public class SimpleRecipe<T extends ItemStackCache<T>> implements Recipe {
    ItemConsumer<T>[] inputs;
    ItemGenerator<T>[] outputs;
    int totalProgress;
}
