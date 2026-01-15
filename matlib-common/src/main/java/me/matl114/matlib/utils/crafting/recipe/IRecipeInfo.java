package me.matl114.matlib.utils.crafting.recipe;

import java.util.stream.Stream;
import org.bukkit.inventory.ItemStack;

public interface IRecipeInfo {

    default ItemStack getDisplay() {
        return getDisplays().findFirst().orElse(null);
    }

    Stream<ItemStack> getDisplays();
}
