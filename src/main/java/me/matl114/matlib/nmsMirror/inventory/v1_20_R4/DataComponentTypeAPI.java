package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.components.*;

import java.util.List;
import java.util.SequencedSet;

import static me.matl114.matlib.nmsMirror.Import.*;


@MultiDescriptive
public interface DataComponentTypeAPI extends TargetDescriptor {
    @MethodTarget
    @RedirectName("codec")
    @RedirectClass(DataComponentType)
    Codec<Object> getDataTypeCodec(Object dataType);


    @CastCheck(ItemLore)
    boolean isItemLore(Object val);

    @RedirectClass(ItemLore)
    @ConstructorTarget
    Object newItemLore(List<? extends Iterable<?>> lines);

    @RedirectClass(ItemLore)
    @MethodTarget
    @RedirectName("lines")
    List<Iterable<?>> itemLore$lines(Object itemLore);

    @ConstVal
    @RedirectClass(ItemEnchantmentsMutable)
    @FieldTarget
    @RedirectName("enchantmentsGetter")
    Object2IntAVLTreeMap<?> itemEnchantMutable$enchants(Object ench);

    @RedirectClass(ItemEnchantmentsMutable)
    @ConstructorTarget
    Object newMutable(@RedirectType(ItemEnchantments)Object enchMap);

    @RedirectClass(ItemEnchantmentsMutable)
    @MethodTarget
    @RedirectName("toImmutable")
    Object itemEnchantMutable$toImmutable(Object mutable);

    static boolean hasTooltipsComponents(){
        return Version.getVersionInstance().isAtLeast(Version.v1_21_R4);
    }


    @RedirectClass(ItemEnchantmentsMutable)
    @FieldTarget
    @RedirectName("showInTooltipGetter")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean itemEnchantMutable$showInTooltip(Object mutable);

    @RedirectClass(ItemEnchantmentsMutable)
    @FieldTarget
    @RedirectName("showInTooltipSetter")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    void itemEnchantMutable$setShowInTooltip(Object mutable, boolean val);

    @RedirectClass(ItemEnchantments)
    @FieldTarget
    @RedirectName("showInTooltipGetter")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean itemEnchants$ShowTooltip(Object itemEnchant);

//    @RedirectClass(ItemEnchantments)
//    @MethodTarget
//    @RedirectName("withTooltip")
//    Object itemEnchants$withTooltip(Object itemEnchant, boolean val);

    @RedirectClass(ItemEnchantmentsMutable)
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R1, below = true)
    @RedirectName("set")
    default void itemEnchantMutable$Set(Object mutable, @RedirectType(Holder)Object enchHolder, int value){
        Object ench = NMSCore.REGISTRIES.holderValue(enchHolder);
        itemEnchantMutable$Set000000000000000000(mutable, ench, value);
    }

    @RedirectClass(ItemEnchantmentsMutable)
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R1,below = false)
    @RedirectName("set")
    void itemEnchantMutable$Set000000000000000000(Object mutable, @RedirectType(Enchantment)Object ench, int value);
//    @RedirectClass(CraftCustomModelDataComponent)
//    @ConstructorTarget
//    CustomModel
//    @RedirectClass(AdventureModePredicate)
//    @MethodTarget
//    @RedirectName("withTooltip")
//    Object adventureModePredicate$withTooltip(Object thi, boolean ttt);

    @RedirectClass(AdventureModePredicate)
    @MethodTarget
    @RedirectName("showInTooltip")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean adventureModePredicate$showInTooltip(Object thi);

//    @RedirectClass(ItemAttributeModifiers)
//    @MethodTarget
//    @RedirectName("withTooltip")
//    Object itemAttributeModifiers$withTooltip(Object thi, boolean ttt);

    @RedirectClass(ItemAttributeModifiers)
    @MethodTarget
    @RedirectName("showInTooltip")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean itemAttributeModifiers$showInTooltip(Object thi);

    @RedirectClass(ItemAttributeModifiers)
    @MethodTarget(isStatic = true)
    @RedirectName("builder")
    Object itemAttributeModifierBuilder();

    @RedirectClass(ItemAttributeModifiersBuilder)
    @MethodTarget
    @RedirectName("add")
    Object attributeBuilder$Add(Object self,@RedirectType(Holder)Object attribute,@RedirectType(AttributeModifier)Object modifier, @RedirectType(EquipmentSlotGroup)Object group );

    @RedirectClass(ItemAttributeModifiersBuilder)
    @MethodTarget
    @RedirectName("build")
    Object attributeBuilder$Build(Object self);


//    @RedirectClass(ArmorTrim)
//    @MethodTarget
//    @RedirectName("withTooltip")
//    Object armorTrim$withTooltip(Object thi, boolean ttt);

    @RedirectClass(ArmorTrim)
    @FieldTarget
    @RedirectName("showInTooltipGetter")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean armorTrim$showInTooltip(Object thi);

//    @RedirectClass(DyeItemColor)
//    @MethodTarget
//    @RedirectName("withTooltip")
//    Object dyedItemColor$withTooltip(Object thi, boolean ttt);

    @RedirectClass(DyeItemColor)
    @MethodTarget
    @RedirectName("showInTooltip")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    boolean dyeItemColor$showInTooltip(Object thi);

    @RedirectClass(ToggleTooltipsHelper)
    @MethodTarget
    @RedirectName("applyIfPresent")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    void toggleTooltipsAtType(Object helper, @RedirectType(ItemStack)Object itemStack, boolean showInTooltips);

    @RedirectClass(UseRemainder)
    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    @RedirectName("convertInto")
    Object useRemainder$Remain(Object mainer);

    @RedirectClass(UseRemainder)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object newUseRemainder(@RedirectType(ItemStack) Object itemStack);

    @RedirectClass(UseCooldown)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object newUseCooldown(float sec);

    @RedirectClass(CraftUseCooldownComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object useCooldownComponent$Craft(@RedirectType(UseCooldown)Object comp);

    @RedirectClass(CraftUseCooldownComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object useCooldownComponent$Copy(@RedirectType(CraftUseCooldownComponent)Object comp);

    @RedirectClass(CraftUseCooldownComponent)
    @MethodTarget
    @RedirectName("getHandle")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object useCooldownComponent$Handle(Object comp);

    @RedirectClass(CraftFoodComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    FoodComponent foodProperties$Craft(@RedirectType(FoodProperties)Object comp);

    @RedirectClass(CraftFoodComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    FoodComponent foodProperties$Copy(@RedirectType(CraftFoodComponent)FoodComponent comp);

    @RedirectClass(CraftFoodComponent)
    @MethodTarget
    @RedirectName("getHandle")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object foodProperties$Handle(FoodComponent comp);

    @RedirectClass(CraftToolComponent)
    @ConstructorTarget
    ToolComponent tool$Craft(@RedirectType(Tool)Object comp);

    @RedirectClass(CraftToolComponent)
    @ConstructorTarget
    ToolComponent tool$Copy(@RedirectType(CraftToolComponent)ToolComponent comp);

    @RedirectClass(CraftToolComponent)
    @MethodTarget
    @RedirectName("getHandle")
    Object tool$Handle(ToolComponent comp);


    @RedirectClass(CraftEquippableComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object equippable$Craft(@RedirectType(Equippable)Object comp);

    @RedirectClass(CraftEquippableComponent)
    @MethodTarget
    @RedirectName("getHandle")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object equippable$Handle(@RedirectType(CraftEquippableComponent)Object comp);

    @RedirectClass(CraftJukeboxComponent)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object jukebox$Craft(@RedirectType(JukeboxPlayable)Object comp);

    @RedirectClass(CraftJukeboxComponent)
    @MethodTarget
    @RedirectName("getHandle")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2,below = true)
    Object jukebox$Handle(@RedirectType(CraftJukeboxComponent) Object comp);

    @RedirectClass(CraftMetaItem)
    @MethodTarget(isStatic = true)
    Multimap<Attribute, AttributeModifier> buildModifiers(@RedirectType(ItemAttributeModifiers)Object tag);

    @RedirectClass(ItemContainerContents)
    @MethodTarget(isStatic = true)
    @RedirectName("fromItems")
    Object itemContainerContents$fromItems(List<?> items);

    @RedirectClass(ItemContainerContents)
    @FieldTarget
    @RedirectName("itemsGetter")
    List<?> itemContainerContents$Items(Object val);

    @RedirectClass(TooltipDisplay)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    Object tooltipsDisplay$newComponent(boolean hide, SequencedSet<?> set);

    @RedirectClass(TooltipDisplay)
    @MethodTarget
    @RedirectName("shows")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    boolean tooltipsDisplay$shows(Object self, @RedirectType(DataComponentType) Object type);

    @RedirectClass(TooltipDisplay)
    @MethodTarget
    @RedirectName("hideTooltip")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    boolean tooltipsDisplay$hideTooltips(Object self);

    @RedirectClass(TooltipDisplay)
    @MethodTarget
    @RedirectName("hiddenComponents")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    SequencedSet<?> tooltipsDisplay$hiddenComponents(Object self);
}
