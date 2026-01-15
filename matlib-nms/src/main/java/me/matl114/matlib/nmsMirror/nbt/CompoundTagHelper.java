package me.matl114.matlib.nmsMirror.nbt;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.jetbrains.annotations.Contract;

@Descriptive(target = "net.minecraft.nbt.CompoundTag")
public interface CompoundTagHelper extends TargetDescriptor, TagHelper {
    @CastCheck(CompoundTag)
    boolean isCompound(Object nbt);

    @ConstructorTarget
    Object newComp();

    @ConstructorTarget
    Object newComp(Map<String, ?> entries);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    default Set<String> getAllKeys(Object nbt) {
        return keySet(nbt);
    }

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    Set<String> keySet(Object nbt);

    @MethodTarget
    int size(Object nbt);

    @MethodTarget
    Object put(Object nbt, String key, @RedirectType(Tag) Object tagElement);

    @MethodTarget
    void putByte(Object nbt, String key, byte value);

    @MethodTarget
    void putShort(Object nbt, String key, short value);

    @MethodTarget
    void putInt(Object nbt, String key, int value);

    @MethodTarget
    void putLong(Object nbt, String key, long value);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4)
    default void putUUID(Object nbt, String key, UUID value) {
        if (this.contains(nbt, key + "Most", TagEnum.TAG_ANY_NUMERIC)
                && this.contains(nbt, key + "Least", TagEnum.TAG_ANY_NUMERIC)) {
            remove(nbt, key + "Most");
            remove(nbt, key + "Least");
        }
        // Paper end - Support old UUID format
        putIntArray(nbt, key, MathUtils.uuidToIntArray(value));
    }

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4)
    default UUID getUUID(Object nbt, String key) {
        if (!contains(nbt, key, TagEnum.TAG_INT_ARRAY)) {
            if(this.contains(nbt, key, TagEnum.TAG_STRING)){
                String str = getString(nbt, key);
                return UUID.fromString(str);
            }
            if(this.contains(nbt, key + "Most", TagEnum.TAG_ANY_NUMERIC)
                && this.contains(nbt, key + "Least", TagEnum.TAG_ANY_NUMERIC)){
                return new UUID(this.getLong(nbt, key + "Most"), this.getLong(nbt, key + "Least"));
            }
        }
        int[] array = getIntArray(nbt, key);
        if (array.length != 4) {
            throw new IllegalArgumentException(
                    "Expected UUID-Array to be of length 4, but found " + array.length + ".");
        }
        // Paper end - Support old UUID format
        return MathUtils.uuidFromIntArray(array);
    }

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4)
    default boolean hasUUID(Object nbt, String key) {
        if (this.contains(nbt, key + "Most", TagEnum.TAG_ANY_NUMERIC)
                && this.contains(nbt, key + "Least", TagEnum.TAG_ANY_NUMERIC)) {
            return true;
        }
        // Paper end - Support old UUID format
        int[] tag = this.getIntArray(nbt, key);
        return tag.length == 4;
    }

    @MethodTarget
    void putFloat(Object nbt, String key, float value);

    @MethodTarget
    void putDouble(Object nbt, String key, double value);

    @MethodTarget
    void putString(Object nbt, String key, String value);

    @MethodTarget
    void putByteArray(Object nbt, String key, byte[] value);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    default void putByteArray(Object nbt, String key, List<Byte> value) {
        putByteArray(nbt, key, CollectionUtils.toByteArray(value));
    }

    @MethodTarget
    void putIntArray(Object nbt, String key, int[] value);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    default void putIntArray(Object nbt, String key, List<Integer> value) {
        putIntArray(nbt, key, CollectionUtils.toIntArray(value));
    }

    @MethodTarget
    void putLongArray(Object nbt, String key, long[] value);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    default void putLongArray(Object nbt, String key, List<Long> value) {
        putLongArray(nbt, key, CollectionUtils.toLongArray(value));
    }

    @MethodTarget
    void putBoolean(Object nbt, String key, boolean value);

    @MethodTarget
    @Nullable @Note("return null if absent")
    @RedirectType(Tag)
    Object get(Object nbt, String key);

    @MethodTarget
    boolean contains(Object nbt, String key);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    public boolean contains(Object nbt, String key, int type);

    @MethodTarget
    byte getTagType(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public byte getByte(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public short getShort(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public int getInt(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public long getLong(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public float getFloat(Object nbt, String key);

    @MethodTarget
    @Note(extra = {"return 0 when absent"})
    public double getDouble(Object nbt, String key);

    @MethodTarget
    @Nonnull
    @Note(extra = {"return empty string when absent"})
    public String getString(Object nbt, String key);

    @MethodTarget
    @Nonnull
    @Note(
            value = "may throw crash report",
            extra = {"return new instance when absent"})
    public byte[] getByteArray(Object nbt, String key);

    @MethodTarget
    @Nonnull
    @Note(
            value = "may throw crash report",
            extra = {"return new instance when absent"})
    public int[] getIntArray(Object nbt, String key);

    @MethodTarget
    @Nonnull
    @Note(
            value = "may throw crash report",
            extra = {"return new instance when absent"})
    public long[] getLongArray(Object nbt, String key);

    @MethodTarget
    @Nonnull
    @Note(
            value = "may throw crash report",
            extra = {"return new instance when absent"})
    @RedirectType(CompoundTag)
    public Object getCompound(Object nbt, String key);

    @Nonnull
    default Object getOrNewCompound(Object nbt, String key) {
        var nbt0 = get(nbt, key);
        if (nbt0 == null || !isCompound(nbt0)) {
            nbt0 = newComp();
            put(nbt, key, nbt0);
        }
        return nbt0;
    }

    @MethodTarget
    @Nonnull
    @Note(
            value = "may throw crash report",
            extra = {"return new instance when absent"})
    @RedirectType(ListTag)
    AbstractList getList(Object nbt, String key, int type);

    @Nonnull
    default AbstractList getOrNewList(Object nbt, String key, int type) {
        var list = getList(nbt, key, type);
        put(nbt, key, list);
        return list;
    }

    // @MethodTarget
    // remove Target because it is shit
    default boolean getBoolean(Object nbt, String key) {
        return getByte(nbt, key) != 0;
    }

    @MethodTarget
    public void remove(Object nbt, String key);

    @MethodTarget
    public boolean isEmpty(Object nbt);

    @MethodTarget
    @Contract("_,_ -> param1")
    public Object merge(Object nbt, @RedirectType(CompoundTag) Object otherNbt);

    @FieldTarget
    @RedirectType("Ljava/util/Map;")
    Map<String, ?> tagsGetter(Object nbt);

    //    @MethodTarget
    //    Map<String,?> entries(Object nbt);
    // below 1.20.6 ignore failed
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    default Object shallowCopy(Object nbt) {
        return newComp(tagsGetter(nbt));
    }

    default void clear(Object nbt) {
        tagsGetter(nbt).clear();
    }

    //    default Object getOrNewCompound(Object nbt, String key){
    //        Object obj = getCompound(nbt, key);
    //        if(obj != null){
    //            return obj;
    //        }else {
    //            obj = newComp();
    //            put(nbt, key, obj);
    //            return obj;
    //        }
    //    }
    // removed
    //    @MethodTarget
    //    @RedirectName("entries")
    //    Map<String, ?> toUnmodified(Object nbt);
}
