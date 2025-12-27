package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import lombok.AllArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;

@AllArgsConstructor
public class Zipping2Iterator<T, W> implements Iterator<Pair<T, W>> {
    public Iterator<T> a;
    public Iterator<W> b;

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

    @Override
    public boolean hasNext() {
        return isANext() && isBNext();
    }

    @Override
    public Pair<T, W> next() {
        return Pair.of(a != null ? a.next() : null, b != null ? b.next() : null);
    }

    @Override
    public void remove() {
        if (a != null) a.remove();
        if (b != null) b.remove();
    }
}
