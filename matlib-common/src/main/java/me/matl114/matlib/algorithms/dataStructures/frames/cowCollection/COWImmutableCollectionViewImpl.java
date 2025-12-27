package me.matl114.matlib.algorithms.dataStructures.frames.cowCollection;

import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.struct.State;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;

public class COWImmutableCollectionViewImpl<S extends Collection<V>, V>
        implements Collection<V>, COWImmutableCollectionView<S> {
    protected volatile State<S> delegate;
    protected Function<S, S> copyFunction;

    public COWImmutableCollectionViewImpl(S value, Function<S, S> toMutableCopyFunction) {
        this.delegate = State.newInstance();
        this.delegate.value = value;
        this.delegate.state = true;
        this.copyFunction = toMutableCopyFunction;
    }
    // make
    protected static final VarHandle UPDATER = Objects.requireNonNull(
                    ReflectUtils.getVarHandlePrivate(COWImmutableCollectionViewImpl.class, "delegate"))
            .withInvokeExactBehavior();
    //    protected static final AtomicReferenceFieldUpdater<COWImmutableCollectionViewImpl, State> UPDATER =
    // AtomicReferenceFieldUpdater.newUpdater(COWImmutableCollectionViewImpl.class, State.class, "delegate");

    protected void preWrite() {
        State<S> oldValue;
        State<S> newValue;
        do {
            oldValue = this.delegate;
            if (!oldValue.state) return;

            newValue = State.newInstance();
            newValue.value = copyFunction.apply(oldValue.value);
        } while (!UPDATER.compareAndSet((COWImmutableCollectionViewImpl) this, (State) oldValue, (State) newValue));
    }

    @Override
    public S getHandle() {
        return this.delegate.value;
    }

    @Override
    public int size() {
        return this.delegate.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.value.contains(o);
    }

    @NotNull @Override
    public Object[] toArray() {
        return this.delegate.value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.delegate.value.toArray(a);
    }

    @Override
    public boolean add(V v) {
        preWrite();
        return this.delegate.value.add(v);
    }

    @Override
    public boolean remove(Object o) {
        preWrite();
        return this.delegate.value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.delegate.value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        preWrite();
        return this.delegate.value.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        preWrite();
        return this.delegate.value.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        preWrite();
        return this.delegate.value.removeAll(c);
    }

    @Override
    public void clear() {
        if (!this.delegate.value.isEmpty()) {
            preWrite();
            this.delegate.value.clear();
            ;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set<?> val && val.equals(this.delegate);
    }

    @Override
    public int hashCode() {
        return this.delegate.value.hashCode();
    }

    @NotNull @Override
    @Note("we could not make random access to any collection, but list can")
    public Iterator<V> iterator() {
        return this.delegate.value.iterator();
    }
}
