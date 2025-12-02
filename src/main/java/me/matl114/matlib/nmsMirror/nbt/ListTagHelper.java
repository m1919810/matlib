package me.matl114.matlib.nmsMirror.nbt;

import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.version.Version;

import java.util.AbstractList;
import java.util.List;

import static me.matl114.matlib.nmsMirror.Import.Tag;

@Descriptive(target = "net.minecraft.nbt.ListTag")
public interface ListTagHelper extends TagHelper {
    @ConstructorTarget
    AbstractList<?> newListTag();

    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4)
    default AbstractList<?> newListTag(List<?> list, byte type){
        return newListTag0(list);
    }

    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    AbstractList<?> newListTag0(List<?> list);

    @MethodTarget
    Object remove(Object listTag, int index);

    @MethodTarget
    boolean isEmpty(Object listTag);

    @MethodTarget
    Object getCompound(Object listTag, int index);

    @MethodTarget
    AbstractList<?> getList(Object listTag, int index);

    @MethodTarget
    short getShort(Object listTag, int index);;

    @MethodTarget
    int getInt(Object listTag, int index);

    @MethodTarget
    int[] getIntArray(Object listTag, int index);

    @MethodTarget
    long[] getLongArray(Object listTag, int index);

    @MethodTarget
    double getDouble(Object listTag, int index);

    @MethodTarget
    float getFloat(Object listTag, int index);

    @MethodTarget
    String getString(Object listTag, int index);

    @MethodTarget
    int size(Object listTag);

    @MethodTarget
    Object get(Object listTag, int index);

    @MethodTarget
    boolean setTag(Object listTag, int index, @RedirectType(Tag)Object element);

    @MethodTarget
    @RedirectName("set")
    Object getAndSet(Object listTag, int index, @RedirectType(Tag)Object nbtbase);

    @MethodTarget
    void addTag(Object listTag, int index,@RedirectType(Tag) Object nbtbase);

    @MethodTarget
    void clear(Object listTag);

//    @FieldTarget
//    @RedirectType("B")
//    byte typeGetter(Object listTag);

}
