package me.matl114.matlib.utils;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlibAdaptor.common.lang.enums.TaskRequest;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;

public class ThreadUtils {
    private static final Plugin MOCK_PLUGIN = new PluginBase() {
        PluginDescriptionFile pdf = new PluginDescriptionFile("Unknown", "unknown", "me.matl114.matlib.UnknownClass");
        FileConfiguration config = new YamlConfiguration();
        Logger log = Logger.getLogger("Mock-Plugin");

        @Override
        public File getDataFolder() {
            return new File(".");
        }

        @Override
        public PluginDescriptionFile getDescription() {
            return pdf;
        }

        @Override
        public @NotNull PluginMeta getPluginMeta() {
            return this.pdf;
        }

        @Override
        public FileConfiguration getConfig() {
            return config;
        }

        @Override
        public InputStream getResource(String s) {
            throw new NotImplementedYet();
        }

        @Override
        public void saveConfig() {}

        @Override
        public void saveDefaultConfig() {}

        @Override
        public void saveResource(String s, boolean b) {}

        @Override
        public void reloadConfig() {}

        @Override
        public PluginLoader getPluginLoader() {
            return null;
        }

        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void onDisable() {}

        @Override
        public void onLoad() {}

        @Override
        public void onEnable() {}

        @Override
        public boolean isNaggable() {
            return false;
        }

        @Override
        public void setNaggable(boolean b) {}

        @Override
        public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
            return null;
        }

        @Override
        public BiomeProvider getDefaultBiomeProvider(String s, String s1) {
            return null;
        }

        @Override
        public Logger getLogger() {
            return this.log;
        }

        @Override
        public @NotNull LifecycleEventManager<Plugin> getLifecycleManager() {
            return null;
        }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            return false;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return List.of();
        }
    };

    /**
     * Gets a fake plugin instance that can be used for scheduling tasks.
     * This mock plugin provides a minimal implementation of the Plugin interface
     * and is used internally by ThreadUtils for task scheduling operations.
     *
     * @return A mock Plugin instance for internal use
     */
    public static Plugin getFakePlugin() {
        return MOCK_PLUGIN;
    }
    //    private static final ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(
    //        4, Integer.MAX_VALUE,30L, TimeUnit.SECONDS, new SynchronousQueue<>(),
    //        new ThreadFactoryBuilder().setNameFormat("Matlib Async Tasks - %1$d").build()
    //    );
    private static final int MAX_CACHED_LOCK = 8000;
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, AtomicBoolean>> lockedSet =
            new ConcurrentHashMap<>();

    private static final Executor MAIN_THREAD_EXECUTOR;

    /**
     * Runs a task asynchronously if the lock is available, or blocks if it's already locked.
     * This method uses a thread-safe locking mechanism to ensure only one task per lock
     * can run simultaneously. The lock is automatically released after the task completes.
     *
     * @param lock The object to use as a lock for preventing concurrent execution
     * @param runnable The task to execute asynchronously
     * @return true if the task was successfully scheduled, false if the lock was already taken
     */
    public static boolean runAsyncOrBlocked(Object lock, Runnable runnable) {
        var set = lockedSet.computeIfAbsent(lock.getClass(), i -> new ConcurrentHashMap<>());
        var locker = set.computeIfAbsent(lock, i -> new AtomicBoolean(false));
        if (locker.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                try {
                    runnable.run();
                } finally {
                    set.remove(lock, locker);
                    locker.set(false);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Executes a task synchronously on the main thread.
     * If the current thread is already the main thread, the task runs immediately.
     * Otherwise, the task is scheduled to run on the main thread.
     *
     * @param runnable The task to execute synchronously
     */
    public static void executeSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            runSyncNMS(runnable);
        }
    }

    /**
     * Executes a task synchronously on the main thread with scheduling.
     * If the current thread is already the main thread, the task is scheduled for later execution.
     * Otherwise, the task is scheduled to run on the main thread.
     *
     * @param runnable The task to execute synchronously
     */
    public static void executeSyncSched(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            CompletableFuture.runAsync(runnable, MAIN_THREAD_EXECUTOR);
        } else {
            runSyncNMS(runnable);
        }
    }

    /**
     * Executes a task synchronously on the main thread after a specified delay.
     *
     * @param runnable The task to execute synchronously
     * @param delay The delay in ticks before executing the task
     */
    public static void executeSync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(MOCK_PLUGIN, runnable, delay);
    }

    /**
     * Schedules a task to run synchronously on the main thread with a repeating interval.
     *
     * @param runnable The task to execute synchronously
     * @param delay The delay in ticks before the first execution
     * @param period The interval in ticks between subsequent executions
     */
    public static void scheduleSync(Runnable runnable, int delay, int period) {
        Bukkit.getScheduler().runTaskTimer(MOCK_PLUGIN, runnable, delay, period);
    }

    /**
     * Executes a task synchronously on the main thread.
     * This method is deprecated and will be removed in a future version.
     * Use {@link #executeSync(Runnable)} instead.
     *
     * @param runnable The task to execute synchronously
     * @param pl The plugin (ignored, kept for backward compatibility)
     * @deprecated Use {@link #executeSync(Runnable)} instead
     */
    @Deprecated(forRemoval = true)
    public static void executeSync(Runnable runnable, Plugin pl) {
        executeSync(runnable);
        //        if(Bukkit.isPrimaryThread()){
        //            runnable.run();
        //        }else {
        //            runSync(runnable,pl);
        //        }
    }

    /**
     * Executes a task asynchronously on a separate thread.
     *
     * @param runnable The task to execute asynchronously
     */
    public static void executeAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MOCK_PLUGIN, runnable);
    }

    /**
     * Schedules a task to run asynchronously with a repeating interval.
     *
     * @param runnable The task to execute asynchronously
     * @param delay The delay in ticks before the first execution
     * @param period The interval in ticks between subsequent executions
     */
    public static void scheduleAsync(Runnable runnable, int delay, int period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MOCK_PLUGIN, runnable, delay, period);
    }

    /**
     * Executes a task asynchronously after a specified delay.
     *
     * @param runnable The task to execute asynchronously
     * @param delay The delay in ticks before executing the task
     */
    public static void executeAsync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(MOCK_PLUGIN, runnable, delay);
    }

    /**
     * Schedules a Callable task to run synchronously on the main thread after a delay.
     * Returns a FutureTask that can be used to retrieve the result or check completion status.
     *
     * @param <T> The type of the result returned by the callable
     * @param callable The callable task to execute
     * @param delay The delay in ticks before executing the task
     * @return A FutureTask representing the scheduled task
     */
    public <T> FutureTask<T> scheduleFutureSync(Callable<T> callable, int delay) {
        FutureTask<T> future = ExecutorUtils.getFutureTask(callable);
        executeSync(future, delay);
        return future;
    }

    /**
     * Schedules a Callable task to run asynchronously after a delay.
     * Returns a FutureTask that can be used to retrieve the result or check completion status.
     *
     * @param <T> The type of the result returned by the callable
     * @param callable The callable task to execute
     * @param delay The delay in ticks before executing the task
     * @return A FutureTask representing the scheduled task
     */
    public <T> FutureTask<T> scheduleFutureAsync(Callable<T> callable, int delay) {
        FutureTask<T> future = ExecutorUtils.getFutureTask(callable);
        executeAsync(future, delay);
        return future;
    }

    /**
     * Schedules a Runnable task to run synchronously on the main thread after a delay.
     * Returns a FutureTask that can be used to check completion status.
     *
     * @param callable The runnable task to execute
     * @param delay The delay in ticks before executing the task
     * @return A FutureTask representing the scheduled task
     */
    public FutureTask<Void> scheduleFutureSync(Runnable callable, int delay) {
        FutureTask<Void> future = ExecutorUtils.getFutureTask(callable);
        executeSync(future, delay);
        return future;
    }

    /**
     * Schedules a Runnable task to run asynchronously after a delay.
     * Returns a FutureTask that can be used to check completion status.
     *
     * @param callable The runnable task to execute
     * @param delay The delay in ticks before executing the task
     * @return A FutureTask representing the scheduled task
     */
    public FutureTask<Void> scheduleFutureAsync(Runnable callable, int delay) {
        FutureTask<Void> future = ExecutorUtils.getFutureTask(callable);
        executeAsync(future, delay);
        return future;
    }

    //    private static void runSync(Runnable runnable,Plugin pl) {
    //        Bukkit.getScheduler().runTask(pl,runnable);
    //    }

    /**
     * Executes a runnable on the main thread using the NMS main executor.
     * This is an internal method used by other ThreadUtils methods.
     *
     * @param runnable The task to execute on the main thread
     */
    private static void runSyncNMS(Runnable runnable) {
        MAIN_THREAD_EXECUTOR.execute(runnable);
    }

    /**
     * Gets the mock plugin instance used internally for task scheduling.
     * This method provides access to the same mock plugin returned by {@link #getFakePlugin()}.
     *
     * @return The mock Plugin instance used for internal operations
     */
    public static Plugin getMockPlugin() {
        return MOCK_PLUGIN;
    }



    static {
        try {
            Class<?> mcUtils = Class.forName("io.papermc.paper.util.MCUtil");
            Field field = mcUtils.getDeclaredField("MAIN_EXECUTOR");
            field.setAccessible(true);
            MAIN_THREAD_EXECUTOR = (Executor) field.get(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
