package me.matl114.matlib.nmsUtils.nbt;

import java.util.Objects;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.nmsUtils.v1_20_R4.DataComponentUtils;

public class ItemDataValueReadRemoveImpl_1_20_R4 implements ItemDataValue {
    final Object componentType;

    public ItemDataValueReadRemoveImpl_1_20_R4(String path) {
        Objects.requireNonNull(path);
        if (HELPER == null) {
            HELPER = (ItemStackHelper_1_20_R4) NMSItem.ITEMSTACK;
        }
        this.componentType = DataComponentUtils.getDataType(path);
    }

    protected static ItemStackHelper_1_20_R4 HELPER;

    @Override
    public String getPath() {
        return componentType.toString();
    }

    @Override
    public Object getPathData() {
        return componentType;
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

    @Override
    public void removeFromStack(Object itemStack) {
        HELPER.removeFromPatch(itemStack, this.componentType);
    }

    @Override
    public Object getFromStack(Object itemStack) {
        return HELPER.saveElementInPath0(itemStack, this.componentType);
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
