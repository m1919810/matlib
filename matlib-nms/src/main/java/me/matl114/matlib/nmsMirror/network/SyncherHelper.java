package me.matl114.matlib.nmsMirror.network;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@MultiDescriptive
public interface SyncherHelper extends TargetDescriptor {
    @RedirectClass(SynchedEntityDataValue)
    @MethodTarget
    @RedirectName("id")
    int entityDataValue$id(Object data);

    @RedirectClass(SynchedEntityDataValue)
    @MethodTarget
    @RedirectName("value")
    Object entityDataValue$value(Object data);

    @RedirectClass(SynchedEntityDataValue)
    @MethodTarget
    @RedirectName("serializer")
    Object entityDataValue$serializer(Object data);
}
