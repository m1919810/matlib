package me.matl114.matlib.algorithms.dataStructures.struct;

import lombok.Getter;
import lombok.Setter;

public class StateTable implements Cloneable {
    @Getter
    @Setter
    public boolean abort;

    public Throwable exception;
    private static final StateTable INSTANCE = new StateTable();

    private StateTable() {}

    public static StateTable get() {
        return INSTANCE.clone();
    }

    public void setError(Throwable e) {
        this.exception = e;
    }

    public Throwable getError() {
        return this.exception;
    }

    @Override
    public StateTable clone() {
        try {
            StateTable clone = (StateTable) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    //    public int getErrCode(){
    //        return this.errCode;
    //    }
}
