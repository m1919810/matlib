package me.matl114.matlib.utils;

import org.bukkit.NamespacedKey;

public class KeyUtils {
    public static NamespacedKey fromString(String string) {
        int index = string.indexOf(":");
        if (index == -1) {
            return new NamespacedKey("minecraft", string);
        } else {
            return new NamespacedKey(string.substring(0, index), string.substring(index + 1));
        }
    }

    public static NamespacedKey minecraft(String string) {
        return NamespacedKey.minecraft(string);
    }
}
