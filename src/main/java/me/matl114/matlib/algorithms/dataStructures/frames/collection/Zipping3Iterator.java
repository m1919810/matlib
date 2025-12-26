package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import lombok.AllArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.struct.Triplet;

@AllArgsConstructor
public class Zipping3Iterator<T, W, R> implements Iterator<Triplet<T, W, R>> {
    public Iterator<T> a;
    public Iterator<W> b;
    public Iterator<R> c;

    private boolean isANext() {
        if (a.hasNext()) return true;
        a = null;
        return false;
    }

    private boolean isBNext() {
        if (b.hasNext()) return true;
        b = null;
        return false;
    }

    private boolean isCNext() {
        if (c.hasNext()) return true;
        c = null;
        return false;
    }

    @Override
    public boolean hasNext() {
        return isANext() && isBNext() && isCNext();
    }

    @Override
    public Triplet<T, W, R> next() {
        return Triplet.of(a != null ? a.next() : null, b != null ? b.next() : null, c != null ? c.next() : null);
    }

    @Override
    public void remove() {
        if (a != null) a.remove();
        if (b != null) b.remove();
        if (c != null) {
            c.remove();
        }
    }
}
