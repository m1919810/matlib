package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.List;
import java.util.ListIterator;

public class BidirListIndexIterator<T> extends ListIndexIterator<T> implements ListIterator<T> {
    public BidirListIndexIterator(List<T> delegate) {
        super(delegate);
    }

    public BidirListIndexIterator(List<T> delegate, int index) {
        super(delegate);
        this.index = index;
    }

    int lastVisit = -1;

    @Override
    public boolean hasPrevious() {
        return index >= 0;
    }

    @Override
    public T previous() {
        this.lastVisit = --index;
        return this.delegate.get(this.lastVisit);
    }

    @Override
    public int nextIndex() {
        return index;
    }

    public T next() {
        this.lastVisit = index;
        return super.next();
    }

    @Override
    public int previousIndex() {
        return index - 1;
    }

    @Override
    public void add(T t) {
        delegate.add(index, t);
        index++; // 插入后索引后移
        lastVisit = -1; // 重置
    }

    @Override
    public void remove() {
        if (lastVisit == -1) {
            throw new IllegalStateException("No element to remove");
        }
        delegate.remove(lastVisit);
        // 调整索引（如果删除的是通过 next() 访问的元素）
        if (lastVisit < index) {
            --index;
        }
        lastVisit = -1;
    }

    @Override
    public void set(T t) {
        if (lastVisit == -1) {
            throw new IllegalStateException("No element to set");
        }
        delegate.set(lastVisit, t);
    }
}
