package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.networkTests.NettyBasicTest;

public class NetworkTestset extends TestSet {
    {
        addTest(new NettyBasicTest());
    }
}
