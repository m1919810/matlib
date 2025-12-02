package me.matl114.matlib.nmsMirror;

import com.google.common.base.Preconditions;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class Utils {
    public static <T> T matchName(List<Field> fields, String... name){
        Preconditions.checkArgument(!fields.isEmpty());
        Set<String> set = Set.<String>of(name);
        try{
            return (T)fields.stream()
                .filter( f -> set.contains(ObfManager.getManager().deobfField(f)))
                .peek(f->f.setAccessible(true))
                .findFirst()
                .orElseThrow()
                .get(null);
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting field "+name+" in "+ fields.getFirst().getDeclaringClass().getSimpleName()+":");
            return null;
        }

    }
    public static <T> T matchNull(List<Field> fields, String name){
        Preconditions.checkArgument(!fields.isEmpty());
        Set<String> set = Set.<String>of(name);
        try{
            Field field = fields.stream()
                .filter( f -> set.contains(ObfManager.getManager().deobfField(f)))
                .peek(f->f.setAccessible(true))
                .findFirst().orElse(null);
            return field == null? null: (T)field.get(null);
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting field "+name+" in "+ fields.getFirst().getDeclaringClass().getSimpleName()+":");
            return null;
        }

    }
    public static Object reflect(Field field, Object tar){
        try{
            return field.get(tar);
        }catch (Throwable e){
            throw  new RuntimeException(e);
        }
    }

    public static Object invokeNoArgument(List<Method> methods, String name, Object instance){
        Preconditions.checkArgument(!methods.isEmpty());
        try{
            return methods.stream()
                .filter(m->m.getParameterCount() == 0)
                .filter(f -> ObfManager.getManager().deobfMethod(f).equals(name))
                .peek(f->f.setAccessible(true))
                .findFirst()
                .orElseThrow()
                .invoke(instance);
        }catch (Throwable e){
            Debug.logger(e, "Exception while reflecting field "+name+" in "+ methods.getFirst().getDeclaringClass().getSimpleName()+":");
            return null;
        }
    }
    public static Object deobfStatic(Class<?> clazz, String name){
        return reflect( ObfManager.getManager().lookupFieldInClass(clazz, name),null);
    }
}
