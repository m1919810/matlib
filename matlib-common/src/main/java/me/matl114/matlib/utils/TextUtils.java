package me.matl114.matlib.utils;

import com.google.common.base.Preconditions;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TextUtils {
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###,###,###.#");
    public static final String PLACEHOLDER = "†";
    /**
     * Formats a double value with comma separators for thousands.
     *
     * <p>This method uses a predefined DecimalFormat to format numbers with
     * comma separators and up to one decimal place.
     *
     * @param s The double value to format
     * @return A formatted string representation of the number
     */
    public static String formatDouble(double s) {
        return FORMAT.format(s);
    }

    public static final String C = "§";

    /**
     * Adds a gray color prefix to a string for description formatting.
     *
     * <p>This method prepends the gray color code (§7) to the input string,
     * commonly used for descriptive text in Minecraft chat.
     *
     * @param str The string to format as a description
     * @return The string with gray color formatting
     */
    public static String description(String str) {
        return "§7" + str;
    }

    public static final String[] COLOR_MAP =
            new String[] {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§A", "§B", "§C", "§D", "§E", "§F"
            };
    public static final int RGB_MASK = 16777215;
    public static final int[] RGB_MASKS = new int[] {15, 15 << 4, 15 << 8, 15 << 12, 15 << 16, 15 << 20};

    /**
     * Converts an RGB integer to a Minecraft hex color string.
     *
     * <p>This method converts a 24-bit RGB integer into a Minecraft hex color
     * string format (§x§R§R§G§G§B§B). The RGB value is masked to ensure only
     * the lower 24 bits are used.
     *
     * @param rgb The RGB integer value (24-bit)
     * @return A Minecraft hex color string
     */
    public static String toHexString(int rgb) {
        rgb = rgb & RGB_MASK;
        StringBuilder builder = new StringBuilder("§x");
        for (int i = 0; i < 6; i++) {
            int value = (RGB_MASKS[i] & rgb) >> (i * 4);
            builder.append(COLOR_MAP[value]);
        }
        return builder.toString();
    }
    /**
     * Converts a decimal number to its hexadecimal character representation.
     *
     * <p>This method converts a single decimal digit (0-15) to its corresponding
     * hexadecimal character. Values 0-9 return the digit as a string, while
     * values 10-15 return the letters A-F.
     *
     * @param c The decimal value to convert (0-15)
     * @return The hexadecimal character as a string
     */
    public static String toHexChar(int c) {
        if (c < 10 && c >= 0) {
            return String.valueOf(c);
        }
        return switch (c) {
            case 10 -> "A";
            case 11 -> "B";
            case 12 -> "C";
            case 13 -> "D";
            case 14 -> "E";
            case 15 -> "F";
            default -> "0";
        };
    }
    /**
     * Converts a decimal number to a Minecraft color code.
     *
     * <p>This method converts a decimal value (0-15) to its corresponding
     * Minecraft color code using the predefined COLOR_MAP array.
     *
     * @param c The decimal value to convert (0-15)
     * @return A Minecraft color code string
     */
    public static String toHexSingleColor(int c) {
        // return C+toHexChar(c);
        return COLOR_MAP[c];
    }

    /**
     * Converts a 6-character hex string to Minecraft legacy hex format.
     *
     * <p>This method converts a 6-character hexadecimal string (e.g., "FF0000")
     * to the Minecraft legacy hex color format (§x§F§F§0§0§0§0).
     *
     * @param rgb The 6-character hexadecimal string
     * @return A Minecraft legacy hex color string
     * @throws IllegalArgumentException if the input string is not exactly 6 characters long
     */
    public static String hexToLegacyHex(String rgb) throws IllegalArgumentException {
        if (rgb.length() != 6) {
            throw new IllegalArgumentException("Invalid RGB String");
        }

        StringBuilder builder = new StringBuilder("§x");
        for (int i = 0; i < 6; i++) {
            builder.append("§").append(Character.toUpperCase(rgb.charAt(i)));
        }
        return builder.toString();
    }
    /**
     * Converts a hexadecimal character to its decimal value.
     *
     * <p>This method converts a single hexadecimal character (0-9, A-F, a-f)
     * to its corresponding decimal value. The conversion is case-insensitive.
     *
     * @param c The hexadecimal character to convert
     * @return The decimal value (0-15), or 0 for invalid characters
     */
    public static int hexCharToDecimal(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        switch (c) {
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
            default:
                return 0;
        }
    }
    /**
     * Converts a 6-character hex string to an RGB integer.
     *
     * <p>This method parses a 6-character hexadecimal string (e.g., "FF0000")
     * and converts it to a 24-bit RGB integer. The result is masked to ensure
     * only the lower 24 bits are used.
     *
     * @param rgb The 6-character hexadecimal string
     * @return The RGB integer value
     * @throws IllegalArgumentException if the input string is not exactly 6 characters long or contains invalid hex characters
     */
    public static int rgbHexToInt(String rgb) throws IllegalArgumentException {
        if (rgb.length() != 6) {
            throw new IllegalArgumentException("Invalid RGB String");
        }
        try {
            return RGB_MASK & Integer.parseInt(rgb, 16);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Invalid RGB String", e);
        }
    }

    public static final int START_CODE = rgbHexToInt("eb33eb");
    // 15409899;
    public static final int END_CODE = rgbHexToInt("970097");
    /**
     * Applies Logitech-style color formatting to a string.
     *
     * <p>This method applies a predefined purple color (START_CODE) to the
     * beginning of the string using Minecraft hex color format.
     *
     * @param str The string to format
     * @return The string with Logitech-style color formatting
     */
    public static String logitechStyle(String str) {
        return toHexString(START_CODE) + str;
    }
    /**
     * Applies Logitech-style fading color formatting to a string.
     *
     * <p>This method creates a gradient effect by interpolating between START_CODE
     * and END_CODE colors for each character in the string. Each character gets
     * a progressively different color, creating a fading effect.
     *
     * @param str The string to format with fading colors
     * @return The string with fading color formatting applied to each character
     */
    public static String logitechFadingStyle(String str) {
        int len = str.length() - 1;
        if (len <= 0) {
            return toHexString(START_CODE) + str;
        } else {
            int start = START_CODE;
            int end = END_CODE;
            int[] rgbs = new int[9];
            for (int i = 0; i < 3; ++i) {
                rgbs[i] = start % 256;
                rgbs[i + 3] = end % 256;
                rgbs[i + 6] = rgbs[i + 3] - rgbs[i];
                start = start / 256;
                end = end / 256;
            }
            String str2 = "";
            for (int i = 0; i <= len; i++) {
                str2 = str2
                        + toHexString(START_CODE
                                + 65536 * ((rgbs[8] * i) / len)
                                + 256 * ((rgbs[7] * i) / len)
                                + ((rgbs[6] * i) / len))
                        + str.substring(i, i + 1);
            }
            return str2;
        }
    }
    /**
     * Converts an RGB integer value to a {@link java.awt.Color} object.
     *
     * <p>This method extracts the red, green, and blue components from a 32-bit integer
     * where the RGB values are packed as follows:
     * <ul>
     *   <li>Bits 23-16: Red component (0-255)</li>
     *   <li>Bits 15-8: Green component (0-255)</li>
     *   <li>Bits 7-0: Blue component (0-255)</li>
     * </ul>
     *
     * @param color The RGB integer value in the format 0xRRGGBB
     * @return A new {@link java.awt.Color} object representing the RGB values
     * @see java.awt.Color#getRGB()
     */
    public static java.awt.Color rgbIntToColor(int color) {
        // Extract RGB components from the integer
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return new java.awt.Color(red, green, blue);
    }

    /**
     * Performs linear interpolation between two colors in RGB color space.
     *
     * <p>This method creates a smooth transition between two colors by linearly
     * interpolating each RGB component. The result is a color that lies between
     * the two input colors based on the specified percentage.
     *
     * <p>The percentage parameter controls the interpolation:
     * <ul>
     *   <li>0.0 returns color1</li>
     *   <li>1.0 returns color2</li>
     *   <li>0.5 returns the midpoint between the two colors</li>
     * </ul>
     *
     * <p>Values outside the range [0.0, 1.0] are automatically clamped.
     *
     * @param color1 The starting color
     * @param color2 The ending color
     * @param percentage The interpolation factor (0.0 to 1.0)
     * @return A new {@link java.awt.Color} representing the interpolated color
     * @see #colorInHSVLerp(java.awt.Color, java.awt.Color, double)
     */
    public static java.awt.Color colorLerp(java.awt.Color color1, java.awt.Color color2, double percentage) {
        // Clamp percentage between 0 and 1
        percentage = Math.max(0.0, Math.min(1.0, percentage));

        // Linear interpolation for each RGB component
        int red = (int) (color1.getRed() + percentage * (color2.getRed() - color1.getRed()));
        int green = (int) (color1.getGreen() + percentage * (color2.getGreen() - color1.getGreen()));
        int blue = (int) (color1.getBlue() + percentage * (color2.getBlue() - color1.getBlue()));

        return new java.awt.Color(red, green, blue);
    }

    /**
     * Performs interpolation between two colors in HSV (Hue, Saturation, Value) color space.
     *
     * <p>This method creates a smooth transition between two colors by interpolating
     * in HSV space rather than RGB space. HSV interpolation often produces more
     * visually pleasing color transitions, especially when interpolating between
     * colors with different hues.
     *
     * <p>The interpolation process:
     * <ol>
     *   <li>Converts both colors from RGB to HSV</li>
     *   <li>Linearly interpolates each HSV component (hue, saturation, value)</li>
     *   <li>Converts the result back to RGB color space</li>
     * </ol>
     *
     * <p>The percentage parameter controls the interpolation:
     * <ul>
     *   <li>0.0 returns color1</li>
     *   <li>1.0 returns color2</li>
     *   <li>0.5 returns the HSV midpoint between the two colors</li>
     * </ul>
     *
     * <p>Values outside the range [0.0, 1.0] are automatically clamped.
     *
     * @param color1 The starting color
     * @param color2 The ending color
     * @param percentage The interpolation factor (0.0 to 1.0)
     * @return A new {@link java.awt.Color} representing the HSV-interpolated color
     * @see #colorLerp(java.awt.Color, java.awt.Color, double)
     * @see java.awt.Color#RGBtoHSB(int, int, int, float[])
     * @see java.awt.Color#getHSBColor(float, float, float)
     */
    public static java.awt.Color colorInHSVLerp(java.awt.Color color1, java.awt.Color color2, double percentage) {
        // Clamp percentage between 0 and 1
        percentage = MathUtils.clamp(percentage, 0.0d, 1.0d);

        // Convert colors to HSV
        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];
        java.awt.Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), hsv1);
        java.awt.Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), hsv2);

        // Interpolate HSV values
        float hue = hsv1[0] + (hsv2[0] - hsv1[0]) * (float) percentage;
        float saturation = hsv1[1] + (hsv2[1] - hsv1[1]) * (float) percentage;
        float value = hsv1[2] + (hsv2[2] - hsv1[2]) * (float) percentage;

        // Convert back to RGB
        return java.awt.Color.getHSBColor(hue, saturation, value);
    }

    /**
     * Applies gradient coloring to a string using a list of colors.
     *
     * <p>This method creates a gradient effect by interpolating between colors
     * in the provided color list. Each character in the string gets a color
     * that is interpolated between two adjacent colors in the list based on
     * the character's position.
     *
     * <p>The method handles edge cases:
     * <ul>
     *   <li>Empty strings are padded with a space</li>
     *   <li>Single character strings are padded with a space</li>
     *   <li>Placeholder characters (%s) are preserved and restored</li>
     * </ul>
     *
     * @param string0 The string to color
     * @param colorList The list of colors to use for the gradient
     * @return The string with gradient coloring applied
     */
    public static String colorString(@Nonnull String string0, @Nonnull List<Color> colorList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (string0.length() == 0) {
            string0 += " ";
        }
        if (string0.length() == 1) {
            string0 += " ";
        }
        String string = string0.replaceAll("%s", PLACEHOLDER);
        for (int i = 0, length = string.length() - 1; i <= length; i++) {
            double p = (((double) i) / length) * (colorList.size() - 1);
            Color color1 = colorList.get((int) Math.floor(p));
            Color color2 = colorList.get((int) Math.ceil(p));
            int blue = (int) (color1.getBlue() * (1 - p + Math.floor(p)) + color2.getBlue() * (p - Math.floor(p)));
            int green = (int) (color1.getGreen() * (1 - p + Math.floor(p)) + color2.getGreen() * (p - Math.floor(p)));
            int red = (int) (color1.getRed() * (1 - p + Math.floor(p)) + color2.getRed() * (p - Math.floor(p)));
            stringBuilder
                    .append("§x")
                    .append("§")
                    .append(toHexChar(red / 16))
                    .append("§")
                    .append(toHexChar(red % 16))
                    .append("§")
                    .append(toHexChar(green / 16))
                    .append("§")
                    .append(toHexChar(green % 16))
                    .append("§")
                    .append(toHexChar(blue / 16))
                    .append("§")
                    .append(toHexChar(blue % 16));
            stringBuilder.append(string.charAt(i));
        }
        String re = stringBuilder.toString();
        re = re.replaceAll(PLACEHOLDER, "%s");
        return re;
    }
    /**
     * Applies random gradient coloring to a string based on its hash code.
     *
     * <p>This method generates a deterministic but seemingly random color gradient
     * for a string. The colors are generated using the string's hash code as a seed,
     * ensuring that the same string will always produce the same color pattern.
     *
     * <p>The color generation uses a specific algorithm that creates vibrant colors
     * within a certain range, and the number of colors is determined by a probability
     * function based on the string length.
     *
     * @param string The string to color with random gradient
     * @return The string with random gradient coloring applied
     */
    public static String colorRandomString(@Nonnull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 0;
        Random random = new Random(string.hashCode() / 2 + 1919810);
        do {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        } while (1 / r >= random.nextDouble() && r * r <= string.length() + 1);

        return colorString(string, colorList);
    }
    /**
     * Applies pseudorandom gradient coloring to a string using a fixed seed.
     *
     * <p>This method generates a consistent color gradient using a fixed random seed
     * (11451419). Unlike {@link #colorRandomString(String)}, this method will always
     * produce the same color pattern regardless of the input string, creating a
     * pseudorandom but consistent visual effect.
     *
     * <p>The color generation algorithm is identical to {@link #colorRandomString(String)},
     * but uses a predetermined seed instead of the string's hash code.
     *
     * @param string The string to color with pseudorandom gradient
     * @return The string with pseudorandom gradient coloring applied
     * @see #colorRandomString(String)
     */
    public static String colorPseudorandomString(@Nonnull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 0;
        Random random = new Random(11451419);
        do {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        } while (1 / r >= random.nextDouble() && r * r <= string.length() + 1);

        return colorString(string, colorList);
    }

    private static final String DISPLAY_PATTERN = "[%s,%.0f,%.0f,%.0f]";
    /**
     * Converts a Bukkit Location to a string representation.
     *
     * <p>This method creates a comma-separated string containing the world name
     * and the exact coordinates (x, y, z) of the location. The format is:
     * "worldName,x,y,z"
     *
     * @param loc The Bukkit Location to convert
     * @return A string representation of the location, or "null" if the location is null
     */
    public static String locationToString(Location loc) {
        if (loc == null) {
            return "null";
        } else {
            return new StringBuilder()
                    .append(loc.getWorld().getName())
                    .append(',')
                    .append(loc.getX())
                    .append(',')
                    .append(loc.getY())
                    .append(',')
                    .append(loc.getZ())
                    .toString();
        }
    }
    /**
     * Converts a Bukkit Location to a string representation using block coordinates.
     *
     * <p>This method creates a comma-separated string containing the world name
     * and the block coordinates (blockX, blockY, blockZ) of the location. The format is:
     * "worldName,blockX,blockY,blockZ"
     *
     * <p>Unlike {@link #locationToString(Location)}, this method uses integer block
     * coordinates instead of precise floating-point coordinates.
     *
     * @param loc The Bukkit Location to convert
     * @return A string representation of the block location, or "null" if the location is null
     * @see #locationToString(Location)
     */
    public static String blockLocationToString(Location loc) {
        if (loc == null) {
            return "null";
        } else {
            return new StringBuilder()
                    .append(loc.getWorld().getName())
                    .append(',')
                    .append(loc.getBlockX())
                    .append(',')
                    .append(loc.getBlockY())
                    .append(',')
                    .append(loc.getBlockZ())
                    .toString();
        }
    }
    /**
     * Converts a string representation back to a Bukkit Location.
     *
     * <p>This method parses a comma-separated string in the format "worldName,x,y,z"
     * and creates a corresponding Bukkit Location object. The method handles
     * floating-point coordinates for precise positioning.
     *
     * <p>This is the inverse operation of {@link #locationToString(Location)}.
     *
     * @param loc The string representation of the location
     * @return A Bukkit Location object, or null if parsing fails or the string is "null"
     * @see #locationToString(Location)
     */
    public static Location locationFromString(String loc) {
        try {
            if ("null".equals(loc)) {
                return null;
            }
            String[] list = loc.split(",");
            if (list.length != 4) return null;
            String world = list[0];
            double x = Double.parseDouble(list[1]);
            double y = Double.parseDouble(list[2]);
            double z = Double.parseDouble(list[3]);
            return new Location(Bukkit.getWorld(world), x, y, z);
        } catch (Throwable e) {
        }
        return null;
    }
    /**
     * Converts a string representation back to a Bukkit Location using block coordinates.
     *
     * <p>This method parses a comma-separated string in the format "worldName,blockX,blockY,blockZ"
     * and creates a corresponding Bukkit Location object. The method uses integer
     * coordinates for block positioning.
     *
     * <p>This is the inverse operation of {@link #blockLocationToString(Location)}.
     *
     * @param loc The string representation of the block location
     * @return A Bukkit Location object, or null if parsing fails or the string is "null"
     * @see #blockLocationToString(Location)
     */
    public static Location blockLocationFromString(String loc) {
        try {
            if ("null".equals(loc)) {
                return null;
            }
            String[] list = loc.split(",");
            if (list.length != 4) return null;
            String world = list[0];
            int x = Integer.parseInt(list[1]);
            int y = Integer.parseInt(list[2]);
            int z = Integer.parseInt(list[3]);
            return new Location(Bukkit.getWorld(world), x, y, z);
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * Converts a Bukkit Location to a formatted display string.
     *
     * <p>This method creates a user-friendly string representation of a location
     * using the format "[worldName,x,y,z]" with coordinates rounded to integers
     * for display purposes.
     *
     * @param loc The Bukkit Location to convert
     * @return A formatted display string, or "null" if the location is null
     */
    public static String locationToDisplayString(Location loc) {
        return loc != null
                ? DISPLAY_PATTERN.formatted(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ())
                : "null";
    }
    /**
     * Adds lore lines to an ItemStack.
     *
     * <p>This method creates a copy of the provided ItemStack and adds the
     * specified lore lines to it. The lore lines are processed through
     * {@link #resolveColor(String)} to handle color codes.
     *
     * @param item The ItemStack to add lore to
     * @param lores The lore lines to add (varargs)
     * @return A new ItemStack with the added lore
     */
    public static ItemStack addLore(ItemStack item, String... lores) {

        ItemStack item2 = item.clone();
        ItemMeta meta = item2.getItemMeta();
        List<String> finallist = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String l : lores) {
            finallist.add(resolveColor(l));
        }
        meta.setLore(finallist);
        item2.setItemMeta(meta);
        return item2;
    }
    /**
     * Adds lore lines to an ItemStack.
     *
     * <p>This method modifies the origin ItemStack. The lore lines are processed through
     * {@link #resolveColor(String)} to handle color codes.
     *
     * @param item The ItemStack to add lore to
     * @param lores The lore lines to add (varargs)
     * @return A new ItemStack with the added lore
     */
    public static ItemStack appendLore(ItemStack item, String... lores) {
        ItemMeta meta = item.getItemMeta();
        List<String> finallist = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String l : lores) {
            finallist.add(resolveColor(l));
        }
        meta.setLore(finallist);
        item.setItemMeta(meta);
        return item;
    }
    /**
     * Renames an ItemStack by setting its display name.
     *
     * <p>This method creates a copy of the provided ItemStack and sets its display
     * name to the specified value, with color codes resolved.
     *
     * @param item The ItemStack to rename
     * @param name The new display name
     * @return A new ItemStack with the updated name
     */
    public static ItemStack renameItem(ItemStack item, String name) {
        ItemStack item2 = item.clone();
        ItemMeta meta = item2.getItemMeta();
        meta.setDisplayName(resolveColor(name));
        item2.setItemMeta(meta);
        return item2;
    }
    /**
     * Resolves color codes in a string.
     *
     * <p>This method replaces all '&' color codes in the input string with the
     * Minecraft section sign (§) color codes.
     *
     * @param s The string to resolve color codes in
     * @return The string with color codes resolved
     */
    public static String resolveColor(String s) {
        return translateAlternateColorCodes('&', s);
    }
    /**
     * Translates alternate color codes in a string.
     *
     * <p>This method replaces all occurrences of the specified alternate color
     * character with the Minecraft section sign (§) color codes.
     *
     * @param altColorChar The alternate color code character (e.g., '&')
     * @param textToTranslate The string to translate
     * @return The string with alternate color codes translated
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
            }
        }

        return new String(b);
    }

    /**
     * Formats a double as a percentage string with up to two decimal places.
     *
     * @param b The value to format as a percentage
     * @return The formatted percentage string
     */
    public static String getPercentFormat(double b) {
        DecimalFormat df = new DecimalFormat("#.##");
        NumberFormat nf = NumberFormat.getPercentInstance();
        return nf.format(Double.parseDouble(df.format(b)));
    }

    /**
     * Sets a specific lore line in an ItemStack without cloning.
     *
     * @param stack The ItemStack to modify
     * @param index The index of the lore line to set
     * @param str The new lore line
     * @return The same ItemStack with the updated lore line
     */
    public static ItemStack setLore(ItemStack stack, int index, String str) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        while (index >= lore.size()) {
            lore.add("");
        }
        lore.set(index, resolveColor(str));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * Sets the entire lore of an ItemStack without cloning.
     *
     * @param stack The ItemStack to modify
     * @param str The new lore lines
     * @return The same ItemStack with the updated lore
     */
    public static ItemStack setLore(ItemStack stack, String... str) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();
        int len = str.length;
        for (int i = 0; i < len; ++i) {
            lore.add(resolveColor(str[i]));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Sends a colored message to a CommandSender.
     *
     * @param p The CommandSender to send the message to
     * @param msg The message to send
     */
    public static void sendMessage(CommandSender p, String msg) {
        p.sendMessage(resolveColor(msg));
    }
    /**
     * Sends a colored title and subtitle to a player.
     *
     * @param p The player to send the title to
     * @param title The title text
     * @param subtitle The subtitle text
     */
    public static void sendTitle(Player p, String title, String subtitle) {
        p.sendTitle(resolveColor(title), resolveColor(subtitle), -1, -1, -1);
    }

    /**
     * Sets the display name of an ItemMeta, resolving color codes.
     *
     * @param meta The ItemMeta to modify
     * @param name The new display name
     * @return The modified ItemMeta
     */
    public static ItemMeta setName(ItemMeta meta, String name) {
        meta.setDisplayName(AddUtils.resolveColor(name));
        return meta;
    }
    /**
     * Gets the current date as a string in yyyyMMdd format.
     *
     * @return The current date as a string
     */
    public static String getDateString() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
