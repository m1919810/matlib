package me.matl114.matlib.algorithms.designs.concurrency;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AsyncWorker implements Runnable, Executor {
    final ArrayBlockingQueue<Runnable> taskQueue;
    final int fixedSize;
    Future<?> runningTask;

    @Getter
    volatile boolean shutdown = true;

    public AsyncWorker() {
        this(10_000);
    }

    public AsyncWorker(int size) {
        this.taskQueue = new ArrayBlockingQueue<>(size);
        this.fixedSize = size - 8;
    }

    @Override
    public void run() {
        Runnable task;
        while (true) {
            try {
                task = this.taskQueue.take();
                // bugfix : should run StoppingSignalTask to complete future
                if (task instanceof StoppingSignalTask) {
                    shutdown = true;
                    task.run();
                    return;
                } else {
                    task.run();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private void submitTask(Runnable task) {
        Preconditions.checkArgument(!shutdown, "This worker is in shutdown state and can not submit task anyMore");
        try {
            this.taskQueue.add(task);
        } catch (IllegalStateException full) {
            task.run();
        }
    }

    @Override
    public void execute(@NotNull Runnable task) {
        submitTask(task);
    }

    public void stopWork() {
        submitTask(new StoppingSignalTask());
        this.shutdown = true;
    }

    public Future<Void> stopWorkFuture() {
        var signal = new StoppingSignalTask();
        this.shutdown = true;
        return signal;
    }

    public void waitForStopWork() throws Throwable {
        StoppingSignalTask task = new StoppingSignalTask();
        submitTask(task);
        task.get();
    }

    public void waitForStopWork(int time, TimeUnit unit) throws Throwable {
        StoppingSignalTask task = new StoppingSignalTask();
        submitTask(task);
        task.get(time, unit);
        this.runningTask = null;
    }

    public void startWork() {
        startWork(ForkJoinPool.commonPool());
    }

    public void shutdown() {
        shutdown(10, TimeUnit.SECONDS);
    }

    public void shutdown(int time, TimeUnit unit) {
        this.taskQueue.clear();
        try {
            waitForStopWork(time, unit);
        } catch (Throwable e) {
            forceShutdown(e);
        }
    }
    // for dead lock tasks, force stop hard,
    private void forceShutdown(Throwable e) {
        System.err.println("Async Worker " + this + "encountered a shutdown timeout! shutting down force...");
        e.printStackTrace();
        if (runningTask != null) {
            runningTask.cancel(true);
            runningTask = null;
        }
    }

    public List<Runnable> shutdownNow() {
        List<Runnable> lst = new ArrayList<>();
        this.taskQueue.removeIf(r -> {
            lst.add(r);
            return true;
        });
        try {
            waitForStopWork(1, TimeUnit.SECONDS);
        } catch (Throwable e) {
            forceShutdown(e);
        }
        return lst;
    }

    public void startWork(ExecutorService asyncExecutor) {
        Preconditions.checkArgument(shutdown, "This worker is in running state and can not start Working now");
        this.shutdown = false;
        this.runningTask = asyncExecutor.submit(this);
    }

    public static AsyncWorker bindToSingleThread(int size) {
        return new AsyncWorker(size) {
            final ExecutorService executor = Executors.newSingleThreadExecutor();

            @Override
            public void startWork() {
                startWork(executor);
            }

            @Override
            public void shutdown() {
                try {
                    super.shutdown();
                } finally {
                    this.executor.shutdown();
                }
            }
        };
    }

    public static class StoppingSignalTask extends FutureTask {
        public StoppingSignalTask() {
            super(() -> null);
        }
    }
}
