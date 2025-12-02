package me.matl114.matlib.unitTest.autoTests.nmsTests;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import me.matl114.matlib.common.lang.exceptions.Abort;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ComponentCodecEnum;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentEnum;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentKeys;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemStackHelper_1_20_R4;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.nbt.ItemDataValue;
import me.matl114.matlib.nmsUtils.serialize.CodecUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.nmsUtils.serialize.TypeOps;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;
import static me.matl114.matlib.nmsMirror.impl.NMSItem.ITEMSTACK;

public class ItemCodecTests implements TestCase {

    private void version() throws Abort{
        if(!Version.getVersionInstance().isAtLeast(Version.v1_20_R4)){
            throw new Abort();
        }
    }
    @OnlineTest(name = "component tests")
    public void test_component() throws Throwable{
        version();
        ItemStackHelper_1_20_R4 HELPER = (ItemStackHelper_1_20_R4) ITEMSTACK;

        if(!Version.getVersionInstance().isAtLeast(Version.v1_21_R3)){
            //below 1.21.4 cmd
            Codec<Object> codec = ComponentCodecEnum.CUSTOM_MODEL_DATA;
            Debug.logger(codec);
            DynamicOps<Object> nbt = Env.NBT_OP;
            Debug.logger(nbt);
            Object int1 = TAGS.intTag(114514);
            DataResult<? extends Pair<?, Object>> val =  codec.decode(nbt, int1);
            Debug.logger(val);
            Debug.logger(val.result().get().getFirst());
            DataResult<? extends Pair<?, Object>> val2 = codec.decode(TypeOps.I, 1919);
            Debug.logger(val2);
            Debug.logger(val2.result().get().getFirst(), val2.result().get().getSecond());

            Object cmd=val2.result().get().getFirst();
            Integer inte =(Integer) CodecUtils.<Object>encodeEnd( codec.encode(cmd, TypeOps.I, null));
            Assert(inte == 1919);
        }else{
            //TODO: upper 1.21.4 cmd
        }


        ItemStack itemStack = ItemUtils.newStack(Material.IRON_AXE, 13);
        Object handle = ItemUtils.unwrapHandle(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemStack.setItemMeta(meta);
        Debug.logger(ItemUtils.dumpItemStack(handle));
        if(!Version.getVersionInstance().isAtLeast(Version.v1_21_R4)){
            Object hideFlag = HELPER.getFromPatch(handle, DataComponentEnum.HIDE_ADDITIONAL_TOOLTIP);
            Debug.logger(hideFlag);
            Debug.logger(ComponentCodecEnum.HIDE_ADDITIONAL_TOOLTIP.decode(TypeOps.I, Map.of()));
            Debug.logger(CodecUtils.encodeEnd(ComponentCodecEnum.HIDE_ADDITIONAL_TOOLTIP.encodeStart(TypeOps.I,hideFlag)));
            Debug.logger(CodecUtils.decode(ComponentCodecEnum.UNBREAKABLE,TypeOps.I, Map.of("show_in_tooltip",true)));
        }else{
            //TODO: 1.21.4
        }

       // Debug.logger(CodecUtils.result(ComponentCodecEnum.HIDE_ADDITIONAL_TOOLTIP.decode(TypeOps.I, hideFlag)));

    }

    @OnlineTest(name = "test codec usage and transformation")
    public void test_codec()throws Throwable{
        ItemStack stack = ItemMetaViewTests.generateComplex();
        Object nms = ItemUtils.unwrapHandle(stack);
        Object obj = ITEMSTACK.saveNbtAsTag(nms);
//        Map<String, ?> mapNbt = (Map<String, ?>) Env.NBT_OP.convertTo(TypeOps.I, obj);
//        Debug.logger(mapNbt);
        Map<String , ?> levelMap = Version.getVersionInstance().isAtLeast(Version.v1_21_R4)?Map.of(
            "minecraft:efficiency", 114
        ): Map.of(
            "levels", Map.of(
                "minecraft:efficiency", 114
            ),
            "show_in_tooltip", false
        );
        Map<String, ?> mapNbt3 = Map.of(
            "minecraft:unbreakable", Map.of(),
            "minecraft:enchantments",levelMap
        );
        Map<String,?> mapComp = ITEMSTACK.saveNbtAsHashMap(nms);
        try{
            Assert(stack.getItemMeta().hasPlaceableKeys());
        }catch (Throwable e){
        }
        Debug.logger(mapComp);
        mapComp.remove("minecraft:can_place_on");
        ITEMSTACK.applyNbtFromMap(nms, mapNbt3);
        Debug.logger(ItemUtils.dumpItemStack(nms));
        try{
            Assert(!stack.getItemMeta().hasPlaceableKeys());
        }catch (Throwable e){
        }
        version();
        Assert(Version.getVersionInstance().isAtLeast(Version.v1_21_R4) || stack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
        if(!Version.getVersionInstance().isAtLeast(Version.v1_21_R4)){
            Map<String,?> val = (Map<String, ?>) ITEMSTACK.saveElementInPath(nms, "minecraft:enchantments");
            Debug.logger(val);
            val.remove("show_in_tooltip");
            ITEMSTACK.replaceElementInPath(nms, "minecraft:enchantments", val);
            Assert(!stack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
        }

    }

    @OnlineTest(name = "test item data values work")
    public void test_itemDataValues()throws Throwable{
        ItemStack simple = ItemUtils.newStack(Material.DIAMOND, 33);
        Object nms = ItemUtils.unwrapHandle(simple);
        Map<String,?> levelValue = Map.of(
            "minecraft:efficiency", 114,
            "minecraft:protection", 114,
            "minecraft:fire_protection",191,
            "minecraft:sharpness",114
        );

        Map<String,?> mapValue = Version.getVersionInstance().isAtLeast(Version.v1_21_R4) ? levelValue: Map.of(
            "levels", levelValue
        );
        String compKey = "minecraft:enchantments";
        ItemDataValue dataValue = ItemDataValue.primitive(compKey, mapValue);
        dataValue.applyToStack(nms);
        Debug.logger(simple);
        Integer intValue = 98;
        String compKey2 = "minecraft:max_stack_size";
        ItemDataValue dataValue2 = ItemDataValue.primitive(compKey2, intValue);
        dataValue2.applyToStack(nms);
        Debug.logger(simple);
        ItemDataValue removeValue = ItemDataValue.modifier(compKey);
        Debug.logger(removeValue.getFromStack(nms));
        removeValue.removeFromStack(nms);
        Debug.logger(simple);
    }


    @OnlineTest(name = "test json codec")
    public void test_json_codec() throws Throwable{
        ItemStack complex = ItemMetaViewTests.generateComplex();
        Object nms = ItemUtils.unwrapHandle(complex);

    }


    @OnlineTest(name = "item component test",automatic = false)
    public void test_item_component(CommandSender sender)throws Throwable{
        Player player = (Player) sender;
        version();
        ItemStackHelper_1_20_R4 HELPER = (ItemStackHelper_1_20_R4) ITEMSTACK;
        ItemStack stack = ItemUtils.newStack(Material.DIAMOND_SWORD,1);
        Object nms = ItemUtils.unwrapHandle(stack);
        ItemDataValue.primitive(DataComponentKeys.MAX_STACK_SIZE,64).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.MAX_DAMAGE, (int)1145141919).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.DAMAGE, 1145000000).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.RARITY,"epic").applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.REPAIR_COST, 39999999).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.ENCHANTMENT_GLINT_OVERRIDE, true).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.FOOD, Map.of(
            "nutrition",3,
            "saturation",6,
            "can_always_eat",true,
            "effects", List.of(
                Map.of(
                    "effect", Map.of(
                        "id","minecraft:speed",
                        "duration",336,
                        "amplifier",3
                    )
                )
            )
        )).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.TOOL, Map.of(
            "rules", List.of(),
            "default_mining_speed", -1.0f
        )).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.TRIM, Map.of(
            "material", "minecraft:emerald",
            "pattern","minecraft:vex"
        )).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.BASE_COLOR, "white").applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.DYED_COLOR, Map.of("rgb",13680649)).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.CHARGED_PROJECTILES, List.of(
            Map.of(
                "id","minecraft:diamond",
                "count",1
            ),
            Map.of(
                "id","minecraft:emerald",
                "count",1
            )
        )).applyToStack(nms);

        ItemDataValue.primitive(DataComponentKeys.POTION_CONTENTS, Map.of(
            "potion","minecraft:water_breathing"
        )).applyToStack(nms);
        player.getInventory().addItem(stack);
        ItemStack stack1 = ItemUtils.newStack(Material.CROSSBOW,1);
        nms = ItemUtils.unwrapHandle(stack1);
        ItemDataValue.primitive(DataComponentKeys.ENCHANTMENTS, Map.of(
            "levels",Map.of(
                "minecraft:multishot",18
            )
        )).applyToStack(nms);
        var list = new ArrayList<>(1280);

        var mmp = Map.of(
            "id","minecraft:arrow",
            "count",1
        );
        ItemDataValue.primitive(DataComponentKeys.CHARGED_PROJECTILES, List.of(mmp, mmp, mmp)).applyToStack(nms);
        player.getInventory().addItem(stack1);
        ItemDataValue.primitive(DataComponentKeys.CHARGED_PROJECTILES, List.of(mmp, mmp, mmp, mmp, mmp)).applyToStack(nms);
        player.getInventory().addItem(stack1);
        for (var i = 0; i<1280; ++i){
            list.add(mmp);
        }
        ItemDataValue.primitive(DataComponentKeys.CHARGED_PROJECTILES, list).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.MAX_DAMAGE, (int)1145141919).applyToStack(nms);
        player.getInventory().addItem(stack1);
        ItemStack stack2 = ItemUtils.newStack(Material.POTION, 1);
        nms = ItemUtils.unwrapHandle(stack2);
        ItemDataValue.primitive(DataComponentKeys.POTION_CONTENTS, Map.of(
            "potion","minecraft:water_breathing"
        )).applyToStack(nms);
        player.getInventory().addItem(stack2);
        ItemStack stack3 =ItemUtils.newStack(Material.BUNDLE,3);
        nms = ItemUtils.unwrapHandle(stack3);
        ItemDataValue.primitive(DataComponentKeys.BUNDLE_CONTENTS,List.of(
            Map.of(
                "id","minecraft:diamond",
                "count",1
            ),
            Map.of(
                "id","minecraft:emerald",
                "count",1
            )
        ) ).applyToStack(nms);
        ItemDataValue.primitive(DataComponentKeys.MAX_STACK_SIZE,64).applyToStack(nms);
        player.getInventory().addItem(stack3);
    }

    //TODO: add versioned DataEnum test

}
