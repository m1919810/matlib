package me.matl114.matlib.algorithms.dataStructures.frames.cowCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.BidirListIndexIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListIndexIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SubListWindow;
import org.jetbrains.annotations.NotNull;

public class COWImmutableListView<T> extends COWImmutableCollectionViewImpl<List<T>, T> implements List<T> {
    public COWImmutableListView(List<T> value, Function<List<T>, List<T>> toMutableCopyFunction) {
        super(value, toMutableCopyFunction);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        preWrite();
        return this.delegate.value.addAll(index, c);
    }

    @Override
    public T get(int index) {
        return this.delegate.value.get(index);
    }

    @Override
    public T set(int index, T element) {
        preWrite();
        return this.delegate.value.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        preWrite();
        this.delegate.value.add(index, element);
    }

    @Override
    public T remove(int index) {
        preWrite();
        return this.delegate.value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.value.lastIndexOf(o);
    }

    public Iterator<T> iterator() {
        return new ListIndexIterator<>(this);
    }

    @NotNull @Override
    public ListIterator<T> listIterator() {
        return new BidirListIndexIterator<>(this);
    }

    @NotNull @Override
    public ListIterator<T> listIterator(int index) {
        return new BidirListIndexIterator<>(this, index);
    }

    @NotNull @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new SubListWindow<>(this, fromIndex, toIndex);
    }
}
