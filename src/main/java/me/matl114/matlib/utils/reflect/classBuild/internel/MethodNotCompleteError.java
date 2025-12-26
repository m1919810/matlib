package me.matl114.matlib.utils.reflect.classBuild.internel;

import me.matl114.matlib.utils.reflect.exceptions.ReflectRuntimeException;

public class MethodNotCompleteError extends ReflectRuntimeException {

    public MethodNotCompleteError(String v) {
        super("Fail hard at uncompleted methods :" + v);
    }
}
