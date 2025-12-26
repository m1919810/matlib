package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataType;

public class EntityToNbt implements TestCase, Listener {
    public EntityToNbt() {
        Bukkit.getPluginManager().registerEvents(this, MatlibTest.getInstance());
    }

    private final NamespacedKey KEY_ITEM = new NamespacedKey("testcase", "entity_wand");
    private final NamespacedKey KEY_UUID = new NamespacedKey("testcase", "entity_uuid");
    private final NamespacedKey KEY_SAVED = new NamespacedKey("testcase", "entity_saved");

    @OnlineTest(name = "test entity nbt save", async = false, automatic = false)
    private void test_entityNbtSave(CommandSender p) throws Throwable {
        Player pp = (Player) p;
        ItemStack stack = new ItemStack(Material.STICK);
        var meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_ITEM, PersistentDataType.BYTE, (byte) 1);
        stack.setItemMeta(meta);
        pp.getInventory().addItem(stack);
    }

    public ItemStack saveEntity(Entity entity) {
        Material material = Bukkit.getItemFactory().getSpawnEgg(entity.getType());
        material = material == null ? Material.PIG_SPAWN_EGG : material;
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof SpawnEggMeta spawnEgg) {
            EntitySnapshot snapshot = entity.createSnapshot();
            if (snapshot != null) {
                spawnEgg.setSpawnedEntity(snapshot);
            }
        }
        meta.getPersistentDataContainer()
                .set(KEY_UUID, PersistentDataType.STRING, entity.getUniqueId().toString());
        stack.setItemMeta(meta);
        return stack;
    }

    private boolean isItem(ItemStack stack) {
        return stack != null
                && stack.getType() == Material.STICK
                && stack.getPersistentDataContainer().has(KEY_ITEM);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof LivingEntity entity
                && entity.isValid()
                && isItem(event.getPlayer().getInventory().getItem(event.getHand()))) {
            event.setCancelled(true);
            // entity.getPersistentDataContainer().set(KEY_SAVED, PersistentDataType.BYTE, (byte)1);
            Location loc = entity.getLocation();
            ItemStack stack = saveEntity(entity);
            loc.getWorld().dropItemNaturally(loc, stack);
            entity.remove();
            event.getPlayer().sendMessage("&aABAB, ABABAB");
        }
    }
}
