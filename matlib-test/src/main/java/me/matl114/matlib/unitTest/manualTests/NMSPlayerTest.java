package me.matl114.matlib.unitTest.manualTests;

import java.util.List;
import java.util.Locale;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class NMSPlayerTest implements TestCase {
    @OnlineTest(name = "special item give test", automatic = false)
    public void test_test(CommandSender sender) throws Throwable {
        Player p = (Player) sender;
        PlayerInventory playerInventory = p.getInventory();
        ItemStack stack = playerInventory.getItemInMainHand();
        Object nms = CraftBukkit.ITEMSTACK.handleGetter(stack);
        NMSItem.ITEMSTACK.setItem(
                nms,
                NMSCore.REGISTRIES.getValue(
                        BuiltInRegistryEnum.ITEM, NMSCore.NAMESPACE_KEY.newNSKey("minecraft", "myitem")));
        Debug.logger(NMSItem.ITEMSTACK.getItem(nms));
        Debug.logger(NMSCore.REGISTRIES.getId(BuiltInRegistryEnum.ITEM, NMSItem.ITEMSTACK.getItem(nms)));
    }

    @OnlineTest(name = "item translation test", automatic = false)
    public void test_translation(CommandSender sender) throws Throwable {
        Player p = (Player) sender;
        // Debug.logger("before set",p.getInventory().getItemInMainHand());
        MatlibTest.getItemLanguageRegistry()
                .registerTranslation(Locale.CHINESE, "me.matlib.test.translation.path1", "&a这是中文");
        MatlibTest.getItemLanguageRegistry()
                .registerTranslation(Locale.US, "me.matlib.test.translation.path1", "&b这是英文");
        MatlibTest.getItemLanguageRegistry()
                .registerTranslation(Locale.CHINESE, "me.matlib.test.translation.path2", "&c这是中文2");
        MatlibTest.getItemLanguageRegistry()
                .registerTranslation(Locale.US, "me.matlib.test.translation.path2", "&d这是英文2");
        ItemStack stackWithTranslation = ItemUtils.newStack(Material.CHEST, 3);
        ItemMeta meta1 = stackWithTranslation.getItemMeta();
        Style commonStyle = Style.style().color(TextColor.color(255, 0, 0)).build();
        Component name = Component.empty()
                .append(Component.text("并非翻译部分名字").style(commonStyle))
                .append(Component.translatable("me.matlib.test.translation.path1")
                        .append(Component.text("并非翻译部分名字2").style(commonStyle)));
        meta1.displayName(name);
        Component lore = Component.empty()
                .append(Component.text("并非翻译部分lore").style(commonStyle))
                .append(Component.translatable("me.matlib.test.translation.path2"))
                .append(Component.text("并非翻译部分lore2").style(commonStyle));
        meta1.lore(List.of(lore));
        stackWithTranslation.setItemMeta(meta1);
        ItemStack stackClone = stackWithTranslation.clone();
        p.getInventory().setItemInMainHand(stackClone);
        stackClone = p.getInventory().getItemInMainHand();
        ExecutorUtils.sleep(1000);
        Debug.logger(stackClone);
    }
}
