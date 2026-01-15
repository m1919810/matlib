package me.matl114.matlib.nmsUtils.inventory;

import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public interface NMSItemHolder {
    public Object getNMS();

    default boolean hasMeta() {
        return NMSItem.ITEMSTACK.hasExtraData(getNMS());
    }

    default boolean isAir() {
        return NMSItem.ITEMSTACK.isEmpty(getNMS());
    }

    default ItemStack toBukkit() {
        return ItemUtils.asBukkitCopy(getNMS());
    }

    <T extends NMSItemHolder> T copy();
}
