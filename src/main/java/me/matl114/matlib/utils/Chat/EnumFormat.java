package me.matl114.matlib.utils.chat;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;

public enum EnumFormat {
    BLACK("BLACK", '0', 0, 0),
    DARK_BLUE("DARK_BLUE", '1', 1, 170),
    DARK_GREEN("DARK_GREEN", '2', 2, 43520),
    DARK_AQUA("DARK_AQUA", '3', 3, 43690),
    DARK_RED("DARK_RED", '4', 4, 11141120),
    DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
    GOLD("GOLD", '6', 6, 16755200),
    GRAY("GRAY", '7', 7, 11184810),
    DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
    BLUE("BLUE", '9', 9, 5592575),
    GREEN("GREEN", 'a', 10, 5635925),
    AQUA("AQUA", 'b', 11, 5636095),
    RED("RED", 'c', 12, 16733525),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
    YELLOW("YELLOW", 'e', 14, 16777045),
    WHITE("WHITE", 'f', 15, 16777215),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1, (Integer) null);

    public static final char PREFIX_CODE = '§';

    private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private final String name;
    public final char code;
    private final boolean isFormat;
    private final String toString;
    private final int id;

    @Nullable private final Integer color;

    private static final Map<String, EnumFormat> FORMATTING_BY_NAME = (Map) Arrays.stream(values())
            .collect(Collectors.toMap(
                    (var0) -> {
                        return cleanName(var0.name);
                    },
                    (var0) -> {
                        return var0;
                    }));
    private static final Char2ObjectMap<EnumFormat> formatMap;

    static {
        Char2ObjectMap<EnumFormat> re = new Char2ObjectOpenHashMap<>();
        for (EnumFormat format : EnumFormat.values()) {
            re.put(Character.toLowerCase(format.toString().charAt(1)), format);
            re.put(Character.toUpperCase(format.toString().charAt(1)), format);
        }
        formatMap = Char2ObjectMaps.unmodifiable(re);
    }

    public static EnumFormat getColor(ChatColor color) {
        return formatMap.get(color.getChar());
    }

    public static EnumFormat getFormat(char a) {
        return formatMap.get(a);
    }

    public static ChatColor getColor(EnumFormat format) {
        return ChatColor.getByChar(format.code);
    }

    private static String cleanName(String var0) {
        return var0.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private EnumFormat(final String var2, final char var3, final int var4, final Integer var5) {
        this(var2, var3, false, var4, var5);
    }

    private EnumFormat(final String var2, final char var3, final boolean var4) {
        this(var2, var3, var4, -1, (Integer) null);
    }

    private EnumFormat(final String var2, final char var3, final boolean var4, final int var5, final Integer var6) {
        this.name = var2;
        this.code = var3;
        this.isFormat = var4;
        this.id = var5;
        this.color = var6;
        this.toString = "§" + String.valueOf(var3);
    }

    public char getChar() {
        return this.code;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    @Nullable public Integer getColor() {
        return this.color;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    @Nullable @Contract("!null->!null;_->_")
    public static String stripFormatting(@Nullable String var0) {
        return var0 == null ? null : STRIP_FORMATTING_PATTERN.matcher(var0).replaceAll("");
    }

    @Nullable public static EnumFormat getByName(@Nullable String var0) {
        return var0 == null ? null : (EnumFormat) FORMATTING_BY_NAME.get(cleanName(var0));
    }

    @Nullable public static EnumFormat getById(int var0) {
        if (var0 < 0) {
            return RESET;
        } else {
            EnumFormat[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                EnumFormat var4 = var1[var3];
                if (var4.getId() == var0) {
                    return var4;
                }
            }

            return null;
        }
    }

    @Nullable public static EnumFormat getByCode(char var0) {
        char var1 = Character.toLowerCase(var0);
        EnumFormat[] var2 = values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            EnumFormat var5 = var2[var4];
            if (var5.code == var1) {
                return var5;
            }
        }

        return null;
    }

    public static Collection<String> getNames(boolean var0, boolean var1) {
        List<String> var2 = Lists.newArrayList();
        EnumFormat[] var3 = values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            EnumFormat var6 = var3[var5];
            if ((!var6.isColor() || var0) && (!var6.isFormat() || var1)) {
                var2.add(var6.getName());
            }
        }

        return var2;
    }

    public String getSerializedName() {
        return this.getName();
    }

    @Nullable public TextColor toAdventure() {
        return color == null ? null : NamedTextColor.namedColor(color);
    }
}
