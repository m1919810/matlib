package me.matl114.matlib.implement.slimefun.machineRecipe;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class CraftingOperationAdaptor<T extends MachineOperation> extends CraftingOperation {
    public static <W extends MachineOperation> CraftingOperationAdaptor<W> of(W operation) {
        return new CraftingOperationAdaptor<>(operation);
    }

    private final T handle;
    private static final ItemStack[] EXAMPLE = new ItemStack[] {new ItemStack(Material.STONE)};

    public CraftingOperationAdaptor(T operation) {
        super(EXAMPLE, EXAMPLE, 0);
        handle = operation;
    }
    // for slimehud display use
    @Getter
    @Setter
    private int currentTick = 0;

    public int getProgress() {
        return this.currentTick;
    }

    @Getter
    @Setter
    private int totalTick = 0;

    public int getTotalTicks() {
        return this.totalTick;
    }
}
