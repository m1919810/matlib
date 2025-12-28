package me.matl114.matlib.utils.reflect.classBuild;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.*;
import static org.objectweb.asm.Type.getInternalName;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import me.matl114.matlib.utils.reflect.ASMUtils;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorBuildException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.objectweb.asm.*;

@SuppressWarnings("all")
public class ClassBuilder {


    /**
     * flag, whether to set up try-catch block in MethodHandle invocation
     */
    private static final boolean checkThrowable = false;

    /**
     * core building , pieces of shit,
     * @param targetClass
     * @param mainInterfaceImpl
     * @param fieldGetDescrip
     * @param fieldSetDescrip
     * @param methodDescrip
     * @param constructorDescrip
     * @param uncompletedMethod
     * @return
     * @param <T>
     * @throws Throwable
     */
    public static synchronized <T extends TargetDescriptor> T buildTargetFlattenInvokeImpl(
            @Nullable Class<?> targetClass,
            Class<T> mainInterfaceImpl,
            Class<?> superClass,
            Class<?>[] appendedInterfaces,
            Map<Method, Field> fieldGetDescrip,
            Map<Method, Field> fieldSetDescrip,
            Map<Method, Method> methodDescrip,
            Map<Method, Constructor<?>> constructorDescrip,
            Map<Method, Class<?>> typeCastDescrip,
            Map<Method, Class<?>> typeGetDescrip,
            List<Method> uncompletedMethod)
            throws Throwable {
        // remove all completed methods as a faillback mechanism
        ClassBuildingUtils.checkUncompleted(uncompletedMethod, mainInterfaceImpl);
        // start creating clazz
        T result = null;
        synchronized (CustomClassLoader.getInstance()) {
            try {
                var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                String implName = mainInterfaceImpl.getName().replace("$", ".") + "Impl" + rand.nextInt(1000);
                // path.to.your.descriptorImpl114
                String implPath = implName.replace('.', '/');
                String interfacePath = getInternalName(mainInterfaceImpl);
                List<String> interfaces = new ArrayList<>();
                interfaces.add(interfacePath);
                for (var itf : appendedInterfaces) {
                    interfaces.add(getInternalName(itf));
                }
                cw.visit(
                        V21,
                        ACC_PUBLIC | ACC_FINAL | ACC_SUPER,
                        implPath,
                        null,
                        getInternalName(superClass),
                        interfaces.toArray(String[]::new));
                cw.visitSource(null, null);
                // create 内部类
                int index = 0;
                Reference2IntArrayMap<Field> handledField = new Reference2IntArrayMap<>();
                Reference2IntArrayMap<Field> reflectionNeedField = new Reference2IntArrayMap<>();
                Reference2IntArrayMap<Method> handledMethod = new Reference2IntArrayMap<>();
                Reference2IntArrayMap<Constructor<?>> handledConstructor = new Reference2IntArrayMap<>();
                Reference2IntArrayMap<Class<?>> handledTypeInstance = new Reference2IntArrayMap<>();
                HashSet<Field> relatedFields = new HashSet<>();
                relatedFields.addAll(fieldGetDescrip.values());
                relatedFields.addAll(fieldSetDescrip.values());
                Set<Field> finalFieldsToSet = fieldSetDescrip.values().stream()
                        .filter(f -> Modifier.isFinal(f.getModifiers()))
                        .collect(Collectors.toSet());
                Set<Field> fieldGetDescripSet = new HashSet<>(fieldGetDescrip.values());
                FieldVisitor fv;
                fv = cw.visitField(ACC_FINAL | ACC_STATIC, "delegate", "Ljava/lang/Class;", null, null);
                fv.visitEnd();
                // fix the bug of generating exact cast to package-private class
                Int2BooleanArrayMap exactAccessible = new Int2BooleanArrayMap();
                for (var entry : relatedFields) {
                    int mod = entry.getModifiers();
                    // fix access to owner
                    // fix final field access Field reflection
                    if (Modifier.isFinal(mod) && finalFieldsToSet.contains(entry)) {
                        reflectionNeedField.put(entry, index);
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                    ACC_FINAL | ACC_STATIC, fieldName, "Ljava/lang/reflect/Field;", null, null);
                            fv.visitEnd();
                        }
                        index++;
                        // if no get, only set, there is no need for VarHandle field to create, just directly skip
                        if (!fieldGetDescripSet.contains(entry)) {
                            continue;
                        }
                    }
                    if (Modifier.isPublic(mod)
                            && Modifier.isPublic(entry.getDeclaringClass().getModifiers())) {
                        continue;
                    } else {
                        // catch need-handle fields
                        handledField.put(entry, index);
                        // exactAccess if class is public, and
                        exactAccessible.put(index, canExact(entry.getDeclaringClass()));
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                    ACC_FINAL | ACC_STATIC, fieldName, "Ljava/lang/invoke/VarHandle;", null, null);
                            fv.visitEnd();
                        }
                        index++;
                    }
                }

                for (var entry : methodDescrip.entrySet()) {
                    int mod = entry.getValue().getModifiers();
                    boolean canExact = canExactInvoke(entry.getValue());
                    if (canExact
                            && Modifier.isPublic(mod)
                            && Modifier.isPublic(
                                    entry.getValue().getDeclaringClass().getModifiers())) {
                        continue;
                    } else {
                        // catch need-handle methods
                        handledMethod.put(entry.getValue(), index);
                        exactAccessible.put(index, canExact);
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                    ACC_FINAL | ACC_STATIC, fieldName, "Ljava/lang/invoke/MethodHandle;", null, null);
                            fv.visitEnd();
                        }
                        index++;
                    }
                }
                for (var entry : constructorDescrip.entrySet()) {
                    int mod = entry.getValue().getModifiers();
                    // fix: check class access
                    if (Modifier.isPublic(mod)
                            && Modifier.isPublic(
                                    entry.getValue().getDeclaringClass().getModifiers())) {
                        continue;
                    } else {
                        handledConstructor.put(entry.getValue(), index);
                        exactAccessible.put(
                                index,
                                Modifier.isPublic(
                                        entry.getValue().getDeclaringClass().getModifiers()));
                        String fieldName = "handle" + index;
                        {
                            fv = cw.visitField(
                                    ACC_FINAL | ACC_STATIC, fieldName, "Ljava/lang/invoke/MethodHandle;", null, null);
                            fv.visitEnd();
                        }
                        index++;
                    }
                }
                for (var entry : typeGetDescrip.entrySet()) {
                    handledTypeInstance.put(entry.getValue(), index);
                    String fieldName = "handle" + index;
                    {
                        fv = cw.visitField(ACC_FINAL | ACC_STATIC, fieldName, "Ljava/lang/Class;", null, null);
                        fv.visitEnd();
                    }
                    index++;
                }
                // create need-handle fields;
                MethodVisitor mv;
                {
                    ASMUtils.generateEmptyInit(cw, null);
                }
                {
                    mv = cw.visitMethod(ACC_PUBLIC, "getTargetClass", "()Ljava/lang/Class;", null, null);
                    mv.visitCode();
                    mv.visitFieldInsn(GETSTATIC, implPath, "delegate", "Ljava/lang/Class;");
                    mv.visitInsn(ARETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                for (var entry : fieldGetDescrip.entrySet()) {
                    Method itfMethod = entry.getKey();
                    Field tarField = entry.getValue();
                    int mod = tarField.getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    if (Modifier.isPublic(mod)
                            && Modifier.isPublic(tarField.getDeclaringClass().getModifiers())) {
                        // create bytecode access directly
                        if (Modifier.isStatic(mod)) {
                            mv.visitCode();
                            mv.visitFieldInsn(
                                    GETSTATIC,
                                    getInternalName(tarField.getDeclaringClass()),
                                    tarField.getName(),
                                    ByteCodeUtils.toJvmType(tarField.getType()));
                            if (!itfMethod.getReturnType().isAssignableFrom(tarField.getType())) {
                                // cast
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(tarField.getType()),
                                        getInternalName(itfMethod.getReturnType()));
                            }
                            ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                            mv.visitMaxs(0, 0);
                            mv.visitEnd();

                        } else {

                            mv.visitCode();
                            if (itfMethod.getParameterCount() == 0) {
                                throw new DescriptorBuildException(
                                        "Illegal parameter detected at " + itfMethod.toString()
                                                + ", getter of a non-static field, There should be more than one parameter");
                            }
                            // 访问第0个参数
                            mv.visitVarInsn(ALOAD, 1);
                            Class<?> instanecType = itfMethod.getParameterTypes()[0];
                            if (!tarField.getDeclaringClass().isAssignableFrom(instanecType)) {
                                // instance 不能直接赋值给tarField的
                                // cast
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(instanecType),
                                        getInternalName(tarField.getDeclaringClass()));
                            }
                            mv.visitFieldInsn(
                                    GETFIELD,
                                    getInternalName(tarField.getDeclaringClass()),
                                    tarField.getName(),
                                    ByteCodeUtils.toJvmType(tarField.getType()));
                            if (!itfMethod.getReturnType().isAssignableFrom(tarField.getType())) {
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(tarField.getType()),
                                        getInternalName(itfMethod.getReturnType()));
                            }
                            ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                            mv.visitMaxs(0, 0);
                            mv.visitEnd();
                        }
                    } else {
                        // to be continued
                        // using handlei
                        index = handledField.getInt(tarField);
                        String fieldName = "handle" + index;
                        boolean exactInvoke = exactAccessible.get(index);
                        mv.visitCode();
                        if (!Modifier.isStatic(mod) && itfMethod.getParameterCount() == 0) {
                            throw new DescriptorBuildException("Illegal parameter detected at " + itfMethod.toString()
                                    + ", getter of a non-static field, There should be more than one parameter");
                        }
                        mv.visitFieldInsn(GETSTATIC, implPath, fieldName, "Ljava/lang/invoke/VarHandle;");
                        if (!Modifier.isStatic(mod)) {
                            mv.visitVarInsn(ALOAD, 1);
                            Class<?> instanecType = itfMethod.getParameterTypes()[0];
                            if (exactInvoke && !tarField.getDeclaringClass().isAssignableFrom(instanecType)) {
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(instanecType),
                                        getInternalName(tarField.getDeclaringClass()));
                            }
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/lang/invoke/VarHandle",
                                    "get",
                                    "("
                                            + (exactInvoke
                                                    ? ByteCodeUtils.toJvmType(tarField.getDeclaringClass())
                                                    : ByteCodeUtils.toJvmType(instanecType))
                                            + ")"
                                            + (exactInvoke
                                                    ? ByteCodeUtils.toJvmType(tarField.getType())
                                                    : ByteCodeUtils.toJvmType(itfMethod.getReturnType())),
                                    false);
                            if (exactInvoke && !itfMethod.getReturnType().isAssignableFrom(tarField.getType())) {
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(tarField.getType()),
                                        getInternalName(itfMethod.getReturnType()));
                            }

                        } else {
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/lang/invoke/VarHandle",
                                    "get",
                                    "()"
                                            + (exactInvoke
                                                    ? ByteCodeUtils.toJvmType(tarField.getType())
                                                    : ByteCodeUtils.toJvmType(itfMethod.getReturnType())),
                                    false);
                            if (exactInvoke && !itfMethod.getReturnType().isAssignableFrom(tarField.getType())) {
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(tarField.getType()),
                                        getInternalName(itfMethod.getReturnType()));
                            }
                        }
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                        mv.visitMaxs(0, 0);
                        mv.visitEnd();
                    }
                }
                for (var entry : fieldSetDescrip.entrySet()) {
                    Method itfMethod = entry.getKey();
                    Field tarField = entry.getValue();
                    int mod = tarField.getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    mv.visitCode();
                    if (itfMethod.getParameterCount() + (Modifier.isStatic(mod) ? 1 : 0) < 2) {
                        throw new DescriptorBuildException("Illegal parameter detected at " + itfMethod.toString()
                                + ", setter should have more parameters");
                    }
                    // fix: fix final field set
                    if (Modifier.isFinal(mod)) {
                        index = reflectionNeedField.getInt(tarField);
                        String fieldName = "handle" + index;
                        mv.visitCode();
                        mv.visitFieldInsn(GETSTATIC, implPath, fieldName, "Ljava/lang/reflect/Field;");
                        if (!Modifier.isStatic(mod)) {
                            Class<?> valType = itfMethod.getParameterTypes()[1];
                            mv.visitVarInsn(ALOAD, 1);
                            //  Class<?> instanecType = itfMethod.getParameterTypes()[0];
                            // boxing primitive type
                            // ASMUtils.castType(mv, getInternalName(instanecType), getInternalName(Object.class));
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 2);

                            ASMUtils.castType(mv, getInternalName(valType), getInternalName(Object.class));
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/lang/reflect/Field",
                                    "set",
                                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                                    false);
                        } else {
                            throw new DescriptorBuildException(
                                    "Static final field can not be modified except using Unsafe, which we do not support here");
                        }
                    } else if (Modifier.isPublic(mod)
                            && Modifier.isPublic(tarField.getDeclaringClass().getModifiers())) {
                        if (Modifier.isStatic(mod)) {
                            // 如果参数类型不能直接赋值给tarField
                            Class<?> valType = itfMethod.getParameterTypes()[0];
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 1);
                            if (!tarField.getType().isAssignableFrom(valType)) {
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitFieldInsn(
                                    PUTSTATIC,
                                    getInternalName(tarField.getDeclaringClass()),
                                    tarField.getName(),
                                    ByteCodeUtils.toJvmType(tarField.getType()));
                        } else {
                            Class<?> inst = itfMethod.getParameterTypes()[0];
                            Class<?> valType = itfMethod.getParameterTypes()[1];
                            mv.visitVarInsn(ALOAD, 1);
                            if (!tarField.getDeclaringClass().isAssignableFrom(inst)) {
                                ASMUtils.castType(
                                        mv, getInternalName(inst), getInternalName(tarField.getDeclaringClass()));
                            }
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 2);
                            if (!tarField.getType().isAssignableFrom(valType)) {
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitFieldInsn(
                                    PUTFIELD,
                                    getInternalName(tarField.getDeclaringClass()),
                                    tarField.getName(),
                                    ByteCodeUtils.toJvmType(tarField.getType()));
                        }
                    } else {
                        // here , we have final public fields , too,
                        // it should be in the handle map, if everything goes right
                        index = handledField.getInt(tarField);
                        String fieldName = "handle" + index;
                        boolean exactInvoke = exactAccessible.get(index);
                        mv.visitCode();
                        mv.visitFieldInsn(GETSTATIC, implPath, fieldName, "Ljava/lang/invoke/VarHandle;");
                        if (!Modifier.isStatic(mod)) {
                            Class<?> valType = itfMethod.getParameterTypes()[1];
                            mv.visitVarInsn(ALOAD, 1);
                            Class<?> instanecType = itfMethod.getParameterTypes()[0];
                            if (exactInvoke && !tarField.getDeclaringClass().isAssignableFrom(instanecType)) {
                                ASMUtils.castType(
                                        mv,
                                        getInternalName(instanecType),
                                        getInternalName(tarField.getDeclaringClass()));
                            }
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 2);
                            if (!tarField.getType().isAssignableFrom(valType)) {
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/lang/invoke/VarHandle",
                                    "set",
                                    "("
                                            + (exactInvoke
                                                    ? ByteCodeUtils.toJvmType(tarField.getDeclaringClass())
                                                    : ByteCodeUtils.toJvmType(instanecType))
                                            + ByteCodeUtils.toJvmType(tarField.getType()) + ")V",
                                    false);
                        } else {
                            Class<?> valType = itfMethod.getParameterTypes()[0];
                            ASMUtils.createSuitableLoad(mv, getInternalName(valType), 1);
                            if (!tarField.getType().isAssignableFrom(valType)) {
                                ASMUtils.castType(mv, getInternalName(valType), getInternalName(tarField.getType()));
                            }
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/lang/invoke/VarHandle",
                                    "set",
                                    "(" + ByteCodeUtils.toJvmType(tarField.getType()) + ")V",
                                    false);
                        }
                    }
                    ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfMethod.getReturnType()));
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                for (var entry : methodDescrip.entrySet()) {
                    Method itfMethod = entry.getKey();
                    Method tarMethod = entry.getValue();
                    int mod = tarMethod.getModifiers();
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    int count = itfMethod.getParameterCount();
                    Preconditions.checkArgument(
                            count == tarMethod.getParameterCount() + (Modifier.isStatic(mod) ? 0 : 1),
                            "Parameter count not match at method " + itfMethod + " with target " + tarMethod);
                    Class<?>[] itfType = itfMethod.getParameterTypes();
                    Class<?>[] tarType = tarMethod.getParameterTypes();

                    Class<?> returnType = tarMethod.getReturnType();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    // itf has int return value, but target return void
                    final boolean castReturn = returnType != void.class && itfReturnType != void.class;
                    // load all invoke arguments
                    // if not public, load MethodHandle here, also load fucking try-catch block
                    index = handledMethod.getOrDefault(tarMethod, -1);
                    boolean exactInvoke = exactAccessible.getOrDefault(index, true);
                    boolean useHandle = index >= 0 || !exactInvoke;
                    Label label0 = null;
                    Label label1 = null;
                    Label label2 = null;

                    if (useHandle) {
                        if (checkThrowable) {
                            label0 = new Label();
                            label1 = new Label();
                            label2 = new Label();
                            mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
                            mv.visitLabel(label0);
                        }

                        mv.visitFieldInsn(GETSTATIC, implPath, "handle" + index, "Ljava/lang/invoke/MethodHandle;");
                    }

                    // load
                    if (Modifier.isStatic(mod)) {
                        int loadIndex = 1;
                        for (int i = 0; i < count; ++i) {
                            loadIndex += ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), loadIndex);
                            if (exactInvoke && !tarType[i].isAssignableFrom(itfType[i])) {
                                ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i]));
                            }
                        }
                    } else {
                        mv.visitVarInsn(ALOAD, 1);
                        // care about exactInvoke
                        if (exactInvoke && !tarMethod.getDeclaringClass().isAssignableFrom(itfType[0])) {
                            ASMUtils.castType(
                                    mv, getInternalName(itfType[0]), getInternalName(tarMethod.getDeclaringClass()));
                        }
                        int loadIndex = 2;
                        for (int i = 1; i < count; ++i) {
                            loadIndex += ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), loadIndex);
                            if (exactInvoke && !tarType[i - 1].isAssignableFrom(itfType[i])) {
                                ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i - 1]));
                            }
                        }
                    }
                    // execute invoke
                    // care about exactCast
                    if (!useHandle) {
                        // use pure bytecode
                        if (Modifier.isStatic(mod)) {
                            // invokestatic
                            // load parameters
                            mv.visitMethodInsn(
                                    INVOKESTATIC,
                                    getInternalName(tarMethod.getDeclaringClass()),
                                    tarMethod.getName(),
                                    getMethodDescriptor(tarMethod),
                                    tarMethod.getDeclaringClass().isInterface());
                        } else {
                            // load instance
                            mv.visitMethodInsn(
                                    tarMethod.getDeclaringClass().isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL,
                                    getInternalName(tarMethod.getDeclaringClass()),
                                    tarMethod.getName(),
                                    getMethodDescriptor(tarMethod),
                                    tarMethod.getDeclaringClass().isInterface());
                        }
                    } else {
                        // use MethodHandle.invokeExact
                        StringBuilder builder = new StringBuilder();
                        builder.append('(');
                        if (exactInvoke) {
                            if (!Modifier.isStatic(mod)) {
                                builder.append(ByteCodeUtils.toJvmType(tarMethod.getDeclaringClass()));
                            }
                            for (Class<?> arg : tarMethod.getParameterTypes()) {
                                builder.append(ByteCodeUtils.toJvmType(arg));
                            }
                        } else {

                            for (Class<?> arg : itfType) {
                                builder.append(ByteCodeUtils.toJvmType(arg));
                            }
                        }
                        builder.append(')');
                        builder.append(
                                exactInvoke
                                        ? ByteCodeUtils.toJvmType(tarMethod.getReturnType())
                                        : ByteCodeUtils.toJvmType(itfReturnType));
                        mv.visitMethodInsn(
                                INVOKEVIRTUAL,
                                "java/lang/invoke/MethodHandle",
                                exactInvoke ? "invokeExact" : "invoke",
                                builder.toString(),
                                false);
                        // invoke with no Exact, gen matched return type
                    }
                    if (castReturn) {
                        if (!itfReturnType.isAssignableFrom(returnType)) {
                            if (exactInvoke) {
                                ASMUtils.castType(mv, getInternalName(returnType), getInternalName(itfReturnType));
                            }
                            // should be already in ret type
                        }
                    }
                    if (!castReturn && returnType != void.class) {
                        mv.visitInsn(POP);
                    }
                    if (useHandle) {
                        if (checkThrowable) {
                            mv.visitLabel(label1);
                        }
                    }

                    if (!castReturn) {
                        // ignore return value, to make stack size correct
                        ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfReturnType));
                    } else {
                        // if return void, the itf also return void
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    }
                    if (useHandle) {
                        if (checkThrowable) {
                            mv.visitLabel(label2);
                            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
                            // catch block 直接消耗掉throwable
                            mv.visitMethodInsn(
                                    INVOKESTATIC,
                                    getInternalName(DescriptorException.class),
                                    "dump",
                                    "(Ljava/lang/Throwable;)" + ByteCodeUtils.toJvmType(DescriptorException.class),
                                    false);
                            mv.visitInsn(Opcodes.ATHROW);
                        }
                    }
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                for (var entry : constructorDescrip.entrySet()) {
                    Method itfMethod = entry.getKey();
                    Constructor<?> cons = entry.getValue();
                    int mod = cons.getModifiers();
                    int count = itfMethod.getParameterCount();
                    Class<?>[] itfType = itfMethod.getParameterTypes();
                    Class<?>[] tarType = cons.getParameterTypes();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    final boolean castReturn = itfReturnType != void.class;
                    Preconditions.checkArgument(
                            count == cons.getParameterCount(),
                            "Parameters not match for method " + itfMethod + " with constructor " + cons);
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    // fix: 增加对类的访问权限的检查
                    boolean useHandle = !Modifier.isPublic(mod)
                            || !Modifier.isPublic(cons.getDeclaringClass().getModifiers());
                    Label label0 = null;
                    Label label1 = null;
                    Label label2 = null;
                    boolean exactInvoke = true;
                    if (useHandle) {
                        if (checkThrowable) {
                            label0 = new Label();
                            label1 = new Label();
                            label2 = new Label();
                            mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
                            mv.visitLabel(label0);
                        }
                        index = handledConstructor.getInt(cons);
                        exactInvoke = exactAccessible.get(index);
                        mv.visitFieldInsn(GETSTATIC, implPath, "handle" + index, "Ljava/lang/invoke/MethodHandle;");
                    }
                    String tarClass = getInternalName(cons.getDeclaringClass());
                    if (!useHandle) {
                        mv.visitTypeInsn(NEW, tarClass);
                        mv.visitInsn(DUP);
                    }
                    // load and cast types
                    int loadIndex = 1;
                    for (int i = 0; i < count; ++i) {
                        loadIndex += ASMUtils.createSuitableLoad(mv, getInternalName(itfType[i]), loadIndex);
                        if (!tarType[i].isAssignableFrom(itfType[i])) {
                            ASMUtils.castType(mv, getInternalName(itfType[i]), getInternalName(tarType[i]));
                        }
                    }
                    if (!useHandle) {
                        // load instance
                        mv.visitMethodInsn(INVOKESPECIAL, tarClass, "<init>", getConstructorDescriptor(cons), false);

                        if (castReturn) {
                            if (!itfReturnType.isAssignableFrom(cons.getDeclaringClass())) {
                                ASMUtils.castType(mv, tarClass, getInternalName(itfReturnType));
                            }
                        }
                    } else {
                        // not implemented yet
                        StringBuilder builder = new StringBuilder();
                        builder.append('(');
                        for (Class<?> arg : cons.getParameterTypes()) {
                            builder.append(ByteCodeUtils.toJvmType(arg));
                        }
                        builder.append(')');
                        builder.append(
                                exactInvoke
                                        ? ByteCodeUtils.toJvmType(cons.getDeclaringClass())
                                        : ByteCodeUtils.toJvmType(itfReturnType));
                        mv.visitMethodInsn(
                                INVOKEVIRTUAL,
                                "java/lang/invoke/MethodHandle",
                                exactInvoke ? "invokeExact" : "invoke",
                                builder.toString(),
                                false);
                    }
                    if (!castReturn) {
                        mv.visitInsn(POP);
                    }
                    if (useHandle) {
                        if (checkThrowable) mv.visitLabel(label1);
                    }
                    if (!castReturn && exactInvoke) {
                        // ignore return value, to make stack size correct
                        ASMUtils.createSuitableDefaultValueReturn(mv, getInternalName(itfReturnType));
                    } else {
                        // if return void, the itf also return void
                        ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    }
                    if (useHandle) {
                        if (checkThrowable) {
                            mv.visitLabel(label2);
                            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
                            // catch block 直接消耗掉throwable
                            mv.visitMethodInsn(
                                    INVOKESTATIC,
                                    getInternalName(DescriptorException.class),
                                    "dump",
                                    "(Ljava/lang/Throwable;)" + ByteCodeUtils.toJvmType(DescriptorException.class),
                                    false);
                            mv.visitInsn(Opcodes.ATHROW);
                        }
                    }
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                for (var cast : typeCastDescrip.entrySet()) {
                    Method itfMethod = cast.getKey();
                    Class<?> castClass = cast.getValue();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    Class<?>[] paramTypes = itfMethod.getParameterTypes();
                    Preconditions.checkArgument(paramTypes.length >= 1);
                    boolean castReturn = itfReturnType != void.class;
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    ASMUtils.createSuitableLoad(mv, getInternalName(paramTypes[0]), 1);
                    mv.visitTypeInsn(INSTANCEOF, getInternalName(castClass));
                    ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                for (var type : typeGetDescrip.entrySet()) {
                    Method itfMethod = type.getKey();
                    Class<?> instance = type.getValue();
                    Class<?> itfReturnType = itfMethod.getReturnType();
                    int varIndex = handledTypeInstance.getInt(instance);
                    boolean castReturn = itfReturnType != void.class;
                    mv = ASMUtils.createOverrideMethodImpl(cw, itfMethod);
                    mv.visitFieldInsn(GETSTATIC, implPath, "handle" + varIndex, "Ljava/lang/Class;");
                    ASMUtils.createSuitableReturn(mv, getInternalName(itfMethod.getReturnType()));
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }

                for (Method tar : uncompletedMethod) {
                    mv = ASMUtils.createOverrideMethodImpl(cw, tar);
                    mv.visitCode();
                    mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(DescriptorException.class),
                            "notImpl",
                            "()" + ByteCodeUtils.toJvmType(DescriptorException.class),
                            false);
                    mv.visitInsn(ATHROW);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                // now all methods are created successfully?
                // we should complete <clinit>
                {
                    mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
                    mv.visitCode();

                    for (Field entry : handledField.keySet()) {
                        index = handledField.getInt(entry);
                        String fieldName = "handle" + index;
                        boolean invokeExact = exactAccessible.get(index);
                        MethodHandles.Lookup lookup =
                                MethodHandles.privateLookupIn(entry.getDeclaringClass(), MethodHandles.lookup());
                        // creating invokeExact VarHandle
                        VarHandle handle = lookup.unreflectVarHandle(entry);
                        //                        if(!invokeExact){
                        //                            handle = handle.withInvokeExactBehavior();
                        //                        }
                        String randId = randStr();
                        values0.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(ClassBuilder.class),
                                "initVarHandle",
                                "(ILjava/lang/String;)Ljava/lang/invoke/VarHandle;",
                                false);
                        mv.visitFieldInsn(PUTSTATIC, implPath, fieldName, "Ljava/lang/invoke/VarHandle;");
                    }
                    for (Field entry : reflectionNeedField.keySet()) {
                        index = reflectionNeedField.getInt(entry);
                        String fieldName = "handle" + index;
                        Field value = entry;
                        value.setAccessible(true);
                        String randId = randStr();
                        values3.put(randId, value);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(ClassBuilder.class),
                                "initField",
                                "(ILjava/lang/String;)Ljava/lang/reflect/Field;",
                                false);
                        mv.visitFieldInsn(PUTSTATIC, implPath, fieldName, "Ljava/lang/reflect/Field;");
                    }
                    for (Method method : handledMethod.keySet()) {
                        index = handledMethod.getInt(method);
                        String fieldName = "handle" + index;
                        MethodHandles.Lookup lookup =
                                MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
                        MethodHandle handle = lookup.unreflect(method);
                        String randId = randStr();
                        values1.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(ClassBuilder.class),
                                "initMethodHandle",
                                "(ILjava/lang/String;)Ljava/lang/invoke/MethodHandle;",
                                false);
                        mv.visitFieldInsn(PUTSTATIC, implPath, fieldName, "Ljava/lang/invoke/MethodHandle;");
                    }
                    for (Constructor cons : handledConstructor.keySet()) {
                        index = handledConstructor.getInt(cons);
                        String fieldName = "handle" + index;
                        MethodHandles.Lookup lookup =
                                MethodHandles.privateLookupIn(cons.getDeclaringClass(), MethodHandles.lookup());
                        MethodHandle handle = lookup.unreflectConstructor(cons);
                        String randId = randStr();
                        values1.put(randId, handle);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(ClassBuilder.class),
                                "initMethodHandle",
                                "(ILjava/lang/String;)Ljava/lang/invoke/MethodHandle;",
                                false);
                        mv.visitFieldInsn(PUTSTATIC, implPath, fieldName, "Ljava/lang/invoke/MethodHandle;");
                    }
                    for (Class<?> type : handledTypeInstance.keySet()) {
                        index = handledTypeInstance.getInt(type);
                        String fieldName = "handle" + index;
                        String randId = randStr();
                        values2.put(randId, type);
                        mv.visitLdcInsn(randCode);
                        mv.visitLdcInsn(randId);
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                getInternalName(ClassBuilder.class),
                                "initDelegate",
                                "(ILjava/lang/String;)Ljava/lang/Class;",
                                false);
                        mv.visitFieldInsn(PUTSTATIC, implPath, fieldName, "Ljava/lang/Class;");
                    }
                    String randId = randStr();
                    values2.put(randId, targetClass);
                    mv.visitLdcInsn(randCode);
                    mv.visitLdcInsn(randId);
                    mv.visitMethodInsn(
                            INVOKESTATIC,
                            getInternalName(ClassBuilder.class),
                            "initDelegate",
                            "(ILjava/lang/String;)Ljava/lang/Class;",
                            false);
                    mv.visitFieldInsn(PUTSTATIC, implPath, "delegate", "Ljava/lang/Class;");
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                cw.visitEnd();
                byte[] code = cw.toByteArray();
                CustomClassLoader.getInstance().defineAccessClass(implName, code);
                Class<T> clazz = CustomClassLoader.getInstance().loadAccessClass(implName);
                T val = clazz.getConstructor().newInstance();
                result = val;
            } finally {
                reset0(randCode);
            }
        }

        return result;
    }

    private static boolean canExactInvoke(Method method) {
        if (!canExact(method.getDeclaringClass())) return false;
        for (var arg : method.getParameterTypes()) {
            if (!canExact(arg)) return false;
        }
        return true;
    }

    private static boolean canExact(Class<?> clazz0) {
        if (!Modifier.isPublic(clazz0.getModifiers())) {
            return false;
        }
        if (clazz0.getNestHost() != clazz0) {
            return canExact(clazz0.getNestHost());
        }
        return true;
    }

    private static String randStr() {
        String val;
        do {
            val = UUID.randomUUID().toString();
        } while (stringPool.contains(val));
        stringPool.add(val);
        return val;
    }

    private static final Random rand = new Random();

    private static void reset0(int rand1) {
        Preconditions.checkArgument(rand1 == randCode, "IllegalAccess!");
        values0.clear();
        values1.clear();
        randCode = rand.nextInt(1145141919);
    }

    private static int randCode;
    private static final Set<String> stringPool = new HashSet<>();
    private static final Map<String, VarHandle> values0 = new HashMap<>();
    private static final Map<String, MethodHandle> values1 = new HashMap<>();
    private static final Map<String, Class<?>> values2 = new HashMap<>();
    private static final Map<String, Field> values3 = new HashMap<>();

    public static VarHandle initVarHandle(int code, String val) {
        Preconditions.checkArgument(code == randCode, "IllegalAccess!");
        return Objects.requireNonNull(values0.remove(val));
    }

    public static Field initField(int code, String val) {
        Preconditions.checkArgument(code == randCode, "IllegalAccess!");
        return Objects.requireNonNull(values3.remove(val));
    }

    public static MethodHandle initMethodHandle(int code, String value) {
        Preconditions.checkArgument(code == randCode, "IllegalAccess!");
        return Objects.requireNonNull(values1.remove(value));
    }

    public static Class initDelegate(int code, String value) {
        Preconditions.checkArgument(code == randCode, "IllegalAccess!");
        return values2.remove(value);
    }

    static {
        reset0(randCode);
    }
}
