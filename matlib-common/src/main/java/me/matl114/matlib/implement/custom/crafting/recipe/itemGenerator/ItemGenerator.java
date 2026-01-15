package me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator;

import java.util.stream.Stream;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import org.bukkit.inventory.ItemStack;

public interface ItemGenerator<T extends ItemStackCache<T>> extends IGenerator {
    ItemStack getSample();

    default Stream<ItemStack> getSamples() {
        return getItems().map(ItemStackCache::getItem);
    }

    Stream<T> getItems();
}
