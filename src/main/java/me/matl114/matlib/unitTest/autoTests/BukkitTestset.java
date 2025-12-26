package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.bukkitTests.BukkitAPITests;

public class BukkitTestset extends TestSet {
    {
        addTest(new BukkitAPITests());
        //        addTest(new BukkitTranslationTests());
        // addTest(new PdcTests());
    }
}
