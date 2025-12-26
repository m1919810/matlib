package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Arrays;
import me.matl114.matlib.common.lang.annotations.CodecLike;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import me.matl114.matlib.utils.version.DependsOnVersion;
import me.matl114.matlib.utils.version.Version;

public class ComponentCodecEnum {
    public static final Codec<Object> CUSTOM_DATA = codec(DataComponentEnum.CUSTOM_DATA);
    public static final Codec<Iterable<?>> CUSTOM_NAME = codec(DataComponentEnum.CUSTOM_NAME);
    public static final Codec<Iterable<?>> ITEM_NAME = codec(DataComponentEnum.ITEM_NAME);
    public static final Codec<Object> LORE = codec(DataComponentEnum.LORE);

    @CodecLike(value = "int or Map", instance = "CustomModelData")
    public static final Codec<Object> CUSTOM_MODEL_DATA = codec(DataComponentEnum.CUSTOM_MODEL_DATA);

    public static final Codec<Object> TOOL = codec(DataComponentEnum.TOOL);
    public static final Codec<Object> FOOD = codec(DataComponentEnum.FOOD);
    public static final Codec<Object> REPAIR_COST = codec(DataComponentEnum.REPAIR_COST);

    @CodecLike(value = "Empty Map", instance = "Unit")
    public static final Codec<Object> HIDE_TOOLTIP = codec(DataComponentEnum.HIDE_TOOLTIP);

    @CodecLike(value = "Empty Map", instance = "Unit")
    public static final Codec<Object> HIDE_ADDITIONAL_TOOLTIP = codec(DataComponentEnum.HIDE_ADDITIONAL_TOOLTIP);

    public static final Codec<Object> RARITY = codec(DataComponentEnum.RARITY);
    public static final Codec<Object> ENCHANTMENTS = codec(DataComponentEnum.ENCHANTMENTS);
    public static final Codec<Object> UNBREAKABLE = codec(DataComponentEnum.UNBREAKABLE);
    public static final Object CAN_PLACE_ON = codec(DataComponentEnum.CAN_PLACE_ON);
    public static final Object CAN_BREAK = codec(DataComponentEnum.CAN_BREAK);
    public static final Object ATTRIBUTE_MODIFIERS = codec(DataComponentEnum.ATTRIBUTE_MODIFIERS);
    public static final Object DYED_COLOR = codec(DataComponentEnum.DYED_COLOR);

    @DependsOnVersion(higherThan = Version.v1_21_R2)
    @CodecLike("int")
    public static final Codec<Object> ENCHANTABLE = codec(DataComponentEnum.ENCHANTABLE);

    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Codec<Object> CONSUMABLE = codec(DataComponentEnum.CONSUMABLE);

    @DependsOnVersion(higherThan = Version.v1_21_R2)
    public static final Codec<Object> DAMAGE_RESISTANT = codec(DataComponentEnum.DAMAGE_RESISTANT);

    public static final Codec<Object> DATACOMPONENTPATCH = DataComponentEnum.DATACOMPONENTPATCH_CODEC;

    public static final Codec<Object> DATACOMPONENTMAP = DataComponentEnum.DATACOMPONENTMAP_CODEC;

    public static final Reference2ReferenceMap<Object, Codec<Object>> DATA_COMPONENT_CODEC_MAP;

    static {
        DATA_COMPONENT_CODEC_MAP = new Reference2ReferenceOpenHashMap<>();
        for (var entry : DataComponentEnum.ALL_DATA_COMPONENTS) {
            try {
                Codec<Object> val = codec(entry);
                if (val != null) DATA_COMPONENT_CODEC_MAP.put(entry, val);
            } catch (Throwable e) {
                // ignored
                // 临时组件没有codec
                // Debug.logger("Codec absent for",entry);
            }
        }
        // todo: add version check for safety
        VersionedUtils.checkVersionAnnotations(ComponentCodecEnum.class);
    }

    private static <T> Codec<T> codec(Object componentType) {
        if (componentType == null) {
            return null;
        }
        // Debug.logger("trying to get codec for", componentType);

        return Arrays.stream(componentType.getClass().getMethods())
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> m.getReturnType() == Codec.class)
                .filter(m -> !m.isDefault())
                .findAny()
                .map(i -> {
                    try {
                        i.setAccessible(true);
                        return (Codec<T>) i.invoke(componentType);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow();
    }
}
