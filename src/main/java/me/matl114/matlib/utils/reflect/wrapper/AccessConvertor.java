package me.matl114.matlib.utils.reflect.wrapper;

import com.google.common.base.Preconditions;
import me.matl114.matlib.common.functions.reflect.FieldAccessor;
import me.matl114.matlib.common.functions.reflect.MethodInvoker;
import me.matl114.matlib.utils.reflect.reflectasm.FieldAccess;
import me.matl114.matlib.utils.reflect.reflectasm.MethodAccess;

@Deprecated(forRemoval = true)
public class AccessConvertor {
    public static AsmArgument<MethodAccess> convertMethodAccess(
            me.matl114.matlib.utils.reflect.wrapper.MethodAccess<?> access, boolean convertInvoker) {
        access.init(null);
        Preconditions.checkArgument(access.isPublic, "Unable to create asm methodAccess for non-public method");
        try {
            MethodAccess fastAccessInternal =
                    me.matl114.matlib.utils.reflect.reflectasm.MethodAccess.get(access.field.getDeclaringClass());
            int fastAccessIndex = fastAccessInternal.getIndex(access.field.getName(), access.field.getParameterTypes());
            if (convertInvoker) {
                access.invoker = new MethodInvoker() {
                    @Override
                    public Object invokeInternal(Object obj, Object... args) throws Throwable {
                        throw new IllegalStateException("Method shouldn't be called");
                    }

                    @Override
                    public Object invoke(Object obj, Object... args) {
                        return fastAccessInternal.invoke(obj, fastAccessIndex, args);
                    }
                };
            }
            return new AsmArgument<>(fastAccessInternal, fastAccessIndex);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldAccessor createASMAccessor(AsmArgument<FieldAccess> asm) {
        final int a = asm.index;
        FieldAccess access = asm.access;
        return new FieldAccessor() {
            @Override
            public void set(Object obj, Object value) {
                access.set(obj, a, value);
            }

            @Override
            public Object get(Object obj) {
                return access.get(obj, a);
            }
        };
    }

    public static AsmArgument<FieldAccess> convertFieldAccess(
            me.matl114.matlib.utils.reflect.wrapper.FieldAccess access, boolean convertInvoker) {
        access.init(null);
        Preconditions.checkArgument(access.publicField, "Unable to create asm methodAccess for non-public field");
        try {
            FieldAccess fastInternalAccess = FieldAccess.get(access.field.getDeclaringClass());
            int fastAccessIndex = fastInternalAccess.getIndex(access.field);
            if (convertInvoker) {
                access.setter = (a, b) -> fastInternalAccess.set(a, fastAccessIndex, b);
                access.getter = (a) -> fastInternalAccess.get(a, fastAccessIndex);
            }
            return new AsmArgument<>(fastInternalAccess, fastAccessIndex);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> MethodInvoker<T> createASMInvoker(AsmArgument<MethodAccess> asm) {
        MethodAccess a = asm.access;
        int b = asm.index;
        return new MethodInvoker<>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public T invoke(Object obj, Object... args) {
                return (T) a.invoke(obj, b, args);
            }
        };
    }

    public static record AsmArgument<T>(T access, int index) {
        public FieldAccessor<?> createFieldAccessor() {
            return createASMAccessor((AsmArgument<FieldAccess>) this);
        }

        public MethodInvoker<?> createMethodInvoker() {
            return createASMInvoker((AsmArgument<MethodAccess>) this);
        }
    }
}
