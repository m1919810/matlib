package me.matl114.matlib.nmsMirror.nbt.v1_21_R4;

import me.matl114.matlib.nmsMirror.nbt.TagAPI;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;

import static me.matl114.matlib.nmsMirror.Import.NumericTag;

@MultiDescriptive(targetDefault = "net.minecraft.nbt.Tag")
public interface TagAPI_v1_21_R4 extends TagAPI {
    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("longValue")
    public abstract long getAsLong(Object tag) ;

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("intValue")
    public abstract int getAsInt(Object tag);

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("shortValue")
    public abstract short getAsShort(Object tag);

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("byteValue")
    public abstract byte getAsByte(Object tag);

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("doubleValue")
    public abstract double getAsDouble(Object tag);

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("floatValue")
    public abstract float getAsFloat(Object tag);

    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("box")
    public abstract Number getAsNumber(Object tag);
}
