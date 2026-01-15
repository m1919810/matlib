package me.matl114.matlib.utils;

import me.matl114.matlib.algorithms.algorithm.IntUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationUtils {
    public static long toChunkPos(int x, int z) {
        return IntUtils.makePair(x >> 4, z >> 4);
    }

    public static long toChunkPos(Block block) {
        return IntUtils.makePair(block.getX() >> 4, block.getZ() >> 4);
    }

    public static long toChunkPos(Location loc) {
        return IntUtils.makePair(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    public static Chunk getChunk(World world, long key, boolean generate) {
        return world.getChunkAt(IntUtils.getFirstInt(key), IntUtils.getSecondInt(key), generate);
    }

    public static boolean isChunkLoaded(World world, long key) {
        return world.isChunkLoaded(IntUtils.getFirstInt(key), IntUtils.getSecondInt(key));
    }
}
