package me.matl114.matlib.slimefunUtils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Optional;
import me.matl114.matlib.common.lang.enums.Flags;
import me.matl114.matlib.core.AutoInit;
import me.matl114.matlib.utils.CraftUtils;
import org.bukkit.inventory.meta.ItemMeta;

@AutoInit(level = "SlimefunAddon")
public class SlimefunSetup {
    public static void init() {
        CraftUtils.registerCustomItemIdHook(new CraftUtils.CustomItemMatcher() {
            @Override
            public Optional<String> parseId(ItemMeta meta1) {
                return Slimefun.getItemDataService().getItemData(meta1);
            }

            @Override
            public Flags doMatch(String id, ItemMeta meta1, ItemMeta meta2) {
                SlimefunItem it = SlimefunItem.getById(id);
                // 自动跳过当前附属的物品
                // distinctive物品必须判断
                if (it instanceof DistinctiveItem dt) {
                    return dt.canStack(meta1, meta2) ? Flags.ACCEPT : Flags.REJECT;
                }
                return Flags.IGNORED;
            }
        });
    }
}
