package me.matl114.matlib.unitTest;

import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.implement.bukkit.chat.ChatInputManager;
import me.matl114.matlib.unitTest.autoTests.*;
import me.matl114.matlib.unitTest.manualTests.DisplayManagerTest;
import me.matl114.matlib.unitTest.manualTests.NMSPlayerTest;
import me.matl114.matlib.unitTest.manualTests.PlayerTest;
import me.matl114.matlib.utils.experimential.FakeSchedular;
import me.matl114.matlib.core.AddonInitialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;


public class MatlibTest extends JavaPlugin {
    public MatlibTest(){
    }
    @Getter
    @Setter
    private static MatlibTest instance;
    private static AddonInitialization initialization ;
    private static TestRunner testRunner;
    private static ChatInputManager chatInputManager;
    public void onLoad(){

    }
    public void onEnable() {
        instance = this;
        initialization = new AddonInitialization(this,"Matlib")
            .displayName("Matlib-Unittest")
            .testMode(true)
            .onEnable()
            .cast();
        FakeSchedular.init();
        chatInputManager =  new ChatInputManager()
            .init(this);
        testRunner = new TestRunner()
            .init(this)
            .setWarmup(false)
            .registerTestCase(new BukkitTestset())
            .registerTestCase(new CommonTestset())
            .registerTestCase(new NMSTestset())
            .registerTestCase(new ReflectionTestset())
            .registerTestCase(new DependsTestset())
            //project tests tests that havn't complete
//            .registerTestCase(new ComponentTests())
//            .registerTestCase(new EntityTests())
//            .registerTestCase(new ExperimentialTest())
            //manual tests
            .registerTestCase(new DisplayManagerTest())
            .registerTestCase(new PlayerTest())
            .registerTestCase(new NMSPlayerTest())
        ;
        this.getServer().getPluginManager().registerEvents(new TestListeners(),this);
    }
    public void onDisable() {

        instance = null;
        initialization.onDisable();
        initialization = null;
        chatInputManager.deconstruct();
        chatInputManager = null;
        testRunner.deconstruct();
        testRunner = null;
        HandlerList.unregisterAll(this);
    }
}
