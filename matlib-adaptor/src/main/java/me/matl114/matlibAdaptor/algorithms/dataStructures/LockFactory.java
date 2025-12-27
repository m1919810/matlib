package me.matl114.matlibAdaptor.algorithms.dataStructures;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import me.matl114.matlibAdaptor.proxy.annotations.AdaptorInterface;
import me.matl114.matlibAdaptor.proxy.annotations.DefaultMethod;

/**
 * LockFactory means that each T(in equal() equivalence) object holds a lock, some task need to ensure that only one task holds a object at the same time,
 * @param <T>
 */
@AdaptorInterface
public interface LockFactory<T extends Object> {
    /**
     * this task need to hold objs to run
     * @param task
     * @param objs
     */
    public void ensureLock(Runnable task, T... objs);

    /**
     * this supplier need to hold objs to run,the return value will be the task's return value
     * @param task
     * @param objs
     * @return
     * @param <C>
     */
    public <C> C ensureLock(Supplier<C> task, T... objs);

    /**
     * this task will wait in async thread until it holds all obj in objs
     * @param task
     * @param objs
     */
    public void asyncEnsureLock(Runnable task, T... objs);

    /**
     * this task will be created into FutureTask, and delay ticks is waited until the task try to holds all obj in objs,
     * @param delay
     * @param task
     * @param objs
     * @return
     * @param <C>
     */
    public <C extends Object> FutureTask<C> ensureFuture(int delay, Callable<C> task, T... objs);

    @DefaultMethod
    default <C extends Object> FutureTask<C> ensureFuture(Callable<C> task, T... objs) {
        return ensureFuture(0, task, objs);
    }

    /**
     * this method implies that if any of other thread holds one obj in objs
     * @param objs
     * @return
     */
    public boolean checkThreadStatus(T... objs);
}
