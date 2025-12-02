package me.matl114.matlib.nmsMirror.inventory;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "net.minecraft.world.item.Item")
public interface ItemsAPI extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    int getId(@RedirectType(Item)Object item);

    @MethodTarget(isStatic = true)
    Object byId(int id);

    @MethodTarget(isStatic = true)
    Object byBlock(@RedirectType(Block)Object block);

//    @MethodTarget
//     int getDefaultMaxStackSize(Object item);

    @MethodTarget
    Object getName(Object item, @RedirectType(ItemStack)Object itemStack);

    @MethodTarget
    @Note("glowing")
    boolean isFoil(Object item, @RedirectType(ItemStack)Object itemStack);


//    @CastCheck("net.minecraft.world.item.DiggerItem")
//    boolean isDiggerItem(Object item);

    @CastCheck("net.minecraft.world.item.BlockItem")
    boolean isBlockItem(Object item);
}
