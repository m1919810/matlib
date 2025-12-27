package me.matl114.matlib.utils.chat.lan;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.utils.chat.lan.i18n.DefaultLocalizationHelper;
import me.matl114.matlib.utils.chat.lan.i18n.RegistryLocalizationHelper;
import me.matl114.matlib.utils.chat.lan.pinyinAdaptor.PinyinHelper;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisplayNameUtils {
    private static final RegistryLocalizationHelper DEFAULT_NAME_HELPER = new DefaultLocalizationHelper();
    private static final PinyinHelper DEFAULT_PINYIN_HELPER = PinyinHelper.createDefaultImpl();

    private static @Nonnull RegistryLocalizationHelper validHelper(@Nullable RegistryLocalizationHelper h) {
        return h == null ? DEFAULT_NAME_HELPER : h;
    }

    private static @Nonnull PinyinHelper validHelper(@Nullable PinyinHelper helper) {
        return helper == null ? DEFAULT_PINYIN_HELPER : helper;
    }

    public static String getDisplayName(@Nullable RegistryLocalizationHelper helper, ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return validHelper(helper).getItemName(item);
    }

    public static Comparator<ItemStack> getChineseNameSorter(@Nullable RegistryLocalizationHelper helper) {
        return Comparator.comparing(
                (item) -> {
                    return getDisplayName(helper, item);
                },
                Collator.getInstance(Locale.CHINESE)::compare);
    }

    public static Predicate<ItemStack> getPinyinFilter(
            @Nullable RegistryLocalizationHelper helper1, @Nullable PinyinHelper helper2, String value) {
        final var helper22 = validHelper(helper2);
        final var helper11 = validHelper(helper1);
        return (item) -> {
            if (value == null || value.isEmpty()) {
                return true;
            }
            String itemName = getDisplayName(helper11, item);
            if (itemName == null || itemName.isEmpty()) {
                return false;
            }
            String name = ChatColor.stripColor(itemName.toLowerCase(Locale.ROOT));
            if (name.contains(value)) {
                return true;
            }
            String pinyin1 = helper22.toPinyin(name, helper22.getPinyinStyleEnum("INPUT"), "");
            if (pinyin1 == null || pinyin1.isEmpty()) {
                return false;
            } else if (pinyin1.contains(value)) {
                return true;
            }
            String pinyin2 = helper22.toPinyin(name, helper22.getPinyinStyleEnum("FIRST_LETTER"), "");
            if (pinyin2 == null || pinyin2.isEmpty()) {
                return false;
            } else return pinyin2.contains(value);
        };
    }
}
