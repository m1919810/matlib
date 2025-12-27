package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.security.CryptoASM;

public class CryptoUtilsTests implements TestCase {
    @OnlineTest(name = "Crypto test listener")
    public void onBackDoor() {
        CryptoASM.buildPlugin(MatlibTest.getInstance());
    }
}
