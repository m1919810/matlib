package me.matl114.matlib.algorithms.dataStructures.frames.collection;

public interface Stack<T> {
    public T peek();

    public T pop();

    public T poll();

    public void push(T val);

    public boolean isEmpty();

    public void clear();
}
