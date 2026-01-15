package me.matl114.matlib.implement.custom.inventory.inventoryImpl;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.matl114.matlib.implement.custom.inventory.Screen;
import me.matl114.matlib.implement.custom.inventory.SlotType;
import me.matl114.matlib.implement.slimefun.menu.menuGroup.IMenuGroup;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

public class ScreenMenuGroup implements IMenuGroup {
    Screen builder;
    IntSet prev;
    IntSet next;
    int[] contents;

    public ScreenMenuGroup(Screen builder) {
        this.builder = builder;
        IntArrayList arrayList = new IntArrayList();
        prev = new IntArraySet();
        next = new IntArraySet();
        for (var i = 0; i < builder.getPageSize(); ++i) {
            SlotType type = builder.getSlotType(i);
            switch (type) {
                case PREV_PAGE -> prev.add(i);
                case NEXT_PAGE -> next.add(i);
                case PAGE_CONTENT -> arrayList.add(i);
            }
        }
        contents = arrayList.toIntArray();
    }

    @Override
    public int getPages() {
        return builder.getMaxPages();
    }

    @Override
    public int getSizePerPage() {
        return builder.getPageSize();
    }

    @Override
    public IntSet getPrev() {
        return prev;
    }

    @Override
    public IntSet getNext() {
        return next;
    }

    @Override
    public int[] getContentSlots() {
        return contents;
    }

    @Override
    public ChestMenu buildMenuPage(int page) {
        ChestMenuImpl impl = builder.createInventory(page, ChestMenuImpl.FACTORY);
        return impl.getResult();
    }
}
