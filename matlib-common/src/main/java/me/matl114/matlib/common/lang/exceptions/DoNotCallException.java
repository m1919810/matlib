package me.matl114.matlib.common.lang.exceptions;

public class DoNotCallException extends IllegalStateException {
    public DoNotCallException() {
        super("This method shouldn't be accessed from here!");
    }
}
