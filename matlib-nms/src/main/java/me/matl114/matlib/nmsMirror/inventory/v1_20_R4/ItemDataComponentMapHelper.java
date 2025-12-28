package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.*;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.Optional;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

@Descriptive(target = "net.minecraft.core.component.PatchedDataComponentMap")
public interface ItemDataComponentMapHelper extends DataComponentMapHelper {
    @FieldTarget
    @RedirectType(DataComponentMap)
    public Iterable<?> prototypeGetter(Object patchedMap);

    @MethodTarget
    void ensureMapOwnership(Object patchedMap);

    default void removeFromPatch(Object patchMap, Object type) {
        // copy on write
        ensureMapOwnership(patchMap);
        patchGetter(patchMap).remove(type);
    }

    @FieldTarget
    @RedirectType("Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;")
    @Note("use ensureMapOwnerShip before write via this map")
    Reference2ObjectMap<Object, Optional<?>> patchGetter(Object patchedMap);

    @MethodTarget
    public Object get(Object patchedMap, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    public Object set(Object patchedMap, @RedirectType(DataComponentType) Object type, Object value);

    @MethodTarget
    public Object remove(Object patchedMap, @RedirectType(DataComponentType) Object type);

    @MethodTarget
    public void setAll(Object patchedMap, @RedirectType(DataComponentMap) Object comp);

    @MethodTarget
    public void restorePatch(Object patchedMap, @RedirectType(DataComponentPatch) Object changes);
}
