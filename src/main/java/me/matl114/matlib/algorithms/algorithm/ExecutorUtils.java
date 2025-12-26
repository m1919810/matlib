package me.matl114.matlib.algorithms.algorithm;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Utility class for executor operations, thread management, and task execution.
 * This class provides methods for creating BukkitRunnable instances, thread sleeping,
 * FutureTask creation, and thread debugging operations.
 */
public class ExecutorUtils {

    /**
     * Converts a Runnable to a BukkitRunnable if it isn't already one.
     * If the input is already a BukkitRunnable, it is returned as-is.
     * Otherwise, a new BukkitRunnable is created that wraps the original Runnable.
     *
     * @param runnable The runnable to convert
     * @return A BukkitRunnable instance
     */
    public static BukkitRunnable getRunnable(Runnable runnable) {
        return runnable instanceof BukkitRunnable
                ? (BukkitRunnable) runnable
                : new BukkitRunnable() {
                    public void run() {
                        runnable.run();
                    }
                };
    }

    /**
     * Sleeps the current thread for the specified number of milliseconds.
     * This method silently catches any InterruptedException.
     *
     * @param ms The number of milliseconds to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Throwable e) {
        }
    }

    /**
     * Sleeps the current thread for the specified number of nanoseconds.
     * This method uses a combination of Thread.sleep() for milliseconds and
     * LockSupport.parkNanos() for the remaining nanoseconds.
     *
     * @param ns The number of nanoseconds to sleep
     */
    public static void sleepNs(long ns) {
        long ms = ns / 1_000_000;
        long left = ns % 1_000_000;
        if (ms > 0) {
            sleep(ms);
        }
        LockSupport.parkNanos(left);
    }

    /**
     * Creates a FutureTask that immediately returns the specified value.
     * This is useful for creating completed futures with a specific result.
     *
     * @param <T> The type of the value
     * @param val The value to return
     * @return A FutureTask that returns the specified value
     */
    public static <T> FutureTask<T> signal(T val) {
        return getFutureTask(() -> {}, val);
    }

    /**
     * Creates a FutureTask that represents a completed void operation.
     *
     * @return A FutureTask representing a completed void operation
     */
    public static FutureTask<Void> signal() {
        return getFutureTask(() -> {});
    }

    /**
     * Creates a FutureTask from a Runnable.
     * If the input is already a FutureTask, it is cast and returned.
     * Otherwise, a new FutureTask is created with the Runnable and null result.
     *
     * @param runnable The runnable to wrap
     * @return A FutureTask wrapping the runnable
     */
    public static FutureTask<Void> getFutureTask(Runnable runnable) {
        return runnable instanceof FutureTask<?> future
                ? (FutureTask<Void>) future
                : new FutureTask<>(runnable, (Void) null);
    }

    /**
     * Creates a FutureTask from a Runnable with a specific result value.
     *
     * @param <T> The type of the result
     * @param runnable The runnable to execute
     * @param val The result value to return
     * @return A FutureTask that executes the runnable and returns the specified value
     */
    public static <T> FutureTask<T> getFutureTask(Runnable runnable, T val) {
        return new FutureTask<>(runnable, val);
    }

    /**
     * Creates a FutureTask from a Callable.
     *
     * @param <T> The type of the result
     * @param callable The callable to wrap
     * @return A FutureTask wrapping the callable
     */
    public static <T> FutureTask<T> getFutureTask(Callable<T> callable) {
        return new FutureTask<>(callable);
    }

    /**
     * Waits for a FutureTask to complete and returns its result.
     * This method wraps any checked exceptions in a RuntimeException.
     *
     * @param <T> The type of the result
     * @param futureTask The FutureTask to await
     * @return The result of the FutureTask
     * @throws RuntimeException if the FutureTask throws an exception
     */
    public static <T extends Object> T awaitFuture(FutureTask<T> futureTask) {
        try {
            return futureTask.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dumps information about all threads to the specified logger.
     * This method is useful for debugging thread-related issues.
     *
     * @param log The logger to write thread information to
     */
    public static void dumpAllThreads(Logger log) {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        for (ThreadInfo thread : threads) {
            dumpThread(thread, (syr) -> log.log(Level.SEVERE, syr));
        }
    }

    /**
     * Dumps information about a single thread to the specified consumer.
     * This method formats thread information including stack traces and monitor information.
     *
     * @param thread The ThreadInfo to dump
     * @param out The consumer to receive the formatted thread information
     */
    private static void dumpThread(ThreadInfo thread, Consumer<String> out) {
        out.accept("------------------------------");
        //
        out.accept("Current Thread: " + thread.getThreadName());
        out.accept("\tPID: " + thread.getThreadId()
                + " | Suspended: " + thread.isSuspended()
                + " | Native: " + thread.isInNative()
                + " | State: " + thread.getThreadState());
        if (thread.getLockedMonitors().length != 0) {
            out.accept("\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                out.accept("\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }
        out.accept("\tStack:");
        //
        for (StackTraceElement stack : thread.getStackTrace()) // Paper
        {
            out.accept("\t\t" + stack);
        }
    }

    /**
     * Dumps information about all threads to the specified PrintStream.
     * This method is useful for debugging thread-related issues.
     *
     * @param printStream The PrintStream to write thread information to
     */
    private static void dumpAllThread(PrintStream printStream) {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        for (ThreadInfo thread : threads) {
            dumpThread(thread, printStream::println);
        }
    }
}
