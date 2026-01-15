package me.matl114.matlib.utils.version;

import com.google.common.base.Preconditions;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Objects;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

public abstract class VersionedMeta {
    private static VersionedMeta Instance;

    public static VersionedMeta getInstance() {
        if (Instance == null) {
            init0();
        }
        return Instance;
    }

    private static void init0() {
        Instance = switch (Version.getVersionInstance()) {
            case v1_20_R4 -> new v1_20_R4();
            case v1_21_R1, v1_21_R2 -> new v1_21_R1();
            default -> new Default();};
    }

    public boolean comparePotionType(PotionMeta instanceOne, PotionMeta instanceTwo) {
        return Objects.equals(instanceOne.getBasePotionData(), instanceTwo.getBasePotionData());
    }

    public boolean comparePotionMeta(PotionMeta instanceOne, PotionMeta instanceTwo) {
        if (!comparePotionType(instanceOne, instanceTwo)) {
            return false;
        }
        if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
            return false;
        }
        if (instanceOne.hasColor() != instanceTwo.hasColor()) {
            return false;
        }
        if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
            return false;
        }
        if (!instanceOne.getCustomEffects().equals(instanceTwo.getCustomEffects())) {
            return false;
        }
        return true;
    }

    public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo) {
        // Axolotl
        if (metaOne instanceof AxolotlBucketMeta instanceOne && metaTwo instanceof AxolotlBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }

            if (!instanceOne.hasVariant() || !instanceTwo.hasVariant()) {
                return true;
            }

            if (instanceOne.getVariant() != instanceTwo.getVariant()) {
                return true;
            }
        }
        // Banner
        if (metaOne instanceof BannerMeta instanceOne && metaTwo instanceof BannerMeta instanceTwo) {
            if (instanceOne.numberOfPatterns() != instanceTwo.numberOfPatterns()) {
                return true;
            }

            if (!instanceOne.getPatterns().equals(instanceTwo.getPatterns())) {
                return true;
            }
        }
        // BlockData
        if (metaOne instanceof BlockDataMeta instanceOne && metaTwo instanceof BlockDataMeta instanceTwo) {
            if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                return true;
            }
        }
        // BlockState
        if (metaOne instanceof BlockStateMeta instanceOne && metaTwo instanceof BlockStateMeta instanceTwo) {
            if (instanceOne.hasBlockState() || instanceTwo.hasBlockState()) {
                return true;
            }

            if (!CraftUtils.matchBlockStateMetaField(instanceOne, instanceTwo)) {
                return true;
            }
        }
        if (metaOne instanceof BundleMeta bundle1 && metaTwo instanceof BundleMeta bundle2) {
            // if anyone has item, then mark as different
            if (bundle1.hasItems() || bundle2.hasItems()) {
                return true;
            }
        }
        // Books
        if (metaOne instanceof BookMeta instanceOne && metaTwo instanceof BookMeta instanceTwo) {
            if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getAuthor(), instanceTwo.getAuthor())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getTitle(), instanceTwo.getTitle())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getGeneration(), instanceTwo.getGeneration())) {
                return true;
            }
        }

        // Bundle
        if (metaOne instanceof BundleMeta instanceOne && metaTwo instanceof BundleMeta instanceTwo) {
            if (instanceOne.hasItems() != instanceTwo.hasItems()) {
                return true;
            }
            if (!instanceOne.getItems().equals(instanceTwo.getItems())) {
                return true;
            }
        }

        // Compass
        if (metaOne instanceof CompassMeta instanceOne && metaTwo instanceof CompassMeta instanceTwo) {
            if (instanceOne.isLodestoneTracked() != instanceTwo.isLodestoneTracked()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLodestone(), instanceTwo.getLodestone())) {
                return true;
            }
        }
        // Crossbow
        if (metaOne instanceof CrossbowMeta instanceOne && metaTwo instanceof CrossbowMeta instanceTwo) {
            if (instanceOne.hasChargedProjectiles() != instanceTwo.hasChargedProjectiles()) {
                return true;
            }
            if (!instanceOne.getChargedProjectiles().equals(instanceTwo.getChargedProjectiles())) {
                return true;
            }
        }
        // Enchantment Storage
        if (metaOne instanceof EnchantmentStorageMeta instanceOne
                && metaTwo instanceof EnchantmentStorageMeta instanceTwo) {
            if (instanceOne.hasStoredEnchants() != instanceTwo.hasStoredEnchants()) {
                return true;
            }
            if (!instanceOne.getStoredEnchants().equals(instanceTwo.getStoredEnchants())) {
                return true;
            }
        }
        // Firework Star
        if (metaOne instanceof FireworkEffectMeta instanceOne && metaTwo instanceof FireworkEffectMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getEffect(), instanceTwo.getEffect())) {
                return true;
            }
        }
        // Firework
        if (metaOne instanceof FireworkMeta instanceOne && metaTwo instanceof FireworkMeta instanceTwo) {
            if (instanceOne.getPower() != instanceTwo.getPower()) {
                return true;
            }
            if (!instanceOne.getEffects().equals(instanceTwo.getEffects())) {
                return true;
            }
        }
        // Leather Armor
        if (metaOne instanceof LeatherArmorMeta instanceOne && metaTwo instanceof LeatherArmorMeta instanceTwo) {
            if (!instanceOne.getColor().equals(instanceTwo.getColor())) {
                return true;
            }
        }
        // Maps
        if (metaOne instanceof MapMeta instanceOne && metaTwo instanceof MapMeta instanceTwo) {
            if (instanceOne.hasMapView() != instanceTwo.hasMapView()) {
                return true;
            }
            if (instanceOne.hasLocationName() != instanceTwo.hasLocationName()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getMapView(), instanceTwo.getMapView())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLocationName(), instanceTwo.getLocationName())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
            }
        }
        // Potion
        if (metaOne instanceof PotionMeta instanceOne && metaTwo instanceof PotionMeta instanceTwo) {
            if (!comparePotionMeta(instanceOne, instanceTwo)) {
                return true;
            }
        }
        if (metaOne instanceof SuspiciousStewMeta instanceOne && metaTwo instanceof SuspiciousStewMeta instanceTwo) {
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getCustomEffects(), instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Fish Bucket
        if (metaOne instanceof TropicalFishBucketMeta instanceOne
                && metaTwo instanceof TropicalFishBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }
            if (!instanceOne.getPattern().equals(instanceTwo.getPattern())) {
                return true;
            }
            if (!instanceOne.getBodyColor().equals(instanceTwo.getBodyColor())) {
                return true;
            }
            if (!instanceOne.getPatternColor().equals(instanceTwo.getPatternColor())) {
                return true;
            }
        }

        // Knowledge Book
        if (metaOne instanceof KnowledgeBookMeta instanceOne && metaTwo instanceof KnowledgeBookMeta instanceTwo) {
            if (instanceOne.hasRecipes() != instanceTwo.hasRecipes()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getRecipes(), instanceTwo.getRecipes())) {
                return true;
            }
        }

        // Music Instrument
        if (metaOne instanceof MusicInstrumentMeta instanceOne && metaTwo instanceof MusicInstrumentMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getInstrument(), instanceTwo.getInstrument())) {
                return true;
            }
        }

        // Armor
        if (metaOne instanceof ArmorMeta instanceOne && metaTwo instanceof ArmorMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getTrim(), instanceTwo.getTrim())) {
                return true;
            }
        }

        return false;
    }

    public boolean matchBlockStateMeta(BlockStateMeta meta1, BlockStateMeta meta2) {
        try {
            return matchBlockStateMeta0(meta1, meta2);
        } catch (Throwable e) {
            return Objects.equals(meta1, meta2);
        }
    }

    protected abstract boolean matchBlockStateMeta0(BlockStateMeta meta1, BlockStateMeta meta2);

    static class Default extends VersionedMeta {
        private static final VarHandle handle = Holder.of(new ItemStack(Material.SPAWNER).getItemMeta())
                .thenApplyUnsafe((meta) -> {
                    BlockStateMeta blockState = (BlockStateMeta) meta;
                    var result = ReflectUtils.getFieldsRecursively(blockState.getClass(), "blockEntityTag");
                    Preconditions.checkArgument(result != null, "Field Absent!");
                    Field field = result.getA();
                    return MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup())
                            .unreflectVarHandle(field);
                })
                .get();

        protected boolean matchBlockStateMeta0(BlockStateMeta meta1, BlockStateMeta meta2) {
            return Objects.equals(handle.get(meta1), handle.get(meta2));
        }
    }

    static class v1_20_R4 extends Default {
        private static final VarHandle componentsHandle = Holder.of(new ItemStack(Material.SPAWNER).getItemMeta())
                .thenApplyUnsafe((meta) -> {
                    BlockStateMeta blockState = (BlockStateMeta) meta;
                    Field targetField = ReflectUtils.getFieldsRecursively(blockState.getClass(), "components")
                            .getA();
                    return MethodHandles.privateLookupIn(targetField.getDeclaringClass(), MethodHandles.lookup())
                            .unreflectVarHandle(targetField);
                })
                .get();

        public boolean comparePotionType(PotionMeta instanceOne, PotionMeta instanceTwo) {
            return instanceOne.getBasePotionType() == instanceTwo.getBasePotionType();
        }

        protected boolean matchBlockStateMeta0(BlockStateMeta meta1, BlockStateMeta meta2) {
            if (!super.matchBlockStateMeta0(meta1, meta2)) {
                return false;
            }
            return Objects.equals(componentsHandle.get(meta1), componentsHandle.get(meta2));
        }

        @Override
        public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo) {
            if (metaOne.isFireResistant() != metaTwo.isFireResistant()) {
                return true;
            }

            // Check if unbreakable
            if (metaOne.isUnbreakable() != metaTwo.isUnbreakable()) {
                return true;
            }

            // Check if hide tooltip
            if (metaOne.isHideTooltip() != metaTwo.isHideTooltip()) {
                return true;
            }

            // Check rarity
            final boolean hasRarityOne = metaOne.hasRarity();
            final boolean hasRarityTwo = metaTwo.hasRarity();
            if (hasRarityOne) {
                if (!hasRarityTwo || metaOne.getRarity() != metaTwo.getRarity()) {
                    return true;
                }
            } else if (hasRarityTwo) {
                return true;
            }

            // Check food components
            if (metaOne.hasFood() && metaTwo.hasFood()) {
                if (!Objects.equals(metaOne.getFood(), metaTwo.getFood())) {
                    return true;
                }
            } else if (metaOne.hasFood() != metaTwo.hasFood()) {
                return true;
            }

            // Check tool components
            if (metaOne.hasTool() && metaTwo.hasTool()) {
                if (!Objects.equals(metaOne.getTool(), metaTwo.getTool())) {
                    return true;
                }
            } else if (metaOne.hasTool() != metaTwo.hasTool()) {
                return true;
            }

            if (super.differentSpecialMeta(metaOne, metaTwo)) {
                return true;
            }
            if (metaOne instanceof WritableBookMeta instanceOne && metaTwo instanceof WritableBookMeta instanceTwo) {
                if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                    return true;
                }
                if (!Objects.equals(instanceOne.getPages(), instanceTwo.getPages())) {
                    return true;
                }
            }
            return false;
        }
    }

    static class v1_21_R1 extends v1_20_R4 {
        private static final boolean hasShieldMetaInterface = Holder.of(null)
                .thenApplyCaught((v) -> {
                    Class<?> testClass = ShieldMeta.class;
                    Preconditions.checkArgument(testClass.isInterface());
                    return true;
                })
                .valException(false)
                .get();

        @Override
        public boolean differentSpecialMeta(ItemMeta metaOne, ItemMeta metaTwo) {
            if (metaOne.hasJukeboxPlayable() && metaTwo.hasJukeboxPlayable()) {
                if (!Objects.equals(metaOne.getJukeboxPlayable(), metaTwo.getJukeboxPlayable())) {
                    return true;
                }
            } else if (metaOne.hasJukeboxPlayable() != metaTwo.hasJukeboxPlayable()) {
                return true;
            }
            if (super.differentSpecialMeta(metaOne, metaTwo)) {
                return true;
            }
            if (metaOne instanceof OminousBottleMeta instanceOne && metaTwo instanceof OminousBottleMeta instanceTwo) {
                if (instanceOne.hasAmplifier() != instanceTwo.hasAmplifier()) {
                    return true;
                }

                if (instanceOne.getAmplifier() != instanceTwo.getAmplifier()) {
                    return true;
                }
            }
            // Shield
            if (hasShieldMetaInterface
                    && metaOne instanceof ShieldMeta instanceOne
                    && metaTwo instanceof ShieldMeta instanceTwo) {
                if (Objects.equals(instanceOne.getBaseColor(), instanceTwo.getBaseColor())) {
                    return true;
                }
            }
            return false;
        }

        protected boolean matchBlockStateMeta0(BlockStateMeta meta1, BlockStateMeta meta2) {
            if (meta1.getClass() != meta2.getClass()) {
                return false;
            }
            if (hasShieldMetaInterface && (meta1 instanceof ShieldMeta meta11 && meta2 instanceof ShieldMeta meta22)) {
                return true; // Objects.equals( meta11.getBaseColor(),meta22.getBaseColor())
                // just let then gooooooooooooooooooooo fuck
                // return ;
            } else {
                return super.matchBlockStateMeta0(meta1, meta2);
            }
        }
    }
}
