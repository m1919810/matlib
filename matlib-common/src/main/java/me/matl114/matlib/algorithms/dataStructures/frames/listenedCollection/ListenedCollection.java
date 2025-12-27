package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.Collection;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleCollection;

public interface ListenedCollection<S extends Collection<T>, T> extends SimpleCollection<S, T> {
    public void onUpdate(T val, boolean val2);

    public S getHandle();
}
