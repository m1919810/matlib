package me.matl114.matlib.implement.slimefun.menu.menuClickHandler;

import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import javax.annotation.Nullable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface GuideClickHandler extends ChestMenu.MenuClickHandler {
    boolean onGuideClick(
            Player var1,
            int var2,
            ItemStack var3,
            ClickAction var4,
            @Nullable PlayerProfile var5,
            @Nullable SlimefunGuideMode var6,
            @Nullable FlexItemGroup group,
            int page);

    default boolean onClick(Player var1, int var2, ItemStack var3, ClickAction var4) {
        return onGuideClick(var1, var2, var3, var4, null, null, null, 1);
    }
}
