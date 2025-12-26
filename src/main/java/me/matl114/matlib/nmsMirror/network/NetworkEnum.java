package me.matl114.matlib.nmsMirror.network;

import java.lang.reflect.Field;
import java.util.List;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class NetworkEnum {
    public static final Object ENTITYDATA_ITEMSTACK;

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.network.syncher.EntityDataSerializers");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        List<Field> fields = ReflectUtils.getAllFieldsRecursively(clazz);
        ENTITYDATA_ITEMSTACK = Utils.matchName(fields, "ITEM_STACK");
    }
}
