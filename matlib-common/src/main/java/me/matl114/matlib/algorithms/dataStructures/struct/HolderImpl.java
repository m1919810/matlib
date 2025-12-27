package me.matl114.matlib.algorithms.dataStructures.struct;

import java.util.function.*;
import me.matl114.matlib.common.functions.core.UnsafeBiFunction;
import me.matl114.matlib.common.functions.core.UnsafeFunction;

class HolderImpl<T> implements Holder<T>, Cloneable {
    Throwable e = null;
    T value;
    static final HolderImpl<?> INSTANCE = new HolderImpl<>();

    private HolderImpl() {}

    @Override
    public <W> Holder<W> setValue(W value) {
        HolderImpl<W> holder = (HolderImpl<W>) this;
        holder.value = value;
        return holder;
    }

    public <W> Holder<W> thenApply(Function<T, W> function) {
        HolderImpl<W> holder = (HolderImpl<W>) this;
        holder.value = (W) function.apply(this.value);
        return holder;
    }

    @Override
    public Holder<T> thenRun(Runnable task) {
        task.run();
        return this;
    }

    public Holder<T> thenPeek(Consumer<T> task) {
        task.accept(this.value);
        return this;
    }

    @Override
    public <R> Holder<T> thenPeek(BiConsumer<T, R> task, R value) {
        task.accept(this.value, value);
        return this;
    }

    public <W> Holder<W> thenApplyUnsafe(UnsafeFunction<T, W> function) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        try {
            holder.value = (W) function.applyUnsafe(this.value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return holder;
    }

    @Override
    public <W, R> Holder<W> thenApplyUnsafe(UnsafeBiFunction<T, R, W> function, R value) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        try {
            holder.value = (W) function.applyUnsafe(this.value, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return holder;
    }

    public <W> Holder<W> thenApplyCaught(UnsafeFunction<T, W> function) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        try {
            holder.value = (W) function.applyUnsafe(this.value);
        } catch (Throwable e) {
            this.e = e;
        }
        return holder;
    }

    public <W, R> Holder<W> thenApply(BiFunction<T, R, W> function, R value) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        holder.value = function.apply(this.value, value);
        return holder;
    }

    public <W, R> Holder<W> thenApplyCaught(UnsafeBiFunction<T, R, W> function, R value) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        try {
            holder.value = (W) function.applyUnsafe(this.value, value);
        } catch (Throwable e) {
            this.e = e;
            this.value = null;
        }
        return holder;
    }

    @Override
    public Holder<T> runException(Consumer<Throwable> exceptionHandler) {
        if (this.e != null) {
            exceptionHandler.accept(e);
            this.e = null;
            this.value = null;
        }
        return this;
    }

    public Holder<T> whenException(Function<Throwable, T> exceptionHandler) {
        if (this.e != null) {
            this.value = exceptionHandler.apply(this.e);
            this.e = null;
        }
        return this;
    }

    public <W> Holder<W> whenComplete(BiFunction<T, Throwable, W> completeHandler) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        holder.value = completeHandler.apply(this.value, this.e);
        holder.e = null;
        return holder;
    }

    @Override
    public Holder<T> whenNoException(Consumer<T> task) {
        if (this.e == null) {
            task.accept(this.value);
        }
        return this;
    }

    public T get() {
        return this.value;
    }

    public <W> Holder<W> cast() {
        return (Holder<W>) this;
    }

    public <W> Holder<W> branch(Predicate<T> predicate, Function<T, W> func1, Function<T, W> func2) {
        HolderImpl<W> holder = ((HolderImpl<W>) this);
        if (predicate.test(this.value)) {
            holder.value = func1.apply(this.value);
        } else {
            holder.value = func2.apply(this.value);
        }
        return holder;
    }

    @Override
    public Holder<T> failHard() {
        if (this.e != null) {
            return new HolderExceptional<>(this.value, this.e);
        }
        return this;
    }

    @Override
    public Holder<T> peekFail(Consumer<Throwable> task) {
        return this;
    }

    @Override
    public Holder<T> ifFail(Function<Throwable, T> defaultValue) {
        return this;
    }

    public Holder<T> recover(Predicate<T> recover) {
        return this;
    }

    @Override
    public Holder<T> shouldRecover(Predicate<Throwable> predicate) {
        return this;
    }

    @Override
    public Holder<T> throwException(Predicate<Throwable> predicate) {
        if (predicate.test(this.e)) {
            throw new RuntimeException(this.e);
        }
        return this;
    }

    @Override
    public Holder<T> checkArgument(Predicate<T> predicate) {
        if (predicate.test(this.value)) {
            return this;
        }
        this.e = new AssertionError(this.e);
        return this;
    }

    protected HolderImpl<T> clone() {
        try {
            return (HolderImpl<T>) super.clone();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
