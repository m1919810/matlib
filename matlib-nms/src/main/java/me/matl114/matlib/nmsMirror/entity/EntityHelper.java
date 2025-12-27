package me.matl114.matlib.nmsMirror.entity;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.List;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "net.minecraft.world.entity.Entity")
public interface EntityHelper extends TargetDescriptor {
    @MethodTarget
    org.bukkit.entity.Entity getBukkitEntity(Object entity);

    @MethodTarget
    Object getEntityData(Object entity);
    //
    //    @MethodTarget
    //    @RedirectClass(SynchedEntityData)
    //    void resendPossiblyDesyncedEntity(Object data, @RedirectType(ServerPlayer)Object player);

    @MethodTarget
    @RedirectClass(SynchedEntityData)
    List<?> getNonDefaultValues(Object data);

    @MethodTarget
    @RedirectClass(SynchedEntityData)
    List<?> packDirty(Object data);

    @MethodTarget
    @RedirectClass(SynchedEntityData)
    List<?> packAll(Object data);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    default void refreshEntityData(Object entity, @RedirectType(ServerPlayer) Object player) {
        Object data = getEntityData(entity);
        refresh(data, player);
    }

    @Internal
    @MethodTarget
    @RedirectClass(SynchedEntityData)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    void refresh(Object data, @RedirectType(ServerPlayer) Object player);

    @MethodTarget
    Object createHoverEvent(Object value);
}
