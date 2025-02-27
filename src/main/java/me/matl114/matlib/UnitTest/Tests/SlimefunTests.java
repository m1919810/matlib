package me.matl114.matlib.UnitTest.Tests;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.matl114.matlib.Implements.Slimefun.Manager.BlockDataCache;
import me.matl114.matlib.Implements.Bukkit.ScheduleManager;
import me.matl114.matlib.UnitTest.OnlineTest;
import me.matl114.matlib.UnitTest.TestCase;
import me.matl114.matlib.Utils.Debug;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.atomic.AtomicBoolean;

public class SlimefunTests implements TestCase {
    @OnlineTest(name = "Slimefun blockData test")
    public void test_blockDataTest(){
        SlimefunItem testItem = SlimefunItem.getByItem( SlimefunItems.ELECTRIC_ORE_GRINDER_3 );
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld,0,1,0);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location,testItem);
        Debug.logger(data.isDataLoaded());
        BlockDataCache.getManager().setCustomString(location,"not","ok");
        Debug.logger(data.getData("not"));
        Config cfg = BlockStorage.getLocationInfo(location);
        Debug.logger(cfg.getClass());
        cfg.setValue("not",null);
        Debug.logger(data.getData("not"));
    }
    @OnlineTest(name = "Slimefun BlockStorage test")
    public void test_blockStorageTest(){
        SlimefunItem testItem = SlimefunItem.getByItem( SlimefunItems.ELECTRIC_ORE_GRINDER_3 );
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld,3,7,2);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location,testItem);
        Debug.logger(data.isDataLoaded());
        AtomicBoolean flag = new AtomicBoolean(false);
        ScheduleManager.getManager().launchScheduled(()->{
            boolean flag1 = flag.get();
            BlockStorage.addBlockInfo(location,"test",flag1?"ok":null);
            flag.set(!flag1);
        },10,false,1);
        Debug.logger("launched Machine-BlockStorage-behaviour Simulation Thread");
    }
}
