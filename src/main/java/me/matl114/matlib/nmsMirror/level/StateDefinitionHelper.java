package me.matl114.matlib.nmsMirror.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import java.util.Collection;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.world.level.block.state.StateDefinition")
interface StateDefinitionHelper extends TargetDescriptor {
    @MethodTarget
    ImmutableList<?> getPossibleStates(Object def);

    @MethodTarget
    Collection<?> getProperties(Object def);

    @MethodTarget
    Object getProperty(Object def, String name);

    @MethodTarget
    Object getOwner(Object def);

    @FieldTarget
    @RedirectType("Lcom/google/common/collect/ImmutableSortedMap")
    ImmutableSortedMap<String, ?> propertiesByNameGetter(Object def);
}
