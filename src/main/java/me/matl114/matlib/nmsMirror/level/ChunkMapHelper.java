package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@MultiDescriptive(targetDefault = "net.minecraft.server.level.ChunkMap")
public interface ChunkMapHelper extends TargetDescriptor {
    @FieldTarget
    Int2ObjectMap<?> entityMapGetter(Object map);

    @RedirectClass(ChunkMapTrackedEntity)
    @MethodTarget
    void updatePlayer(Object trackedEntity, @RedirectType(ServerPlayer) Object player);

    @FieldTarget
    @RedirectClass(ChunkMapTrackedEntity)
    ReferenceOpenHashSet<?> seenByGetter(Object trackingView);

    @FieldTarget
    @RedirectClass(ChunkMapTrackedEntity)
    Object entityGetter(Object track);

    //    @FieldTarget
    //    @RedirectClass(Entity)
    //    Object trackerGetter(Object entity);
}
