package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.Map;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.persistence.PersistentDataContainer;

@Descriptive(target = "net.minecraft.world.level.chunk.LevelChunk")
public interface LevelChunkHelper extends TargetDescriptor {
    @MethodTarget
    Object getBlockState(Object chunkAccess, int x, int y, int z);

    @MethodTarget
    Object getBlockStateFinal(Object chunk, int x, int y, int z);

    @MethodTarget
    Object getFluidState(Object chunk, int x, int y, int z);

    @MethodTarget
    Object setBlockState(
            Object chunk,
            @RedirectType(BlockPos) Object pos,
            @RedirectType(BlockState) Object iblockData,
            boolean flag,
            boolean doPlace);

    @MethodTarget
    @NeedTest("Not force on main thread")
    Object getBlockEntity(Object chunk, @RedirectType(BlockPos) Object pos);

    @MethodTarget
    @NotRecommended
    void setBlockEntity(Object chunk, @RedirectType(BlockEntity) Object entity);

    @MethodTarget
    @NotRecommended
    void removeBlockEntity(Object chunk, @RedirectType(BlockPos) Object pos);

    @MethodTarget
    Map<?, ?> getBlockEntities(Object chunk);

    @MethodTarget
    @Note("their data will be moved but not saved to nbt(?)")
    @NotRecommended
    void clearAllBlockEntities(Object chunk);

    @FieldTarget
    PersistentDataContainer persistentDataContainerGetter(Object chunk);
}
