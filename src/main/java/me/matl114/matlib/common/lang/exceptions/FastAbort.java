package me.matl114.matlib.common.lang.exceptions;

public class FastAbort extends Abort {
    Object reason;

    public <T> T getAbortReason() {
        return (T) reason;
    }

    public FastAbort(Object val) {
        super();
        this.reason = val;
    }
}
