package me.matl114.matlib.nmsMirror.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class RegistryKeyEnum {
    public static final Object ENCHANTMENT;

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.core.registries.Registries");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        List<Field> fields = Arrays.stream(clazz.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .toList();
        ENCHANTMENT = Utils.matchName(fields, "ENCHANTMENT");
    }
}
