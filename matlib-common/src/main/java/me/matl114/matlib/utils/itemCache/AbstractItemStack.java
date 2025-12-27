package me.matl114.matlib.utils.itemCache;

import org.bukkit.inventory.ItemStack;

public interface AbstractItemStack {
    // under this interface should itemstack clone to get an instance before they do sth
    public <T extends ItemStack> T copy();
    // can stack with matchItemCore method,
    // when return true,you should register Matcher to CraftUtil
    boolean canStackWithMatch();
    // can use setAmount to set item amount
    default boolean canSetAmount() {
        return true;
    }
    // if not, use this method to set amount
    default void setRealAmount(int amount) {}

    // 纯虚物品,不会以任何方式和玩家/输出槽交互
    default boolean isVirtual() {
        return false;
    }
}
