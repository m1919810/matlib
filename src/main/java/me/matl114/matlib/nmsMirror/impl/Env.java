package me.matl114.matlib.nmsMirror.impl;

import static me.matl114.matlib.nmsMirror.Utils.*;

import com.mojang.serialization.DynamicOps;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class Env {
    public static final Object SERVER;
    public static final Object REGISTRY_ACCESS;
    public static final Object REGISTRY_FROZEN;
    public static final Executor MAIN_EXECUTOR;
    public static final Object SERVER_CONNECTION;
    public static final DynamicOps<Object> NBT_OP;
    public static final Object UNIT;

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.server.MinecraftServer");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        List<Field> fields =
                ReflectUtils.getAllFieldsRecursively(clazz).stream().toList();
        ;
        Field field;
        SERVER = Utils.matchName(fields, "SERVER");
        field = ObfManager.getManager().matchFieldOrThrow(fields, "registries");
        REGISTRY_ACCESS = Utils.reflect(field, SERVER);
        field = ObfManager.getManager().lookupFieldInClass(REGISTRY_ACCESS.getClass(), "composite");
        REGISTRY_FROZEN = Utils.reflect(field, REGISTRY_ACCESS);
        Class<?> clazz1;
        try {
            clazz1 = ObfManager.getManager().reobfClass("io.papermc.paper.util.MCUtil");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        MAIN_EXECUTOR = (Executor) deobfStatic(clazz1, "MAIN_EXECUTOR");
        List<Method> methods =
                ReflectUtils.getAllMethodsRecursively(clazz).stream().toList();

        SERVER_CONNECTION = Utils.invokeNoArgument(methods, "getConnection", SERVER);

        Class<?> clazz2;
        try {
            clazz2 = ObfManager.getManager().reobfClass("net.minecraft.nbt.NbtOps");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        NBT_OP = (DynamicOps<Object>) deobfStatic(clazz2, "INSTANCE");
        Class<?> clazz3;
        try {
            clazz3 = ObfManager.getManager().reobfClass("net.minecraft.util.Unit");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        UNIT = clazz3.getEnumConstants()[0];
    }
}
