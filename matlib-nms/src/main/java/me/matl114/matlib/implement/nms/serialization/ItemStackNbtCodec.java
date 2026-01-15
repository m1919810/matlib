package me.matl114.matlib.implement.nms.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.implement.serialization.ItemStackCodec;
import me.matl114.matlib.nmsMirror.impl.CodecEnum;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.DynamicOpUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.utils.serialization.CodecUtils;
import me.matl114.matlib.utils.serialization.ExtraCodecs;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemStackNbtCodec {
    private static DataResult<ItemStack> readItemStackFromNbtString(String string) {
        try {
            if (string == null || string.equals("null")) {
                return DataHelper.A.I.success(new ItemStack(Material.AIR));
            }
            Object nbt = NMSCore.TAGS.parseNbt(string);
            Object nms = CodecUtils.decode(CodecEnum.ITEMSTACK, DynamicOpUtils.nbtOp(), nbt);
            return DataHelper.A.I.success(ItemUtils.asCraftMirror(nms));
        } catch (Throwable e) {
            return DataHelper.A.I.error(() -> "Unable to deserialize item: " + e.getMessage());
        }
    }

    private static String writeItemStackToNbtString(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return "null";
        }
        Object nms = ItemUtils.unwrapHandle(stack);
        Object nbt = NMSItem.ITEMSTACK.save(nms);
        return nbt.toString();
    }

    Codec<ItemStack> NBT_STR = Codec.STRING.<ItemStack>comapFlatMap(
            ItemStackNbtCodec::readItemStackFromNbtString, ItemStackNbtCodec::writeItemStackToNbtString);
    Codec<ItemStack> NBT_MAP = ExtraCodecs.optionalEmptyMapElseGet(CodecEnum.ITEMSTACK, EmptyEnum.EMPTY_ITEMSTACK)
            .xmap(ItemUtils::asCraftMirror, ItemUtils::unwrapNullable);
    Codec<ItemStack> NBT = Codec.withAlternative(NBT_MAP, NBT_STR);

    public static void init() {
        // call the static things
    }

    public static void _init() {
        ItemStackCodec.CODEC_REGISTRY.registerThis("nbt", NBT);
    }

    static Holder<Void> INIT_TASKS = Holder.empty().thenRun(ItemStackNbtCodec::_init);
}
