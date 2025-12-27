package me.matl114.matlib.unitTest.autoTests.bukkitTests;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.chat.ComponentUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class BukkitAPITests implements TestCase {
    @OnlineTest(name = "Unsafe value tests")
    public void test_unsafe() throws Throwable {
        ItemStack item1 = CleanItemStack.ofBukkitClean(SlimefunItems.ELECTRIC_ORE_GRINDER_3);
        Debug.logger(item1);
        byte[] bytes = Bukkit.getUnsafe().serializeItem(item1);
        List<Byte> bytes1 = new ArrayList<>();
        for (byte var : bytes) {
            bytes1.add(var);
        }
        //  Debug.logger(bytes1);
        // Debug.logger(new String(bytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] var4;
        try {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);

            try {
                bukkitObjectOutputStream.writeObject(item1);
                var4 = outputStream.toByteArray();
            } catch (Throwable var9) {
                try {
                    bukkitObjectOutputStream.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            bukkitObjectOutputStream.close();
        } catch (Throwable var10) {
            try {
                outputStream.close();
            } catch (Throwable var7) {
                var10.addSuppressed(var7);
            }

            throw var10;
        }

        outputStream.close();
        List<Byte> bytes2 = new ArrayList<>();
        for (byte var : var4) {
            bytes2.add(var);
        }
        //   Debug.logger(bytes2);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Debug.logger(Bukkit.getUnsafe().getProtocolVersion());
        Debug.logger(Bukkit.getUnsafe().getMainLevelName());
        Debug.logger(Bukkit.getUnsafe().getItemTranslationKey(Material.CHEST));
    }

    @OnlineTest(name = "component item test")
    public void test_component_item() throws Throwable {
        ItemStack stack = new CleanItemStack(
                Material.DIAMOND, "shit, it is a name", List.of("&rShit, it is a lore with &aRESET flag"));
        ItemStack stackCopy = ItemUtils.copyStack(stack);
        Debug.logger(stackCopy);
        Component component = ComponentUtils.fromLegacyString(
                AddUtils.resolveColor(
                        "&ashit, it is a fucking lore with green color")); // Component.empty().append(Component.text("shit, it is a fucking lore"));
        Debug.logger(stackCopy.getLore());
        Debug.logger(stackCopy.lore());
        ItemStack stackCopy1 = stackCopy.clone();
        stackCopy1.setLore(stackCopy.getLore());
        Debug.logger(stackCopy1);
        var stackCopy2 = stackCopy.clone();
        var lore = stackCopy.lore();
        lore.add(component);
        stackCopy2.lore(lore);
        Debug.logger(stackCopy2);
        ItemMeta meta = stackCopy2.getItemMeta();
        meta.setDisplayName("");
        Debug.logger(meta.displayName());
        Debug.logger(meta.getDisplayName());
        meta.setLore(List.of(""));
        Debug.logger(meta.lore());
        Debug.logger(meta.getLore());
        meta.displayName(ComponentUtils.EMPTY);
        Debug.logger(meta.getDisplayName());
        Debug.logger(meta.displayName());
        meta.lore(List.of(ComponentUtils.EMPTY));
        Debug.logger(meta.getLore());
        ComponentUtils.addToLore(meta, ComponentUtils.EMPTY);
        Debug.logger(meta.lore());
        ComponentUtils.removeLoreLast(meta, 2);
        Debug.logger(meta.lore());
    }

    @OnlineTest(name = "skull tests")
    public void test_skull() throws Throwable {
        String hash = "e35032f4d7d01de8ec99d89f8723012d4e74fa73022c4facf1b57c7ff6ff0";
        String hash2 = "e35032f4d7d01de8ec99d89f8723012d4e74fa73022c4facf1b57c7ff6ff0";
        String hash3 = "e35032f4d7d01de8ec99d89f8723012d4e74fa73022c4facf1b57c7ff6ff0";
        UUID uuid = UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8));
        Debug.logger("check uid", uuid);
        Debug.logger(
                "check lapis uid",
                ((SkullMeta) (SlimefunItems.SYNTHETIC_SAPPHIRE.getItemMeta()))
                        .getPlayerProfile()
                        .getId());
    }
}
