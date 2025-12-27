package me.matl114.matlib.implement.slimefun.menu.menuGroup;

import java.util.function.IntFunction;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

public class CustomMenu {
    @Getter
    CustomMenuGroup parent;

    @Getter
    int page;

    @Getter
    ChestMenu menu;

    public CustomMenu(CustomMenuGroup parent, int page) {
        this(parent, page, null);
    }

    @Getter
    IntFunction<ChestMenu> preset;

    public CustomMenu(CustomMenuGroup parent, int page, IntFunction<ChestMenu> generator) {
        assert parent != null || generator != null;
        this.parent = parent;
        this.page = page;
        this.preset = generator;
        this.menu = generator == null ? new ChestMenu(parent.getTitle()) : generator.apply(page);
    }

    public void loadInternal() {}

    public void openMenu(Player player) {
        this.menu.open(player);
    }

    public void requestReload() {
        this.parent.loadPage(this);
    }
}
