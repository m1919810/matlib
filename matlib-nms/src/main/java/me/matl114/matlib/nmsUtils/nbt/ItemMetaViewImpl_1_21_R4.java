package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4.*;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.*;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentEnum;
import org.bukkit.inventory.ItemFlag;

class ItemMetaViewImpl_1_21_R4 extends ItemMetaViewImpl_1_20_R4 {
    public ItemMetaViewImpl_1_21_R4(Object itemStack) {
        super(itemStack);
    }

    private static final Set<?> HIDDEN_COMPONENTS_PREVIOUSLY = Set.of(
            DataComponentEnum.BANNER_PATTERNS,
            DataComponentEnum.BEES,
            DataComponentEnum.BLOCK_ENTITY_DATA,
            DataComponentEnum.BLOCK_STATE,
            DataComponentEnum.BUNDLE_CONTENTS,
            DataComponentEnum.CHARGED_PROJECTILES,
            DataComponentEnum.CONTAINER,
            DataComponentEnum.CONTAINER_LOOT,
            DataComponentEnum.FIREWORK_EXPLOSION,
            DataComponentEnum.FIREWORKS,
            DataComponentEnum.INSTRUMENT,
            DataComponentEnum.MAP_ID,
            DataComponentEnum.PAINTING_VARIANT,
            DataComponentEnum.POT_DECORATIONS,
            DataComponentEnum.POTION_CONTENTS,
            DataComponentEnum.TROPICAL_FISH_PATTERN,
            DataComponentEnum.WRITTEN_BOOK_CONTENT);
    private static final Map<ItemFlag, Set<?>> ITEM_FLAG_EQUIVALENTS = ImmutableMap.<ItemFlag, Set<?>>builder()
            .put(ItemFlag.HIDE_ATTRIBUTES, Set.of(DataComponentEnum.ATTRIBUTE_MODIFIERS))
            .put(ItemFlag.HIDE_ENCHANTS, Set.of(DataComponentEnum.ENCHANTMENTS))
            .put(ItemFlag.HIDE_STORED_ENCHANTS, Set.of(DataComponentEnum.STORED_ENCHANTMENTS))
            .put(ItemFlag.HIDE_UNBREAKABLE, Set.of(DataComponentEnum.UNBREAKABLE))
            .put(ItemFlag.HIDE_DYE, Set.of(DataComponentEnum.DYED_COLOR))
            .put(ItemFlag.HIDE_ARMOR_TRIM, Set.of(DataComponentEnum.TRIM))
            .put(ItemFlag.HIDE_PLACED_ON, Set.of(DataComponentEnum.CAN_PLACE_ON))
            .put(ItemFlag.HIDE_DESTROYS, Set.of(DataComponentEnum.CAN_BREAK))
            .put(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, HIDDEN_COMPONENTS_PREVIOUSLY)
            .buildOrThrow();
    // TODO: left for implementation
    protected void setFlag(ItemFlag flag, boolean shouldDisplay) {
        Object tooltips = HELPER.getFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
        Set<?> hides = ITEM_FLAG_EQUIVALENTS.get(flag);
        if (tooltips != null) {
            // already have flags
            SequencedSet<?> hidden = Env1_20_R4.DATA_TYPES.tooltipsDisplay$hiddenComponents(tooltips);
            if (hidden.containsAll(hides) == !shouldDisplay) {
                // ret
                return;
            } else {
                SequencedSet newSet = new ReferenceLinkedOpenHashSet<>(hidden);
                if (shouldDisplay) {
                    newSet.removeAll(hides);
                } else {
                    newSet.addAll(hides);
                }
                boolean hideFlag = Env1_20_R4.DATA_TYPES.tooltipsDisplay$hideTooltips(tooltips);
                if (!newSet.isEmpty() || hideFlag) {
                    HELPER.setDataComponentValue(
                            itemStack,
                            DataComponentEnum.TOOLTIP_DISPLAY,
                            Env1_20_R4.DATA_TYPES.tooltipsDisplay$newComponent(hideFlag, newSet));
                } else {
                    HELPER.removeFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
                }
            }
        } else {
            // don't have any flags here
            if (!shouldDisplay) {
                // do not display, so
                // add these Flag
                HELPER.setDataComponentValue(
                        itemStack,
                        DataComponentEnum.TOOLTIP_DISPLAY,
                        Env1_20_R4.DATA_TYPES.tooltipsDisplay$newComponent(
                                false, new ReferenceLinkedOpenHashSet<>(hides)));
            }
        }
    }

    protected boolean getVisibilityForFlag(ItemFlag flag) {
        Object tooltips = HELPER.getFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
        if (tooltips == null) {
            // no flag
            return true;
        }
        return !Env1_20_R4.DATA_TYPES
                .tooltipsDisplay$hiddenComponents(tooltips)
                .containsAll(ITEM_FLAG_EQUIVALENTS.getOrDefault(flag, Set.of()));
    }

    public boolean isHideTooltip() {
        Object tooltips = HELPER.getFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
        if (tooltips == null) {
            return false;
        }
        return Env1_20_R4.DATA_TYPES.tooltipsDisplay$hideTooltips(tooltips);
    }

    public void setHideTooltip(boolean b) {
        Object tooltips = HELPER.getFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
        if (tooltips != null) {
            SequencedSet<?> set = Env1_20_R4.DATA_TYPES.tooltipsDisplay$hiddenComponents(tooltips);
            if (!set.isEmpty() || b) {
                HELPER.setDataComponentValue(
                        itemStack,
                        DataComponentEnum.TOOLTIP_DISPLAY,
                        Env1_20_R4.DATA_TYPES.tooltipsDisplay$newComponent(b, set));
            } else {
                // empty and hideTooltips = false, remove
                HELPER.removeFromPatch(itemStack, DataComponentEnum.TOOLTIP_DISPLAY);
            }
        } else {
            if (b) {
                HELPER.setDataComponentValue(
                        itemStack,
                        DataComponentEnum.TOOLTIP_DISPLAY,
                        Env1_20_R4.DATA_TYPES.tooltipsDisplay$newComponent(true, new ReferenceLinkedOpenHashSet<>()));
            }
        }
    }
}
