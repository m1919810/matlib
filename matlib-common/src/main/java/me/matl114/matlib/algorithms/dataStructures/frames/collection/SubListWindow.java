package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.AbstractList;
import java.util.List;

public class SubListWindow<T> extends AbstractList<T> {
    int fromHead;
    int toEnd;
    List<T> delegate;

    public SubListWindow(List<T> value, int from, int to) {
        this.delegate = value;
        this.fromHead = from;
        this.toEnd = this.delegate.size() - to;
    }

    @Override
    public T get(int index) {
        return this.delegate.get(index + fromHead);
    }

    @Override
    public int size() {
        return this.delegate.size() - toEnd - fromHead;
    }

    public void add(int index, T element) {
        this.delegate.add(index + fromHead, element);
    }

    public T remove(int index) {
        return this.delegate.remove(index + fromHead);
    }

    public T set(int index, T element) {
        return this.delegate.set(index + fromHead, element);
    }
}
