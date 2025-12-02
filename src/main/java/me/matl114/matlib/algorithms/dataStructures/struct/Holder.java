package me.matl114.matlib.algorithms.dataStructures.struct;

import me.matl114.matlib.common.functions.core.UnsafeBiFunction;
import me.matl114.matlib.common.functions.core.UnsafeFunction;

import java.util.function.*;

public interface Holder<T> {
    public <W> Holder<W> setValue(W value);
    public <W> Holder<W> thenApply(Function<T,W> function);
    public Holder<T> thenRun(Runnable task);
    public Holder<T> thenPeek(Consumer<T> task);
    public <R> Holder<T> thenPeek(BiConsumer<T,R> task, R value);
    public <W> Holder<W> thenApplyUnsafe(UnsafeFunction<T,W> function);
    public <W,R> Holder<W> thenApplyUnsafe(UnsafeBiFunction<T,R,W> function,R value);
    public <W> Holder<W> thenApplyCaught(UnsafeFunction<T,W> function);
    public <W,R> Holder<W> thenApply(BiFunction<T,R,W> function, R value);
    public <W,R> Holder<W> thenApplyCaught(UnsafeBiFunction<T,R,W> function,R value);
    public Holder<T> runException(Consumer<Throwable> exceptionHandler);
    public Holder<T> whenException(Function<Throwable,T> exceptionHandler);
    default Holder<T> valException(T val){
        return whenException((v)->val);
    }
    public <W> Holder<W> whenComplete(BiFunction<T,Throwable,W> completeHandler);
    public Holder<T> whenNoException(Consumer<T> task);
    public T get();
    public <W> Holder<W> cast();
    public <W> Holder<W> branch(Predicate<T> predicate, Function<T,W> func1, Function<T,W> func2);
    public Holder<T> failHard();
    public Holder<T> peekFail(Consumer<Throwable> task);
    public Holder<T> ifFail(Function<Throwable,T> defaultValue);
    public Holder<T> recover(Predicate<T> recover);
    default Holder<T> recover(){
        return recover((v)->true);
    }
    public Holder<T> shouldRecover(Predicate<Throwable> predicate);
    public Holder<T> throwException(Predicate<Throwable> predicate);
    default Holder<T> throwException(){
        return throwException((w)->true);
    }
    public Holder<T> checkArgument(Predicate<T> predicate);
    public static <T> Holder<T> of(T value){
        HolderImpl<T> holder = (HolderImpl<T>) HolderImpl.INSTANCE.clone();
        holder.value = value;
        return holder;
    }
    public static Holder<Void> empty(){
        HolderImpl<Void> holder = (HolderImpl<Void>) HolderImpl.INSTANCE.clone();
        holder.value = null;
        return holder;
    }


}
