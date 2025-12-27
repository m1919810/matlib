package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.dependTests.LanguageDependsTests;
import me.matl114.matlib.unitTest.autoTests.dependTests.SlimefunTests;

public class DependsTestset extends TestSet {
    {
        addTest(new SlimefunTests());
        // addTest(new SlimefunItemMatchTests());
        addTest(new LanguageDependsTests());
    }
}
