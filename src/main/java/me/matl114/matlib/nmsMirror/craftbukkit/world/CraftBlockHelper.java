package me.matl114.matlib.nmsMirror.craftbukkit.world;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.block.Block;

@Descriptive(target = "org.bukkit.craftbukkit.block.CraftBlock")
public interface CraftBlockHelper extends TargetDescriptor {
    @MethodTarget
    Object getNMS(Block block);

    @MethodTarget
    Object getPosition(Block block);

    @Note("return levelAccessor")
    @MethodTarget
    Object getHandle(Block block);
}
