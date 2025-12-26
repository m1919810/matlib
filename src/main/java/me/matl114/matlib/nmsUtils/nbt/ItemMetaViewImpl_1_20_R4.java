package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.ADVENTURE;
import static me.matl114.matlib.nmsMirror.impl.NMSItem.ITEMSTACK;
import static me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4.*;
import static me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ComponentCodecEnum.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.inventory.ItemEnum;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentEnum;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.RegistryUtils;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import me.matl114.matlib.nmsUtils.serialize.CodecUtils;
import me.matl114.matlib.nmsUtils.serialize.TypeOps;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.Version;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ItemMetaViewImpl_1_20_R4 extends AbstractItemMetaView {
    public ItemMetaViewImpl_1_20_R4(Object itemStack) {
        super(itemStack);
        if (HELPER == null) {
            HELPER = (ItemStackHelper_1_20_R4) ITEMSTACK;
        }
    }

    @Override
    protected Object2IntMap<Enchantment> initMap() {

        EnchantmentKeyMappingObject2IntMap map0 = new EnchantmentKeyMappingObject2IntMap();
        map0.init();
        return map0;
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> initModifiers() {
        Object val = HELPER.getFromPatch(itemStack, DataComponentEnum.ATTRIBUTE_MODIFIERS);
        if (val == null) {
            return LinkedHashMultimap.create();
        }
        var map = CraftBukkit.META.buildModifiersFromRaw(val);
        return map == null ? LinkedHashMultimap.create() : map;
    }

    private static final boolean hasTooltipField = !Version.getVersionInstance().isAtLeast(Version.v1_21_R4);

    protected class EnchantmentKeyMappingObject2IntMap extends AbstractObject2IntMap<Enchantment> {
        protected Object itemEnchantsMutable;
        Object2IntAVLTreeMap<?> rawMap;
        protected Object immutableView;

        public void init() {
            Object itemEnchants = HELPER.getFromPatch(itemStack, DataComponentEnum.ENCHANTMENTS);
            if (itemEnchants == null) {
                itemEnchants = DataComponentEnum.ITEMENCHANTMENTS_EMPTY;
            }
            this.itemEnchantsMutable = DATA_TYPES.newMutable(itemEnchants);
            this.rawMap = DATA_TYPES.itemEnchantMutable$enchants(this.itemEnchantsMutable);
            this.immutableView = DATA_TYPES.itemEnchantMutable$toImmutable(this.itemEnchantsMutable);
            // they share the same map; and here we set back the map!
            writeBack();
        }

        public void writeBack() {
            // set even when rawMap is empty: hide flag, it is different from empty
            if (!this.rawMap.isEmpty()
                    || (hasTooltipField && !(DATA_TYPES.itemEnchantMutable$showInTooltip(this.itemEnchantsMutable)))) {
                HELPER.setDataComponentValue(itemStack, DataComponentEnum.ENCHANTMENTS, this.immutableView);
            } else {
                HELPER.removeFromPatch(itemStack, DataComponentEnum.ENCHANTMENTS);
            }
        }

        @Override
        public int size() {
            return this.rawMap.size();
        }

        @Override
        public ObjectSet<Entry<Enchantment>> object2IntEntrySet() {
            return new AbstractObjectSet<Entry<Enchantment>>() {
                @Override
                public ObjectIterator<Entry<Enchantment>> iterator() {
                    ObjectBidirectionalIterator<? extends Entry<?>> delegate =
                            rawMap.object2IntEntrySet().iterator();
                    return new ObjectIterator<Entry<Enchantment>>() {
                        Entry_a mutableEntry = new Entry_a();

                        class Entry_a implements Entry<Enchantment> {
                            public Entry<?> currentDelegate;

                            @Override
                            public int getIntValue() {
                                return this.currentDelegate.getIntValue();
                            }

                            @Override
                            public int setValue(int i) {
                                int val = this.currentDelegate.setValue(i);
                                EnchantmentKeyMappingObject2IntMap.this.writeBack();
                                return val;
                            }

                            @Override
                            public Enchantment getKey() {
                                return RegistryUtils.minecraftToBukkit(
                                        NMSCore.REGISTRIES.holderValue(this.currentDelegate.getKey()),
                                        Registry.ENCHANTMENT);
                            }
                        }
                        ;

                        @Override
                        public boolean hasNext() {
                            return delegate.hasNext();
                        }

                        @Override
                        public Entry<Enchantment> next() {
                            mutableEntry.currentDelegate = delegate.next();
                            return mutableEntry;
                        }

                        public void remove() {
                            delegate.remove();
                            writeBack();
                        }
                    };
                }

                @Override
                public int size() {
                    return EnchantmentKeyMappingObject2IntMap.this.size();
                }
            };
        }

        public Object bukkitToMc(Enchantment ench) {
            return RegistryUtils.enchantmentToMinecraftHolder(ench);
        }

        @Override
        public int getInt(Object o) {
            return o instanceof Enchantment ench ? this.rawMap.getInt(bukkitToMc(ench)) : 0;
        }

        @Override
        public int put(Enchantment key, int value) {
            int re = ((Object2IntAVLTreeMap) this.rawMap).put(bukkitToMc(key), value);
            writeBack();
            return re;
        }

        @Override
        public int removeInt(Object val) {
            if (val instanceof Enchantment en) {
                int re = this.rawMap.removeInt(bukkitToMc(en));
                writeBack();
                return re;
            }
            return 0;
        }

        @Override
        public void clear() {
            this.rawMap.clear();
            writeBack();
        }
    }

    static ItemStackHelper_1_20_R4 HELPER;

    @Override
    public @Nullable Component displayName() {
        return ADVENTURE.asAdventure(nmsNameView.get());
    }

    @Override
    public void displayName(@Nullable Component component) {
        nmsNameView.set(ADVENTURE.asVanilla(component));
    }

    @Override
    public boolean hasItemName() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.ITEM_NAME);
    }

    @Override
    public @NotNull Component itemName() {
        return ADVENTURE.asAdventure((Iterable<?>) HELPER.getFromPatch(itemStack, DataComponentEnum.ITEM_NAME));
    }

    @Override
    public void itemName(@Nullable Component component) {
        HELPER.setDataComponentValue(itemStack, DataComponentEnum.ITEM_NAME, ADVENTURE.asVanilla(component));
    }

    @Override
    public @NotNull String getItemName() {
        return ChatUtils.serializeToLegacy((Iterable<?>) HELPER.getFromPatch(itemStack, DataComponentEnum.ITEM_NAME));
    }

    @Override
    public void setItemName(@Nullable String s) {
        HELPER.setDataComponentValue(itemStack, DataComponentEnum.ITEM_NAME, ChatUtils.deserializeLegacy(s));
    }

    @Override
    public boolean hasLocalizedName() {
        return false;
    }

    @Override
    public @NotNull String getLocalizedName() {
        throw VersionedUtils.removal();
    }

    @Override
    public void setLocalizedName(@Nullable String s) {
        throw VersionedUtils.removal();
    }

    @Override
    public @Nullable List<Component> lore() {
        return this.nmsLoreView.isEmpty() ? null : ADVENTURE.asAdventure(this.nmsLoreView);
    }

    @Override
    public void lore(@Nullable List<? extends Component> list) {
        CollectionUtils.mapAndSet(list, ADVENTURE::asVanilla, this.nmsLoreView);
    }

    @Override
    public boolean hasCustomModelData() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.CUSTOM_MODEL_DATA);
    }

    @Override
    public int getCustomModelData() {
        // return DATA_TYPES.customModelData$value();
        Object value = HELPER.get(itemStack, DataComponentEnum.CUSTOM_MODEL_DATA);
        return (Integer) CodecUtils.encodeEnd(CUSTOM_MODEL_DATA.encodeStart(TypeOps.I, value));
    }

    private <T> T version(T val) {
        if (val != null) {
            return val;
        }
        throw VersionedUtils.versionLow();
    }

    private boolean versionTag(Object val) {
        return val != null && HELPER.hasInPatch(itemStack, val);
    }

    @Override
    public void setCustomModelData(@Nullable Integer integer) {
        if (integer == null) {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.CUSTOM_MODEL_DATA);
        } else {
            HELPER.setDataComponentValue(
                    itemStack,
                    DataComponentEnum.CUSTOM_MODEL_DATA,
                    CodecUtils.result(CUSTOM_MODEL_DATA.decode(TypeOps.I, integer)));
        }
    }

    @Override
    public boolean hasEnchantable() {
        return versionTag((DataComponentEnum.ENCHANTABLE));
    }

    @Override
    public int getEnchantable() {
        // return DATA_TYPES.enchantable$value(HELPER.get(itemStack, DataComponentEnum.ENCHANTABLE));
        Object value = HELPER.get(itemStack, version(DataComponentEnum.ENCHANTABLE));
        return (Integer) CodecUtils.encodeEnd(version(ENCHANTABLE).encodeStart(TypeOps.I, value));
    }

    @Override
    public void setEnchantable(@Nullable Integer integer) {
        if (integer == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.ENCHANTABLE));
        } else {
            HELPER.setDataComponentValue(
                    itemStack,
                    version(DataComponentEnum.ENCHANTABLE),
                    CodecUtils.result(version(ENCHANTABLE).decode(TypeOps.I, integer)));
        }
    }

    @Override
    public boolean addEnchant(@NotNull Enchantment ench, int level, boolean b) {
        if (b || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            int val = enchantmentMap().put(ench, level);
            return val != level;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(@NotNull Enchantment enchantment) {
        return enchantmentMap().removeInt(enchantment) != 0;
    }

    @Override
    public void removeEnchantments() {
        enchantmentMap().clear();
    }

    private static final Object UNBREAKABLE_TOOLTIPS =
            CodecUtils.decode(UNBREAKABLE, TypeOps.I, Map.of("show_in_tooltip", true));
    private static final Object UNBREAKABLE_NO_TOOLTIPS =
            CodecUtils.decode(UNBREAKABLE, TypeOps.I, Map.of("show_in_tooltip", false));
    private static final Map<Object, Object> TYPE_TO_TOGGLEHELPERS;
    private static final Object HELPER_TRIM;
    private static final Object HELPER_DYED_COLOR;
    private static final Object HELPER_ENCHANTMENTS;
    private static final Object HELPER_UNBREAKABLE;
    private static final Object HELPER_CAN_BREAK;
    private static final Object HELPER_STORED_ENCHANTMENTS;
    private static final Object HELPER_CAN_PLACE_ON;
    private static final Object HELPER_ATTRIBUTE_MODIFIERS;
    // private static final Object HELPER_JUKEBOX_PLAYABLE;
    static {
        if (!ItemMetaView.versionAtLeast1_21_R4) {
            Class<?> clazz0;
            try {
                clazz0 = ObfManager.getManager()
                        .reobfClass("net.minecraft.world.level.storage.loot.functions.ToggleTooltips");
                List<Field> fields = ReflectUtils.getAllFieldsRecursively(clazz0).stream()
                        .filter(m -> Modifier.isStatic(m.getModifiers()))
                        .toList();
                TYPE_TO_TOGGLEHELPERS = Utils.matchName(fields, "TOGGLES");
                HELPER_TRIM = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.TRIM));
                HELPER_DYED_COLOR = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.DYED_COLOR));
                HELPER_ENCHANTMENTS = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.ENCHANTMENTS));
                HELPER_UNBREAKABLE = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.UNBREAKABLE));
                HELPER_CAN_BREAK = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.CAN_BREAK));
                HELPER_STORED_ENCHANTMENTS =
                        Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.STORED_ENCHANTMENTS));
                HELPER_CAN_PLACE_ON = Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.CAN_PLACE_ON));
                HELPER_ATTRIBUTE_MODIFIERS =
                        Objects.requireNonNull(TYPE_TO_TOGGLEHELPERS.get(DataComponentEnum.ATTRIBUTE_MODIFIERS));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            TYPE_TO_TOGGLEHELPERS = null;
            HELPER_TRIM = null;
            HELPER_DYED_COLOR = null;
            HELPER_ENCHANTMENTS = null;
            HELPER_UNBREAKABLE = null;
            HELPER_CAN_BREAK = null;
            HELPER_STORED_ENCHANTMENTS = null;
            HELPER_CAN_PLACE_ON = null;
            HELPER_ATTRIBUTE_MODIFIERS = null;
        }
    }

    protected void setFlag(ItemFlag flag, boolean shouldDisplay) {
        Object type = null;
        switch (flag) {
            case HIDE_ENCHANTS:
                EnchantmentKeyMappingObject2IntMap mmmap = ((EnchantmentKeyMappingObject2IntMap) this.enchantmentMap());
                // if no enchant exist , should set the flag
                //                if(mmmap.rawMap.isEmpty()){
                //                    break;
                //                }
                DATA_TYPES.itemEnchantMutable$setShowInTooltip(mmmap.itemEnchantsMutable, shouldDisplay);
                mmmap.immutableView = DATA_TYPES.itemEnchantMutable$toImmutable(mmmap.itemEnchantsMutable);
                mmmap.writeBack();
                break;
            case HIDE_ADDITIONAL_TOOLTIP:
                if (shouldDisplay) {
                    HELPER.removeFromPatch(itemStack, DataComponentEnum.HIDE_ADDITIONAL_TOOLTIP);
                } else {
                    HELPER.setDataComponentValue(itemStack, DataComponentEnum.HIDE_ADDITIONAL_TOOLTIP, Env.UNIT);
                }
                break;
            case HIDE_DESTROYS:
                DATA_TYPES.toggleTooltipsAtType(HELPER_CAN_BREAK, itemStack, shouldDisplay);
                break;
            case HIDE_PLACED_ON:
                DATA_TYPES.toggleTooltipsAtType(HELPER_CAN_PLACE_ON, itemStack, shouldDisplay);
                break;
            case HIDE_UNBREAKABLE:
                DATA_TYPES.toggleTooltipsAtType(HELPER_UNBREAKABLE, itemStack, shouldDisplay);
                break;
            case HIDE_ATTRIBUTES:
                DATA_TYPES.toggleTooltipsAtType(HELPER_ATTRIBUTE_MODIFIERS, itemStack, shouldDisplay);
                break;
            case HIDE_STORED_ENCHANTS:
                DATA_TYPES.toggleTooltipsAtType(HELPER_STORED_ENCHANTMENTS, itemStack, shouldDisplay);
                break;
            case HIDE_ARMOR_TRIM:
                DATA_TYPES.toggleTooltipsAtType(HELPER_TRIM, itemStack, shouldDisplay);
                break;
            case HIDE_DYE:
                DATA_TYPES.toggleTooltipsAtType(HELPER_DYED_COLOR, itemStack, shouldDisplay);
                break;
            default:
                throw new IllegalArgumentException("Unexpected item flag " + flag);
        }
    }

    protected boolean getVisibilityForFlag(ItemFlag flag) {
        Object type = null;
        switch (flag) {
            case HIDE_ENCHANTS:
                EnchantmentKeyMappingObject2IntMap mmmap = ((EnchantmentKeyMappingObject2IntMap) this.enchantmentMap());
                // if no enchant exist , do not set the flag
                if (mmmap.rawMap.isEmpty()) {
                    return true;
                }
                // use cached instead of reading again, because we are not sure whether this is modified by someone else
                return DATA_TYPES.itemEnchantMutable$showInTooltip(mmmap.itemEnchantsMutable);
            case HIDE_ADDITIONAL_TOOLTIP:
                return !HELPER.hasInPatch(itemStack, DataComponentEnum.HIDE_ADDITIONAL_TOOLTIP);
            case HIDE_DESTROYS:
                type = DataComponentEnum.CAN_BREAK;
            case HIDE_PLACED_ON:
                if (type == null) {
                    type = DataComponentEnum.CAN_PLACE_ON;
                }
                Object predicate = HELPER.getFromPatch(itemStack, type);
                // if no adventureMode predicate exist, it is visible as default
                return predicate == null || DATA_TYPES.adventureModePredicate$showInTooltip(predicate);
            case HIDE_UNBREAKABLE:
                Object unbreakable = HELPER.getFromPatch(itemStack, DataComponentEnum.UNBREAKABLE);
                if (unbreakable == null) return true;
                Map encodeValue = (Map) CodecUtils.encode(UNBREAKABLE, TypeOps.I, unbreakable);
                Object value = encodeValue.get("show_in_tooltip");
                return value instanceof Boolean bool ? bool : true;
            case HIDE_ATTRIBUTES:
                Object itemModifier = HELPER.getFromPatch(itemStack, DataComponentEnum.ATTRIBUTE_MODIFIERS);
                if (itemModifier != null) {
                    return DATA_TYPES.itemAttributeModifiers$showInTooltip(itemModifier);
                }
                return true;
            case HIDE_STORED_ENCHANTS:
                Object storedItemEnchant = HELPER.getFromPatch(itemStack, DataComponentEnum.STORED_ENCHANTMENTS);
                if (storedItemEnchant != null) {
                    return DATA_TYPES.itemEnchants$ShowTooltip(storedItemEnchant);
                }
                return true;
            case HIDE_ARMOR_TRIM:
                Object trim = HELPER.getFromPatch(itemStack, DataComponentEnum.TRIM);
                if (trim != null) {
                    return DATA_TYPES.armorTrim$showInTooltip(trim);
                }
                return true;
            case HIDE_DYE:
                Object dye = HELPER.getFromPatch(itemStack, DataComponentEnum.DYED_COLOR);
                if (dye != null) {
                    return DATA_TYPES.dyeItemColor$showInTooltip(dye);
                }
                return true;
            default:
                throw new IllegalArgumentException("Unexpected item flag " + flag);
        }
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {
        for (var flag : itemFlags) {
            setFlag(flag, false);
        }
    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
        for (var flag : itemFlags) {
            setFlag(flag, true);
        }
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        EnumSet<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
        for (var flag : ItemFlag.values()) {
            if (!getVisibilityForFlag(flag)) {
                flags.add(flag);
            }
        }
        return flags;
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        return !getVisibilityForFlag(itemFlag);
    }

    @Override
    public boolean isHideTooltip() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.HIDE_TOOLTIP);
    }

    @Override
    public void setHideTooltip(boolean b) {
        if (b) {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.HIDE_TOOLTIP, Env.UNIT);
        } else {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.HIDE_TOOLTIP);
        }
    }

    @Override
    public boolean hasTooltipStyle() {
        return versionTag((DataComponentEnum.TOOLTIP_STYLE));
    }

    @Override
    public @Nullable NamespacedKey getTooltipStyle() {
        Object resourceLocation = HELPER.get(itemStack, version(DataComponentEnum.TOOLTIP_STYLE));
        return resourceLocation == null ? null : RegistryUtils.fromMinecraftNSK(resourceLocation);
    }

    @Override
    public void setTooltipStyle(@Nullable NamespacedKey namespacedKey) {
        if (namespacedKey == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.TOOLTIP_STYLE));
        } else {
            HELPER.setDataComponentValue(
                    itemStack, version(DataComponentEnum.TOOLTIP_STYLE), RegistryUtils.fromBukkit(namespacedKey));
        }
    }

    @Override
    public boolean hasItemModel() {
        return versionTag((DataComponentEnum.ITEM_MODEL));
    }

    @Override
    public @Nullable NamespacedKey getItemModel() {
        Object resourceLocation = HELPER.get(itemStack, version(DataComponentEnum.ITEM_MODEL));
        return resourceLocation == null ? null : RegistryUtils.fromMinecraftNSK(resourceLocation);
    }

    @Override
    public void setItemModel(@Nullable NamespacedKey namespacedKey) {
        if (namespacedKey == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.ITEM_MODEL));
        } else {
            HELPER.setDataComponentValue(
                    itemStack, version(DataComponentEnum.ITEM_MODEL), RegistryUtils.fromBukkit(namespacedKey));
        }
    }

    @Override
    public boolean isUnbreakable() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.UNBREAKABLE);
    }

    @Override
    public void setUnbreakable(boolean b) {
        if (b) {
            // HELPER.setDataComponentValue(itemStack, DataComponentEnum.UNBREAKABLE, );
            // fix the unbreakable flag here
            if (!isUnbreakable()) {
                HELPER.setDataComponentValue(itemStack, DataComponentEnum.UNBREAKABLE, UNBREAKABLE_TOOLTIPS);
            }
        } else {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.UNBREAKABLE);
        }
    }

    @Override
    public boolean hasEnchantmentGlintOverride() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.ENCHANTMENT_GLINT_OVERRIDE);
    }

    @Override
    public @NotNull Boolean getEnchantmentGlintOverride() {
        return (Boolean) HELPER.getFromPatch(itemStack, DataComponentEnum.ENCHANTMENT_GLINT_OVERRIDE);
    }

    @Override
    public void setEnchantmentGlintOverride(@Nullable Boolean aBoolean) {
        if (aBoolean != null) {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.ENCHANTMENT_GLINT_OVERRIDE, aBoolean);
        } else {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.ENCHANTMENT_GLINT_OVERRIDE);
        }
    }

    @Override
    public boolean isGlider() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.GLIDER);
    }

    @Override
    public void setGlider(boolean b) {
        if (b) {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.GLIDER, Env.UNIT);
        } else {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.GLIDER);
        }
    }

    @Override
    public boolean hasDamageResistant() {
        return versionTag((DataComponentEnum.DAMAGE_RESISTANT));
    }

    @Override
    public @Nullable Tag<DamageType> getDamageResistant() {
        Codec<Object> codec = version(DAMAGE_RESISTANT);
        Object damageResistent = HELPER.getFromPatch(itemStack, version(DataComponentEnum.DAMAGE_RESISTANT));
        if (damageResistent == null) return null;
        Map<String, String> mapper = (Map<String, String>) CodecUtils.encode(codec, TypeOps.I, damageResistent);
        String value = mapper.get("types");
        return Bukkit.getTag(
                DamageTypeTags.REGISTRY_DAMAGE_TYPES,
                Objects.requireNonNull(RegistryUtils.fromTagName(value)),
                DamageType.class);
    }

    @Override
    public void setDamageResistant(@Nullable Tag<DamageType> tag) {
        if (tag != null) {
            String key = "#" + tag.getKey().toString();
            Object decode = CodecUtils.decode(version(DAMAGE_RESISTANT), TypeOps.I, Map.of("types", key));
            HELPER.setDataComponentValue(itemStack, version(DataComponentEnum.DAMAGE_RESISTANT), decode);
        } else {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.DAMAGE_RESISTANT));
        }
    }

    @Override
    public boolean hasMaxStackSize() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.MAX_STACK_SIZE);
    }

    @Override
    public int getMaxStackSize() {
        return (Integer) HELPER.getFromPatch(itemStack, DataComponentEnum.MAX_STACK_SIZE);
    }

    @Override
    public void setMaxStackSize(@Nullable Integer integer) {
        if (integer != null) {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.MAX_STACK_SIZE, integer);
        } else {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.MAX_STACK_SIZE);
        }
    }

    private static final Map<ItemRarity, Object> RA_BUKKIT_TO_NMS = new EnumMap<>(ItemRarity.class);
    private static final Map<Enum, ItemRarity> RA_NMS_TO_BUKKIT;

    static {
        RA_BUKKIT_TO_NMS.put(ItemRarity.COMMON, ItemEnum.RARITY_COMMON);
        RA_BUKKIT_TO_NMS.put(ItemRarity.UNCOMMON, ItemEnum.RARITY_UNCOMMON);
        RA_BUKKIT_TO_NMS.put(ItemRarity.RARE, ItemEnum.RARITY_RARE);
        RA_BUKKIT_TO_NMS.put(ItemRarity.EPIC, ItemEnum.RARITY_EPIC);
        // RA_NMS_TO_BUKKIT = new EnumMap<>(ItemEnum.RARITY_COMMON.getClass());
        Map<Enum, ItemRarity> mapA = new HashMap<>();
        mapA.put(ItemEnum.RARITY_COMMON, ItemRarity.COMMON);
        mapA.put(ItemEnum.RARITY_UNCOMMON, ItemRarity.UNCOMMON);
        mapA.put(ItemEnum.RARITY_EPIC, ItemRarity.EPIC);
        mapA.put(ItemEnum.RARITY_RARE, ItemRarity.RARE);
        RA_NMS_TO_BUKKIT = new EnumMap<>(((Map<? extends Enum, ItemRarity>) mapA));
    }

    @Override
    public boolean hasRarity() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.RARITY);
    }

    @Override
    public @NotNull ItemRarity getRarity() {
        return RA_NMS_TO_BUKKIT.get(HELPER.getFromPatch(itemStack, DataComponentEnum.RARITY));
    }

    @Override
    public void setRarity(@Nullable ItemRarity itemRarity) {
        if (itemRarity == null) {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.RARITY);
        } else {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.RARITY, RA_BUKKIT_TO_NMS.get(itemRarity));
        }
    }

    @Override
    public boolean hasUseRemainder() {
        return versionTag(DataComponentEnum.USE_REMAINDER);
    }

    @Override
    public @Nullable ItemStack getUseRemainder() {
        Object item = HELPER.getFromPatch(itemStack, version(DataComponentEnum.USE_REMAINDER));
        return item == null ? null : ItemUtils.asCraftMirror(DATA_TYPES.useRemainder$Remain(item));
    }

    @Override
    public void setUseRemainder(@Nullable ItemStack itemStack0) {
        if (itemStack0 == null || itemStack0.getType().isAir()) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.USE_REMAINDER));
        } else {
            HELPER.setDataComponentValue(
                    itemStack, version(DataComponentEnum.USE_REMAINDER), ItemUtils.asNMSCopy(itemStack0));
        }
    }

    @Override
    public boolean hasUseCooldown() {
        return versionTag(DataComponentEnum.USE_COOLDOWN);
    }

    @Override
    public @NotNull UseCooldownComponent getUseCooldown() {
        Object val = HELPER.getFromPatch(itemStack, version(DataComponentEnum.USE_COOLDOWN));
        return (UseCooldownComponent)
                (val != null
                        ? DATA_TYPES.useCooldownComponent$Craft(val)
                        : DATA_TYPES.useCooldownComponent$Craft(DATA_TYPES.newUseCooldown(1.0f)));
    }

    @Override
    public void setUseCooldown(@Nullable UseCooldownComponent useCooldownComponent) {
        if (useCooldownComponent == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.USE_COOLDOWN));
        } else {
            HELPER.setDataComponentValue(
                    itemStack,
                    version(DataComponentEnum.USE_COOLDOWN),
                    DATA_TYPES.useCooldownComponent$Handle(useCooldownComponent));
        }
    }

    @Override
    public boolean hasFood() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.FOOD);
    }

    private static final ItemMeta SAMPLE_META = new ItemStack(Material.STONE).getItemMeta();

    static {
        Preconditions.checkArgument(!SAMPLE_META.hasFood());
        Preconditions.checkArgument(!SAMPLE_META.hasTool());
    }

    @Override
    public @NotNull FoodComponent getFood() {
        Object val = HELPER.getFromPatch(itemStack, DataComponentEnum.FOOD);
        return val != null ? DATA_TYPES.foodProperties$Craft(val) : SAMPLE_META.getFood();
    }

    @Override
    public void setFood(@Nullable FoodComponent foodComponent) {
        if (foodComponent == null) {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.FOOD);
        } else {
            HELPER.setDataComponentValue(
                    itemStack, DataComponentEnum.FOOD, DATA_TYPES.foodProperties$Handle(foodComponent));
        }
    }

    @Override
    public boolean hasTool() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.TOOL);
    }

    @Override
    public @NotNull ToolComponent getTool() {
        Object val = HELPER.getFromPatch(itemStack, DataComponentEnum.TOOL);
        return val != null ? DATA_TYPES.tool$Craft(val) : SAMPLE_META.getTool();
    }

    @Override
    public void setTool(@Nullable ToolComponent toolComponent) {
        if (toolComponent == null) {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.TOOL);
        } else {
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.TOOL, DATA_TYPES.tool$Handle(toolComponent));
        }
    }

    @Override
    public boolean hasEquippable() {
        return versionTag(DataComponentEnum.EQUIPPABLE);
    }

    @Override
    public @NotNull EquippableComponent getEquippable() {
        Object val = HELPER.getFromPatch(itemStack, version(DataComponentEnum.EQUIPPABLE));
        return val != null ? (EquippableComponent) DATA_TYPES.equippable$Craft(val) : SAMPLE_META.getEquippable();
    }

    @Override
    public void setEquippable(@Nullable EquippableComponent equippableComponent) {
        if (equippableComponent == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.EQUIPPABLE));
        } else {
            HELPER.setDataComponentValue(
                    itemStack,
                    version(DataComponentEnum.EQUIPPABLE),
                    DATA_TYPES.equippable$Handle(equippableComponent));
        }
    }

    @Override
    public boolean hasJukeboxPlayable() {
        return versionTag(DataComponentEnum.JUKEBOX_PLAYABLE);
    }

    @Override
    public @NotNull JukeboxPlayableComponent getJukeboxPlayable() {
        Object val = HELPER.getFromPatch(itemStack, version(DataComponentEnum.JUKEBOX_PLAYABLE));
        return val != null
                ? (JukeboxPlayableComponent) DATA_TYPES.jukebox$Craft(val)
                : SAMPLE_META.getJukeboxPlayable();
    }

    @Override
    public void setJukeboxPlayable(@Nullable JukeboxPlayableComponent jukeboxPlayableComponent) {
        if (jukeboxPlayableComponent == null) {
            HELPER.removeFromPatch(itemStack, version(DataComponentEnum.JUKEBOX_PLAYABLE));
        } else {
            HELPER.setDataComponentValue(
                    itemStack,
                    version(DataComponentEnum.JUKEBOX_PLAYABLE),
                    DATA_TYPES.jukebox$Handle(jukeboxPlayableComponent));
        }
    }

    @Override
    public boolean hasAttributeModifiers() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.ATTRIBUTE_MODIFIERS);
    }

    private static final boolean isAttributeVersion = Holder.empty()
            .thenApplyCaught(i -> {
                return EquipmentSlotGroup.class;
            })
            .whenComplete((i, e) -> {
                return e == null;
            })
            .get();
    private static final Map<Object, Object> SLOT_GROUP_BUKKIT2NMS = new HashMap<>();

    static {
        if (isAttributeVersion) {
            Class<?> clazz0;
            try {
                clazz0 = ObfManager.getManager().reobfClass("net.minecraft.world.entity.EquipmentSlotGroup");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            Map<String, ?> enumMap = ReflectUtils.getEnumMap(clazz0);
            enumMap.forEach(
                    (string, object) -> SLOT_GROUP_BUKKIT2NMS.put(EquipmentSlotGroup.getByName(string), object));
        }
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot) {
        if (isAttributeVersion) {
            SetMultimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
            if (!hasAttributeModifiers()) return result;
            for (Map.Entry<Attribute, AttributeModifier> entry :
                    this.modifiers().entries()) {
                if (entry.getValue().getSlotGroup().test(slot)) { // Paper - correctly test slot against group
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return result;
        } else {
            return super.getAttributeModifiers(slot);
        }
    }

    @Override
    protected void syncModifierChange() {
        Multimap<Attribute, AttributeModifier> mdMap = this.modifierMultimap;
        if (mdMap == null) return;
        if (mdMap.isEmpty()) {
            HELPER.removeFromPatch(itemStack, DataComponentEnum.ATTRIBUTE_MODIFIERS);
        } else {
            // if tooltips field is absent, then all seen as show
            boolean showAttribute = !hasTooltipField || this.getVisibilityForFlag(ItemFlag.HIDE_ATTRIBUTES);
            Object builder = DATA_TYPES.itemAttributeModifierBuilder();
            for (var entry : mdMap.entries()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                Object attrNMS = CraftBukkit.META.attributeB2N(entry.getValue());
                Object attrHolder = RegistryUtils.attributeToMinecraftHolder(entry.getKey());
                if (attrHolder == null) continue;
                Object nmsSlot = SLOT_GROUP_BUKKIT2NMS.get(entry.getValue().getSlotGroup());
                DATA_TYPES.attributeBuilder$Add(builder, attrHolder, attrNMS, nmsSlot);
            }
            Object itemAttribute = DATA_TYPES.attributeBuilder$Build(builder);
            HELPER.setDataComponentValue(itemStack, DataComponentEnum.ATTRIBUTE_MODIFIERS, itemAttribute);
            // restore tooltips
            if (!showAttribute) {
                DATA_TYPES.toggleTooltipsAtType(HELPER_ATTRIBUTE_MODIFIERS, itemStack, false);
            }
        }
    }

    @Override
    public boolean removeAttributeModifier(@NotNull EquipmentSlot equipmentSlot) {
        if (!isAttributeVersion) {
            return super.removeAttributeModifier(equipmentSlot);
        }
        if (!hasAttributeModifiers()) {
            return false;
        }
        int removed = 0;
        Iterator<Map.Entry<Attribute, AttributeModifier>> iter =
                this.modifiers().entries().iterator();

        while (iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = iter.next();
            if (entry.getValue().getSlotGroup().test(equipmentSlot)) { // Paper - correctly test slot against group
                iter.remove();
                ++removed;
            }
        }
        if (removed > 0) {
            syncModifierChange();
            return true;
        }
        return false;
    }

    @Override
    public @NotNull String getAsComponentString() {
        Object patch = getAsComponentPatch();
        DynamicOps<?> nbtop = CodecUtils.nbtOp();
        StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
        for (var entry : COMPONENT_PATCH.entrySet(patch)) {
            Object compType = entry.getKey();
            String keyValue = NMSCore.REGISTRIES
                    .getKey(BuiltInRegistryEnum.DATA_COMPONENT_TYPE, compType)
                    .toString();
            Optional<?> valValue = entry.getValue();
            if (valValue.isPresent()) {
                Object tag = CodecUtils.encode(
                        Objects.requireNonNull(DATA_TYPES.getDataTypeCodec(compType)), nbtop, valValue.get());
                stringJoiner.add(keyValue + "=" + NMSCore.TAGS.printAsSnbt(tag, "", 0, new ArrayList<>()));
            } else {
                stringJoiner.add("!" + keyValue);
            }
        }
        return stringJoiner.toString();
    }

    @Override
    public boolean hasPlaceableKeys() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.CAN_PLACE_ON);
    }

    @Override
    public boolean hasDestroyableKeys() {
        return HELPER.hasInPatch(itemStack, DataComponentEnum.CAN_BREAK);
    }

    @Override
    public Object getAsComponentPatch() {
        return HELPER.getComponentsPatch(itemStack);
    }
}
