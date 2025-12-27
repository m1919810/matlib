package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@MultiDescriptive(targetDefault = "net.minecraft.core.component.DataComponentPatch")
public interface DataComponentPatchHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    Object builder();

    @MethodTarget
    @RedirectClass(DataComponentPatchBuilder)
    Object set(Object builder, @RedirectType(DataComponentType) Object type, Object value);

    @MethodTarget
    @RedirectClass(DataComponentPatchBuilder)
    Object remove(Object builder, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    @RedirectClass(DataComponentPatchBuilder)
    void clear(Object builder, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    @RedirectClass(DataComponentPatchBuilder)
    boolean isSet(Object builder, @RedirectType(DataComponentType) Object type);

    default Object override(Object builder, @RedirectType(DataComponentType) Object type, Object value) {
        if (value != null) {
            set(builder, type, value);
        } else {
            remove(builder, type);
        }
        return builder;
    }

    @MethodTarget
    @RedirectClass(DataComponentPatchBuilder)
    Object build(Object builder);

    @MethodTarget
    Set<Map.Entry<?, Optional<?>>> entrySet(Object patch);
}
