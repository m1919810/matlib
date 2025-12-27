package me.matl114.matlib.nmsMirror.core;

import static me.matl114.matlib.nmsMirror.Import.*;

import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "net.minecraft.core.Registry")
public interface RegistriesHelper extends TargetDescriptor {
    @MethodTarget
    @RedirectClass(Holder)
    @RedirectName("value")
    Object holderValue(Object holder);

    @MethodTarget
    Object wrapAsHolder(Object registry, Object val);

    @CastCheck(HolderReference)
    boolean isHolderReference(Object val);

    @MethodTarget
    @RedirectClass(TagKey)
    @RedirectName("location")
    Object tagKeyLocation(Object tag);

    @MethodTarget
    @RedirectClass(TagKey)
    @RedirectName("registry")
    Object tagKeyRegistry(Object tag);

    @Internal
    @RedirectClass(HolderLookupProvider)
    @RedirectName("createSerializationContext")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    @MethodTarget
    <T> DynamicOps<T> provideRegistryForDynamicOp(Object registryFrozen, DynamicOps<T> delegate);

    @MethodTarget(isStatic = true)
    @RedirectClass(ResourceKey)
    @RedirectName("create")
    Object createResourceKey(
            @RedirectType(ResourceLocation) Object registryLocation, @RedirectType(ResourceLocation) Object value);

    @MethodTarget
    Object key(Object registry);

    @MethodTarget
    int getId(Object registry, Object val);

    @MethodTarget
    public Object byId(Object registry, int index);

    @MethodTarget
    Object getKey(Object registry, Object value);

    @MethodTarget
    Object get(Object registry, @RedirectType(ResourceLocation) Object namespacedKey);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2, below = true)
    default Object getValue(Object registry, @RedirectType(ResourceLocation) Object namespacedKey) {
        return get(registry, namespacedKey);
    }

    //    default Object getRegistryByKey(Object registry, String key){
    //        return getRegistryByKey(registry, NMSCore.NAMESPACE_KEY.newNSKey(key));
    //    }

    @Note("Suggested")
    @MethodTarget
    Optional getOptional(Object registry, @RedirectType(ResourceLocation) Object id);

    @MethodTarget
    boolean containsKey(Object registry, @RedirectType(ResourceLocation) Object id);

    default boolean containsKey(Object registry, String id) {
        return containsKey(registry, NMSCore.NAMESPACE_KEY.newNSKey(id));
    }

    @MethodTarget
    Stream stream(Object registry);

    @MethodTarget
    Set keySet(Object registry);

    default Iterable<?> toIterable(Object registries) {
        return (Iterable<?>) registries;
    }
}
