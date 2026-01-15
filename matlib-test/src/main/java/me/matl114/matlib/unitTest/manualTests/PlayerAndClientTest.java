package me.matl114.matlib.unitTest.manualTests;

import java.util.Random;

import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.core.nms.network.PacketEvent;
import me.matl114.matlib.core.nms.network.PacketHandler;
import me.matl114.matlib.core.nms.network.PacketListener;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSEntity;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.nmsMirror.impl.NMSNetwork;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.WorldUtils;
import me.matl114.matlib.utils.chat.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

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
    PacketListenerAttack packetListenerAttack = new PacketListenerAttack();
    @OnlineTest(name = "player attack packet listener", automatic = false)
    public void test_playerattack(CommandSender sender) {
        if(packetListenerAttack == null){
            packetListenerAttack = new PacketListenerAttack();
            MatlibTest.getPacketEventManager().registerListener(packetListenerAttack, this);

        }else{
            MatlibTest.getPacketEventManager().unregisterAll(packetListenerAttack, this);
            packetListenerAttack = null;
        }
    }

    public static class PacketListenerAttack implements PacketListener{
        @PacketHandler(type = GamePacket.SERVERBOUND_INTERACT, priority = 0, ignoreIfCancel = false)
        public void onServerboundInteract(PacketEvent packet) {
            Entity user = NMSEntity.ENTITY.getBukkitEntity( packet.getClient().getPlayer());
            if(user instanceof Player pl){
                World world = pl.getWorld();

                ThreadUtils.executeSync(()->{
                    Object target = NMSNetwork.PACKETS.serverboundInteractPacket$getTarget(packet.getPacket(), WorldUtils.getHandledWorld(world));
                    Entity targetEntity = NMSEntity.ENTITY.getBukkitEntity(target);
                    if(targetEntity instanceof Player player){
                        double interactRange = 3.0F;
                        BoundingBox box = player.getBoundingBox();
                        double dis =  distanceToSqr(box, pl.getLocation());
                        Component component;
                        if(dis > MathUtils.square(interactRange + 1.0F)){
                            component = ComponentUtils.fromLegacyString(AddUtils.resolveColor("&c%s 在试图攻击 %s 的时候失败了, 距离 %.2f".formatted(pl.getName(), player.getName(), Math.sqrt( dis))));

                        }else{
                            component = ComponentUtils.fromLegacyString(AddUtils.resolveColor("&a%s 成功攻击 %s ,距离 %.2f".formatted(pl.getName(), player.getName(), Math.sqrt(dis))));

                        }
                        Location loc2 = pl.getLocation();
                        Location loc3 = player.getLocation();
                        String cmp = "[%.2f,%.2f,%.2f]".formatted(loc2.getX(), loc2.getY(), loc2.getZ()) +  "[%.2f,%.2f,%.2f]".formatted(loc3.getX(), loc3.getY(), loc3.getZ());
                        component = component.style(component.style().toBuilder().hoverEvent(
                            ComponentUtils.fromLegacyString(AddUtils.resolveColor("&c双方位置: 攻击方: " + "[%.2f,%.2f,%.2f]".formatted(loc2.getX(), loc2.getY(), loc2.getZ()) + ", 被攻击方 : " + "[%.2f,%.2f,%.2f], 点击拷贝".formatted(loc3.getX(), loc3.getY(), loc3.getZ())))
                        ).clickEvent(ClickEvent.copyToClipboard(cmp)));

                        Bukkit.getServer().broadcast(component);
                    }

                });

            }
        }

        public double distanceToSqr(BoundingBox box, Location pos) {
            double d = Math.max(Math.max(box.getMinX() - pos.getX(), pos.getX() - box.getMaxX()), 0.0);
            double e = Math.max(Math.max(box.getMinY() - pos.getY(), pos.getY() - box.getMaxY()), 0.0);
            double f = Math.max(Math.max(box.getMinZ() - pos.getZ(), pos.getZ() - box.getMaxZ()), 0.0);
            return d* d + e* e + f* f;
        }
    }
}
