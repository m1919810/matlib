package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.Protected;

public class MixBase {

    static <T, W> T castIns(W c){
        throw MixImplException.placeHolder();
    }

    static <W> boolean checkCastIns(Object c){
        throw MixImplException.placeHolder();
    }
}
