package me.matl114.matlib.implement.bukkit.schedule;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntConsumer;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.core.AutoInit;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.utils.ThreadUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
@AutoInit(level = "Plugin")
public class ScheduleManager implements Manager {
    Plugin plugin;
    AbstractExecutorService asyncExecutor = null;

    @Deprecated
    public ScheduleManager setAsyncExecutor(AbstractExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
        return this;
    }

    @Override
    @Deprecated
    public ScheduleManager init(Plugin pl, String... path) {
        plugin = pl;
        new BukkitRunnable() {
            public void run() {
                runPostSetup();
            }
        }.runTaskLater(this.plugin, 1);
        addToRegistry();
        return this;
    }

    @Override
    @Deprecated
    public ScheduleManager reload() {
        manager = null;
        deconstruct();
        init(plugin);
        return this;
    }

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    @Deprecated
    public void deconstruct() {
        removeFromRegistry();
    }

    @Getter
    private static ScheduleManager manager;

    @Deprecated
    public ScheduleManager() {
        manager = this;
    }

    public <T extends Runnable> void execute(T runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            launchScheduled(runnable, 0, true, 0);
        }
    }

    public <T extends Runnable> void execute(T runnable, boolean onMainThread) {
        if (Bukkit.isPrimaryThread() == onMainThread) {
            runnable.run();
        } else {
            launchScheduled(runnable, 0, onMainThread, 0);
        }
    }

    private List<Runnable> postSetupTasks = new ArrayList<>();

    public void addPostSetup(Runnable runnable) {
        postSetupTasks.add(runnable);
    }

    public void runPostSetup() {
        postSetupTasks.forEach(Runnable::run);
    }

    public <T extends Runnable> void launchScheduled(T r, int delayTick, boolean runSync, int periodTick) {
        launchScheduled(ExecutorUtils.getRunnable(r), delayTick, runSync, periodTick);
    }

    public void launchScheduled(BukkitRunnable thread, int delay, boolean isSync, int period) {
        if (period <= 0) {
            if (isSync) {
                if (delay != 0) {
                    thread.runTaskLater(plugin, delay);
                } else {
                    ThreadUtils.executeSyncSched(thread);
                    // thread.runTask(plugin);
                }
            } else {
                if (delay != 0) thread.runTaskLaterAsynchronously(plugin, delay);
                else {
                    if (asyncExecutor != null) {
                        CompletableFuture.runAsync(thread, asyncExecutor);
                    } else {
                        thread.runTaskAsynchronously(plugin);
                    }
                }
            }
        } else {
            if (isSync) {
                thread.runTaskTimer(plugin, delay, period);
            } else {
                thread.runTaskTimerAsynchronously(plugin, delay, period);
            }
        }
    }

    public void launchRepeatingSchedule(IntConsumer thread, int delay, boolean isSync, int period, int repeatTime) {
        launchScheduled(
                new BukkitRunnable() {
                    int runTime = 0;

                    @Override
                    public void run() {
                        try {
                            thread.accept(runTime);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            this.runTime += 1;
                            if (this.runTime >= repeatTime) {
                                this.cancel();
                            }
                        }
                    }
                },
                delay,
                isSync,
                period);
    }

    public void asyncWaithRepeatingSchedule(IntConsumer thread, int delay, boolean isSync, int period, int repeatTime) {
        Preconditions.checkArgument(!Bukkit.isPrimaryThread(), "This method should be called in async thread");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        launchScheduled(
                new BukkitRunnable() {
                    int runTime = 0;

                    @Override
                    public void run() {
                        try {
                            thread.accept(runTime);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            this.runTime += 1;
                            if (this.runTime >= repeatTime) {
                                countDownLatch.countDown();
                                this.cancel();
                            }
                        }
                    }
                },
                delay,
                isSync,
                period);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public <T> FutureTask<T> getScheduledFuture(Callable<T> callable, int delay, boolean isSync) {
        FutureTask<T> future = new FutureTask<>(callable);
        launchScheduled(future, delay, isSync, 0);
        return future;
    }
}
