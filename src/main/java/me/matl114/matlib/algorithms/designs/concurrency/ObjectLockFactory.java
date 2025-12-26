package me.matl114.matlib.algorithms.designs.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlibAdaptor.algorithms.dataStructures.LockFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ObjectLockFactory<T extends Object> implements LockFactory<T> {
    boolean shutdown = false;
    Plugin pl;
    Class<T> clazz;
    Function<T, T> cloneFactory;
    final Map<T, SortableReentrantLock> locksRecord = new ConcurrentHashMap<>();
    BukkitTask autoRefreshTask;
    AbstractExecutorService asyncExecutor;

    public ObjectLockFactory(Class<T> clazz) {
        this.clazz = clazz;
        this.cloneFactory = Function.identity();
    }

    public ObjectLockFactory(Class<T> clazz, Function<T, T> cloneFactory) {
        this.clazz = clazz;
        this.cloneFactory = cloneFactory;
    }

    public ObjectLockFactory<T> init(Plugin pl, String... path) {
        this.pl = pl;

        return this;
    }

    public ObjectLockFactory<T> setupRefreshTask(int tickRefreshPeriod) {
        autoRefreshTask = ExecutorUtils.getRunnable(this::onLockRecordRefresh)
                .runTaskTimerAsynchronously(pl, tickRefreshPeriod, tickRefreshPeriod);
        return this;
    }

    public ObjectLockFactory<T> reload() {
        deconstruct();
        return init(pl);
    }

    public void deconstruct() {
        if (this.autoRefreshTask != null) {
            this.autoRefreshTask.cancel();
        }
        this.shutdown = true;
    }

    public void onLockRecordRefresh() {
        synchronized (this.locksRecord) {
            this.locksRecord.clear();
        }
    }

    private final AtomicLong lockIndexer = new AtomicLong(0);

    @Nonnull
    private SortableReentrantLock getLock(T obj) {
        synchronized (this.locksRecord) {
            SortableReentrantLock value = this.locksRecord.get(obj);
            if (value == null) {
                // clone the origin object in case external change
                T copied = this.cloneFactory.apply(obj);
                value = new SortableReentrantLock(lockIndexer.getAndIncrement());
                this.locksRecord.put(copied, value);
            }
            return value;
        }
    }
    // private final byte[] requestLock = new byte[0];
    //    @Getter
    //    AtomicLong summary = new AtomicLong(0);
    private SortableReentrantLock[] getLocks(T... objs) {
        int required = objs.length;
        SortableReentrantLock[] requiredLocks = new SortableReentrantLock[required];
        for (int i = 0; i < required; i++) {
            requiredLocks[i] = this.getLock(objs[i]);
        }
        return requiredLocks;
    }

    private void requestLocks(SortableReentrantLock[] lock) {
        //        long a= System.nanoTime();
        // lock must be sorted not to cause dead - lock
        Arrays.sort(lock);
        //                synchronized (this.requestLock){
        //        long b = System.nanoTime();
        //        summary.addAndGet(b-a);
        int required = lock.length;

        for (int i = 0; i < required; i++) {
            lock[i].lock();
        }
        //        }

    }

    public void ensureLock(Runnable task, T... objs) {
        // int required = objs.length;
        if (shutdown) {
            return;
        }
        SortableReentrantLock[] requiredLocks = getLocks(objs);
        requestLocks(requiredLocks);
        try {
            task.run();
        } finally {
            for (var lock : requiredLocks) {
                lock.unlock();
            }
        }
    }

    public <C> C ensureLock(Supplier<C> task, T... objs) {
        if (shutdown) {
            return null;
        }
        SortableReentrantLock[] requiredLocks = getLocks(objs);
        requestLocks(requiredLocks);
        try {
            return task.get();
        } finally {
            for (var lock : requiredLocks) {
                lock.unlock();
            }
        }
    }

    public void asyncEnsureLock(Runnable task, T... objs) {
        if (asyncExecutor != null) {
            CompletableFuture.runAsync(() -> ensureLock(task, objs), asyncExecutor);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> ensureLock(task, objs));
        }
    }

    public void asyncLaterEnsureLock(int tickLate, Runnable task, T... objs) {
        if (tickLate > 0) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> ensureLock(task, objs), tickLate);
        } else {
            asyncEnsureLock(task, objs);
        }
    }

    public boolean checkThreadStatus(T... objs) {
        var locks = getLocks(objs);
        boolean status = true;
        for (var lock : locks) {
            // 没有锁 或者正在被当前线程锁
            status &= !lock.isLocked() || lock.isHeldByCurrentThread();
        }
        return status;
    }

    public boolean checkStatus(T... objs) {
        var locks = getLocks(objs);
        boolean status = false;
        for (var lock : locks) {
            status |= lock.isLocked();
        }
        return !status;
    }

    public <C extends Object> FutureTask<C> ensureFuture(int delay, Callable<C> task, T... objs) {
        FutureTask<C> future = new FutureTask<>(task);
        asyncLaterEnsureLock(delay, future, objs);
        return future;
    }

    protected static class SortableReentrantLock extends ReentrantLock implements Comparable<SortableReentrantLock> {
        long index;

        public SortableReentrantLock(long index) {
            super();
            this.index = index; // lockIndexer.getAndIncrement();
        }

        @Override
        public int compareTo(SortableReentrantLock o) {
            ; // this.index - o.index;
            return Long.compare(this.index, o.index);
            //            long del = this.index - o.index;
            //            return del == 0 ? 0 : del < 0 ? -1 : 1;
        }
    }
}
