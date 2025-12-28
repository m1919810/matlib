package me.matl114.matlib.utils.reflect.internal;

import java.util.Map;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Internal
@Note(
        "this is a sample of how to use helper annotations, also this class is used internally for building Helpers for reobf")
@Descriptive(target = "io.papermc.paper.util.ObfHelper$ClassMapping")
public interface ClassMapperHelper extends TargetDescriptor {
    //    @ConstructorTarget
    //    @RedirectType("io.papermc.paper.util.ObfHelper$ClassMapping")
    //    @Note("RedirectType announced that this method should return a ClassMapping Object")
    //    public Object newInstance0(String obfName, String mojangName, Map mapping);
    @Note("this requires a const fieldAccessor create for field \"obfName\"")
    @FieldTarget
    @RedirectType("Ljava/lang/String")
    public String obfNameGetter(Object obj);

    @FieldTarget
    @RedirectType("Ljava/lang/String")
    public String mojangNameGetter(Object obj);

    @MethodTarget
    public Map methodsByObf(
            @RedirectType("Lio/papermc/paper/util/ObfHelper$ClassMapping")
                    @Note(
                            "type redirect on self object can be ignored, but those on other argument should be processed and method should involve class cast")
                    Object self);
}
