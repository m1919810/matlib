package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

public class AbstractReadWriteCollection<DELEGATE extends Collection<?>, VIEW> implements Collection<VIEW>, Set<VIEW> {
    @Delegate(types = {Collection.class})
    protected final DELEGATE delegate;

    public AbstractReadWriteCollection(DELEGATE value) {
        this.delegate = value;
    }

    public DELEGATE getHandle() {
        return this.delegate;
    }

    @NotNull @Override
    public Iterator<VIEW> iterator() {
        return (Iterator<VIEW>) this.delegate.iterator();
    }

    @NotNull @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.delegate.toArray(a);
    }
    //
    @Override
    public boolean add(VIEW v) {
        return ((Collection) this.delegate).add(v);
    }

    //
    //
    @Override
    public boolean addAll(Collection<? extends VIEW> c) {
        return ((Collection) this.delegate).addAll(c);
    }
    //

    public boolean retainAll(Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        return this.delegate.removeAll(c);
    }
    //
    //
    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }
    //
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        Iterator<VIEW> it = iterator();
        if (!it.hasNext()) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ; ) {
            VIEW e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (!it.hasNext()) return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    @Override
    public boolean removeIf(Predicate<? super VIEW> filter) {
        return ((Collection) this.delegate).removeIf(filter);
    }
}
