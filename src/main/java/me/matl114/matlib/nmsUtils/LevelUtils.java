package me.matl114.matlib.nmsUtils;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;
import static me.matl114.matlib.nmsMirror.impl.NMSLevel.*;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class LevelUtils {
    public static Material getBlockTypeAsync(Block block, boolean forceLoadChunk) {
        var world = CraftBukkit.BLOCK.getHandle(block);
        var nms = LEVEL.getBlockStateCustom(world, block.getX(), block.getY(), block.getZ(), forceLoadChunk);
        return nms == null ? null : BLOCK_STATE.getBukkitMaterial(nms);
    }

    public static BlockData getBlockDataAsync(Block block, boolean forceLoadChunk) {
        var world = CraftBukkit.BLOCK.getHandle(block);
        var nms = LEVEL.getBlockStateCustom(world, block.getX(), block.getY(), block.getZ(), forceLoadChunk);
        return nms == null ? null : BLOCK_STATE.createCraftBlockData(nms);
    }

    @Note("creating blockState requires on main thread, because of paper-made flags of useSnapshot")
    public static Object getBlockEntityAsync(Block block, boolean forceLoadChunk) {
        var world = CraftBukkit.BLOCK.getHandle(block);
        int x = block.getX(), z = block.getZ();
        var chunk = LEVEL.getChunkCustomAt(world, x, z, forceLoadChunk);
        return chunk == null ? null : LEVEL_CHUNK.getBlockEntity(chunk, BLOCKPOS.ofVec(x, block.getY(), z));
    }
}
