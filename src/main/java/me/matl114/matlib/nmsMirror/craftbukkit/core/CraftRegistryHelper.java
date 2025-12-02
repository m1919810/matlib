package me.matl114.matlib.nmsMirror.craftbukkit.core;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.CraftRegistry")
public interface CraftRegistryHelper extends TargetDescriptor {
//    @MethodTarget(isStatic = true)
//    @Internal
//    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
//    public <B extends Keyed, M> B minecraftToBukkit(M minecraft, @RedirectType(ResourceKey)Object registryKey, Registry<B> bukkitRegistry);

    @MethodTarget(isStatic = true)
    @Internal
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    public <B extends Keyed, M> M bukkitToMinecraft(B bukkit);

    @MethodTarget
    @Note("This marks the Enchantment API change in 1.20.4")
    @RedirectName("getRaw")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3)
    default Object enchantmentToMinecraft(org.bukkit.enchantments.Enchantment enchantment){
        return bukkitToMinecraft(enchantment);
    }
    @Internal
    @MethodTarget(isStatic = true)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    public Object getMinecraftRegistry(@RedirectType(ResourceKey)Object key);

    @FieldTarget
    Iterable<?> minecraftRegistryGetter(Registry<?> registry);

    //It is bukkitToMinecraft + validateAsHolder
//    @MethodTarget(isStatic = true)
//    <B extends Keyed, M> Object bukkitToMinecraftHolder(B bukkit, @RedirectType(ResourceKey)Object registryKey);

    @RedirectClass(CraftTag)
    @MethodTarget
    @RedirectName("getHandle")
    public Object getTagHandle( org.bukkit.Tag<?> craftTag);
}
