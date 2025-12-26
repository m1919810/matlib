package me.matl114.matlib.unitTest.autoTests.bukkitTests;

import static me.matl114.matlib.utils.chat.lan.TranslateKeyUtils.*;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.version.VersionedRegistry;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public class BukkitTranslationTests implements TestCase {
    @OnlineTest(name = "ENUS translation test")
    public void test_translation() throws Throwable {
        Debug.logger(getEnchantmentTranslationDefault(Enchantment.AQUA_AFFINITY));
        Debug.logger(getEntityTranslationDefault(EntityType.SPAWNER_MINECART));
        Debug.logger(getPotionTranslationDefault(PotionEffectType.FIRE_RESISTANCE));
        Debug.logger(getDyeColorTranslationDefault(DyeColor.BROWN));
        Debug.logger(getMaterialTranslateDefault(Material.CHERRY_BOAT));
        Debug.logger(getMaterialTranslateDefault(Material.PLAYER_HEAD));
        Debug.logger(getMaterialTranslateDefault(Material.CALIBRATED_SCULK_SENSOR));
        Debug.logger(
                getAttributeTranslationDefault(VersionedRegistry.getInstance().getAttribute("armor")));
    }
}
