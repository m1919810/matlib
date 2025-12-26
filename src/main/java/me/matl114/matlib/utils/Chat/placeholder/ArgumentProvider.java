package me.matl114.matlib.utils.chat.placeholder;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface ArgumentProvider {
    String getAsString(String arg);

    default Style getAsStyle(String arg) {
        return null;
    }

    default Consumer<Style.Builder> getAsDecorator(String arg) {
        return null;
    }

    default ItemStack getAsItemStack(String arg) {
        return null;
    }

    default Entity getAsEntity(String arg) {
        return null;
    }

    default Collection<Entity> getAsEntityGroup(String args) {
        return null;
    }

    default Component getAsComponent(String arg) {
        String val = getAsString(arg);
        return val == null ? null : Component.text(val);
    }

    static ArgumentProvider identity() {
        return (str) -> str;
    }

    static <T> ArgumentProvider of(Map<String, T> val) {
        return (str) -> String.valueOf(val.get(str));
    }
}
