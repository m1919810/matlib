package me.matl114.matlib.slimefunUtils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.debug.Debug;
import io.github.thebusybiscuit.slimefun4.core.debug.TestCase;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemMetaSnapshot;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class SlimefunItemUtils {

    @Nullable public static String parseSfId(ItemStack item) {
        Optional<String> itemID = Slimefun.getItemDataService().getItemData(item);
        return itemID.orElse(null);
    }

    public static String parseSfId(ItemMeta meta) {
        Optional<String> itemID = Slimefun.getItemDataService().getItemData(meta);
        return itemID.orElse(null);
    }

    public static SlimefunItem parseSfItem(ItemMeta meta) {
        Optional<String> itemID = Slimefun.getItemDataService().getItemData(meta);
        return itemID.map(SlimefunItem::getById).orElse(null);
    }

    public static SlimefunItem parseSfItem(ItemStack item) {
        Optional<String> itemID = Slimefun.getItemDataService().getItemData(item);
        return itemID.map(SlimefunItem::getById).orElse(null);
    }

    public static boolean isItemSimilar(
            @Nullable ItemStack item,
            @Nullable ItemStack sfitem,
            boolean checkLore,
            boolean checkAmount,
            boolean checkDistinctiveItem,
            boolean checkCustomModelData) {
        if (item == null) {
            return sfitem == null;
        } else if (sfitem != null
                && item.getType() == sfitem.getType()
                && (!checkAmount || item.getAmount() == sfitem.getAmount())) {
            if (checkDistinctiveItem && sfitem instanceof SlimefunItemStack) {
                SlimefunItemStack stackOne = (SlimefunItemStack) sfitem;
                if (item instanceof SlimefunItemStack) {
                    SlimefunItemStack stackTwo = (SlimefunItemStack) item;
                    if (stackOne.getItemId().equals(stackTwo.getItemId())) {
                        if (stackOne instanceof DistinctiveItem && stackTwo instanceof DistinctiveItem) {
                            DistinctiveItem distinctiveItem = (DistinctiveItem) stackTwo;
                            return distinctiveItem.canStack(stackOne.getItemMeta(), stackTwo.getItemMeta());
                        }

                        return true;
                    }

                    return false;
                }
            }

            if (item.hasItemMeta()) {
                Debug.log(TestCase.CARGO_INPUT_TESTING, "SlimefunUtils#isItemSimilar - item.hasItemMeta()");
                ItemMeta itemMeta = item.getItemMeta();
                if (sfitem instanceof SlimefunItemStack) {
                    SlimefunItemStack sfItemStack = (SlimefunItemStack) sfitem;
                    String id = (String)
                            Slimefun.getItemDataService().getItemData(itemMeta).orElse(null);
                    if (id != null) {
                        if (id.equals(sfItemStack.getItemId())) {
                            if (checkDistinctiveItem) {
                                /*
                                 * PR #3417
                                 *
                                 * Some items can't rely on just IDs matching and will implement Distinctive Item
                                 * in which case we want to use the method provided to compare
                                 */
                                Optional<DistinctiveItem> optionalDistinctive = getDistinctiveItem(id);
                                if (optionalDistinctive.isPresent()) {
                                    ItemMeta sfItemMeta = sfitem.getItemMeta();
                                    return optionalDistinctive.get().canStack(sfItemMeta, itemMeta);
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                    ItemMetaSnapshot meta = ((SlimefunItemStack) sfitem).getItemMetaSnapshot();
                    return equalsItemMeta(itemMeta, meta, checkLore);

                } else {
                    ItemMeta sfItemMeta;
                    ItemMeta possibleSfItemMeta;
                    if (sfitem instanceof ItemStackWrapper && sfitem.hasItemMeta()) {
                        Debug.log(TestCase.CARGO_INPUT_TESTING, "  is wrapper");
                        Debug.log(
                                TestCase.CARGO_INPUT_TESTING,
                                "  sfitem is ItemStackWrapper - possible SF Item: {}",
                                new Object[] {sfitem});
                        sfItemMeta = sfitem.getItemMeta();
                        possibleSfItemMeta = (sfitem).getItemMeta();
                        String id = Slimefun.getItemDataService()
                                .getItemData(itemMeta)
                                .orElse(null);
                        String possibleItemId = Slimefun.getItemDataService()
                                .getItemData(possibleSfItemMeta)
                                .orElse(null);
                        // Prioritize SlimefunItem id comparison over ItemMeta comparison
                        if (id != null && possibleItemId != null) {
                            /*
                             * PR #3417
                             *
                             * Some items can't rely on just IDs matching and will implement Distinctive Item
                             * in which case we want to use the method provided to compare
                             */
                            var match = id.equals(possibleItemId);
                            if (match) {
                                Optional<DistinctiveItem> optionalDistinctive = getDistinctiveItem(id);
                                if (optionalDistinctive.isPresent()) {
                                    return optionalDistinctive.get().canStack(possibleSfItemMeta, itemMeta);
                                }
                            }
                            Debug.log(TestCase.CARGO_INPUT_TESTING, "  Use Item ID match: {}", match);
                            return match;
                        } else {
                            Debug.log(
                                    TestCase.CARGO_INPUT_TESTING,
                                    "  one of item have no Slimefun ID, checking meta {} == {} (lore: {})",
                                    itemMeta,
                                    possibleSfItemMeta,
                                    checkLore);

                            return equalsItemMeta(itemMeta, possibleSfItemMeta, checkLore, checkCustomModelData);
                        }
                    } else if (sfitem.hasItemMeta()) {
                        sfItemMeta = sfitem.getItemMeta();
                        Debug.log(
                                TestCase.CARGO_INPUT_TESTING,
                                "  Comparing meta (vanilla items?) - {} == {} (lore: {})",
                                new Object[] {itemMeta, sfItemMeta, checkLore});
                        return equalsItemMeta(itemMeta, sfItemMeta, checkLore, checkCustomModelData);
                    } else {
                        return false;
                    }
                }
            } else {
                return !sfitem.hasItemMeta();
            }
        } else {
            return false;
        }
    }

    @Nonnull
    private static Optional<DistinctiveItem> getDistinctiveItem(@Nonnull String id) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem instanceof DistinctiveItem distinctiveItem) {
            return Optional.of(distinctiveItem);
        } else {
            return Optional.empty();
        }
    }

    private static boolean equalsItemMeta(
            @Nonnull ItemMeta itemMeta, @Nonnull ItemMetaSnapshot itemMetaSnapshot, boolean checkLore) {
        return equalsItemMeta(itemMeta, itemMetaSnapshot, checkLore, true);
    }

    private static boolean equalsItemMeta(
            @Nonnull ItemMeta itemMeta,
            @Nonnull ItemMetaSnapshot itemMetaSnapshot,
            boolean checkLore,
            boolean bypassCustomModelCheck) {
        Optional<String> displayName = itemMetaSnapshot.getDisplayName();
        if (itemMeta.hasDisplayName() != displayName.isPresent()) {
            return false;
        } else if (itemMeta.hasDisplayName()
                && displayName.isPresent()
                && !itemMeta.getDisplayName().equals(displayName.get())) {
            return false;
        } else {
            if (checkLore) {
                Optional<List<String>> itemLore = itemMetaSnapshot.getLore();
                if (itemMeta.hasLore()
                        && itemLore.isPresent()
                        && !equalsLore(itemMeta.getLore(), (List) itemLore.get())) {
                    return false;
                }

                if (itemMeta.hasLore() != itemLore.isPresent()) {
                    return false;
                }
            }

            if (bypassCustomModelCheck) {
                return true;
            } else {
                OptionalInt itemCustomModelData = itemMetaSnapshot.getCustomModelData();
                if (itemMeta.hasCustomModelData()
                        && itemCustomModelData.isPresent()
                        && itemMeta.getCustomModelData() != itemCustomModelData.getAsInt()) {
                    return false;
                } else {
                    return itemMeta.hasCustomModelData() == itemCustomModelData.isPresent();
                }
            }
        }
    }

    private static boolean equalsItemMeta(@Nonnull ItemMeta itemMeta, @Nonnull ItemMeta sfitemMeta, boolean checkLore) {
        return equalsItemMeta(itemMeta, sfitemMeta, checkLore, true);
    }

    private static boolean equalsItemMeta(
            @Nonnull ItemMeta itemMeta,
            @Nonnull ItemMeta sfitemMeta,
            boolean checkLore,
            boolean bypassCustomModelCheck) {
        if (itemMeta.hasDisplayName() != sfitemMeta.hasDisplayName()) {
            return false;
        } else if (itemMeta.hasDisplayName()
                && sfitemMeta.hasDisplayName()
                && !itemMeta.getDisplayName().equals(sfitemMeta.getDisplayName())) {
            return false;
        } else {
            boolean hasItemMetaCustomModelData;
            boolean hasSfItemMetaCustomModelData;
            if (checkLore) {
                hasItemMetaCustomModelData = itemMeta.hasLore();
                hasSfItemMetaCustomModelData = sfitemMeta.hasLore();
                if (hasItemMetaCustomModelData && hasSfItemMetaCustomModelData) {
                    if (!equalsLore(itemMeta.getLore(), sfitemMeta.getLore())) {
                        return false;
                    }
                } else if (hasItemMetaCustomModelData != hasSfItemMetaCustomModelData) {
                    return false;
                }
            }

            if (!bypassCustomModelCheck) {
                hasItemMetaCustomModelData = itemMeta.hasCustomModelData();
                hasSfItemMetaCustomModelData = sfitemMeta.hasCustomModelData();
                if (hasItemMetaCustomModelData
                        && hasSfItemMetaCustomModelData
                        && itemMeta.getCustomModelData() != sfitemMeta.getCustomModelData()) {
                    return false;
                }

                if (hasItemMetaCustomModelData != hasSfItemMetaCustomModelData) {
                    return false;
                }
            }

            return itemMeta instanceof PotionMeta && sfitemMeta instanceof PotionMeta
                    ? ((PotionMeta) itemMeta).getBasePotionData().equals(((PotionMeta) sfitemMeta).getBasePotionData())
                    : true;
        }
    }

    public static boolean equalsLore(@Nonnull List<String> lore1, @Nonnull List<String> lore2) {
        Validate.notNull(lore1, "Cannot compare lore that is null!");
        Validate.notNull(lore2, "Cannot compare lore that is null!");
        List<String> longerList = lore1.size() > lore2.size() ? lore1 : lore2;
        List<String> shorterList = lore1.size() > lore2.size() ? lore2 : lore1;
        int a = 0;

        int b;
        for (b = 0; a < longerList.size(); ++a) {
            if (!isLineIgnored((String) longerList.get(a))) {
                while (shorterList.size() > b && isLineIgnored((String) shorterList.get(b))) {
                    ++b;
                }

                if (b >= shorterList.size()) {
                    return false;
                }

                if (!((String) longerList.get(a)).equals(shorterList.get(b))) {
                    return false;
                }

                ++b;
            }
        }

        while (shorterList.size() > b && isLineIgnored((String) shorterList.get(b))) {
            ++b;
        }

        return b == shorterList.size();
    }

    private static boolean isLineIgnored(@Nonnull String line) {
        return false;
    }
}
