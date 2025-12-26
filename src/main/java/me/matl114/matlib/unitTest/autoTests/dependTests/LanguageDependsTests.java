package me.matl114.matlib.unitTest.autoTests.dependTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.chat.lan.i18n.RegistryLocalizationHelper;
import me.matl114.matlib.utils.chat.lan.i18n.ZhCNLocalizationHelper;
import me.matl114.matlib.utils.chat.lan.pinyinAdaptor.PinyinHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.version.VersionedRegistry;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class LanguageDependsTests implements TestCase {
    @OnlineTest(name = "zhCN guizhanLib bridge test")
    public void test_guizhanlib() throws Throwable {
        RegistryLocalizationHelper helper = DescriptorProxyBuilder.createMultiHelper(ZhCNLocalizationHelper.class);
        Debug.logger(helper.getClass());
        Debug.logger(helper.getAttributeName(VersionedRegistry.getInstance().getAttribute("armor")));
        Debug.logger(helper.getMaterialName(Material.CALIBRATED_SCULK_SENSOR));
        Debug.logger(helper.getItemName(new ItemStack(Material.SPLASH_POTION)));
        Debug.logger(helper.getEnchantmentName(VersionedRegistry.enchantment("aqua_affinity")));
        Debug.logger(helper.getEntityTypeName(VersionedRegistry.entityType("spawner_minecart")));
        Debug.logger(helper.getDyeColorName(DyeColor.YELLOW));
        Debug.logger(helper.getPotionEffectTypeName(PotionEffectType.FIRE_RESISTANCE));

        Debug.logger(helper.getBiomeName(Biome.BASALT_DELTAS));
        Debug.logger(helper.getEnchantmentName(VersionedRegistry.enchantment("sharpness")));
        //        Debug.logger(helper.getAttributeName(VersionedRegistry.getInstance().getAttribute("step_height")));
    }

    @OnlineTest(name = "pinyin lib bridge test")
    public void test_pinyin() throws Throwable {
        PinyinHelper helper = DescriptorProxyBuilder.createMultiHelper(PinyinHelper.class);
        Debug.logger(helper.toPinyin("卧槽"));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("FIRST_LETTER")));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("FIRST_LETTER"), ""));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("INPUT"), ""));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("NORMAL"), ""));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("DEFAULT"), ""));
        Debug.logger(helper.toPinyin("首字母缩写", helper.getPinyinStyleEnum("NUM_LAST"), ""));

        Debug.logger(helper.toPinyinList('行'));
        Debug.logger(helper.samePinyinMap('行'));
        Debug.logger(helper.samePinyinList("cao4"));
    }
}
