package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.bits.BitList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;

public class MappingList<W, T> extends AbstractList<T> implements ListMapView<W, T> {
    BitList validBits;
    Function<W, T> reader;
    Function<T, W> writer;

    @Getter
    protected List<W> origin;

    Object[] cache;
    protected Consumer<MappingList<W, T>> writeback;

    public void batchWriteback() {
        if (writeback != null) {
            writeback.accept(this);
        }
    }

    public MappingList<W, T> withWriteBack(Consumer<MappingList<W, T>> callback) {
        this.writeback = callback;
        return this;
    }

    public static <T, W> W readOnly(T value) {
        throw new RuntimeException("Unsupported operation : writing to a read-only mapping list");
    }

    @Override
    public boolean isDelayWrite() {
        return false;
    }

    @Override
    public void flush() {
        this.validBits.clear();
    }

    public MappingList(Function<W, T> reader, Function<T, W> writer, List<W> origin) {
        this.reader = reader;
        this.writer = writer;
        this.origin = origin;
        cache = new Object[origin.size() + 1];
        validBits = new BitList();
    }

    private void ensureSize(int index) {
        if (index >= cache.length) {
            int newSize = cache.length;
            do {
                newSize <<= 1;
            } while (index >= newSize);
            cache = Arrays.copyOf(cache, newSize);
        }
    }

    private void flushCache() {
        validBits.clear();
    }

    private T getCacheInternal(int index) {
        ensureSize(index);
        return (T) cache[index];
    }

    private void setCacheInternal(int index, T value) {
        ensureSize(index);
        cache[index] = value;
        validBits.setTrue(index);
    }

    @Override
    public T get(int index) {
        if (validBits.get(index)) {
            return getCacheInternal(index);
        }
        W originValue = origin.get(index);
        T mappingValue = reader.apply(originValue);
        setCacheInternal(index, mappingValue);
        return mappingValue;
    }

    @Override
    public T set(int index, T element) {
        W originValue = writer.apply(element);
        origin.set(index, originValue);
        if (validBits.get(index)) {
            T retCache = getCacheInternal(index);
            setCacheInternal(index, element);
            return retCache;
        } else {
            setCacheInternal(index, element);
            // to be more quick
            return null;
        }
    }

    @Override
    public void add(int index, T element) {
        W mappingValue = writer.apply(element);
        int size = size();
        origin.add(index, mappingValue);
        ensureSize(size + 1);
        // from index ~ size ->copyto index +1 size+1
        System.arraycopy(cache, index, cache, index + 1, size - index);
        setCacheInternal(index, element);
        validBits.addFalse(index);
    }

    @Override
    public T remove(int index) {
        int size = size();

        // 获取被移除的值

        // 移除原始数据并调整缓存
        origin.remove(index);
        ensureSize(size);
        if (size - 1 > index) {
            System.arraycopy(cache, index + 1, cache, index, size - 1 - index);
        }
        cache[size - 1] = null;
        // 调整 BitSet
        validBits.remove(index);
        // to be more quick
        return null;
    }

    @Override
    public void clear() {
        origin.clear();
        validBits.clear();
        cache = new Object[0];
    }

    @Override
    public int size() {
        return origin.size();
    }
}
