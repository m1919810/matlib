package me.matl114.matlib.unitTest.autoTests.algorithmTests;

import me.matl114.matlib.algorithms.dataStructures.frames.bits.BitList;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;

public class CollectionTest implements TestCase {
    @OnlineTest(name = "bit list test")
    public void test_bitlist() throws Throwable {
        BitList list = new BitList();

        list.setTrue(0, 128 + 32);
        Debug.logger(1, list);
        list.setTrue(10);
        Debug.logger(2, list);
        list.setFalse(20);
        Debug.logger(3, list);
        Assert(!list.get(20));
        list.addTrue(64);
        Debug.logger(4, list);
        list.addFalse(64);
        Debug.logger(5, list);
        list.remove(64);
        list.remove(64);
        Debug.logger(6, list);
        list.setFalse(0, 336778837);
        Debug.logger(7, list);
        for (int i = 0; i < 256; ++i) {
            if (i % 2 == 1) {
                list.setTrue(i);
            } else {
                list.setFalse(i);
            }
        }
        Debug.logger(8, list);
        list.addFalse(64);
        Debug.logger(9, list);
        list.addFalse(76);
        Debug.logger(10, list);
        list.remove(64);
        Debug.logger(11, list);
        list.remove(76);
        Debug.logger(12, list);
    }
}
