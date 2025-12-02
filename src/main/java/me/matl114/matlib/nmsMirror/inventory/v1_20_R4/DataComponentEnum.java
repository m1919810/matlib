package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.DependsOnVersion;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@VersionAtLeast(Version.v1_20_R4)
public class DataComponentEnum {
    private static final String mojangName1 = "net.minecraft.core.component.DataComponents";
    private static final Class<?> targetClass;
    public static final Object CUSTOM_DATA;
    public static final Object CUSTOM_NAME;
    public static final Object ITEM_NAME;
    public static final Object LORE;
    public static final Object CUSTOM_MODEL_DATA;
    public static final Object TOOL;
    public static final Object FOOD;
    public static final Object REPAIR_COST;
    @DependsOnVersion(lowerThan = Version.v1_21_R4)
    public static final Object HIDE_TOOLTIP;
    @DependsOnVersion(lowerThan = Version.v1_21_R4)
    public static final Object HIDE_ADDITIONAL_TOOLTIP;
    public static final Object RARITY;
    public static final Object ENCHANTMENTS;
    public static final Object CAN_PLACE_ON;
    public static final Object CAN_BREAK;
    public static final Object ATTRIBUTE_MODIFIERS;
    public static final Object UNBREAKABLE;
    public static final Object STORED_ENCHANTMENTS;
    public static final Object TRIM;
    public static final Object DYED_COLOR;
    public static final Object ENCHANTMENT_GLINT_OVERRIDE;
    public static final Object MAX_STACK_SIZE;
    public static final Object ENTITY_DATA;
    public static final Object BUCKET_ENTITY_DATA;
    public static final Object BLOCK_ENTITY_DATA;
    public static final Object RECIPES;
    public static final Object CONTAINER;
    public static final Object BEES;
    public static final Object MAX_DAMAGE;
    public static final Object DAMAGE;
    public static final Object BASE_COLOR;
    public static final Object CHARGED_PROJECTILES;
    public static final Object BUNDLE_CONTENTS;
    public static final Object POTION_CONTENTS;
    public static final Object BANNER_PATTERNS;
    public static final Object BLOCK_STATE;
    public static final Object CONTAINER_LOOT;
    public static final Object FIREWORK_EXPLOSION;
    public static final Object FIREWORKS ;
    public static final Object INSTRUMENT;
    public static final Object MAP_ID;
    public static final Object POT_DECORATIONS;
    public static final Object WRITTEN_BOOK_CONTENT;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object ITEM_MODEL;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object ENCHANTABLE;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object CONSUMABLE;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object TOOLTIP_STYLE;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object GLIDER;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object DAMAGE_RESISTANT;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object USE_REMAINDER;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object USE_COOLDOWN;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object EQUIPPABLE;
    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Object JUKEBOX_PLAYABLE;
    @DependsOnVersion(higherThan = Version.v1_21_R4)
    public static final Object PAINTING_VARIANT;
    @DependsOnVersion(higherThan = Version.v1_21_R4)
    public static final Object TROPICAL_FISH_PATTERN;
    @DependsOnVersion(higherThan = Version.v1_21_R4)
    public static final Object TOOLTIP_DISPLAY;

    public static final Set<Object> ALL_DATA_COMPONENTS;


    private static final String mojangName2 = "net.minecraft.core.component.DataComponentMap";
    @Note("item default components map")
    public static final Object COMMON_ITEM_COMPONENTS;
    @Note("empty component map")
    public static final Object COMPONENT_MAP_EMPTY ;
    public static final Object COMPONENT_PATCH_EMPTY;

    public static final Codec<Object> DATACOMPONENTMAP_CODEC ;

    public static final Codec<Object> DATACOMPONENTPATCH_CODEC ;
    public static final Reference2ReferenceMap<Object, Function<Object, Object>> COMPONENT_COPY_POLICY;

//    public static final Comparator<?> ITEMENCHANT_ORDER;
    public static final Object ITEMENCHANTMENTS_EMPTY;
    static{
        Class<?> a= null;
        try{
            a = ObfManager.getManager().reobfClass(mojangName1);
        }catch (Throwable e){
        }
        List<Field> fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
        targetClass = a;
        CUSTOM_DATA = Utils.matchName(fields, "CUSTOM_DATA");
        CUSTOM_NAME = Utils.matchName(fields, "CUSTOM_NAME");
        ITEM_NAME = Utils.matchName(fields, "ITEM_NAME");
        LORE = Utils.matchName(fields, "LORE");
        CUSTOM_MODEL_DATA = Utils.matchName(fields, "CUSTOM_MODEL_DATA");
        ENCHANTMENTS = Utils.matchName(fields, "ENCHANTMENTS");
        TOOL = Utils.matchName(fields, "TOOL");
        FOOD = Utils.matchName(fields, "FOOD");
        REPAIR_COST = Utils.matchName(fields, "REPAIR_COST");
        HIDE_TOOLTIP = Utils.matchNull(fields, "HIDE_TOOLTIP");
        HIDE_ADDITIONAL_TOOLTIP = Utils.matchNull(fields, "HIDE_ADDITIONAL_TOOLTIP");
        RARITY = Utils.matchName(fields, "RARITY");
        CAN_PLACE_ON = Utils.matchName(fields, "CAN_PLACE_ON");
        CAN_BREAK = Utils.matchName(fields, "CAN_BREAK");
        ATTRIBUTE_MODIFIERS = Utils.matchName(fields,"ATTRIBUTE_MODIFIERS");
        UNBREAKABLE = Utils.matchName(fields, "UNBREAKABLE");
        STORED_ENCHANTMENTS = Utils.matchName(fields, "STORED_ENCHANTMENTS");
        TRIM = Utils.matchName(fields, "TRIM");
        DYED_COLOR = Utils.matchName(fields, "DYED_COLOR");
        ENCHANTMENT_GLINT_OVERRIDE = Utils.matchName(fields, "ENCHANTMENT_GLINT_OVERRIDE");
        MAX_STACK_SIZE = Utils.matchName(fields, "MAX_STACK_SIZE");
        ENTITY_DATA = Utils.matchName(fields, "ENTITY_DATA");
        BUCKET_ENTITY_DATA = Utils.matchName(fields, "BUCKET_ENTITY_DATA");
        BLOCK_ENTITY_DATA = Utils.matchName(fields, "BLOCK_ENTITY_DATA");
        RECIPES = Utils.matchName(fields, "RECIPES");
        CONTAINER = Utils.matchName(fields, "CONTAINER");
        BEES = Utils.matchName(fields, "BEES");
        MAX_DAMAGE = Utils.matchName(fields, "MAX_DAMAGE");
        DAMAGE = Utils.matchName(fields, "DAMAGE");
        BASE_COLOR = Utils.matchName(fields, "BASE_COLOR");
        CHARGED_PROJECTILES = Utils.matchName(fields, "CHARGED_PROJECTILES");
        BUNDLE_CONTENTS = Utils.matchName(fields, "BUNDLE_CONTENTS");
        POTION_CONTENTS = Utils.matchName(fields, "POTION_CONTENTS");
        BANNER_PATTERNS = Utils.matchName(fields, "BANNER_PATTERNS");
        BLOCK_STATE = Utils.matchName(fields, "BLOCK_STATE");
        CONTAINER_LOOT = Utils.matchName(fields, "CONTAINER_LOOT");
        FIREWORK_EXPLOSION = Utils.matchName(fields, "FIREWORK_EXPLOSION");
        FIREWORKS = Utils.matchName(fields, "FIREWORKS");
        INSTRUMENT = Utils.matchName(fields, "INSTRUMENT");
        MAP_ID = Utils.matchName(fields, "MAP_ID");
        POT_DECORATIONS = Utils.matchName(fields, "POT_DECORATIONS");
        WRITTEN_BOOK_CONTENT = Utils.matchName(fields, "WRITTEN_BOOK_CONTENT");


        ITEM_MODEL = Utils.matchNull(fields, "ITEM_MODEL");
        CONSUMABLE = Utils.matchNull(fields, "CONSUMABLE");
        ENCHANTABLE = Utils.matchNull(fields, "ENCHANTABLE");
        TOOLTIP_STYLE = Utils.matchNull(fields, "TOOLTIP_STYLE");
        GLIDER = Utils.matchNull(fields, "GLIDER");
        DAMAGE_RESISTANT = Utils.matchNull(fields, "DAMAGE_RESISTANT");
        USE_REMAINDER = Utils.matchNull(fields, "USE_REMAINDER");
        USE_COOLDOWN = Utils.matchNull(fields, "USE_COOLDOWN");
        EQUIPPABLE = Utils.matchNull(fields,"EQUIPPABLE");
        JUKEBOX_PLAYABLE = Utils.matchNull(fields, "JUKEBOX_PLAYABLE");
        PAINTING_VARIANT = Utils.matchNull(fields, "PAINTING_VARIANT");
        TROPICAL_FISH_PATTERN = Utils.matchNull(fields, "TROPICAL_FISH_PATTERN");
        TOOLTIP_DISPLAY = Utils.matchNull(fields, "TOOLTIP_DISPLAY");

        COMMON_ITEM_COMPONENTS = Utils.matchName(fields, "COMMON_ITEM_COMPONENTS");
        ALL_DATA_COMPONENTS = new LinkedHashSet<>();
        for (var cp: BuiltInRegistryEnum.DATA_COMPONENT_TYPE){
            ALL_DATA_COMPONENTS.add(cp);
        }

        try{
            a = ObfManager.getManager().reobfClass(mojangName2);
        }catch (Throwable e){
        }
        fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
        COMPONENT_MAP_EMPTY = Utils.matchName(fields, "EMPTY");
        DATACOMPONENTMAP_CODEC = Utils.matchName(fields, "CODEC");


        try{
            a = ObfManager.getManager().reobfClass("net.minecraft.core.component.DataComponentPatch");
        }catch (Throwable e){
        }
        fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
        COMPONENT_PATCH_EMPTY = Utils.matchName(fields, "EMPTY");
        DATACOMPONENTPATCH_CODEC = Utils.matchName(fields, "CODEC");
        try{
            a = ObfManager.getManager().reobfClass("net.minecraft.world.item.enchantment.ItemEnchantments");
        }catch (Throwable e){
        }
        fields = Arrays.stream(a.getFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
            .toList();
//        ITEMENCHANT_ORDER = (Comparator<?>) Utils.matchName(fields, "ENCHANTMENT_ORDER");
        ITEMENCHANTMENTS_EMPTY= Utils.matchName(fields,"EMPTY");
        Reference2ReferenceMap<Object, Function<Object,Object>> val = new Reference2ReferenceOpenHashMap<>();
        Function<Object, Object> copyCustomData = (value)->{
            if(value == null)return null;
            return Env1_20_R4.ICUSTOMDATA.copyCustomData(value);
        };
        val.put(CUSTOM_DATA ,copyCustomData);
        val.put(ENTITY_DATA, copyCustomData);
        val.put(BUCKET_ENTITY_DATA, copyCustomData);
        val.put(BLOCK_ENTITY_DATA, copyCustomData);
        val.put(RECIPES,(lst)->List.copyOf((List)lst));
        val.put(BEES, (lst)->List.copyOf((List)lst));
        val.put(CONTAINER, (container)->{
            //copy the list, if needed?
            return Env1_20_R4.DATA_TYPES.itemContainerContents$fromItems(Env1_20_R4.DATA_TYPES.itemContainerContents$Items(container));
        });
        COMPONENT_COPY_POLICY = val;

        //todo: add version check for safety
        VersionedUtils.checkVersionAnnotations(DataComponentEnum.class);
    }
}
