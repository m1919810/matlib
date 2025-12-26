package me.matl114.matlib.nmsMirror.core;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.jetbrains.annotations.Contract;

@Descriptive(target = "net.minecraft.core.Vec3i")
public interface Vec3iHelper extends TargetDescriptor {
    @ConstructorTarget
    Comparable<?> ofVec(int x, int y, int z);

    default Comparable<?> cast(Object obj) {
        return (Comparable<?>) obj;
    }

    @MethodTarget
    int getX(Object vec);

    @MethodTarget
    int getY(Object vec);

    @MethodTarget
    int getZ(Object vec);

    @MethodTarget
    Object setX(Object vec, int i);

    @MethodTarget
    Object setY(Object vec, int i);

    @MethodTarget
    Object setZ(Object vec, int i);

    @MethodTarget
    @Contract("_,_,_,_ -> new")
    Object offset(Object vec, int x, int y, int z);

    @MethodTarget
    Object offset(Object vec, @RedirectType(Vec3i) Object vec3);

    @MethodTarget
    @Contract("_,_ -> new")
    Object multiply(Object vec, int scale);

    @MethodTarget
    @Contract("_,_,_ -> new")
    Object relative(Object vec, @RedirectType(Direction) Object dir, int distance);

    @MethodTarget
    @Contract("_,_ -> new")
    @Note("叉乘")
    Object cross(Object vec, @RedirectType(Vec3i) Object vec3);

    @MethodTarget
    String toShortString(Object vec);
}
