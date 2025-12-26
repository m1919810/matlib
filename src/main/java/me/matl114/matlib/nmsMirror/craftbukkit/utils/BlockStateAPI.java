package me.matl114.matlib.nmsMirror.craftbukkit.utils;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.block.CraftBlockStates")
public interface BlockStateAPI {
    default boolean isTileEntityOptional(Material material) {
        return material == Material.MOVING_PISTON;
    }

    @MethodTarget(isStatic = true)
    Object getFactory(Material mat, Object beType);

    @MethodTarget
    @RedirectClass("org.bukkit.craftbukkit.block.CraftBlockStates$BlockStateFactory")
    BlockState createBlockState(
            Object factory,
            World world,
            @RedirectType(BlockPos) Object pos,
            @RedirectType(BlockState) Object data,
            @RedirectType(BlockEntity) Object tileEntity);
}
