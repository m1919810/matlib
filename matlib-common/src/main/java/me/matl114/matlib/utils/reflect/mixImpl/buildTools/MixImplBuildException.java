package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorException;
import me.matl114.matlib.utils.reflect.exceptions.ReflectRuntimeException;

public class MixImplBuildException extends ReflectRuntimeException {
    public static MixImplBuildException warp(Throwable e) {
        return e instanceof MixImplBuildException b ? b : new MixImplBuildException(e);
    }

    public MixImplBuildException(Throwable e) {
        super("Error while creating mixImpl :", DescriptorException.handled(e));
        Debug.logger(e, "Error while building Descriptor! ");
    }

    public MixImplBuildException(String v) {
        super("Error while creating mixImpl :" + v);
    }
}
