package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.Collection;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

@Descriptive(target = "net.minecraft.world.level.block.state.BlockState")
public interface BlockStateHelper extends TargetDescriptor {
    @MethodTarget
    Material getBukkitMaterial(Object state);

    @MethodTarget
    BlockData createCraftBlockData(Object state);

    @MethodTarget
    Object getBlock(Object type);

    @MethodTarget
    Collection<?> getProperties(Object type);

    @MethodTarget
    boolean hasProperty(Object type, @RedirectType(StateProperty) Object property);

    @MethodTarget
    Comparable<?> getValue(Object type, @RedirectType(StateProperty) Object property);

    //    @MethodTarget
    //    Comparable<?> getNullableValue(Object type, @RedirectType(StateProperty)Object property);

    @Note(
            "this is a quick transfer method, visiting a table to transfer current STATE ->{modify property=value}-> NEXT_STATE ")
    @MethodTarget
    Object setValue(Object type, @RedirectType(StateProperty) Object property, Comparable<?> value);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R1)
    boolean isSolid(Object type);

    @MethodTarget
    boolean isDestroyable(Object type);

    @MethodTarget
    boolean isAir(Object type);

    @MethodTarget
    boolean ignitedByLava(Object type);

    @MethodTarget
    boolean liquid(Object type);

    //    @MethodTarget
    //    void onPlace()
    @MethodTarget
    @Note("This is block tick, not scheduled tick or blockEntity tick or sth")
    void tick(
            Object type,
            @RedirectType(ServerLevel) Object world,
            @RedirectType(BlockPos) Object pos,
            @RedirectType(RandomSource) Object rand);

    @MethodTarget
    void randomTick(
            Object type,
            @RedirectType(ServerLevel) Object world,
            @RedirectType(BlockPos) Object pos,
            @RedirectType(RandomSource) Object rand);

    //    boolean canBeReplaced(Object type);
    @MethodTarget
    Object getMenuProvider(Object type, @RedirectType(Level) Object world, @RedirectType(BlockPos) Object pos);

    @MethodTarget
    boolean hasBlockEntity(Object type);

    //    @MethodTarget
    //    Object getTicker(Object type, @RedirectType(Level)Object world, )

}
