package me.matl114.matlib.slimefunUtils;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.matl114.matlib.utils.Debug;

public class SlimefunRegistryUtils {
    public static void unregisterSlimefunItem(SlimefunItem item) {
        Iterator<ItemHandler> handlers = item.getHandlers().iterator();
        while (handlers.hasNext()) {
            ItemHandler handler = handlers.next();
            try {
                var re = Slimefun.getRegistry().getGlobalItemHandlers().get(handler.getIdentifier());
                if (re != null) {
                    re.remove(handler);
                }
            } catch (Throwable e) {
                Debug.logger(e);
            }
        }
        item.disable();
        Slimefun.getRegistry().getAllSlimefunItems().remove(item);
        Slimefun.getRegistry().getSlimefunItemIds().remove(item.getId());
        Slimefun.getRegistry().getDisabledSlimefunItems().remove(item);
        if (item instanceof GEOResource geo) {
            Slimefun.getRegistry().getGEOResources().remove(geo.getKey());
        }
        item.getItemGroup().remove(item);
    }

    public static void disableItemGroup(ItemGroup group) {
        Slimefun.getRegistry().getAllItemGroups().remove(group);
        List<ItemGroup> categories = Slimefun.getRegistry().getAllItemGroups();
        Collections.sort(categories, Comparator.comparingInt(ItemGroup::getTier));
    }
}
