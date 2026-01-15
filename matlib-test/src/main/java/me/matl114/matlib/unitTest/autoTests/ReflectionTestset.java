package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.reflectionTests.ASMUtilsTests;
import me.matl114.matlib.unitTest.autoTests.reflectionTests.ProxyUtilTests;
import me.matl114.matlib.unitTest.autoTests.reflectionTests.ReflectionUtilTests;

public class ReflectionTestset extends TestSet {
    public void init() {
        // addTest(new DynamicCodeTests());
        addTest(new ReflectionUtilTests());
        addTest(new ProxyUtilTests());
        addTest(new ASMUtilsTests());
    }
}
