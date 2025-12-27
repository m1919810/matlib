package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.List;

public class ListenedListImpl<T> extends ListenedList<T> {
    UpdateListener<T> listener;

    public ListenedListImpl(List<T> delegate, UpdateListener<T> listener, boolean applyOnInit) {
        super(delegate, applyOnInit);
        this.listener = listener;
    }

    @Override
    public void onUpdate(T val, boolean val2) {
        if (val != null) {
            this.listener.onElementUpdate(val, val2);
        }
    }
}
