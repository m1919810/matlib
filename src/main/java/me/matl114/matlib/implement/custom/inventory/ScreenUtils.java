package me.matl114.matlib.implement.custom.inventory;

import java.util.Arrays;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ScreenUtils {
    public static final ItemStack PREV_BUTTON_ACTIVE =
            new CleanItemStack(Material.LIME_STAINED_GLASS_PANE, "&r⇦ Previous Page", new String[0]);
    public static final ItemStack NEXT_BUTTON_ACTIVE =
            new CleanItemStack(Material.LIME_STAINED_GLASS_PANE, "&rNext Page ⇨", new String[0]);
    public static final ItemStack PREV_BUTTON_INACTIVE =
            new CleanItemStack(Material.BLACK_STAINED_GLASS_PANE, "&8⇦ Previous Page", new String[0]);
    public static final ItemStack NEXT_BUTTON_INACTIVE =
            new CleanItemStack(Material.BLACK_STAINED_GLASS_PANE, "&8Next Page ⇨", new String[0]);
    public static final ItemStack UI_BACKGROUND =
            new CleanItemStack(Material.GRAY_STAINED_GLASS_PANE, " ", new String[0]);
    public static final ItemStack SEARCH_BUTTON = new CleanItemStack(Material.NAME_TAG, "&7搜索...", "", "&7⇨ &b单击搜索物品");

    public static ItemStack getPreviousButton(int page, int pages) {
        return pages != 1 && page != 1
                ? new CleanItemStack(PREV_BUTTON_ACTIVE, (meta) -> {
                    ChatColor var10001 = ChatColor.WHITE;
                    meta.setDisplayName("" + var10001 + "⇦ " + "上一页");
                    meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
                })
                : new CleanItemStack(PREV_BUTTON_INACTIVE, (meta) -> {
                    ChatColor var10001 = ChatColor.DARK_GRAY;
                    meta.setDisplayName("" + var10001 + "⇦ " + "上一页");
                    meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
                });
    }

    public static ItemStack getNextButton(int page, int pages) {
        return pages != 1 && page != pages
                ? new CleanItemStack(NEXT_BUTTON_ACTIVE, (meta) -> {
                    ChatColor var10001 = ChatColor.WHITE;
                    meta.setDisplayName("" + var10001 + "下一页" + " ⇨");
                    meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
                })
                : new CleanItemStack(NEXT_BUTTON_INACTIVE, (meta) -> {
                    ChatColor var10001 = ChatColor.DARK_GRAY;
                    meta.setDisplayName("" + var10001 + "下一页" + " ⇨");
                    meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
                });
    }

    public static final ItemStack BACK_BUTTON = new CleanItemStack(Material.ENCHANTED_BOOK, 1, (meta) -> {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7⇦ 返回"));
        meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_ENCHANTS});
        return meta;
    });

    public static ItemStack getBackButton(String... lore) {
        return new CleanItemStack(BACK_BUTTON, "&7⇦ " + "返回", lore);
    }

    public static final ItemStack getPageSwitch(int current, int next, int max) {
        if (next < current) {
            return getPreviousButton(current, max);
        } else {
            return getNextButton(current, max);
        }
    }
}
