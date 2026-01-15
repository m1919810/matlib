package me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer;

import java.util.stream.Stream;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import org.bukkit.inventory.ItemStack;

public interface ItemConsumer<T extends ItemStackCache<T>> extends IConsumer {
    ItemStack getSample();

    default Stream<ItemStack> getSamples() {
        return getItems().map(ItemStackCache::getItem);
    }

    Stream<T> getItems();
}
