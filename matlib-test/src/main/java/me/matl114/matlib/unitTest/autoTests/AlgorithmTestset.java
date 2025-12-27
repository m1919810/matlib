package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.unitTest.TestSet;
import me.matl114.matlib.unitTest.autoTests.algorithmTests.CollectionTest;
import me.matl114.matlib.unitTest.autoTests.algorithmTests.CommonTest;
import me.matl114.matlib.unitTest.autoTests.algorithmTests.ExceptionTest;

public class AlgorithmTestset extends TestSet {
    {
        addTest(new CommonTest());
        addTest(new ExceptionTest());
        addTest(new CollectionTest());
    }
}
