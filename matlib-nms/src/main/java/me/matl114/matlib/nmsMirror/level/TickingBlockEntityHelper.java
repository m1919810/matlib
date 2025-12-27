package me.matl114.matlib.nmsMirror.level;

import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.world.level.block.entity.TickingBlockEntity")
public interface TickingBlockEntityHelper extends TargetDescriptor {
    @MethodTarget
    void tick(Object tick);

    @MethodTarget
    boolean isRemoved();

    @MethodTarget
    Object getPos();

    @MethodTarget
    String getType();
}
