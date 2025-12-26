package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

import java.util.AbstractList;
import java.util.Objects;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsUtils.serialize.TypeOps;

class ItemDataValueImpl extends ItemDataValueReadRemoveImpl {

    final Object nbtTag;

    private static Object solvePrimitiveToNbt(Object primitive) {
        return TypeOps.I.convertTo(Env.NBT_OP, primitive);
    }

    public ItemDataValueImpl(String path, Object primitive) {
        super(path);
        // like PersistentDataValue.slimefun:shit_id.
        // for efficiency, we just disable all the ""

        this.nbtTag = primitive == null ? null : solvePrimitiveToNbt(primitive);
    }

    @Override
    public Object getPrimitive() {
        throw new UnsupportedOperationException("No primitive record");
    }

    @Override
    public Object getSafe() {
        return this.nbtTag == null ? null : TAGS.copy(this.nbtTag);
    }

    @Override
    public Object getUnsafe() {
        return this.nbtTag;
    }

    @Override
    public void applyToStack(Object itemStack) {
        if (this.nbtTag == null || (TAGS.isCompound(this.nbtTag) && COMPOUND_TAG.isEmpty(this.nbtTag))) {
            // remove mode;
            removeFromStack(itemStack);
        } else {
            Object nbt0 = HELPER.getOrCreateCustomTag(itemStack);
            Object nbt = getOrCreateTagBefore(nbt0);
            COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], getSafe());
            // trigger enchantment sorting
            HELPER.setTag(itemStack, nbt0);
        }
    }

    @Override
    public void mergeToStack(Object itemStack) {
        if (this.getUnsafe() == null) return;
        // map like
        if (TAGS.isCompound(this.nbtTag)) {
            Object nbt0 = HELPER.getOrCreateCustomTag(itemStack);
            Object nbt = getOrCreateTagBefore(nbt0);
            if (nbt == null) {
                COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], getSafe());
                // trigger enchantment sorting
            } else if (TAGS.isCompound(nbt)) {
                Object toMerge = COMPOUND_TAG.get(nbt, this.path[this.path.length - 1]);
                if (toMerge == null) {
                    // 刚创建的
                    COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], getSafe());
                } else if (TAGS.isCompound(toMerge)) {
                    // merge function will copy
                    COMPOUND_TAG.merge(toMerge, this.getUnsafe());
                } else {
                    throw new UnsupportedOperationException("Can not merge CompoundTag to Tag-Element " + toMerge);
                }
            }
            // trigger nbt Update
            HELPER.setTag(itemStack, nbt0);

            // list like
        } else throw new UnsupportedOperationException("This value is not a Map-Like, you can not execute merge");
    }

    public void listAppendToStack(Object itemStack, int index) {
        if (this.getUnsafe() == null) return;
        Object nbt0 = HELPER.getOrCreateCustomTag(itemStack);
        Object nbt = getOrCreateTagBefore(nbt0);
        if (nbt == null) {
            if (this.nbtTag instanceof AbstractList list) {
                COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], getSafe());
            } else {
                AbstractList listTag = TAGS.listTag();
                listTag.add(getSafe());
                COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], listTag);
            }
            // trigger enchantment sorting
        } else if (TAGS.isCompound(nbt)) {
            Object toMerge = COMPOUND_TAG.get(nbt, this.path[this.path.length - 1]);
            if (toMerge == null) {
                // 刚创建的
                if (this.nbtTag instanceof AbstractList list) {
                    COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], getSafe());
                } else {
                    AbstractList listTag = TAGS.listTag();
                    listTag.add(getSafe());
                    COMPOUND_TAG.put(nbt, this.path[this.path.length - 1], listTag);
                }
            } else if (toMerge instanceof AbstractList listMerge) {
                // merge function will copy
                if (this.nbtTag instanceof AbstractList list) {
                    if (index >= 0 && index < listMerge.size()) {
                        int size2 = list.size();
                        for (int i = 0; i < size2; ++i) {
                            listMerge.add(i + index, TAGS.copy(list.get(i)));
                        }
                    } else {
                        for (var val : list) {
                            listMerge.add(TAGS.copy(val));
                        }
                    }
                } else {
                    if (index > 0 && index < listMerge.size()) {
                        listMerge.add(index, getUnsafe());
                    } else {
                        listMerge.add(getUnsafe());
                    }
                }
            } else {
                throw new UnsupportedOperationException("Can not append to Tag-Element " + toMerge);
            }
        }
        HELPER.setTag(itemStack, nbt0);
    }

    public boolean compareWithStack(Object itemStack) {
        Object nbt0 = HELPER.getCustomTag(itemStack);
        if (nbt0 == null) return this.getUnsafe() == null;
        Object nbt = getTagBefore(nbt0);
        if (nbt == null) return this.getUnsafe() == null;
        if (COMPOUND_TAG.contains(nbt, this.path[this.path.length - 1])) {
            return Objects.equals(this.getUnsafe(), COMPOUND_TAG.get(nbt, this.path[this.path.length - 1]));
        } else return this.getUnsafe() == null;
    }

    public static class Const extends ItemDataValueImpl {
        final Object primitive;

        public Const(String path, Object primitive) {
            super(path, primitive);
            this.primitive = primitive;
        }

        public Object getPrimitive() {
            return primitive;
        }
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
