package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

public class MixBase {

    static <T, W> T castIns(W c) {
        throw MixImplException.placeHolder();
    }

    static <W> boolean checkCastIns(Object c) {
        throw MixImplException.placeHolder();
    }
}
