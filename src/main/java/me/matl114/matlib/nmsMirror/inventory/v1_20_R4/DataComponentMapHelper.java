package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.Set;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.core.component.DataComponentMap")
public interface DataComponentMapHelper extends TargetDescriptor {
    @MethodTarget
    Object get(Object value, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    Set keySet(Object value);

    @MethodTarget
    int size(Object value);

    default boolean isEmpty(Object value) {
        return size(value) == 0;
    }
}
