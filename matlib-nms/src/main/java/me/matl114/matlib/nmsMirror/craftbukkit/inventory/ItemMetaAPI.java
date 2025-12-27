package me.matl114.matlib.nmsMirror.craftbukkit.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import com.google.common.collect.Multimap;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.inventory.CraftMetaItem")
public interface ItemMetaAPI extends TargetDescriptor {
    default Multimap<Attribute, AttributeModifier> buildModifiersFromRaw(Object unknown) {
        if (ATTR_META_KEY != null) {
            return buildModifiers(unknown, ATTR_META_KEY);
        } else {
            return buildModifiers(unknown);
        }
    }

    static Object ATTR_META_KEY = me.matl114
            .matlib
            .algorithms
            .dataStructures
            .struct
            .Holder
            .empty()
            .thenApplyCaught((i) -> {
                return ObfManager.getManager().reobfClass("org.bukkit.craftbukkit.inventory.CraftMetaItem");
            })
            .thenApplyCaught(i -> Arrays.stream(i.getDeclaredFields())
                    .filter(s -> Modifier.isStatic(s.getModifiers()))
                    .filter(s -> s.getName().equals("ATTRIBUTES"))
                    .filter(s -> s.getType().getSimpleName().equals("ItemMetaKey"))
                    .findAny()
                    .map(f -> {
                        f.setAccessible(true);
                        return f;
                    })
                    .orElseThrow()
                    .get(null))
            .valException(null)
            .get();

    @Internal
    @MethodTarget(isStatic = true)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    public Multimap<Attribute, AttributeModifier> buildModifiers(
            @RedirectType(CompoundTag) Object nbt,
            @RedirectType("Lorg/bukkit/craftbukkit/inventory/CraftMetaItem$ItemMetaKey;") Object metaKey);

    @Internal
    @MethodTarget(isStatic = true)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    public Multimap<Attribute, AttributeModifier> buildModifiers(@RedirectType(ItemAttributeModifiers) Object nbt);

    @Internal
    @MethodTarget(isStatic = true)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    void applyModifiers(
            Multimap<Attribute, AttributeModifier> modifiers,
            @RedirectType(CompoundTag) Object tag,
            @RedirectType("Lorg/bukkit/craftbukkit/inventory/CraftMetaItem$ItemMetaKey;") Object key);

    @MethodTarget(isStatic = true)
    @RedirectClass(CraftAttributeInstance)
    @RedirectName("convert")
    Object attributeB2N(AttributeModifier modifier);
}
