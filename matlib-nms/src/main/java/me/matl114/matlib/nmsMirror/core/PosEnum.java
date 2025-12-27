package me.matl114.matlib.nmsMirror.core;

import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class PosEnum {
    public static final Object DIR_DOWN;
    public static final Object DIR_UP;
    public static final Object DIR_NORTH;
    public static final Object DIR_SOUTH;
    public static final Object DIR_WEST;
    public static final Object DIR_EAST;

    public static final Object AXIS_X;
    public static final Object AXIS_Y;
    public static final Object AXIS_Z;

    static {
        try {
            Class<?> clazz = ObfManager.getManager().reobfClass("net.minecraft.core.Direction");
            Object[] enums = clazz.getEnumConstants();
            DIR_DOWN = enums[0];
            DIR_UP = enums[1];
            DIR_NORTH = enums[2];
            DIR_SOUTH = enums[3];
            DIR_WEST = enums[4];
            DIR_EAST = enums[5];
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            Class<?> clazz = ObfManager.getManager().reobfClass("net.minecraft.core.Direction$Axis");
            Object[] enums = clazz.getEnumConstants();
            AXIS_X = enums[0];
            AXIS_Y = enums[1];
            AXIS_Z = enums[2];
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
