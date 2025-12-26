package me.matl114.matlib.unitTest.autoTests.algorithmTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.samples.DemoTargetClass;

public class CommonTest implements TestCase {
    @OnlineTest(name = "common test")
    public void test_common() throws Throwable {
        DemoTargetClass targetClass = new DemoTargetClass();
        targetClass.testMethod();
    }
}
