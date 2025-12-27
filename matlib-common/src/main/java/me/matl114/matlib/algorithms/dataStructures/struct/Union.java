package me.matl114.matlib.algorithms.dataStructures.struct;

public class Union<T, W> {
    private Union() {}

    private Object v;
    private int t = -1;

    public static <T, W> Union<T, W> ofA(T v) {
        Union<T, W> u = new Union<T, W>();
        u.v = v;
        u.t = 0;
        return u;
    }

    public static <T, W> Union<T, W> ofB(W v) {
        Union<T, W> u = new Union<T, W>();
        u.v = v;
        u.t = 1;
        return u;
    }

    public boolean isA() {
        return t == 0;
    }

    public T getA() {
        return (T) v;
    }

    public W getB() {
        return (W) v;
    }
}
