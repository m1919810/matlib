package me.matl114.matlib.unitTest.autoTests.bukkitTests;

import static me.matl114.matlib.utils.chat.lan.TranslateKeyUtils.*;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.chat.lan.TranslateKeyUtils;
import me.matl114.matlib.utils.version.VersionedRegistry;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public class BukkitTranslationTests implements TestCase {
    @OnlineTest(name = "ENUS translation test")
    public void test_translation() throws Throwable {
        Debug.logger(TranslateKeyUtils.getEnchantmentTranslationDefault(Enchantment.AQUA_AFFINITY));
        Debug.logger(TranslateKeyUtils.getEntityTranslationDefault(EntityType.SPAWNER_MINECART));
        Debug.logger(TranslateKeyUtils.getPotionTranslationDefault(PotionEffectType.FIRE_RESISTANCE));
        Debug.logger(TranslateKeyUtils.getDyeColorTranslationDefault(DyeColor.BROWN));
        Debug.logger(TranslateKeyUtils.getMaterialTranslateDefault(Material.CHERRY_BOAT));
        Debug.logger(TranslateKeyUtils.getMaterialTranslateDefault(Material.PLAYER_HEAD));
        Debug.logger(TranslateKeyUtils.getMaterialTranslateDefault(Material.CALIBRATED_SCULK_SENSOR));
        Debug.logger(TranslateKeyUtils.getAttributeTranslationDefault(
                VersionedRegistry.getInstance().getAttribute("armor")));
    }
}
