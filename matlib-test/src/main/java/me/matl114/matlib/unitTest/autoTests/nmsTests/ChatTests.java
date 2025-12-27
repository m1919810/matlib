package me.matl114.matlib.unitTest.autoTests.nmsTests;

import java.util.List;
import me.matl114.matlib.algorithms.algorithm.StringUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.nmsMirror.impl.NMSChat;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatTests implements TestCase {
    @OnlineTest(name = "nms chat2legacy test")
    public void test_chatComp() throws Throwable {
        Debug.logger("start deserialize test");
        Debug.logger(ChatUtils.deserializeLegacy("114514"));
        for (int i = 0; i < 5; ++i) {
            String rand = StringUtils.randString(7);
            String colored = TextUtils.colorPseudorandomString(rand);
            Debug.logger("testing on string", colored);
            ItemStack stack = ItemUtils.newStack(Material.CHEST, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(colored);
            Object val = CraftUtils.getDisplayNameHandle().get(meta);
            if (val instanceof String) {
                AssertEq(val, NMSChat.CHATCOMPONENT.toJson(ChatUtils.deserializeLegacy(colored)));
            } else {
                AssertEq(val, ChatUtils.deserializeLegacy(colored));
            }
        }
        Debug.logger("start serialize test");
        String rand0 = StringUtils.randString(7);
        String colored0 = TextUtils.colorPseudorandomString(rand0);
        Iterable<?> comp = ChatUtils.deserializeLegacy(colored0);
        Debug.logger(comp);
        Debug.logger(ChatUtils.serializeToLegacy(comp));
        for (int i = 0; i < 5; ++i) {
            String rand = StringUtils.randString(7);
            String colored = TextUtils.colorPseudorandomString(rand);
            Debug.logger("testing on string", colored);
            ItemStack stack = ItemUtils.newStack(Material.CHEST, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(colored);
            Object val = CraftUtils.getDisplayNameHandle().get(meta);
            if (val instanceof String) {
                AssertEq(meta.getDisplayName(), ChatUtils.serializeToLegacy(NMSChat.CHATCOMPONENT.fromJson((String)
                        val)));
            }
        }
        Debug.logger("start plain text test");
        for (int i = 0; i < 5; ++i) {
            String rand = StringUtils.randString(7);
            String colored = TextUtils.colorPseudorandomString(rand);
            Debug.logger("testing on string", colored);
            Iterable<?> comp0 = ChatUtils.deserializeLegacy(colored);
            AssertEq(rand, ChatUtils.getPlainString(comp0));
        }
    }

    @OnlineTest(name = "test component format rule")
    public void test_cpfr() throws Throwable {
        Component component = Component.text("main");
        component = component.append(Component.text("sub1"));
        component = component.append(
                Component.text("sub2").append(Component.text("subsub1")).append(Component.text("subsub2")));

        Bukkit.getServer().sendMessage(component);
        ItemMeta meta = new ItemStack(Material.STONE).getItemMeta();
        meta.displayName(component);
        Debug.logger(meta.getDisplayName());
        Iterable<?> adventure = NMSChat.CHATCOMPONENT.newAdventure(component);
        Iterable<?> nms = NMSChat.CHATCOMPONENT.deepConverted(adventure);
        Debug.logger(nms);
        Debug.logger(NMSChat.CHATCOMPONENT.toJson(nms));
        Debug.logger(ChatUtils.serializeToLegacy(adventure));
        Debug.logger(ChatUtils.serializeToLegacy(nms));
    }

    @OnlineTest(name = "test item name and lore access")
    public void test_itemNameAndLoreAPI() throws Throwable {
        ItemStack stack = ItemUtils.newStack(Material.CHEST, 3);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtils.resolveColor("&3这是一个物品名字"));
        meta.setLore(List.of("&6这是第一行lore", "&7这是第二行lore", "&8&l这是第三行&alore"));
        stack.setItemMeta(meta);
        Object nms = ItemUtils.unwrapHandle(stack);
        Assert(NMSItem.ITEMSTACK.hasLore(nms));
        Assert(NMSItem.ITEMSTACK.hasCustomHoverName(nms));
        Debug.logger(NMSItem.ITEMSTACK.getHoverName(nms));

        Debug.logger(NMSItem.ITEMSTACK.getLoreView(nms, false));
        ListMapView<?, Iterable<?>> lorelist = NMSItem.ITEMSTACK.getLoreView(nms, false);
        lorelist.add(NMSChat.CHATCOMPONENT.append(NMSChat.CHATCOMPONENT.empty(), NMSChat.CHATCOMPONENT.literal("这是第四行lore,惊不惊喜意不意外")));
        lorelist.batchWriteback();
        Debug.logger(stack);
        Assert(stack.getItemMeta().getLore().size() == 4);
        // ValueAccess<Iterable<?>> displayNameAccess =
    }
}
