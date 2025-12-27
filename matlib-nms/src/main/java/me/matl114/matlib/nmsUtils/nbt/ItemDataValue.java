package me.matl114.matlib.nmsUtils.nbt;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.version.Version;

public interface ItemDataValue {
    boolean isVersionAtLeastV1_20_R4 = Version.getVersionInstance().isAtLeast(Version.v1_20_R4);

    @Note(
            value =
                    "In version higher than v1_20_R4, we don't support multiple path anyMore, path must be one of DataComponentTypes",
            extra = {"If you want to set custom NBT in higer version, use ItemStackHelper.getCustomedNbtView"})
    public static ItemDataValue primitive(String path, Object primitive) {
        if (isVersionAtLeastV1_20_R4) {
            return new ItemDataValueImpl_1_20_R4(path, primitive);
        } else {
            return new ItemDataValueImpl(path, primitive);
        }
    }

    @Note(
            value =
                    "This method returns a ready only ItemDataValue where you can only use get and remove, it may takes lower costs")
    public static ItemDataValue modifier(String path) {
        if (isVersionAtLeastV1_20_R4) {
            return new ItemDataValueReadRemoveImpl_1_20_R4(path);
        } else {
            return new ItemDataValueReadRemoveImpl(path);
        }
    }

    @Note("this Const keeps primitive Object, who jb cares, nobody")
    public static ItemDataValue fromPrimitiveToConst(String path, Object primitive) {
        if (isVersionAtLeastV1_20_R4) {
            return new ItemDataValueImpl_1_20_R4(path, primitive);
        } else {
            return new ItemDataValueImpl.Const(path, primitive);
        }
    }

    public String getPath();

    public Object getPathData();

    public Object getPrimitive();

    public Object getSafe();

    public Object getUnsafe();

    @Note("It overrides the old value")
    public void applyToStack(Object itemStack);

    @Note("It merges if the old value and this value are both Map-Like")
    public void mergeToStack(Object itemStack);

    @Note("it appends at index if old value are List-Like")
    public void listAppendToStack(Object itemStack, int index);

    @Note("It removes the path")
    public void removeFromStack(Object itemStack);

    @Note("it gets primitive values from the path")
    public Object getFromStack(Object itemStack);

    @Note("it compares current value with getUnsafe")
    public boolean compareWithStack(Object itemStack);

    public boolean isWritable();
}
