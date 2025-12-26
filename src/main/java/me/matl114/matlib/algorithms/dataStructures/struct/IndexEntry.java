package me.matl114.matlib.algorithms.dataStructures.struct;

public interface IndexEntry<t> {
    public int getIndex();

    public t getValue();
    // public void setValue(t x);
    public static <T> IndexEntry<T> immutable0(T val) {
        return new IndexEntry<T>() {
            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public T getValue() {
                return val;
            }
        };
    }
}
