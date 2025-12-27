package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import java.util.concurrent.FutureTask;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.ThreadUtils;
import org.bukkit.Bukkit;

public class ThreadUtilTests implements TestCase {
    @OnlineTest(name = "run sync tests")
    public void test_runsync() throws Throwable {
        for (int i = 0; i < 10; ++i) {
            long a = System.nanoTime();
            FutureTask<Void> task = ExecutorUtils.getFutureTask(() -> {
                long b = System.nanoTime();
                Debug.logger("Main Executor Response Time", b - a);
                Assert(Bukkit.isPrimaryThread());
            });
            ThreadUtils.executeSync(task);
            task.get();
        }
        ThreadUtils.executeSync(() -> {
            Debug.logger("throw a fucking exception on main ");
            throw new RuntimeException();
        });
        ExecutorUtils.sleepNs(50_900_000);
    }
}
