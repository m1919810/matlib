package me.matl114.matlib.nmsMirror.level.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.level.BlockEntityHelper;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

@Descriptive(target = "net.minecraft.world.level.block.entity.BlockEntity")
public interface BlockEntityHelper_1_20_R4 extends BlockEntityHelper {
    @Override
    default Object saveWithId(Object be) {
        return saveWithId(be, Env.REGISTRY_FROZEN);
    }

    @Override
    default Object saveWithFullMetadata(Object be) {
        return saveWithFullMetadata(be, Env.REGISTRY_FROZEN);
    }

    @Override
    default void load(Object entity, @RedirectType(CompoundTag) Object rewritingNBT) {
        loadWithComponents(entity, rewritingNBT, Env.REGISTRY_FROZEN);
    }

    @Override
    default Object loadStatic(Object pos, Object state, Object nbt) {
        return loadStatic(pos, state, nbt, Env.REGISTRY_FROZEN);
    }

    @MethodTarget
    Iterable<?> components(Object entity);

    @MethodTarget
    void setComponents(Object entity, @RedirectType(DataComponentMap) Object map);

    @MethodTarget(isStatic = true)
    Object loadStatic(
            @RedirectType(BlockPos) Object pos,
            @RedirectType(BlockState) Object state,
            @RedirectType(CompoundTag) Object nbt,
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object reg);

    @MethodTarget
    void loadWithComponents(
            Object entity,
            @RedirectType(CompoundTag) Object rewritingNBT,
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object reg);

    @MethodTarget
    Object saveWithId(Object be, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object reg);

    @MethodTarget
    Object saveWithFullMetadata(Object be, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object reg);
}
