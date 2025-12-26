package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ReadOnlyIterator;

public class AbstractReadOnlyCollection<DELEGATE extends Collection<?>, E>
        extends AbstractReadRemoveCollection<DELEGATE, E> {
    public AbstractReadOnlyCollection(DELEGATE value) {
        super(value);
    }

    public final boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public final boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    public final boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    public Iterator<E> iterator() {
        return new ReadOnlyIterator<>(super.iterator());
    }
}
