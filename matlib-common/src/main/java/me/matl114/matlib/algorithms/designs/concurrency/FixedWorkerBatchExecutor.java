package me.matl114.matlib.algorithms.designs.concurrency;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import me.matl114.matlib.utils.Debug;
import org.jetbrains.annotations.NotNull;

public class FixedWorkerBatchExecutor extends AbstractExecutorService {
    final int nWorker;
    final AsyncWorker[] workers;
    final Random rand;
    boolean shutdown = false;
    volatile boolean busy = false;

    public FixedWorkerBatchExecutor(int nWorker, int workerCache) {
        this.nWorker = nWorker;
        this.workers = IntStream.range(0, nWorker)
                .mapToObj(i -> AsyncWorker.bindToSingleThread(workerCache))
                .toArray(AsyncWorker[]::new);
        this.rand = new Random();
    }

    @Override
    public void shutdown() {
        for (var worker : workers) {
            worker.shutdown();
        }
    }

    @NotNull @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> lst = new ArrayList();
        for (var worker : workers) {
            lst.addAll(worker.shutdownNow());
        }
        return lst;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return shutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void execute(@NotNull Runnable command) {
        executeAtWorker(command, rand.nextInt());
    }

    public void executeAtWorker(Runnable task, int workerIndex) {
        Preconditions.checkArgument(busy, "Executor is on resting-state! Can not execute task!");
        workerIndex = workerIndex % nWorker;
        workers[workerIndex].execute(task);
    }

    public void startBusy() {
        Preconditions.checkArgument(!busy, "Executor is on Busy-state! Can not startBusy!");
        busy = true;
        for (var worker : workers) {
            worker.startWork();
        }
    }

    public void startRest() {
        Preconditions.checkArgument(busy, "Executor is on resting-state! Can not startRest");
        busy = false;
        for (var worker : workers) {
            worker.stopWork();
        }
    }

    public CompletableFuture<Void> startRestAsync() {
        Preconditions.checkArgument(busy, "Executor is on resting-state! Can not startRest");
        List<Future<Void>> restFuture = new ArrayList<>(nWorker + 2);
        for (var worker : workers) {
            restFuture.add(worker.stopWorkFuture());
        }
        busy = false;
        return CompletableFuture.runAsync(() -> {
            for (int i = 0, size = restFuture.size(); i < size; i++) {
                Future<Void> f = restFuture.get(i);
                if (!f.isDone()) {
                    try {
                        f.get();
                    } catch (Throwable ignore) {
                        Debug.logger("WorkerExecutor: Worker " + i + " failed to stop work!");
                    }
                }
            }
        });
    }
}
