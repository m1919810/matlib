package me.matl114.matlib.common.lang.exceptions;

/**
 * this is a quick throwable, we hope not to fillInStackTrace when create it, so its creating speed can be improved
 */
public class Abort extends Throwable {
    private static final Abort INSTANCE = new Abort();

    public Abort getInstance() {
        return INSTANCE;
    }

    public Abort() {
        super();
    }

    public Abort(String message) {
        super(message);
    }

    public Abort(String message, Throwable cause) {
        super(message, cause);
    }

    public Abort(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // override this method to avoid fill stacktrace when create Abort
        return this;
    }
}
