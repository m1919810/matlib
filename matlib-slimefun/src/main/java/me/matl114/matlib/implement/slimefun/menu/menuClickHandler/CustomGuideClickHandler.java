package me.matl114.matlib.implement.slimefun.menu.menuClickHandler;

import me.matl114.matlib.implement.slimefun.menu.menuGroup.CustomMenu;
import me.matl114.matlib.implement.slimefun.menu.menuGroup.CustomMenuGroup;

public interface CustomGuideClickHandler extends CustomMenuGroup.CustomMenuClickHandler {
    public GuideClickHandler getHandler(CustomMenu menu);
}
