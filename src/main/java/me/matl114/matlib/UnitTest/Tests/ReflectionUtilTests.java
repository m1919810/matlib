package me.matl114.matlib.UnitTest.Tests;

import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.CraftUtils;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Inventory.ItemStacks.CleanItemStack;
import me.matl114.matlib.Utils.Version.DefaultVersionedFeatureImpl;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class ReflectionUtilTests implements TestCase {
    @OnlineTest(name = "CraftUtils VarHandle test")
    public void testDisplayVarHandle(){
        ItemStack item = new CleanItemStack(Material.BOOK,"这是&a一个&c书","这&e是一本&r书","这并&6不是两&3本书");
        AddUtils.addGlow(item);
        ItemMeta meta = item.getItemMeta();
        Debug.logger(CraftUtils.getDisplayNameHandle().get(meta));
        Debug.logger(CraftUtils.getLoreHandle().get(meta));
        item = CraftUtils.getCraftCopy(item,true);
        Debug.logger(item);
        Assert(CraftUtils.isCraftItemStack(item));
        Debug.logger(CraftUtils.getHandleHandle().get(item));
        ItemMeta meta2 = item.getItemMeta();
        Assert(CraftUtils.matchDisplayNameField(meta,meta2));
        Assert(CraftUtils.matchLoreField(meta,meta2));
        Assert(CraftUtils.matchEnchantmentsFields(meta,meta2));
        Debug.logger(CraftUtils.getEnchantmentsHandle().get(meta2));
        Assert( ((Map)CraftUtils.getEnchantmentsHandle().get(meta)).size()==1);
        Assert(CraftUtils.matchItemStack(item,item,true));
        ItemStack blockStateItem = new CleanItemStack(Material.SPAWNER);
        ItemMeta blockStateMeta = blockStateItem.getItemMeta();
        Assert(blockStateMeta instanceof BlockStateMeta );
        BlockStateMeta blockState = (BlockStateMeta) blockStateMeta;
        BlockState blockStateThis = blockState.getBlockState();
        Assert(blockStateThis instanceof CreatureSpawner);
        CreatureSpawner spawner = (CreatureSpawner) blockStateThis;
        spawner.setSpawnedType(EntityType.ZOMBIE);
        spawner.setSpawnRange(114);
        blockState.setBlockState(spawner);
        Assert(CraftUtils.matchBlockStateMetaField(blockState,blockState));
        var reflectAsm = DefaultVersionedFeatureImpl.getBlockEntityTagAccess().getReflectAsm();
        Assert( reflectAsm );
        Debug.logger(reflectAsm.getA(),reflectAsm.getA().getClass(),reflectAsm.getB());
        try{
            reflectAsm.getA().get(blockState,reflectAsm.getB());
        }catch (Throwable protect){
        }
        Debug.logger(blockState.getClass(),blockState.getClass().getClassLoader(), ClassLoader.getSystemClassLoader());
    }


    @OnlineTest(name = "CraftUtils Invoker test")
    public void testCraftInvoker(){
        ItemStack item = new CleanItemStack(Material.BOOK);
        ItemStack citem = CraftUtils.getCraftCopy(item);
        Debug.logger(citem);
        Debug.logger(citem.getClass());
        Assert(CraftUtils.isCraftItemStack(citem));
        Object nmsItem = CraftUtils.getNMSCopy(item);
        Debug.logger(nmsItem);
        Debug.logger(nmsItem.getClass());
        Assert(CraftUtils.isNMSItemStack(nmsItem));
        Debug.logger("Test Success");
    }
}
