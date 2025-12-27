package me.matl114.matlib.implement.slimefun.menu;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import me.matl114.matlib.implement.slimefun.menu.menuGroup.CustomMenu;
import me.matl114.matlib.implement.slimefun.menu.menuGroup.CustomMenuGroup;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class MenuUtils {

    public static final ItemStack PROCESSOR_NULL = new CleanItemStack(Material.BLACK_STAINED_GLASS_PANE, " ");
    public static final ItemStack PROCESSOR_SPACE =
            new CleanItemStack(Material.RED_STAINED_GLASS_PANE, "&6进程完成", "&c空间不足");
    public static final ItemStack PREV_BUTTON_ACTIVE = new SlimefunItemStack(
            "_UI_PREVIOUS_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&r⇦ Previous Page", new String[0]);
    public static final ItemStack NEXT_BUTTON_ACTIVE =
            new SlimefunItemStack("_UI_NEXT_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&rNext Page ⇨", new String[0]);
    public static final ItemStack PREV_BUTTON_INACTIVE = new SlimefunItemStack(
            "_UI_PREVIOUS_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8⇦ Previous Page", new String[0]);
    public static final ItemStack NEXT_BUTTON_INACTIVE = new SlimefunItemStack(
            "_UI_NEXT_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8Next Page ⇨", new String[0]);
    public static final ItemStack BACK_BUTTON =
            new SlimefunItemStack("_UI_BACK", Material.ENCHANTED_BOOK, "&7⇦ 返回", (meta) -> {
                meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_ENCHANTS});
            });
    public static final ItemStack SEARCH_BUTTON =
            new SlimefunItemStack("_UI_SEARCH", Material.NAME_TAG, "&b搜索...", "", ChatColor.GRAY + "⇨ " + "&b单击搜索物品");
    public static final ItemStack SEARCH_OFF_BUTTON =
            new SlimefunItemStack("_UI_SEARCH", Material.NAME_TAG, "&b搜索...", "", ChatColor.GRAY + "⇨ " + "&b单击取消搜索");
    public static final ItemStack NO_ITEM = new SlimefunItemStack("_UI_NO_ITEM", Material.BARRIER, "&8 ");
    public static final ItemStack PRESET_INFO = new CleanItemStack(Material.CYAN_STAINED_GLASS_PANE, "&3配方类型信息");
    public static final ItemStack PRESET_MORE = new CleanItemStack(Material.LIME_STAINED_GLASS_PANE, "&a更多物品(已省略)");

    public static final ChestMenu.MenuClickHandler CLOSE_HANDLER = ((player, i, itemStack, clickAction) -> {
        player.closeInventory();
        return false;
    });

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

    public static ItemStack getBackButton(String... lore) {
        return new CleanItemStack(BACK_BUTTON, "&7⇦ " + "返回", lore);
    }

    public static ItemStack getSearchButton() {
        return new CleanItemStack(SEARCH_BUTTON);
    }

    public static ItemStack getSearchOffButton() {
        return new CleanItemStack(SEARCH_OFF_BUTTON);
    }

    public static <T extends Object> Pair<List<ItemStack>, List<CustomMenuGroup.CustomMenuClickHandler>> getSelector(
            String filter,
            BiConsumer<T, Player> clickCallback,
            BiConsumer<T, Player> shiftClickCallback,
            Iterator<Map.Entry<String, T>> objectIterator,
            Function<T, ItemStack> iconGenerator) {
        return getSelector(filter, cm -> clickCallback, cm -> shiftClickCallback, objectIterator, iconGenerator);
    }

    public static <T extends Object> Pair<List<ItemStack>, List<CustomMenuGroup.CustomMenuClickHandler>> getSelector(
            String filter,
            Function<CustomMenu, BiConsumer<T, Player>> clickCallback,
            Function<CustomMenu, BiConsumer<T, Player>> shiftClickCallback,
            Iterator<Map.Entry<String, T>> objectIterator,
            Function<T, ItemStack> iconGenerator) {
        List<ItemStack> itemlist = new ArrayList<>();
        List<CustomMenuGroup.CustomMenuClickHandler> handlerlist = new ArrayList<>();
        while (objectIterator.hasNext()) {
            Map.Entry<String, T> entry = objectIterator.next();
            String key = entry.getKey();
            if (filter != null && !key.contains(filter)) {
                continue;
            }
            T value = entry.getValue();
            ItemStack stack = iconGenerator.apply(value);
            itemlist.add(stack);
            handlerlist.add((cm) -> ((player, i, itemStack, clickAction) -> {
                if (clickAction.isShiftClicked()) {
                    shiftClickCallback.apply(cm).accept(value, player);
                } else {
                    clickCallback.apply(cm).accept(value, player);
                }
                return false;
            }));
        }
        return new Pair<>(itemlist, handlerlist);
    }

    public static <T extends Object> void openSelectMenu(
            Player player,
            int page,
            String filter,
            HashMap<String, T> dataMap,
            BiConsumer<T, Player> clickCallback,
            BiConsumer<T, Player> shiftClickCallback,
            Function<T, ItemStack> iconGenerator,
            BiConsumer<CustomMenu, Player> fallback) {
        openSelectMenu(
                player, page, filter, dataMap, (cm) -> clickCallback, (cm) -> shiftClickCallback, iconGenerator, null);
    }

    public static <T extends Object> void openSelectMenu(
            Player player,
            int page,
            String filter,
            HashMap<String, T> dataMap,
            BiConsumer<T, Player> clickCallback,
            BiConsumer<T, Player> shiftClickCallback,
            Function<T, ItemStack> iconGenerator) {
        openSelectMenu(player, page, filter, dataMap, clickCallback, shiftClickCallback, iconGenerator, null);
    }

    public static <T extends Object> void openSelectMenu(
            Player player,
            int page,
            String filter,
            HashMap<String, T> dataMap,
            Function<CustomMenu, BiConsumer<T, Player>> clickCallback,
            Function<CustomMenu, BiConsumer<T, Player>> shiftClickCallback,
            Function<T, ItemStack> iconGenerator) {
        openSelectMenu(player, page, filter, dataMap, clickCallback, shiftClickCallback, iconGenerator, null);
    }

    public static <T extends Object> void openSelectMenu(
            Player player,
            int page,
            String filter,
            HashMap<String, T> dataMap,
            Function<CustomMenu, BiConsumer<T, Player>> clickCallback,
            Function<CustomMenu, BiConsumer<T, Player>> shiftClickCallback,
            Function<T, ItemStack> iconGenerator,
            BiConsumer<CustomMenu, Player> fallbackHandler) {
        if (page <= 0) {
            AddUtils.sendMessage(player, "&c无效的页数!");
            return;
        }
        CustomMenuGroup menuGroup = new CustomMenuGroup(AddUtils.resolveColor("&a选择界面"), 54, 1)
                .enableContentPlace(IntStream.range(0, 45).toArray())
                .setPageChangeSlots(46, 52)
                .enableOverrides()
                .setOverrideItem(47, ChestMenuUtils.getBackground(), CustomMenuGroup.CustomMenuClickHandler.ofEmpty())
                .setOverrideItem(51, ChestMenuUtils.getBackground(), CustomMenuGroup.CustomMenuClickHandler.ofEmpty());
        if (filter == null) {
            menuGroup
                    .setOverrideItem(45, MenuUtils.getSearchButton(), (cm) -> ((player1, i, itemStack, clickAction) -> {
                        player1.closeInventory();
                        ChatUtils.awaitInput(player1, (string -> {
                            openSelectMenu(
                                    player1,
                                    1,
                                    string,
                                    dataMap,
                                    clickCallback,
                                    shiftClickCallback,
                                    iconGenerator,
                                    fallbackHandler);
                        }));
                        return false;
                    }))
                    .setOverrideItem(53, MenuUtils.getSearchButton(), (cm) -> ((player1, i, itemStack, clickAction) -> {
                        player1.closeInventory();
                        ChatUtils.awaitInput(player1, (string -> {
                            openSelectMenu(
                                    player1,
                                    1,
                                    string,
                                    dataMap,
                                    clickCallback,
                                    shiftClickCallback,
                                    iconGenerator,
                                    fallbackHandler);
                        }));
                        return false;
                    }));
        } else {
            menuGroup.setOverrideItem(
                    45, MenuUtils.getSearchOffButton(), (cm) -> ((player1, i, itemStack, clickAction) -> {
                        openSelectMenu(
                                player1,
                                1,
                                null,
                                dataMap,
                                clickCallback,
                                shiftClickCallback,
                                iconGenerator,
                                fallbackHandler);
                        ;
                        return false;
                    }));
            menuGroup.setOverrideItem(
                    53, MenuUtils.getSearchOffButton(), (cm) -> ((player1, i, itemStack, clickAction) -> {
                        openSelectMenu(
                                player1,
                                1,
                                null,
                                dataMap,
                                clickCallback,
                                shiftClickCallback,
                                iconGenerator,
                                fallbackHandler);
                        ;
                        return false;
                    }));
        }
        if (fallbackHandler != null) {
            menuGroup.setOverrideItem(49, MenuUtils.getBackButton(), (cm) -> (player1, i, itemStack, clickAction) -> {
                fallbackHandler.accept(cm, player1);
                return false;
            });
        } else {
            menuGroup.setOverrideItem(
                    49, MenuUtils.getBackButton("&c没有返回路径", "&c哈哈哈"), CustomMenuGroup.CustomMenuClickHandler.ofEmpty());
        }
        int index = 48;
        var re = getSelector(
                filter, clickCallback, shiftClickCallback, dataMap.entrySet().iterator(), iconGenerator);
        menuGroup.resetItems(re.getFirstValue());
        menuGroup.resetHandlers(re.getSecondValue());
        final int openpage = Math.min(menuGroup.getPages(), page);
        menuGroup.openPage(player, openpage);
    }
}
