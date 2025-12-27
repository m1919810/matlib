package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Utils.*;

import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class LevelEnum {
    public static final Object CHUNK_STATUS_FULL;
    public static final Object CHUNK_STATUS_SPAWN;
    // that is periods in chunk gen, we don't actually use them
    //    public static final Object CHUNK_STATUS_LIGHT;
    //    public static final Object CHUNK_STATUS_INITIALIZE_LIGHT;
    //    public static final Object CHUNK_STATUS_FEATURES;
    //    public static final Object CHUNK_STATUS_CARVERS;
    //    public static final Object CHUNK_STATUS_SURFACE
    static {
        try {
            Class<?> clazz = ObfManager.getManager().reobfClass("net.minecraft.world.level.chunk.status.ChunkStatus");
            CHUNK_STATUS_FULL = deobfStatic(clazz, "FULL");
            CHUNK_STATUS_SPAWN = deobfStatic(clazz, "SPAWN");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
