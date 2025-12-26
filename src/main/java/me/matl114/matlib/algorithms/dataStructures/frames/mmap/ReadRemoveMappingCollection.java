package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.CollectionMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.MappingIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.AbstractReadRemoveCollection;
import org.jetbrains.annotations.NotNull;

public class ReadRemoveMappingCollection<R, T> extends AbstractReadRemoveCollection<Collection<R>, T>
        implements CollectionMapView<R, T> {
    Function<R, T> reader;
    Function<T, R> optionalConvertor = null;

    public ReadRemoveMappingCollection(Collection<R> value, Function<R, T> reader) {
        super(value);
        this.reader = reader;
    }

    public ReadRemoveMappingCollection(Collection<R> value, Function<R, T> reader, Function<T, R> convertor) {
        super((value));
        this.reader = reader;
        this.optionalConvertor = convertor;
    }

    @Override
    public void batchWriteback() {}

    @Override
    public boolean isDelayWrite() {
        return false;
    }

    @Override
    public void flush() {}

    // not recommended
    @Override
    public boolean contains(Object o) {
        if (optionalConvertor != null) {
            return this.delegate.contains(this.optionalConvertor.apply((T) o));
        } else {
            for (var entry : this) {
                if (Objects.equals(entry, o)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean remove(Object o) {
        if (optionalConvertor != null) {
            return this.delegate.contains(this.optionalConvertor.apply((T) o));
        } else {
            Iterator<T> iter = this.iterator();
            boolean removal = false;
            while (iter.hasNext()) {
                if (Objects.equals(iter.next(), o)) {
                    iter.remove();
                    ;
                    removal = true;
                }
            }
            return removal;
        }
    }

    public boolean removeAll(Collection<?> coll) {
        if (this.optionalConvertor != null) {
            Set<?> mapped = coll.stream()
                    .map(((Class<T>) Object.class)::cast)
                    .map(optionalConvertor)
                    .collect(Collectors.toSet());
            return this.delegate.removeAll(mapped);
        }
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> coll) {
        if (this.optionalConvertor != null) {
            Set<?> mapped = coll.stream()
                    .map(((Class<T>) Object.class)::cast)
                    .map(optionalConvertor)
                    .collect(Collectors.toSet());
            return this.delegate.retainAll(mapped);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        Iterator<T> iter = this.iterator();
        boolean removal = false;
        while (iter.hasNext()) {
            if (filter.test(iter.next())) {
                iter.remove();
                ;
                removal = true;
            }
        }
        return removal;
    }

    @NotNull @Override
    public Iterator<T> iterator() {
        return new MappingIterator<>((Iterator<R>) super.iterator(), reader);
    }

    //

    //
    //

}
