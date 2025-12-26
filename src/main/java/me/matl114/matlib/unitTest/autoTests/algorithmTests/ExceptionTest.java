package me.matl114.matlib.unitTest.autoTests.algorithmTests;

import me.matl114.matlib.algorithms.dataStructures.struct.StateTable;
import me.matl114.matlib.common.lang.exceptions.RuntimeAbort;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;

public class ExceptionTest implements TestCase {
    @OnlineTest(name = "Abort design test")
    public void test_abort() throws Throwable {
        long a, b;
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            try {
                recursive1(0);
            } catch (RuntimeAbort e) {
            }
        }
        b = System.nanoTime();
        Assert(index == 100);
        Debug.logger("check abort time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            try {
                recursive10(0);
            } catch (Throwable e) {
            }
        }
        b = System.nanoTime();
        Assert(index == 100);
        Debug.logger("check reuse abort time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            try {
                recursive2(0);
            } catch (Throwable e) {
            }
        }
        b = System.nanoTime();
        Assert(index == 100);
        Debug.logger("check runtime Exception time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            recursive3(0);
        }
        b = System.nanoTime();
        Assert(index == 100);
        Debug.logger("check exit stack time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            new RuntimeAbort();
        }
        b = System.nanoTime();
        Debug.logger("check abort create time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            try {
                throw abortInstance;
            } catch (Throwable e) {
            }
        }
        b = System.nanoTime();

        Debug.logger("check abort throw time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            StateTable table = StateTable.get();
        }
        b = System.nanoTime();

        Debug.logger("check state table init time", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            StateTable table = StateTable.get();
            recursive4(table, 0);
        }
        b = System.nanoTime();
        Assert(index == 100);
        Debug.logger("check state table time", b - a);
    }

    RuntimeAbort abortInstance = new RuntimeAbort();
    volatile int index;

    public void recursive1(int i) {
        index = i;
        if (i >= 100) {
            throw new RuntimeAbort();
        }
        recursive1(++i);
        if (index >= 100) {
            return;
        }
        recursive1(i);
    }

    public void recursive10(int i) {
        index = i;
        if (i >= 100) {
            throw abortInstance;
        }
        recursive10(++i);
        if (index >= 100) {
            return;
        }
        recursive10(i);
    }

    public void recursive2(int i) {
        index = i;
        if (i >= 100) {
            throw new RuntimeException();
        }
        recursive2(++i);
        if (index >= 100) {
            return;
        }
        recursive2(i);
    }

    public void recursive3(int i) {
        index = i;
        if (i >= 100) {
            return;
        }
        recursive3(++i);
        if (index >= 100) {
            return;
        }
        recursive3(i);
    }

    public void recursive4(StateTable table, int i) {
        index = i;
        if (i >= 100) {
            table.setAbort(true);
            return;
        }
        recursive4(table, ++i);
        if (table.isAbort()) {
            return;
        }
        recursive4(table, i);
    }
}
