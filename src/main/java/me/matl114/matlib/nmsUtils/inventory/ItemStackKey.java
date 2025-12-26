package me.matl114.matlib.nmsUtils.inventory;

import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.utils.itemCache.ItemStackHolder;
import org.bukkit.inventory.ItemStack;

public class ItemStackKey extends ItemStackHolder implements ItemHashCache {
    Integer hashCode;
    Integer hashCodeNoLore;

    protected ItemStackKey(ItemStack cis) {
        super(cis);
    }

    @Note("this method should be only used for creating key for map")
    public static ItemStackKey of(ItemStack item) {
        if (item == null) return null;
        ItemStack cis = ItemUtils.cleanStack(item);
        return new ItemStackKey(cis);
    }

    public int getHashCode() {
        if (hashCode == null) {
            hashCode = ItemUtils.itemStackHashCode(this.handle);
        }
        return hashCode;
    }

    public int getHashCodeNoLore() {
        if (hashCodeNoLore == null) {
            hashCodeNoLore = ItemUtils.itemStackHashCodeWithoutLore(this.handle);
        }
        return hashCodeNoLore;
    }

    @Override
    public ItemStack getCraftStack() {
        return this.handle;
    }
}
