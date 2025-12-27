package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.List;
import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;

@Descriptive(target = "net.minecraft.world.level.Level")
public interface LevelHelper extends TargetDescriptor {
    @MethodTarget
    World getWorld(Object level);

    @MethodTarget
    @Note("Return a levelChunk if it is fully loaded, else return null")
    Object getChunkIfLoaded(Object level, int chunkX, int chunkZ);

    @MethodTarget
    @Note("return a ServerChunkCache")
    Object getChunkSource(Object level);

    @MethodTarget
    @Note("same as getChunkSource.getChunkIfLoadedImmediately")
    Object getChunkIfLoadedImmediately(Object level, int chunkx, int chunkz);

    @MethodTarget
    @Note("same as getChunkSource.getChunk(x,z,FULL,true)")
    @NotRecommended
    Object getChunk(Object level, int chunkX, int chunkZ);

    @MethodTarget
    Object getBlockState(Object level, @RedirectType(BlockPos) Object pos);

    @MethodTarget
    boolean isOutsideBuildHeight(Object level, int y);

    default Object getChunkCustomAt(Object level, int blockx, int blockz, boolean forceLoadChunk) {
        Object chunkSource = this.getChunkSource(level);
        return NMSLevel.CHUNK_CACHE_SYSTEM.getChunkCustom(
                chunkSource, blockx >> 4, blockz >> 4, LevelEnum.CHUNK_STATUS_FULL, forceLoadChunk);
    }

    default Object getChunkCustom(Object level, int cx, int cz, boolean forceLoadChunk) {
        Object chunkSource = this.getChunkSource(level);
        return NMSLevel.CHUNK_CACHE_SYSTEM.getChunkCustom(
                chunkSource, cx, cz, LevelEnum.CHUNK_STATUS_FULL, forceLoadChunk);
    }

    @Contract("_,_,_,_,false -> null if not load")
    @Note(
            "when forceLoadChunk is true, will load chunk if absent, will run main thread task to get chunk and wait for task finish")
    default Object getBlockStateCustom(Object level, int x, int y, int z, boolean forceLoadChunk) {
        // we assert that this method is not called during tree generation
        // we assert that xyz is in bound
        Object chunkSource = this.getChunkSource(level);
        Object chunkAccess = NMSLevel.CHUNK_CACHE_SYSTEM.getChunkCustom(
                chunkSource, x >> 4, z >> 4, LevelEnum.CHUNK_STATUS_FULL, forceLoadChunk);
        return chunkAccess == null ? null : NMSLevel.LEVEL_CHUNK.getBlockState(chunkAccess, x, y, z);
    }

    @FieldTarget
    @RedirectType("Ljava/util/List;")
    List<?> blockEntityTickersGetter(Object chunk);

    @MethodTarget
    public boolean setBlock(
            Object level, @RedirectType(BlockPos) Object pos, @RedirectType(BlockState) Object state, int flags);

    @FieldTarget
    Object spigotConfigGetter(Object level);

    //    @MethodTarget
    //    void moonrise$midTickTasks(Object chunk);
    // addEntity
}
