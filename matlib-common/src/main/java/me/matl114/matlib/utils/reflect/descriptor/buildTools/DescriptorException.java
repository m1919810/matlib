package me.matl114.matlib.utils.reflect.descriptor.buildTools;

import java.lang.reflect.InvocationTargetException;
import me.matl114.matlib.utils.reflect.exceptions.ReflectRuntimeException;

public class DescriptorException extends ReflectRuntimeException {
    public static Throwable handled(Throwable e) {
        if (e instanceof InvocationTargetException || e instanceof IllegalAccessException) {
            var re = e.getCause();
            return re == null ? e : re;
        }
        return e;
    }

    public static DescriptorException dump(Throwable e) {
        return new DescriptorException(e);
    }

    private static final String[] reasons = new String[] {
        "Error while executing reflection through Descriptor: Target absent! This method is not implemented!"
    };

    public static DescriptorException notImpl() {
        return new DescriptorException(0);
    }

    private DescriptorException(int a) {
        super(reasons[a]);
    }

    public DescriptorException(Throwable e) {
        super("Error while executing reflection through Descriptor:", handled(e));
    }
}
