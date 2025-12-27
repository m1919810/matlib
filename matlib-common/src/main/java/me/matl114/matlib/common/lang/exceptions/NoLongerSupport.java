package me.matl114.matlib.common.lang.exceptions;

public class NoLongerSupport extends UnsupportedOperationException {
    public NoLongerSupport() {
        super("this method is no longer supported");
    }
}
