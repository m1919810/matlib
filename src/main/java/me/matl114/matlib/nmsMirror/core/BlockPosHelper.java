package me.matl114.matlib.nmsMirror.core;

import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

@Descriptive(target = "net.minecraft.core.BlockPos")
public interface BlockPosHelper extends Vec3iHelper {
    @MethodTarget(isStatic = true)
    Object of(long packed);

    @MethodTarget
    long asLong(Object obj);
    // @MethodTarget
    // Object rotate(Object pos, Object rotation );
}
