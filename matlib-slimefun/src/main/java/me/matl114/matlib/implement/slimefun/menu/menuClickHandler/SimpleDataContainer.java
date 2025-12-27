package me.matl114.matlib.implement.slimefun.menu.menuClickHandler;

public class SimpleDataContainer implements DataContainer {
    boolean isInit = false;

    public boolean getStatue() {
        return isInit;
    }

    public void setStatue(boolean statue) {
        isInit = statue;
    }
}
