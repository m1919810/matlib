package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.nmsTests.*;
import me.matl114.matlib.unitTest.autoTests.nmsTests.ItemCodecTests;

public class NMSTestset extends TestSet {
    {
        // addTest(new CoreTests());
        //        addTest(new LevelTests());
        // addTest(new InventoryTests());
        addTest(new CraftBukkitUtilTests());
        addTest(new NMSNetworkTest());
        addTest(new ChatTests());
        addTest(new ItemCodecTests());
        addTest(new ItemMetaViewTests());
        addTest(new ItemDataTests());
    }
}
