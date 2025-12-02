package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.bukkitTests.BukkitAPITests;

public class DemoTestset extends TestSet {
    {

        addTest(new EntityToNbt());
//        addTest(new BukkitTranslationTests());
        //addTest(new PdcTests());
    }
}
