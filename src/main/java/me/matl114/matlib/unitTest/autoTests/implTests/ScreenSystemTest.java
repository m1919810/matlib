package me.matl114.matlib.unitTest.autoTests.implTests;

import com.google.gson.JsonElement;
import java.util.Objects;
import me.matl114.matlib.implement.custom.inventory.ScreenBuilder;
import me.matl114.matlib.implement.custom.inventory.ScreenTemplate;
import me.matl114.matlib.implement.custom.inventory.SlotProvider;
import me.matl114.matlib.implement.custom.inventory.inventoryImpl.ChestMenuImpl;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.serialize.CodecUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ScreenSystemTest implements TestCase {
    @OnlineTest(name = "test screen template")
    private void test_screen_template() {
        for (var entry : getTestCases("screen/screen_template.json")) {
            JsonElement element = entry.getValue();
            JsonElement input = getInput(element);
            ScreenTemplate template =
                    CodecUtils.decode(ScreenTemplate.CODEC, CodecUtils.jsonOp(), getArg(input, "template"));
            Debug.logger(template);
            ScreenBuilder templateBuilder = new ScreenBuilder(template)
                    .override(
                            getArgs(input, "override.slot").getAsInt(),
                            SlotProvider.instance()
                                    .withStack(ItemUtils.newStack(
                                            Material.getMaterial(getArgs(input, "override.type")
                                                    .getAsString()),
                                            1)));
            ChestMenuImpl menuBuilder = templateBuilder.createInventory(1, ChestMenuImpl.FACTORY);
            ChestMenu menu = menuBuilder.getResult();
            for (var value : entrySet(getOutput(element))) {
                int i = Integer.parseInt(value.getKey());
                ItemStack stack = menu.getItemInSlot(i);
                Material type = stack == null ? Material.AIR : stack.getType();
                Assert(Objects.equals(type.toString(), value.getValue().getAsString()));
            }
        }
    }

    @OnlineTest(name = "test screen template view", automatic = false, async = false)
    private void test_screen_template0(CommandSender p) {
        for (var entry : getTestCases("screen/screen_template.json")) {
            JsonElement element = entry.getValue();
            JsonElement input = getInput(element);
            ScreenTemplate template = CodecUtils.decode(ScreenTemplate.CODEC, CodecUtils.jsonOp(), input);
            Debug.logger(template);
            ScreenBuilder templateBuilder = new ScreenBuilder(template)
                    .override(7, SlotProvider.instance().withStack(ItemUtils.newStack(Material.NAME_TAG, 1)));
            ChestMenuImpl menuBuilder = templateBuilder.createInventory(1, ChestMenuImpl.FACTORY);
            ChestMenu menu = menuBuilder.getResult();
            menu.open((Player) p);
            return;
        }
    }
}
