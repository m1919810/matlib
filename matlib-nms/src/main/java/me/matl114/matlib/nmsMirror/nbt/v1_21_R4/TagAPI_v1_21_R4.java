package me.matl114.matlib.nmsMirror.nbt.v1_21_R4;

import static me.matl114.matlib.nmsMirror.Import.NumericTag;
import static me.matl114.matlib.nmsMirror.Import.TagParser;

import com.google.common.base.Suppliers;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.nmsMirror.nbt.TagAPI;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;

import java.util.function.Supplier;

@MultiDescriptive(targetDefault = "net.minecraft.nbt.Tag")
public interface TagAPI_v1_21_R4 extends TagAPI {
    @MethodTarget()
    @RedirectClass(NumericTag)
    @RedirectName("longValue")
    public abstract long getAsLong(Object tag);

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



    static Holder<Object> TAG_PARSER = Holder.of(null);

    @Internal
    @FieldTarget
    @RedirectClass(TagParser)
    @RedirectName("NBT_OPS_PARSER")
    public Object getNbtTagParser();

    @Internal
    @MethodTarget()
    @RedirectClass(TagParser)
    public <T> T parseFully(Object parser, String text);

    default Object parseNbt(String string){
        Object parser = TAG_PARSER.get();
        if(parser == null){
            parser = getNbtTagParser();
            TAG_PARSER.setValue(parser);
        }
        return parseFully(parser, string);
    }
}
