package me.matl114.matlib.nmsMirror.inventory;

import java.util.Map;
import java.util.Objects;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class ItemEnum {
    public static final Enum RARITY_COMMON;
    public static final Enum RARITY_UNCOMMON;
    public static final Enum RARITY_RARE;
    public static final Enum RARITY_EPIC;

    static {
        Class<?> clazz0;
        try {
            clazz0 = ObfManager.getManager().reobfClass("net.minecraft.world.item.Rarity");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Map<String, Enum> enumMap = ReflectUtils.getEnumMap(clazz0);
        RARITY_COMMON = Objects.requireNonNull(enumMap.get("COMMON"));
        RARITY_UNCOMMON = Objects.requireNonNull(enumMap.get("UNCOMMON"));
        RARITY_RARE = Objects.requireNonNull(enumMap.get("RARE"));
        RARITY_EPIC = Objects.requireNonNull(enumMap.get("EPIC"));
    }
}
