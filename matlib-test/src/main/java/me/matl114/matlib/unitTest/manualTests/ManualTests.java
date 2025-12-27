package me.matl114.matlib.unitTest.manualTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;

public class ManualTests implements TestCase {
    @OnlineTest(name = "test thread dump", automatic = false)
    public void test_threaddump() throws Throwable {
        Debug.threadDump();
    }
}
