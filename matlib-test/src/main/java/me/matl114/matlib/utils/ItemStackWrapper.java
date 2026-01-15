package me.matl114.matlib.utils;

import static me.matl114.matlib.nmsUtils.DynamicOpUtils.*;
import static me.matl114.matlib.utils.serialization.CodecUtils.*;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.designs.serialize.JsonCodec;
import me.matl114.matlib.nmsMirror.impl.CodecEnum;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.inventory.NMSItemHolder;
import org.bukkit.inventory.ItemStack;

public class ItemStackWrapper implements NMSItemHolder {
    public static final ItemStackWrapper EMPTY = new ItemStackWrapper(EmptyEnum.EMPTY_ITEMSTACK);
    public static final Codec<ItemStackWrapper> CODEC =
            CodecEnum.ITEMSTACK.xmap(ItemStackWrapper::new, ItemStackWrapper::getNMS);

    public static final JsonCodec<ItemStackWrapper> JSON_CODEC = new JsonCodec<ItemStackWrapper>() {
        public ItemStackWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonNull()) {
                return EMPTY;
            }
            JsonObject jsonMap = json.getAsJsonObject();
            try {
                Object stack = decode(CodecEnum.ITEMSTACK, jsonOp(), jsonMap);
                return NMSItem.ITEMSTACK.isEmpty(stack) ? EMPTY : new ItemStackWrapper(stack);
            } catch (Throwable e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(ItemStackWrapper src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == EMPTY || src.isAir()) {
                return JsonNull.INSTANCE;
            }
            try {
                return encode(CodecEnum.ITEMSTACK, jsonOp(), src.value);
            } catch (Throwable e) {
                throw new JsonParseException(e);
            }
        }
    };

    public Object getNMS() {
        return value;
    }

    public int getCount() {
        return NMSItem.ITEMSTACK.getCount(this.value);
    }

    public void setCount(int val) {
        NMSItem.ITEMSTACK.setCount(this.value, val);
    }

    public boolean isAir() {
        return NMSItem.ITEMSTACK.isEmpty(this.value);
    }

    @Override
    public <T extends NMSItemHolder> T copy() {
        return (T) new ItemStackWrapper(NMSItem.ITEMSTACK.copy(this.value, true));
    }

    @Nonnull
    final Object value;

    public ItemStackWrapper(@Nonnull Object value) {
        this.value = value;
    }

    public ItemStackCounter withAmount(int count) {
        return new ItemStackCounter(this.value, count);
    }

    public static ItemStackWrapper ofNMS(Object val) {
        return val == EmptyEnum.EMPTY_ITEMSTACK ? EMPTY : new ItemStackWrapper(val);
    }

    public static ItemStackWrapper of(ItemStack val) {
        return val == null ? EMPTY : new ItemStackWrapper(val);
    }

    public ItemStackWrapper(@Nullable ItemStack stack) {
        this.value = stack == null ? EmptyEnum.EMPTY_ITEMSTACK : ItemUtils.unwrapHandle(stack);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStackWrapper wrapper
                && NMSItem.ITEMSTACK.matchItem(wrapper.value, this.value, true, true);
    }

    @Override
    public int hashCode() {
        return NMSItem.ITEMSTACK.customHashcode(this.value);
    }
}
