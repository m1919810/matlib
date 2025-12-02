package me.matl114.matlib.nmsMirror.nbt.v1_21_R4;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.nbt.TagEnum;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;

import javax.annotation.Nonnull;
import java.util.AbstractList;

import static me.matl114.matlib.nmsMirror.Import.*;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

@Descriptive(target = "net.minecraft.nbt.CompoundTag")
public interface CompoundTagHelper_v1_21_R4 extends CompoundTagHelper {
    default boolean getBoolean(Object nbt, String key) {
        return getByte(nbt, key) != 0;
    }

    @Override
    default byte getTagType(Object nbt, String key){
        Object val = get(nbt, key);
        return val == null? 0: TAGS.getId(val);
    }
    default boolean contains(Object nbt, String key, int type){
        int i = getTagType(nbt, key);
        return i == type || type == 99 && (i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6);
    }

    default byte getByte(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null? 0: (TAGS.getId(val) == TagEnum.TAG_BYTE ? TAGS.getAsByte(val) : 0);
    }
    default short getShort(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null? 0: (TAGS.getId(val) == TagEnum.TAG_SHORT ? TAGS.getAsShort(val) : 0);
    }
    default int getInt(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null? 0: (TAGS.getId(val) == TagEnum.TAG_INT ? TAGS.getAsInt(val) : 0);
    }
    default long getLong(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null ? 0 : (TAGS.getId(val) == TagEnum.TAG_LONG ? TAGS.getAsLong(val) : 0);
    }
    default float getFloat(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null ? 0 : (TAGS.getId(val) == TagEnum.TAG_FLOAT ? TAGS.getAsFloat(val) : 0);
    }



    default double getDouble(Object nbt, String key) {
       Object val = get(nbt, key);
       return val == null? 0: (TAGS.getId(val) == TagEnum.TAG_DOUBLE ? TAGS.getAsDouble(val) : 0);

    }

    default AbstractList getList(Object nbt, String key, int type){
        return getList(nbt, key);
    }


    default AbstractList getList(Object nbt, String key){
        Object val = get(nbt, key);
        return val instanceof AbstractList ? (AbstractList)val : TAGS.listTag();
    }

    default byte[] getByteArray(Object nbt, String key) {
        Object val = get(nbt, key);
        return val == null ? new byte[0] : (TAGS.getId(val) == TagEnum.TAG_BYTE_ARRAY ? TAGS.getAsByteArray(val) : new byte[0]);
    }

    default int[] getIntArray(Object nbt, String key){
        Object val = get(nbt, key);
        return val == null ? new int[0] : (TAGS.getId(val) == TagEnum.TAG_INT_ARRAY ? TAGS.getAsIntArray(val) : new int[0]);
    }

    default long[] getLongArray(Object nbt, String key){
        Object val = get(nbt, key);
        return val == null ? new long[0] : (TAGS.getId(val) == TagEnum.TAG_LONG_ARRAY ? TAGS.getAsLongArray(val) : new long[0]);
    }

    default Object getCompound(Object nbt, String key){
        Object val = get(nbt, key);
        return val == null ? newComp() : (TAGS.getId(val) == TagEnum.TAG_COMPOUND ? val : newComp());
    }

}
