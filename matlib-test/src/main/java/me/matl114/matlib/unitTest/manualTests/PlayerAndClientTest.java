package me.matl114.matlib.unitTest.manualTests;

import java.util.Random;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerAndClientTest implements TestCase {
    @OnlineTest(name = "player method version test", automatic = false)
    public void test_playermethod(CommandSender sender) {
        Player p = (Player) sender;
        p.setNoPhysics(true);
    }

    @OnlineTest(name = "tp mine arua env build", automatic = false)
    public void test_tpmine(CommandSender sender) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        loc = loc.getBlock().getLocation();
        Random rand = new Random();
        int range = 30;
        for (var i = 0; i < 1000; ++i) {
            int nextDx = rand.nextInt(30);
            int nextDz = rand.nextInt(30);
            int nextDy = rand.nextInt(30);
            loc.getWorld().setType(loc.add(nextDx, nextDy, nextDz), Material.BLACK_CONCRETE);
        }

        ItemStack stack = new ItemStack(Material.NETHERITE_PICKAXE);
        stack.addEnchantment(Enchantment.EFFICIENCY, 20);
        p.getInventory().addItem(stack);
    }
}
