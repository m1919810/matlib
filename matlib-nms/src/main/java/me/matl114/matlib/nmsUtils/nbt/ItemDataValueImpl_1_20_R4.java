package me.matl114.matlib.nmsUtils.nbt;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentEnum;

import me.matl114.matlib.nmsUtils.DynamicOpUtils;
import me.matl114.matlib.nmsUtils.v1_20_R4.DataComponentUtils;
import me.matl114.matlib.utils.serialization.CodecUtils;

class ItemDataValueImpl_1_20_R4 extends ItemDataValueReadRemoveImpl_1_20_R4 {
    final Object primitive;
    final Object componentValue;
    final boolean shouldCopy;

    public ItemDataValueImpl_1_20_R4(String path, Object value) {
        super(path);
        this.primitive = value;
        if (value == null) {
            this.componentValue = null;
        } else {
            Codec<Object> codec = DataComponentUtils.getTypeCodec(this.componentType);
            this.componentValue = CodecUtils.decode(codec, DynamicOpUtils.primOp(), value);
        }

        this.shouldCopy = DataComponentEnum.COMPONENT_COPY_POLICY.containsKey(this.componentType);
    }

    @Override
    public Object getPrimitive() {
        return this.primitive;
    }

    @Override
    public Object getSafe() {
        if (shouldCopy) {
            return this.componentValue == null
                    ? null
                    : DataComponentEnum.COMPONENT_COPY_POLICY.get(componentType).apply(componentValue);
        } else {
            return componentValue;
        }
    }

    @Override
    public Object getUnsafe() {
        return this.componentValue;
    }

    @Override
    public void applyToStack(Object itemStack) {
        if (this.componentValue == null) {
            removeFromStack(itemStack);
        } else {
            HELPER.setDataComponentValue(itemStack, this.componentType, getSafe());
        }
    }

    @Override
    public void mergeToStack(Object itemStack) {
        if (this.primitive instanceof Map pm) {
            Object val = getFromStack(itemStack);
            if (val instanceof Map value) {
                CollectionUtils.mergeMapNoCopy(value, pm);
            } else throw new UnsupportedOperationException("Can not merge Map to " + val);
            HELPER.replaceElementInPath0(itemStack, this.componentType, val);
        } else {
            throw new UnsupportedOperationException("This value is not a Map-Like, you can not execute merge");
        }
    }

    @Override
    public void listAppendToStack(Object itemStack, int index) {
        Object val = getFromStack(itemStack);
        if (val instanceof List list0) {
            if (this.primitive instanceof List list1) {
                if (index >= 0 && index < list0.size()) list0.addAll(index, list1);
                else list0.addAll(list1);
            } else {
                if (index >= 0 && index < list0.size()) list0.add(index, this.primitive);
                else list0.add(this.primitive);
            }
            HELPER.replaceElementInPath0(itemStack, this.componentType, val);
        } else {
            throw new UnsupportedOperationException("Can not append to " + val);
        }
    }

    public boolean compareWithStack(Object itemStack) {
        return Objects.equals(this.getUnsafe(), HELPER.getFromPatch(itemStack, this.componentType));
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
