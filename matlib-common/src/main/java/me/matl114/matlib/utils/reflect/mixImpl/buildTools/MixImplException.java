package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

import me.matl114.matlib.utils.reflect.exceptions.ReflectRuntimeException;

public class MixImplException extends ReflectRuntimeException {
    public static MixImplException notImpl() {
        return new MixImplException(0);
    }

    public static MixImplException internal() {
        return new MixImplException(1);
    }

    public static MixImplException placeHolder() {
        return new MixImplException(2);
    }

    private static final String[] reasons = new String[] {
        "Error in mixImpl: This method is not implemented!",
        "Error in mixImpl: Illegal Access! This method is created as internal method",
        "Error in mixImpl; Illegal Access! This method is created as a placeholder"
    };

    private MixImplException(int i) {
        super(reasons[i]);
    }

    public MixImplException(Throwable e) {
        super("Error in mixImpl :", e);
    }
}
