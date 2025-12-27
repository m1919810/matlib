package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import org.jetbrains.annotations.NotNull;

public class SimpleListImpl<T> extends SimpleCollectionImpl<List<T>, T>
        implements List<T>, ListMapView<T, T>, SimpleList<T> {
    public SimpleListImpl(List<T> value) {
        super(value);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return this.delegate.addAll(index, c);
    }

    @Override
    public T get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.delegate.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.delegate.add(index, element);
    }

    @Override
    public T remove(int index) {
        return this.delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    @NotNull @Override
    public ListIterator<T> listIterator() {
        return this.delegate.listIterator();
    }

    @NotNull @Override
    public ListIterator<T> listIterator(int index) {
        return this.delegate.listIterator(index);
    }

    @NotNull @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.delegate.subList(fromIndex, toIndex);
    }
}
