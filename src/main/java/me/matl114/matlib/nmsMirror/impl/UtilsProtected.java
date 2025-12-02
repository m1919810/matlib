package me.matl114.matlib.nmsMirror.impl;

import com.mojang.serialization.Codec;
import lombok.val;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.Version;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.matl114.matlib.nmsMirror.impl.CodecEnum.*;

class UtilsProtected {
    private static List<Field> fds(String value){
        if( value.endsWith(";")){
            value= ByteCodeUtils.fromJvmType(value);
        }
        try{
            Class<?> clazz = ObfManager.getManager().reobfClass(value);
            return Arrays.stream(clazz.getFields())
                .filter(i-> Modifier.isStatic(i.getModifiers()))
                .filter(i->i.getType() == Codec.class)
                .toList();
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
    static Holder<Codec<Object>> SHARING_OBJECT_1 = me.matl114.matlib.algorithms.dataStructures.struct.Holder.of(null);
    static Holder<Codec<Object>> SHARING_OBJECT_2 = me.matl114.matlib.algorithms.dataStructures.struct.Holder.of(null);
    static <T> Codec<T> fc(String value,String...val){
        List<Field> fields= fds(value);
        if(val.length >= 3){
            SHARING_OBJECT_2.setValue(Utils.matchName(fields, val[2]));
        }
        if(val.length >= 2){
            SHARING_OBJECT_1.setValue(Utils.matchName(fields, val[1]));
        }
        return Objects.requireNonNull( Utils.matchName(fields, val[0]));
    }

    static <T> Codec<T> fcVer(String value, Version version,String...val){
        List<Field> fields= fds(value);
        if(val.length >= 3){
            SHARING_OBJECT_2.setValue(Utils.matchNull(fields, val[2]));
        }
        if(val.length >= 2){
            SHARING_OBJECT_1.setValue(Utils.matchNull(fields, val[1]));
        }
        return !Version.getVersionInstance().isAtLeast(version) ?Utils.matchNull(fields, val[0]) : Utils.matchName(fields, val[0]);
    }

    static Codec<Object> g1(){
        return Objects.requireNonNull(SHARING_OBJECT_1.get());
    }
    static Codec<Object> g1Ver(Version version){
        return !Version.getVersionInstance().isAtLeast(version) ? SHARING_OBJECT_1.get() : Objects.requireNonNull(SHARING_OBJECT_1.get());
    }
    static Codec<Object> g2(){
        return Objects.requireNonNull(SHARING_OBJECT_2.get());
    }
    static Codec<Object> g2Ver(Version version){
        return !Version.getVersionInstance().isAtLeast(version) ? SHARING_OBJECT_2.get() : Objects.requireNonNull(SHARING_OBJECT_2.get());
    }
}
