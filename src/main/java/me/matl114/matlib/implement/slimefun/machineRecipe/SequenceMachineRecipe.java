package me.matl114.matlib.implement.slimefun.machineRecipe;

import com.google.common.base.Preconditions;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.inventory.ItemStack;

public class SequenceMachineRecipe extends MachineRecipe {
    public static final String[] displayPrefixs = new String[] {"&7⇨ %s%-3d&7/%s", "%-3d %s"};
    public String[] displayedNames;

    public SequenceMachineRecipe(int ticks, ItemStack[] inputs, ItemStack[] outputs) {
        super(0, inputs, outputs);
        this.setTicks(ticks);
        int len = inputs.length;
        this.displayedNames = new String[len];
        for (int i = 0; i < len; i++) {
            Preconditions.checkNotNull(inputs[i]);
            displayedNames[i] = new StringBuilder("")
                    .append(displayPrefixs[0])
                    // fixme change ItemStackHelper to some interface
                    .append(displayPrefixs[1].formatted(
                            inputs[i].getAmount(), ItemStackHelper.getDisplayName(inputs[i])))
                    .toString();
        }
    }
}
