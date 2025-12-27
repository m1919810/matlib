package me.matl114.matlib.common.lang.exceptions;

/**
 * this is a quick throwable, we hope not to fillInStackTrace when create it, so its creating speed can be improved
 */
public class RuntimeAbort extends RuntimeException {
    public RuntimeAbort() {
        super();
    }

    public RuntimeAbort(String message) {
        super(message);
    }

    public RuntimeAbort(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeAbort(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // override this method to avoid fill stacktrace when create Abort
        return this;
    }
}
