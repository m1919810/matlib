package me.matl114.matlib.core.nms.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public enum TranslateType {
    ITEM_STACK(ItemStack.class),
    CHAT_COMPONENT(Component.class),
    ALL_TYPE(null);
    public Class<?> type;

    TranslateType(Class<?> type) {}
}
