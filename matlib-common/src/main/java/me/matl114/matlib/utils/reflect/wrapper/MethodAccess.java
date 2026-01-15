package me.matl114.matlib.utils.reflect.wrapper;

import com.google.common.base.Preconditions;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import me.matl114.matlib.common.functions.reflect.MethodInvoker;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;

@Deprecated(forRemoval = true)
@SuppressWarnings("unchecked")
public class MethodAccess<T extends Object> {
    private boolean printError = false;

    @Getter
    private boolean failInitialization = false;

    private Function<Object, Method> lazilyInitializationFunction;
    Method field;
    private MethodHandle handle;
    private boolean failHandle = true;
    boolean isStatic;
    boolean isPublic;
    int argLen;
    boolean isVoidReturn;
    private static final boolean useHandle = false;
    boolean createSnapshot = true;
    MethodInvoker<T> invoker;

    public MethodAccess<T> noSnapShot() {
        this.createSnapshot = false;
        return this;
    }

    public static <T> MethodAccess<T> reflect(String name, Class tar, Class... param) {
        return (MethodAccess<T>)
                ofName(tar, name, param).noSnapShot().printError(true).initWithNull();
    }

    public static <W> MethodAccess<W> ofName(Class clazz, String fieldName) {
        return new MethodAccess((obj) -> {
            var result = ReflectUtils.getMethodsByName(clazz, fieldName);
            return result == null ? null : result.getA();
        });
    }

    public static <W> MethodAccess<W> ofName(Class clazz, String fieldName, Class... parameterTypes) {
        return new MethodAccess((obj) -> {
            var result = ReflectUtils.getMethodsRecursively(clazz, fieldName, parameterTypes);
            return result == null ? null : result.getA();
        });
    }

    public static <W> MethodAccess<W> ofName(String fieldName, Class... parameterTypes) {
        return new MethodAccess((obj) -> {
            var result = ReflectUtils.getMethodsRecursively(obj.getClass(), fieldName, parameterTypes);
            return result == null ? null : result.getA();
        });
    }

    public static <W> MethodAccess<W> of(Method field) {
        return new MethodAccess((obj) -> field);
    }

    private static final MethodAccess FAIL;

    static {
        var access = new MethodAccess<>(null);
        access.failInitialization = true;
        FAIL = access;
    }

    public static MethodAccess ofFailure() {
        return FAIL;
    }

    public <W extends Object> MethodAccess<W> forceCast(Class<W> clazz) {
        if (this.field != null)
            Preconditions.checkArgument(
                    clazz.isAssignableFrom(this.field.getReturnType()),
                    "cast failed,return type does not match :%s ,expect super of %s",
                    clazz,
                    this.field.getReturnType());
        return (MethodAccess<W>) this;
    }

    public MethodAccess(Function<Object, Method> initFunction) {
        this.lazilyInitializationFunction = initFunction;
    }

    public MethodAccess<T> printError(boolean printError) {
        this.printError = printError;
        return this;
    }

    public boolean successfullyInitialized() {
        return this.field != null;
    }

    public MethodAccess<T> init(Object obj) {
        if (this.field == null && !failInitialization) {
            try {
                initInternal(obj);
            } catch (Throwable e) {
                failInitialization = true;
                if (printError) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    private void initInternal(Object obj) throws Throwable {
        Method field = lazilyInitializationFunction.apply(obj);
        Preconditions.checkArgument(
                field != null,
                "MethodAccess init method failed: method is null! using argument: "
                        + (obj == null ? "null" : obj.getClass()));
        field.setAccessible(true);
        this.field = field;
        this.argLen = this.field.getParameterCount();
        if (this.createSnapshot) {
            int mod = this.field.getModifiers();
            this.isStatic = Modifier.isStatic(mod);
            this.isPublic = Modifier.isPublic(mod);
            this.isVoidReturn = this.field.getReturnType() == void.class;

            try {
                this.handle = MethodHandles.privateLookupIn(this.field.getDeclaringClass(), MethodHandles.lookup())
                        .unreflect(this.field);
                this.failHandle = false;
            } catch (Throwable handleFailed) {
                this.failHandle = true;
                if (printError) {
                    Debug.logger("Failed to create method handle for method :", field);
                    handleFailed.printStackTrace();
                }
            }
        }
        this.invoker = invokerInternal();
    }

    public Method getMethodOrDefault(Supplier<Method> defa) {
        init(null);
        return failInitialization ? defa.get() : field;
    }

    public Method finalizeMethodOrDefault(Object initializeObject, Supplier<Method> defa) {
        init(initializeObject);
        return failInitialization ? defa.get() : field;
    }

    private Class getMethodReturnType() {
        Preconditions.checkArgument(!failInitialization, "MehodAccess initialization failed!");
        Preconditions.checkArgument(field != null, "MehodAccess method not initialized!");
        return field.getReturnType();
    }

    private Class getDeclareClass() {
        Preconditions.checkArgument(!failInitialization, "MehodAccess initialization failed!");
        Preconditions.checkArgument(field != null, "MehodAccess method not initialized!");
        return field.getDeclaringClass();
    }

    public MethodAccess<T> initWithNull() {
        init(null);
        return this;
    }

    public MethodHandle getMethodHandleOrDefault(Supplier<MethodHandle> defa) {
        init(null);
        return failHandle ? defa.get() : handle;
    }

    public MethodHandle finalizeHandleOrDefault(Object initializeObject, Supplier<MethodHandle> defa) {
        init(initializeObject);
        return failHandle ? defa.get() : handle;
    }

    public MethodInvoker<T> getInvoker() {
        initWithNull();
        return invoker;
    }

    public MethodInvoker<T> finalizeInvoker(Object initializeObject) {
        init(initializeObject);
        return invoker;
    }

    private Object invokeUsingHandle(Object tar, Object... obj) throws Throwable {
        if (!useHandle) {
            throw new RuntimeException("Method Invoke using MethodHandle is not supported anyMore");
        }
        if (isStatic) {
            if (this.argLen == 0) {
                if (isVoidReturn) {}
            } else {
                return handle.invokeWithArguments(obj);
            }
        } else {
            if (this.argLen == 0) {
                return handle.invoke(tar);
            } else {
                Object[] arg = new Object[obj.length + 1];
                arg[0] = tar;
                System.arraycopy(obj, 0, arg, 1, obj.length);
                return handle.invokeWithArguments(arg);
            }
        }
        return null;
    }

    private MethodInvoker<T> invokerInternal() {
        if (!useHandle || this.failHandle) {
            //            if(!failPublicAccess){
            //                return new MethodInvoker<T>() {
            //                    @Override
            //                    public T invokeInternal(Object obj, Object... args) throws Throwable {
            //                        throw new IllegalStateException("Method shouldn't be called");
            //                    }
            //                    public T invoke(Object obj,Object... args){
            //                        return (T)fastAccessInternal.invoke(obj,fastAccessIndex,args);
            //                    }
            //                } ;
            //            }else {
            return ((obj, args) -> (T) field.invoke(obj, args));
            //            }
        } else {
            return (obj, args) -> (T) invokeUsingHandle(obj, args);
        }
    }

    public T invoke(Object tar, Object... obj) throws Throwable {
        init(tar);
        return invoker.invoke(tar, obj);
    }

    public T invokeIgnoreFailure(Object tar, Object... obj) throws Throwable {
        if (field == null) {
            try {
                initInternal(tar);
            } catch (Throwable e) {
                if (printError) {
                    e.printStackTrace();
                }
            }
        }
        return invoker.invoke(tar, obj);
    }

    public MethodAccess<T> invokeCallback(Consumer<T> callback, Runnable failedCallback, Object tar, Object... obj) {
        if (this.failInitialization) {
            failedCallback.run();
            return this;
        }
        try {
            T result = invoke(tar, obj);
            callback.accept(result);
        } catch (Throwable e) {
            failedCallback.run();
        }
        return this;
    }

    public MethodAccess<T> invokeCallback(Consumer<T> callback, Object tar, Object... obj) {
        return invokeCallback(callback, () -> {}, tar, obj);
    }

    public Supplier<T> getInvokeTask(Object tar, Object... obj) {
        if (this.field == null) {
            init(tar);
        }
        if (this.failInitialization) {
            return null;
        } else {
            // return this::invokeInternal();
            return () -> {
                try {
                    return invoker.invoke(tar, obj);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public <w> MethodAccess<w> cast() {
        return (MethodAccess<w>) this;
    }
}
