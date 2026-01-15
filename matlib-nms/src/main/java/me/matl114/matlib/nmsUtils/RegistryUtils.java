package me.matl114.matlib.nmsUtils;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

import com.destroystokyo.paper.NamespacedTag;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.core.RegistryKeyEnum;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.KeyUtils;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class RegistryUtils {
    private static final Reference2ReferenceMap<Registry<?>, Iterable<?>> bukkitRegistryToMinecraftCache =
            new Reference2ReferenceOpenHashMap<>();

    public static Iterable<?> toMinecraftRegistry(Registry<?> bukkitRegistry) {
        return bukkitRegistryToMinecraftCache.computeIfAbsent(
                bukkitRegistry, CraftBukkit.REGISTRY::minecraftRegistryGetter);
    }

    public static <B extends Keyed, M> B minecraftToBukkit(M minecraft, Registry<B> bukkitRegistry) {
        Object minecraftRegistry = toMinecraftRegistry(bukkitRegistry);
        return bukkitRegistry.get(fromMinecraftNSK(NMSCore.REGISTRIES.getKey(minecraftRegistry, minecraft)));
        // return CraftBukkit.REGISTRY.minecraftToBukkit(minecraft, NMSCore.REGISTRIES.key(minecraftRegistry),
        // bukkitRegistry);
    }

    public static Object getByKey(Object registry, String key) {
        return NMSCore.REGISTRIES.getOptional(registry, resourceLocation(key)).orElseThrow();
    }

    public static Object getByKeyOrNull(Object registry, String key) {
        return NMSCore.REGISTRIES.getValue(registry, resourceLocation(key));
    }

    public static Object getResourceLocation(Object registry, Object value) {
        return NMSCore.REGISTRIES.getKey(registry, value);
    }

    public static <B extends Keyed, M> Object byKeyBukkitToMinecraft(B bukkit, Object nmsRegistry) {
        return NMSCore.REGISTRIES
                .getOptional(nmsRegistry, fromBukkit(bukkit.getKey()))
                .orElseThrow();
    }

    public static Object validateAsHolder(Object nms, Object nmsRegistry) {
        Object val = NMSCore.REGISTRIES.wrapAsHolder(nmsRegistry, nms);
        if (NMSCore.REGISTRIES.isHolderReference(val)) {
            return val;
        } else {
            throw new IllegalArgumentException("Illegal registry here !");
        }
    }

    public static <M> Object enchantmentToMinecraftHolder(Enchantment enchantment) {
        if (BuiltInRegistryEnum.ENCHANTMENT != null) {
            return validateAsHolder(
                    CraftBukkit.REGISTRY.enchantmentToMinecraft(enchantment),
                    BuiltInRegistryEnum.ENCHANTMENT); //  handledBukkitToMinecraftHolder(enchantment,
            // BuiltInRegistryEnum.ENCHANTMENT);
        } else {
            return validateAsHolder(
                    CraftBukkit.REGISTRY.enchantmentToMinecraft(enchantment),
                    CraftBukkit.REGISTRY.getMinecraftRegistry(RegistryKeyEnum.ENCHANTMENT));
        }
    }

    public static <M> Object attributeToMinecraftHolder(Attribute attribute) {
        return validateAsHolder(
                byKeyBukkitToMinecraft(attribute, BuiltInRegistryEnum.ATTRIBUTE), BuiltInRegistryEnum.ATTRIBUTE);
    }

    public static NamespacedKey fromMinecraftNSK(Object nms) {
        if (nms == null) {
            throw new IllegalArgumentException("Can not create NamespacedKey from null!");
        }
        return new NamespacedKey(NMSCore.NAMESPACE_KEY.getNamespace(nms), NMSCore.NAMESPACE_KEY.getPath(nms));
    }

    public static NamespacedKey fromTagName(String tagLocation) {
        if (!tagLocation.isEmpty() && tagLocation.charAt(0) == '#') {
            return KeyUtils.fromString(tagLocation.substring(1));
        }
        return null;
    }

    public static Object resourceLocation(String string) {
        int index = string.indexOf(":");
        if (index == -1) {
            return NMSCore.NAMESPACE_KEY.newNSKey("minecraft", string);
        } else {
            return NMSCore.NAMESPACE_KEY.newNSKey(string.substring(0, index), string.substring(index + 1));
        }
    }

    public static Object fromBukkit(NamespacedKey key) {
        if (key == null) {
            return null;
        }
        return NMSCore.NAMESPACE_KEY.fromBukkit(key);
    }

    @Deprecated(forRemoval = true)
    public static Object parseTagOrKey(String id) {
        int index = id.indexOf(":");
        if (!id.isEmpty() && id.charAt(0) == '#') {
            // this class is for sure removal for a period of version
            if (index == -1) {
                return new NamespacedTag("minecraft", id.substring(1));
            } else {
                return new NamespacedTag(id.substring(1, index), id.substring(index + 1));
            }
        } else {
            if (index == -1) {
                return new NamespacedKey("minecraft", id);
            } else {
                return new NamespacedKey(id.substring(1, index), id.substring(index + 1));
            }
        }
    }
}
