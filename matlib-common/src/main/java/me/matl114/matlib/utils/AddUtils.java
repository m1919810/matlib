package me.matl114.matlib.utils;

import java.util.*;
import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddUtils {

    private static final Random random = new Random();
    private static final Enchantment GLOW_EFFECT = Enchantment.getByKey(new NamespacedKey("minecraft", "infinity"));
    public static final ItemStack RESOLVE_FAILED = AddUtils.addGlow(new CleanItemStack(Material.BARRIER, "&c解析物品失败"));

    /**
     * Creates a copy of an ItemStack.
     *
     * <p>This method creates a clean copy of the provided ItemStack using
     * the {@link #getCleaned(ItemStack)} method.
     *
     * @param stack The ItemStack to copy
     * @return A clean copy of the ItemStack
     * @see #getCleaned(ItemStack)
     */
    public static ItemStack getCopy(ItemStack stack) {
        return getCleaned(stack);
    }
    /**
     * Creates a clean copy of an ItemStack.
     *
     * <p>This method creates a clean version of the provided ItemStack using
     * the CleanItemStack utility. If the input is null, it returns an AIR ItemStack.
     *
     * @param stack The ItemStack to clean
     * @return A clean ItemStack, or AIR if the input is null
     */
    public static ItemStack getCleaned(ItemStack stack) {
        return stack == null ? new ItemStack(Material.AIR) : CleanItemStack.ofBukkitClean(stack);
    }

    /**
     * Copies properties from one ItemStack to another.
     *
     * <p>This method copies the amount, type, data, and item metadata from
     * the source ItemStack to the target ItemStack. Both ItemStacks must
     * not be null for the operation to succeed.
     *
     * @param from The source ItemStack to copy from
     * @param to The target ItemStack to copy to
     * @return true if the copy operation was successful, false otherwise
     */
    public static boolean copyItem(ItemStack from, ItemStack to) {
        if (from == null || to == null) return false;
        to.setAmount(from.getAmount());
        to.setType(from.getType());
        to.setData(from.getData());
        return to.setItemMeta(from.getItemMeta());
    }

    /**
     * Returns a random integer in the range [0, length).
     *
     * @param length The exclusive upper bound
     * @return A random integer between 0 (inclusive) and length (exclusive)
     */
    public static int random(int length) {
        return random.nextInt(length);
    }
    /**
     * Returns a random double in the range (0, 1).
     *
     * @return A random double between 0.0 (inclusive) and 1.0 (exclusive)
     */
    public static double standardRandom() {
        return random.nextDouble();
    }
    // we supposed that u have checked these shits

    /**
     * Gives a player a specified amount of an ItemStack, dropping leftovers if inventory is full.
     *
     * <p>This method splits the amount into stack sizes as needed and drops any leftover items at the player's location.
     *
     * @param p The player to give items to
     * @param toGive The ItemStack to give
     * @param amount The total amount to give
     */
    public static void forceGive(Player p, ItemStack toGive, int amount) {
        ItemStack incoming;
        int maxSize = toGive.getMaxStackSize();
        while (amount > 0) {
            incoming = getCopy(toGive);
            int amount2 = Math.min(maxSize, amount);
            incoming.setAmount(amount2);
            amount -= amount2;
            Collection<ItemStack> leftover = p.getInventory().addItem(incoming).values();
            for (ItemStack itemStack : leftover) {
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            }
        }
    }

    /**
     * Adds a glowing effect to an ItemStack by adding a hidden enchantment.
     *
     * <p>This method does not clone the ItemStack.
     *
     * @param stack The ItemStack to modify
     * @return The same ItemStack with a glowing effect
     */
    public static ItemStack addGlow(ItemStack stack) {
        // stack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(GLOW_EFFECT, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * Adds all item flags to an ItemStack, hiding all flags in the tooltip.
     *
     * @param stack The ItemStack to modify
     * @return The same ItemStack with all flags hidden
     */
    public static ItemStack hideAllFlags(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * Removes all item flags from an ItemStack, showing all flags in the tooltip.
     *
     * @param stack The ItemStack to modify
     * @return The same ItemStack with all flags shown
     */
    public static ItemStack showAllFlags(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            meta.removeItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * Sets the unbreakable property of an ItemStack.
     *
     * @param stack The ItemStack to modify
     * @param unbreakable Whether the item should be unbreakable
     * @return The same ItemStack with the unbreakable property set
     */
    public static ItemStack setUnbreakable(ItemStack stack, boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * Creates an info display ItemStack for use in machine recipe displays.
     *
     * @param title The display title
     * @param name Additional lines for the display
     * @return An ItemStack representing the info display
     */
    public static ItemStack getInfoShow(String title, String... name) {
        return new CleanItemStack(Material.BOOK, title, name);
    }

    public static String resolveColor(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
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
     * Gets the corresponding planks Material for a given log Material, if available.
     *
     * @param log The log Material
     * @return An Optional containing the planks Material, or empty if not found
     */
    public static @Nonnull Optional<Material> getPlanks(@Nonnull Material log) {
        String materialName = log.name().replace("STRIPPED_", "");
        int endIndex = materialName.lastIndexOf('_');

        if (endIndex > 0) {
            materialName = materialName.substring(0, endIndex) + "_PLANKS";
            return Optional.ofNullable(Material.getMaterial(materialName));
        } else {
            // Fixed #3651 - Do not panic because of one weird wood type.
            return Optional.empty();
        }
    }
    /**
     * Displays a clickable, hoverable string to a player that copies text to clipboard.
     *
     * @param player The player to display the string to
     * @param display The display text
     * @param hover The hover text
     * @param copy The text to copy to clipboard
     */
    public static void displayCopyString(Player player, String display, String hover, String copy) {
        final TextComponent link = new TextComponent(display);
        link.setColor(ChatColor.YELLOW);
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy));
        player.spigot().sendMessage(link);
    }

    /**
     * Generates a random UUID string.
     *
     * @return A random UUID as a string
     */
    public static String randUUID() {
        return UUID.randomUUID().toString();
    }
    /**
     * Broadcasts a colored message to all players on the server.
     *
     * @param string The message to broadcast
     */
    public static void broadCast(String string) {
        Bukkit.getServer().broadcastMessage(resolveColor(string));
    }
    /**
     * Creates a clean copy of an ItemStack with a specified amount.
     *
     * @param stack The ItemStack to copy
     * @param amount The amount to set
     * @return A new ItemStack with the specified amount
     */
    public static ItemStack copyWithAmount(ItemStack stack, int amount) {
        ItemStack result = CleanItemStack.ofBukkitClean(stack);
        result.setAmount(amount);
        return result;
    }
    /**
     * Concatenates multiple strings into one.
     *
     * @param strs The strings to concatenate
     * @return The concatenated string
     */
    public static String concat(String... strs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; ++i) {
            sb.append(strs[i]);
        }
        return sb.toString();
    }
    /**
     * Damages a Damageable entity by a specified amount, clamped to valid health range.
     *
     * @param e The entity to damage
     * @param f The amount of damage
     */
    public static void damageGeneric(Damageable e, double f) {
        e.setHealth(MathUtils.clamp(e.getHealth() - f, 0.0, e.getMaxHealth()));
    }
}
