package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.EntityUtils;

public class EntityUtilTests implements TestCase {
    @OnlineTest(name = "Entity util init test")
    public void test_entityUtilCommon() throws Throwable {
        Debug.logger(EntityUtils.getCraftEntityClass());
        Debug.logger(EntityUtils.getCraftPlayerClass());
        Debug.logger(EntityUtils.getGetHandleMethodInvoker());
    }
}
