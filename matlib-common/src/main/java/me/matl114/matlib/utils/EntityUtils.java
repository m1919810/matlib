package me.matl114.matlib.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.functions.reflect.MethodInvoker;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class EntityUtils {
    @Getter
    private static final Class<?> craftPlayerClass = Holder.of(
                    Bukkit.getServer().getClass())
            .thenApply(clazz -> {
                Class clazz0 = clazz;
                while (!"CraftServer".equals(clazz0.getSimpleName())) {
                    clazz0 = clazz0.getSuperclass();
                }
                return clazz0;
            })
            .thenApplyUnsafe(Class::getDeclaredField, "playerView")
            .thenApply(Field::getGenericType)
            .thenApply(ParameterizedType.class::cast)
            .thenApply(ParameterizedType::getActualTypeArguments)
            .thenApply(args -> args[0])
            .thenApply(Class.class::cast)
            .get();

    @Getter
    private static final Class<?> craftEntityClass = Holder.of(craftPlayerClass)
            .thenApply(clazz -> {
                Class class0 = clazz;
                while (!"CraftEntity".equals(class0.getSimpleName())) {
                    class0 = class0.getSuperclass();
                }
                return class0;
            })
            .get();

    @Getter
    private static final MethodInvoker<?> getHandleMethodInvoker = Holder.of(craftEntityClass)
            .thenApplyUnsafe(Class::getMethod, "getHandle")
            .thenApplyUnsafe((method) -> LambdaUtils.createLambdaForMethod(Function.class, method))
            .thenApply(MethodInvoker::<Object>ofNoArgs)
            .get();

    public static Object getEntityHandle(Entity entity) {
        return getHandleMethodInvoker.invoke(entity);
    }
}
