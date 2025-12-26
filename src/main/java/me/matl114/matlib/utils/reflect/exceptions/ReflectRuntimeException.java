package me.matl114.matlib.utils.reflect.exceptions;

public class ReflectRuntimeException extends RuntimeException {
    public ReflectRuntimeException() {}

    public ReflectRuntimeException(String message) {
        super(message);
    }

    public ReflectRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectRuntimeException(Throwable cause) {
        super(cause);
    }

    public ReflectRuntimeException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
