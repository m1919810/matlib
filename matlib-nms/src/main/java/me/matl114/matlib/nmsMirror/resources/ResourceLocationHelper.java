package me.matl114.matlib.nmsMirror.resources;

import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.NamespacedKey;

@Descriptive(target = "net.minecraft.resources.ResourceLocation")
public interface ResourceLocationHelper extends TargetDescriptor {
    @ConstructorTarget
    Object newNSKey(String namespace, String key);

    default Object newNSKey(String key) {
        return newNSKey("minecraft", key);
    }

    @MethodTarget(isStatic = true)
    boolean isValidPath(String path);

    @MethodTarget(isStatic = true)
    boolean isValidNamespace(String namespace);

    @MethodTarget
    String getPath(Object obj);

    @MethodTarget
    String getNamespace(Object obj);

    default NamespacedKey toBukkit(Object obj) {
        return new NamespacedKey(getNamespace(obj), getPath(obj));
    }

    default Object fromBukkit(NamespacedKey key) {
        return newNSKey(key.getNamespace(), key.getKey());
    }
}
