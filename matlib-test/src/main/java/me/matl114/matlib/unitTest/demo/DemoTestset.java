package me.matl114.matlib.unitTest.demo;

import me.matl114.matlib.unitTest.TestSet;

public class DemoTestset extends TestSet {
    public void init() {
        addTest(new EntityToNbt());
        //        addTest(new BukkitTranslationTests());
        // addTest(new PdcTests());
    }
}
