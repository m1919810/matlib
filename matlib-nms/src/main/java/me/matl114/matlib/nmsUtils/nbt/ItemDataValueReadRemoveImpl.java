package me.matl114.matlib.nmsUtils.nbt;

import java.util.Objects;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelperDefault;
import me.matl114.matlib.nmsMirror.nbt.TagEnum;
import me.matl114.matlib.utils.serialization.TypeOps;


class ItemDataValueReadRemoveImpl implements ItemDataValue {
    final String[] path;

    ItemDataValueReadRemoveImpl(String path) {
        Objects.requireNonNull(path);
        this.path = path.split("\\.");
        if (HELPER == null) {
            HELPER = (ItemStackHelperDefault) NMSItem.ITEMSTACK;
        }
    }

    protected static ItemStackHelperDefault HELPER;

    @Override
    public String getPath() {
        return String.join(".", path);
    }

    @Override
    public Object getPathData() {
        return path;
    }

    @Override
    public Object getPrimitive() {
        throw new UnsupportedOperationException("No primitive record");
    }

    @Override
    public Object getSafe() {
        throw new UnsupportedOperationException("No primitive record");
    }

    @Override
    public Object getUnsafe() {
        throw new UnsupportedOperationException("No primitive record");
    }

    @Override
    public void applyToStack(Object itemStack) {
        throw new UnsupportedOperationException("ReadRemove");
    }

    @Override
    public void mergeToStack(Object itemStack) {
        throw new UnsupportedOperationException("ReadRemove");
    }

    @Override
    public void listAppendToStack(Object itemStack, int index) {
        throw new UnsupportedOperationException("ReadRemove");
    }

    protected Object getTagBefore(Object nbt) {
        int i = 0;
        for (; i < this.path.length - 1; ++i) {
            if (nbt != null
                    && NMSCore.COMPOUND_TAG.isCompound(nbt)
                    && NMSCore.COMPOUND_TAG.contains(nbt, this.path[i], TagEnum.TAG_COMPOUND)) {
                nbt = NMSCore.COMPOUND_TAG.getCompound(nbt, this.path[i]);
            } else {
                return null;
                // break;
            }
        }
        return nbt;
    }

    protected Object getOrCreateTagBefore(Object nbt) {
        int i = 0;
        for (; i < this.path.length - 1; ++i) {
            nbt = NMSCore.COMPOUND_TAG.getOrNewCompound(nbt, this.path[i]);
        }
        return nbt;
    }

    @Override
    public void removeFromStack(Object itemStack) {
        Object nbt = HELPER.getCustomTag(itemStack);
        nbt = getTagBefore(nbt);
        if (nbt != null && NMSCore.TAGS.isCompound(nbt)) {
            NMSCore.COMPOUND_TAG.remove(nbt, this.path[this.path.length - 1]);
        }
    }

    @Override
    public Object getFromStack(Object itemStack) {
        Object nbt = HELPER.getCustomTag(itemStack);
        if (nbt == null) return null;
        nbt = getTagBefore(nbt);
        return nbt == null
                ? null
                : (NMSCore.COMPOUND_TAG.contains(nbt, this.path[this.path.length - 1])
                        ? Env.NBT_OP.convertTo(TypeOps.I, nbt)
                        : null);
    }

    @Override
    public boolean compareWithStack(Object itemStack) {
        throw new UnsupportedOperationException("No primitive record");
    }

    @Override
    public boolean isWritable() {
        return false;
    }
}
