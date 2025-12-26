package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.function.Consumer;

public interface UpdateListener<T> {
    public void onElementUpdate(T val, boolean add);

    static <W> UpdateListener<W> add(Consumer<W> val) {
        return (v, v2) -> {
            if (v2) {
                val.accept(v);
            }
        };
    }

    static <W> UpdateListener<W> remove(Consumer<W> val) {
        return (v, v2) -> {
            if (!v2) {
                val.accept(v);
            }
        };
    }

    static <W> UpdateListener<W> modify(Consumer<W> val) {
        return (v, v2) -> {
            val.accept(v);
        };
    }
}
