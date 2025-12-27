package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

@Descriptive(target = "net.minecraft.nbt.Tag")
public interface TagHelper extends TargetDescriptor {
    @MethodTarget
    byte getId(Object tag);

    @MethodTarget
    Object copy(Object tag);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4)
    default String getAsString(Object tag) {
        return tag.toString();
    }

    @MethodTarget
    void accept(Object self, @RedirectType(Import.TagVisitor) Object visitor);
}
