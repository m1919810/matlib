package me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer;

import java.util.stream.Stream;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import me.matl114.matlib.utils.stackCache.ItemCounter;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AnyMatchItemConsumer extends AbstractItemConsumer {
    protected AnyMatchItemConsumer(int required) {
        super(required);
    }

    protected AnyMatchItemConsumer(int required, IntProvider provider) {
        super(required, provider);
    }

    @Override
    public boolean similarConsumer(IConsumer consumer) {
        return consumer instanceof AnyMatchItemConsumer;
    }

    protected static ItemStack DISPLAY_ITEM = new CleanItemStack(Material.STONE, "&a匹配任意物品");

    @Override
    public ItemStack getSample() {
        return null;
    }

    @Override
    public Stream getItems() {
        return Stream.empty();
    }

    @Override
    public boolean accept(@Nullable StackBuffer cache) {
        return cache instanceof ItemCounter slot && !slot.isNull();
    }

    @Override
    public IConsumer copy() {
        return new AnyMatchItemConsumer(required, consumeProvider);
    }

    @Override
    public Stream<ItemStack> getDisplays() {
        return Stream.of(DISPLAY_ITEM).map(this::processDisplay);
    }
}
