package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class EmptyEnum {
    public static final Object EMPTY_ITEMSTACK;
    public static final Object ITEM_AIR;
    public static final Object BLOCK_AIR;

    static {
        try {
            Class<?> clazz1 = ObfManager.getManager().reobfClass("net.minecraft.world.item.ItemStack");
            EMPTY_ITEMSTACK = Utils.deobfStatic(clazz1, "EMPTY");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            Class<?> clazz1 = ObfManager.getManager().reobfClass("net.minecraft.world.level.block.Blocks");
            BLOCK_AIR = Utils.deobfStatic(clazz1, "AIR");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            Class<?> clazz1 = ObfManager.getManager().reobfClass("net.minecraft.world.item.Items");
            ITEM_AIR = Utils.deobfStatic(clazz1, "AIR");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
