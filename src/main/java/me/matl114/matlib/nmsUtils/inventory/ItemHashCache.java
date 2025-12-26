package me.matl114.matlib.nmsUtils.inventory;

import org.bukkit.inventory.ItemStack;

public interface ItemHashCache {
    int getHashCode();

    int getHashCodeNoLore();

    ItemStack getCraftStack();
}
