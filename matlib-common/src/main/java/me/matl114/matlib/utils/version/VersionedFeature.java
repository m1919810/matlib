package me.matl114.matlib.utils.version;

import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public interface VersionedFeature {

    static VersionedFeature feature = new VersionedFeatureImpl();

    static VersionedFeature getFeature() {
        return feature;
    }

    public Version getVersion();

    public Enchantment getEnchantment(String name);

    public Material getMaterial(String name);

    public boolean comparePotionMeta(PotionMeta meta1, PotionMeta meta2);

    public BlockState copyBlockStateTo(BlockState state1, Block block);

    public boolean differentSpecialMeta(ItemMeta meta1, ItemMeta meta2);

    public EntityType getEntityType(String name);

    public AttributeModifier createAttributeModifier(
            UUID uuid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot);

    public String getAttributeModifierName(AttributeModifier modifier);

    public boolean setAttributeModifierValue(AttributeModifier modifier, double value);

    public UUID getAttributeModifierUid(AttributeModifier modifier);

    public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier);

    public boolean matchBlockStateMeta(BlockStateMeta meta1, BlockStateMeta meta2);

    public PotionEffectType getPotionEffectType(String key);

    public <T extends Entity> T spawnEntity(
            Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason);

    static class VersionedFeatureImpl implements VersionedFeature {
        @Getter
        protected Version version;

        VersionedRegistry registry;
        VersionedAttribute attribute;
        VersionedMeta meta;
        VersionedWorld world;

        public VersionedFeatureImpl() {
            registry = VersionedRegistry.getInstance();
            attribute = VersionedAttribute.getInstance();
            meta = VersionedMeta.getInstance();
            world = VersionedWorld.getInstance();
        }

        //    protected final EnumSet<Material> blockItemWithDifferentId=EnumSet.noneOf(Material.class);

        @Override
        public Enchantment getEnchantment(String name) {
            return registry.getEnchantment(name);
        }

        public Material getMaterial(String name) {
            return registry.getMaterial(name);
        }

        public EntityType getEntityType(String name) {
            return registry.getEntityType(name);
        }

        public PotionEffectType getPotionEffectType(String key) {
            return registry.getPotionEffectType(key);
        }

        @Override
        public <T extends Entity> T spawnEntity(
                Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason) {
            return world.spawnEntity(location, clazz, consumer, reason);
        }

        @Override
        public AttributeModifier createAttributeModifier(
                UUID uuid, String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
            return attribute.createAttributeModifier(uuid, name, amount, operation, slot);
        }

        @Override
        public String getAttributeModifierName(AttributeModifier modifier) {
            return attribute.getAttributeModifierName(modifier);
        }

        @Override
        public boolean setAttributeModifierValue(AttributeModifier modifier, double value) {
            return attribute.setAttributeModifierValue(modifier, value);
        }

        @Override
        public UUID getAttributeModifierUid(AttributeModifier modifier) {
            return attribute.getAttributeModifierUid(modifier);
        }

        @Override
        public EquipmentSlot getAttributeModifierSlot(AttributeModifier modifier) {
            return attribute.getAttributeModifierSlot(modifier);
        }

        @Override
        public boolean comparePotionMeta(PotionMeta meta1, PotionMeta meta2) {
            return meta.comparePotionMeta(meta1, meta2);
        }

        @Override
        public BlockState copyBlockStateTo(BlockState state1, Block block) {
            return world.copyBlockStateTo(state1, block);
        }

        @Override
        public boolean matchBlockStateMeta(BlockStateMeta meta1, BlockStateMeta meta2) {
            return meta.matchBlockStateMeta(meta1, meta2);
        }

        @Override
        public boolean differentSpecialMeta(ItemMeta meta1, ItemMeta meta2) {
            return meta.differentSpecialMeta(meta1, meta2);
        }
    }
}
