package me.matl114.matlib.implement.slimefun.menu.guideMenu;

import city.norain.slimefun4.VaultIntegration;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.implement.slimefun.menu.MenuUtils;
import me.matl114.matlib.implement.slimefun.menu.menuClickHandler.GuideClickHandler;
import me.matl114.matlib.implement.slimefun.menu.menuGroup.IMenuGroup;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemGroup extends FlexItemGroup {
    protected static final ItemStack INVOKE_ERROR = new CleanItemStack(Material.BARRIER, "&c", "", "&c获取物品组物品展示失败");
    protected boolean isVisible;
    protected IMenuGroup group;
    protected Supplier<Map<Integer, ItemGroup>> subGroups;
    protected Supplier<Map<Integer, SlimefunItem>> items;
    boolean loaded = false;

    @Override
    public ItemStack getItem(Player p) {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item.setType(item.getType());
        this.item.setItemMeta(item.getItemMeta());
        this.item.setData(item.getData());
    }

    public CustomItemGroup(NamespacedKey key, ItemStack item, boolean hide) {
        super(key, item);
        this.isVisible = !hide;
    }

    public CustomItemGroup setLoader(
            IMenuGroup group, Map<Integer, ItemGroup> subGroup, Map<Integer, SlimefunItem> researches) {
        return setLoader(group, subGroup, () -> researches);
    }

    public CustomItemGroup setLoader(
            IMenuGroup group, Map<Integer, ItemGroup> subGroup, Supplier<Map<Integer, SlimefunItem>> researches) {
        return setLoader(group, () -> subGroup, researches);
    }

    public CustomItemGroup setLoader(
            IMenuGroup group, Supplier<Map<Integer, ItemGroup>> subGroup, Map<Integer, SlimefunItem> researches) {
        return setLoader(group, subGroup, () -> researches);
    }

    public CustomItemGroup setLoader(
            IMenuGroup group,
            Supplier<Map<Integer, ItemGroup>> subGroup,
            Supplier<Map<Integer, SlimefunItem>> researches) {
        this.group = group;
        this.subGroups = subGroup;
        this.items = researches;
        this.loaded = true;
        postLoad();
        return this;
    }

    public void postLoad() {
        // left
    }

    public boolean isVisible(Player var1, PlayerProfile var2, SlimefunGuideMode var3) {
        return isVisible;
    }

    public boolean isHidden(Player p) {
        return !isVisible;
    }

    public void open(Player var1, PlayerProfile var2, SlimefunGuideMode var3) {
        assert loaded;
        int page = getLastPage(var1, var2, var3);
        if (page <= 0 || page > group.getPages()) {
            page = 1;
        }
        openPage(var1, var2, var3, page);
    }

    public CustomItemGroup setSearchButton(int... buttons) {
        return setSearchButton(Arrays.stream(buttons).mapToObj(i -> (Integer) i).collect(Collectors.toSet()));
    }

    public CustomItemGroup setSearchButton(Collection<Integer> buttons) {
        this.searchButton = new HashSet<>(buttons);
        return this;
    }

    @Getter
    private HashSet<Integer> searchButton = new HashSet<>();

    public CustomItemGroup setBackButton(int... buttons) {
        return setBackButton(Arrays.stream(buttons).mapToObj(i -> (Integer) i).collect(Collectors.toSet()));
    }

    public CustomItemGroup setBackButton(Collection<Integer> buttons) {
        this.backButton = new HashSet<>(buttons);
        return this;
    }

    @Getter
    private HashSet<Integer> backButton = new HashSet<>();

    private static final Field iconAccess = ReflectUtils.getFieldPrivate(ItemGroup.class, "item");

    public static ItemStack getItemGroupIcon(ItemGroup group) {
        if (iconAccess != null) {
            try {
                return (ItemStack) iconAccess.get(group);
            } catch (Throwable e) {

            }
        }
        return TextUtils.renameItem(INVOKE_ERROR, group.getUnlocalizedName());
    }

    public static boolean setItemGroupIcon(ItemGroup group, ItemStack stack) {
        try {
            iconAccess.set(group, stack);
            return true;
        } catch (Throwable e) {
            return false;
        }
        //        try{
        //            Class clazz= Class.forName("io.github.thebusybiscuit.slimefun4.api.items.ItemGroup");
        //            Field _hasType=clazz.getDeclaredField("item");
        //            _hasType.setAccessible(true);
        //            _hasType.set(group,stack);
        //            return true;
        //        }catch (Throwable e){
        //            return false;
        //        }

    }

    public void openPage(Player var1, PlayerProfile var2, SlimefunGuideMode var3, int page) {
        int pages = group.getPages();
        assert page >= 1 && page <= pages;
        var2.getGuideHistory().add(this, page);
        ChestMenu menu = this.group.buildMenuPage(page);
        // transfer this to GuideClick
        for (int i = 0; i < this.group.getSizePerPage(); ++i) {
            if (menu.getMenuClickHandler(i) instanceof GuideClickHandler handler) {
                menu.addMenuClickHandler(i, ((player, i1, itemStack, clickAction) -> {
                    return handler.onGuideClick(var1, i1, itemStack, clickAction, var2, var3, this, page);
                }));
            }
        }
        // prev键
        this.group
                .getPrev()
                .forEach(i1 -> menu.addMenuClickHandler(i1, ((player, i, itemStack, clickAction) -> {
                    if (page > 1) {
                        this.openPage(var1, var2, var3, page - 1);
                    }
                    return false;
                })));
        // next键
        this.group
                .getNext()
                .forEach(i2 -> menu.addMenuClickHandler(i2, ((player, i, itemStack, clickAction) -> {
                    if (page < pages) {
                        this.openPage(var1, var2, var3, page + 1);
                    }
                    return false;
                })));
        // 搜索键
        for (Integer i : this.searchButton) {
            menu.replaceExistingItem(i, ChestMenuUtils.getSearchButton(var1));
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> {
                pl.closeInventory();
                Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
                ChatInput.waitForPlayer(
                        Slimefun.instance(),
                        pl,
                        msg -> SlimefunGuide.openSearch(var2, msg, SlimefunGuideMode.SURVIVAL_MODE, true));
                return false;
            });
        }
        for (Integer i : this.backButton) {
            menu.replaceExistingItem(i, MenuUtils.getBackButton());
            menu.addMenuClickHandler(i, ((player, j, itemStack, clickAction) -> {
                var2.getGuideHistory().goBack(Slimefun.getRegistry().getSlimefunGuide(var3));
                return false;
            }));
        }
        int[] contents = this.group.getContentSlots();
        int contentPerPage = contents.length;
        int startIndex = Math.max(0, contentPerPage * (page - 1));
        int endIndex = Math.min(contentPerPage * page, contentPerPage * pages);
        Map<Integer, ItemGroup> displayedSubGroups = this.subGroups.get();
        for (Map.Entry<Integer, ItemGroup> entry : displayedSubGroups.entrySet()) {
            int index = entry.getKey();
            if (index >= startIndex && index < endIndex) {
                int realIndex = contents[(index - startIndex) % contentPerPage];
                final ItemGroup group = entry.getValue();
                menu.replaceExistingItem(realIndex, this.getItemGroupIcon(group));
                menu.addMenuClickHandler(realIndex, ((player, i, itemStack, clickAction) -> {
                    SlimefunGuide.openItemGroup(var2, group, var3, 1);
                    return false;
                }));
            }
        }
        Map<Integer, SlimefunItem> displayedItems = this.items.get();
        for (Map.Entry<Integer, SlimefunItem> entry : displayedItems.entrySet()) {
            int index = entry.getKey();
            if (index >= startIndex && index < endIndex) {
                int realIndex = contents[(index - startIndex) % contentPerPage];
                // leave blank if disabled
                if (entry.getValue().isDisabledIn(var1.getWorld())) {
                    menu.replaceExistingItem(realIndex, null);
                    menu.addMenuClickHandler(realIndex, ChestMenuUtils.getEmptyClickHandler());
                } else {
                    displaySlimefunItem(menu, this, var1, var2, entry.getValue(), var3, page, realIndex);
                }
            }
        }
        menu.open(var1);
    }

    private void displaySlimefunItem(
            ChestMenu menu,
            ItemGroup itemGroup,
            Player p,
            PlayerProfile profile,
            SlimefunItem sfitem,
            SlimefunGuideMode mode,
            int page,
            int index) {
        Research research = sfitem.getResearch();
        if (SlimefunGuideMode.CHEAT_MODE != mode
                && !Slimefun.getPermissionsService().hasPermission(p, sfitem)) {
            List<String> message = Slimefun.getPermissionsService().getLore(sfitem);
            menu.addItem(
                    index, new CleanItemStack(ChestMenuUtils.getNoPermissionItem(), sfitem.getItemName(), (String[])
                            message.toArray(new String[0])));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        } else if (SlimefunGuideMode.CHEAT_MODE != mode && research != null && !profile.hasUnlocked(research)) {
            String lore;
            if (VaultIntegration.isEnabled()) {
                Object[] var10001 = new Object[] {research.getCurrencyCost()};
                lore = String.format("%.2f", var10001) + " 游戏币";
            } else {
                lore = research.getLevelCost() + " 级经验";
            }

            menu.addItem(
                    index,
                    new CleanItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(sfitem.getItem()),
                            new String[] {
                                "&7" + sfitem.getId(),
                                "&4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                                "",
                                "&a> 单击解锁",
                                "",
                                "&7需要 &b",
                                lore
                            }));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                research.unlockFromGuide(
                        Slimefun.getRegistry().getSlimefunGuide(mode), p, profile, sfitem, itemGroup, page);
                return false;
            });
        } else {
            menu.addItem(index, sfitem.getItem());
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                try {
                    if (SlimefunGuideMode.CHEAT_MODE != mode) {
                        Slimefun.getRegistry().getSlimefunGuide(mode).displayItem(profile, sfitem, true);
                    } else if (pl.hasPermission("slimefun.cheat.items")) {
                        if (sfitem instanceof MultiBlockMachine) {
                            Slimefun.getLocalization().sendMessage(pl, "guide.cheat.no-multiblocks");
                        } else {
                            ItemStack clonedItem = sfitem.getItem().clone();
                            if (action.isShiftClicked()) {
                                clonedItem.setAmount(clonedItem.getMaxStackSize());
                            }

                            pl.getInventory().addItem(new ItemStack[] {clonedItem});
                        }
                    } else {
                        Slimefun.getLocalization().sendMessage(pl, "messages.no-permission", true);
                    }
                } catch (LinkageError | Exception var8) {
                    Throwable x = var8;
                    p.sendMessage(
                            ChatColor.DARK_RED
                                    + "An internal server error has occurred. Please inform an admin, check the console for further info.");
                    sfitem.error(
                            "This item has caused an error message to be thrown while viewing it in the Slimefun guide.",
                            x);
                }
                return false;
            });
        }
    }

    private final Method lastEntryAccess =
            ReflectUtils.getMethodPrivate(GuideHistory.class, "getLastEntry", boolean.class);
    private final Method getIndexedObjectAccess = Holder.of("io.github.thebusybiscuit.slimefun4.core.guide.GuideEntry")
            .thenApplyCaught(Class::forName)
            .thenApplyCaught(Class::getDeclaredMethod, "getIndexedObject")
            .thenPeek(Method::setAccessible, true)
            .valException(null)
            .get();
    private final Method getPageAccess = Holder.of("io.github.thebusybiscuit.slimefun4.core.guide.GuideEntry")
            .thenApplyCaught(Class::forName)
            .thenApplyCaught(Class::getDeclaredMethod, "getPage")
            .thenPeek(Method::setAccessible, true)
            .valException(null)
            .get();
    // modified from guizhan Infinity Expansion 2
    private int getLastPage(Player var1, PlayerProfile var2, SlimefunGuideMode var3) {
        if (lastEntryAccess != null && getIndexedObjectAccess != null && getPageAccess != null) {
            try {
                Object entry = lastEntryAccess.invoke(var2.getGuideHistory(), false);
                if (entry != null) {
                    Object indexed = getIndexedObjectAccess.invoke(entry);
                    if (indexed instanceof CustomItemGroup group) {
                        int page = (int) getPageAccess.invoke(entry);
                        return page;
                    }
                }
            } catch (Throwable e) {

            }
        }
        return 1;
    }
}
