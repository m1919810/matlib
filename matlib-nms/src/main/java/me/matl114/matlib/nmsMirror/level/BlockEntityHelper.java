package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.interfaces.PdcCompoundHolder;
import me.matl114.matlib.nmsUtils.CraftBukkitUtils;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.persistence.PersistentDataContainer;

@Descriptive(target = "net.minecraft.world.level.block.entity.BlockEntity")
public interface BlockEntityHelper extends TargetDescriptor, PdcCompoundHolder {
    @FieldTarget
    //    @RedirectType(CraftPersistentDataContainer)
    PersistentDataContainer persistentDataContainerGetter(Object be);

    @FieldTarget
    void persistentDataContainerSetter(
            Object be, @RedirectType(CraftPersistentDataContainer) PersistentDataContainer val);

    default Object getPersistentDataCompound(Object val) {
        PersistentDataContainer container = persistentDataContainerGetter(val);
        return CraftBukkit.PERSISTENT_DATACONTAINER.asCompoundMirror(container);
    }

    @Override
    default Object getPersistentDataCompoundCopy(Object val) {
        PersistentDataContainer container = persistentDataContainerGetter(val);
        return NMSCore.COMPOUND_TAG.copy(CraftBukkit.PERSISTENT_DATACONTAINER.asCompoundMirror(container));
    }

    default COWView<Object> getPersistentDataCompoundView(Object val, boolean forceCreate) {
        Object component = getPersistentDataCompound(val);
        return COWView.withWriteback(component, (i) -> {
            this.setPersistentDataCompoundCopy(val, i);
            return getPersistentDataCompound(val);
        });
    }

    @NeedTest
    default void setPersistentDataCompoundCopy(Object itemStack, Object compound) {
        persistentDataContainerSetter(
                itemStack,
                CraftBukkit.PERSISTENT_DATACONTAINER.newPersistentDataContainer(
                        NMSCore.COMPOUND_TAG.tagsGetter(compound), CraftBukkitUtils.getPdcDataTypeRegistry()));
    }

    @MethodTarget
    boolean isRemoved(Object be);

    @MethodTarget
    void setRemoved(Object be);

    @MethodTarget
    void clearRemoved(Object be);

    @MethodTarget
    @ForceOnMainThread
    void setChanged(Object be);

    @MethodTarget
    Object getBlockPos(Object be);

    @MethodTarget
    Object getBlockState(Object be);

    @MethodTarget
    Object getType(Object be);

    @Note("full nbt without pos information")
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4)
    Object saveWithId(Object be);

    @Note("full nbt")
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4)
    Object saveWithFullMetadata(Object be);

    @MethodTarget(isStatic = true)
    Object loadStatic(
            @RedirectType(BlockPos) Object pos,
            @RedirectType(BlockState) Object state,
            @RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    void load(Object entity, @RedirectType(CompoundTag) Object rewritingNBT);
}
