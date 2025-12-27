package me.matl114.matlib.nmsMirror.craftbukkit.entity;

import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.entity.Entity;

@Descriptive(target = "org.bukkit.craftbukkit.entity.CraftEntity")
public interface CraftEntityHelper extends TargetDescriptor {
    @MethodTarget
    public Object getHandle(Entity entity);

    @MethodTarget
    void update(Entity entity);
}
