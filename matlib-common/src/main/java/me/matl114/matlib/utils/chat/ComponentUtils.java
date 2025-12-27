package me.matl114.matlib.utils.chat;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ComponentUtils {
    private static final ThreadLocal<ItemMeta> DEFAULT_META =
            ThreadLocal.withInitial(() -> new ItemStack(Material.STONE).getItemMeta());
    public static final Component EMPTY = Component.empty();

    public static final String EMPTY_STRING = "";

    public static Component fromLegacyString(@Nonnull String value) {
        if (value.isEmpty()) {
            return EMPTY;
        }
        ItemMeta meta = DEFAULT_META.get();
        meta.setDisplayName(value);
        return meta.displayName();
    }

    public static String toLegacyString(@Nonnull Component component) {
        ItemMeta meta = DEFAULT_META.get();
        meta.displayName(component);
        String name = meta.getDisplayName();
        return name == null ? "" : name;
    }

    private static final VarHandle loreHandle = Holder.of(DEFAULT_META.get())
            .thenApply(Object::getClass)
            .thenApply(ReflectUtils::getVarHandlePrivate, "lore")
            .thenApply(Objects::requireNonNull)
            .get();

    public static <T> void addToLore(ItemMeta meta, Component... adds) {
        if (adds.length == 0) {
            return;
        }
        ItemMeta meta1 = DEFAULT_META.get();
        List<T> lore1 = (List<T>) loreHandle.get(meta);
        meta1.lore(Arrays.asList(adds));
        List<T> lore2 = (List<T>) loreHandle.get(meta1);
        if (lore1 == null) {
            lore1 = new ArrayList<>(lore2);
            loreHandle.set(meta, lore1);
        } else {
            lore1.addAll(lore2);
        }
        meta1.lore(null);
    }

    public static void removeLoreLast(ItemMeta meta, int removalSize) {
        if (removalSize <= 0) {
            return;
        }
        List<?> lore1 = (List<?>) loreHandle.get(meta);
        if (lore1.size() <= removalSize) {
            // remove lore nbt
            loreHandle.set(meta, (List<?>) null);
        } else {
            for (int i = 0; i < removalSize; ++i) {
                lore1.removeLast();
            }
        }
    }

    public static Style parseStyleFromString(String value) {
        Style.Builder builder = Style.style();
        applyStyleBuilder(value).accept(builder);
        return builder.build();
    }

    public static Consumer<Style.Builder> applyStyleBuilder(String value) {
        int len = value.length();
        if (len == 2) {
            EnumFormat format = EnumFormat.getFormat(value.charAt(1));
            if (format.isFormat() && format != EnumFormat.RESET) {
                switch (format) {
                    case BOLD:
                        return (builder) -> {
                            builder.decoration(TextDecoration.BOLD, true);
                        };
                    case ITALIC:
                        return (builder) -> {
                            builder.decoration(TextDecoration.ITALIC, true);
                        };

                    case STRIKETHROUGH:
                        return (builder) -> {
                            builder.decoration(TextDecoration.STRIKETHROUGH, true);
                        };

                    case UNDERLINE:
                        return (builder) -> {
                            builder.decoration(TextDecoration.UNDERLINED, true);
                        };

                    case OBFUSCATED:
                        return (builder) -> {
                            builder.decoration(TextDecoration.OBFUSCATED, true);
                        };

                    default:
                        throw new RuntimeException("Unexpected message format name: " + value);
                }
            } else { // Color resets formatting
                return (builder) -> {
                    builder.merge(Style.empty().color(format.toAdventure()), Style.Merge.Strategy.ALWAYS);
                };
            }
        } else {
            String hex = value.replaceAll("[&§#x]", "");
            if (hex.length() == 6) {
                try {
                    int i = Integer.parseInt(hex, 16);
                    return (builder) -> {
                        builder.color(TextColor.color(i));
                    };
                } catch (Throwable e) {
                    throw new RuntimeException("Unexpected color value: " + value + ", to hex: " + hex);
                }
            } else {
                throw new RuntimeException("Unexpected color format: " + value);
            }
        }
    }
}
