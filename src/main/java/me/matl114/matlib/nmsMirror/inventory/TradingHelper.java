package me.matl114.matlib.nmsMirror.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.inventory.MerchantRecipe;

@MultiDescriptive(targetDefault = "net.minecraft.world.item.trading.MerchantOffer")
public interface TradingHelper extends TargetDescriptor {
    @MethodTarget
    Object getCostB(Object val);

    @MethodTarget
    Object getCostA(Object val);
    //
    //    @MethodTarget
    //    Object copy(Object val);

    @MethodTarget
    Object getResult(Object val);

    @MethodTarget
    MerchantRecipe asBukkit(Object val);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.inventory.CraftMerchantRecipe")
    Object toMinecraft(MerchantRecipe recipe);
}
