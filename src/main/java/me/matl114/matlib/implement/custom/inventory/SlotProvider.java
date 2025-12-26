package me.matl114.matlib.implement.custom.inventory;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Accessors(chain = true, fluent = true)
@Setter
public class SlotProvider {
    public static SlotProvider instance() {
        return new SlotProvider();
    }

    public SlotProvider withStack(ItemStack stack) {
        return stack((f) -> stack);
    }

    public SlotProvider withStack(Supplier<ItemStack> stack) {
        return stack((f) -> stack.get());
    }

    public SlotProvider canInteract() {
        return withHandler(InteractHandler.ACCEPT);
    }

    public SlotProvider withHandler(InteractHandler handler) {
        return handler((f) -> handler);
    }

    public SlotProvider withHandler(Supplier<InteractHandler> handler) {
        return handler((f) -> handler.get());
    }

    public ItemStack getStack(InventoryBuilder inv) {
        return stack == null ? null : stack.apply(inv);
    }

    public InteractHandler getHandler(InventoryBuilder inv) {
        if (this.handler == null) return InteractHandler.EMPTY;
        InteractHandler handler1 = handler.apply(inv);
        return handler1 == null ? InteractHandler.EMPTY : handler1;
    }

    Function<InventoryBuilder, ItemStack> stack;
    Function<InventoryBuilder, InteractHandler> handler;
}
