package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.jetbrains.annotations.Contract;

@Descriptive(target = "net.minecraft.server.level.ServerChunkCache")
public interface ServerChunkCacheHelper extends TargetDescriptor {
    @MethodTarget
    @NotRecommended
    @Note("Return a chunkAccess ,may not present")
    Object getChunkAtImmediately(Object cache, int x, int z);

    @MethodTarget
    @Note("return a LevelChunk")
    Object getChunkAtIfLoadedImmediately(Object cache, int x, int z);

    @MethodTarget
    @NotRecommended
    Object getChunkNow(Object cache, int x, int z);

    @MethodTarget
    @Nullable @Note("when createIfNoExist, will force loading target Chunk if chunk is not loaded")
    @NotRecommended("in lower version(<1_21_R1), it may cause unexpected lag")
    @Contract("_,_,FULL,_ -> LevelChunk")
    Object getChunk(Object obj, int x, int z, @RedirectType(ChunkStatus) Object leastStatus, boolean createIfNoExist);

    default Object getChunkCustom(
            Object obj, int x, int z, @RedirectType(ChunkStatus) Object leastStatus, boolean createIfNoExist) {
        if (leastStatus == LevelEnum.CHUNK_STATUS_FULL) {
            Object chunk = getChunkAtIfLoadedImmediately(obj, x, z);
            if (chunk != null || !createIfNoExist) {
                return chunk;
            }
        }
        return getChunkFallback(obj, x, z, leastStatus, createIfNoExist);
    }
    // @IgnoreFailure(thresholdInclude = Version.v1_21_R1, below = true)
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R1, below = true)
    default Object getChunkFallback(
            Object obj, int x, int z, @RedirectType(ChunkStatus) Object leastStatus, boolean createIfNoExist) {
        return getChunk(obj, x, z, leastStatus, createIfNoExist);
    }

    @MethodTarget
    CompletableFuture<?> getChunkFuture(
            Object cache,
            int chunkX,
            int chunkZ,
            @RedirectType(ChunkStatus) Object leastStatus,
            boolean createIfNoExist);

    @MethodTarget
    Object getLevel(Object cache);

    @FieldTarget
    Executor mainThreadProcessorGetter(Object cache);

    @FieldTarget
    Object chunkMapGetter(Object cache);
}
