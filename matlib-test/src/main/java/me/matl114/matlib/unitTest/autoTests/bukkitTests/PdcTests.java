package me.matl114.matlib.unitTest.autoTests.bukkitTests;

import java.util.List;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PdcTests implements TestCase {
    @OnlineTest(name = "list pdc test")
    public void test_listpdc() throws Throwable {
        ItemStack item = ItemUtils.newStack(Material.CHEST, 3);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(
                new NamespacedKey("matlibtest", "testarray"),
                PersistentDataType.LIST.strings(),
                List.of("a1", "a2", "a3", "a4", "a5"));
        item.setItemMeta(meta);
        Debug.logger(item);
        Debug.logger(NMSItem.ITEMSTACK.save(CraftUtils.getHandled(item), NMSCore.COMPOUND_TAG.newComp()));
    }
}
