package me.matl114.matlib.algorithms.dataStructures.struct;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class State<S> implements Cloneable {
    public S value = null;
    public boolean state = false;
    private static final State INSTANCE = new State();

    public static <T> State<T> newInstance() {
        return INSTANCE.clone();
    }

    @Override
    public State<S> clone() {
        try {
            State clone = (State) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
