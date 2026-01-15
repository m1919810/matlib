package me.matl114.matlib.implement.slimefun.menu.menuGroup;

import it.unimi.dsi.fastutil.ints.IntSet;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

public interface IMenuGroup {
    int getPages();

    int getSizePerPage();

    IntSet getPrev();

    IntSet getNext();

    int[] getContentSlots();

    ChestMenu buildMenuPage(int page);
}
