package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

import java.util.function.Function;
import javax.annotation.Nonnull;
import me.matl114.matlib.common.lang.annotations.Note;
import org.jetbrains.annotations.NotNull;

public abstract class COWView<T> {
    protected boolean validBit;
    protected T cache;

    public void flush() {
        validBit = false;
    }

    @Note("NEVER modify anything of the return value, MAKE SURE you know what you are doing")
    public T getView() {
        if (validBit) {
            return cache;
        } else {
            cache = this.getView0();
            validBit = true;
            return cache;
        }
    }

    protected abstract T getView0();

    @Note(
            "Get a mutable view for the source, if you WANT TO modify the source, pass the return value of THIS METHOD to writeback()")
    @Nonnull
    public abstract T getWritable();

    protected abstract void write0(T val);

    public void writeBack(T val) {
        // stop the reader from reading old views, any request will goes to origin
        this.validBit = false;
        write0(val);
    }

    public static <T> COWView<T> immutable(T val) {
        return new COWView<T>() {
            @Override
            protected T getView0() {
                return val;
            }

            @NotNull @Override
            public T getWritable() {
                return val;
            }

            @Override
            protected void write0(T val) {
                // do nothing because this is immutable
            }
        };
    }

    public static <T> COWView<T> withWriteback(T val, Function<T, T> writebackAndUpdateCache) {
        return new COWView<T>() {
            T cp = val;

            @Override
            protected T getView0() {
                return this.cp;
            }

            @NotNull @Override
            public T getWritable() {
                return this.cp;
            }

            @Override
            protected void write0(T val) {
                if (val != this.cp) {
                    this.cp = writebackAndUpdateCache.apply(val);
                    this.validBit = false;
                }
            }
        };
    }
}
