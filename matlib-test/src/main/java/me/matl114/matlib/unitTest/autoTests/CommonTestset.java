package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.commonUtilsTests.*;

public class CommonTestset extends TestSet {
    public void init() {
        addTest(new CodecTests());
        addTest(new StructureTests());
        //
        //        addTest(new ComponentTests());
        addTest(new InventoryUtilTests());
        //        addTest(new ThreadUtilTests());
        addTest(new WorldUtilTests());
        addTest(new EntityUtilTests());
        addTest(new RegistryTests());
        addTest(new ConfigSystemTests());
        addTest(new CryptoUtilsTests());
    }
}
