package me.matl114.matlib.unitTest.autoTests.reflectionTests;

import static org.objectweb.asm.Opcodes.*;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.samples.DemoTargetClass;
import me.matl114.matlib.unitTest.samples.DemoTargetInterface;
import me.matl114.matlib.unitTest.samples.DemoTargetSuper;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.asm.DebugClassReader;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.objectweb.asm.*;

public class ASMUtilsTests implements TestCase {
    volatile int tmp = 0;

    @OnlineTest(name = "Descriptor Build Test")
    public void test_descriptor() throws Throwable {
        Class clazz = Class.forName("me.matl114.matlib.unitTest.samples.DemoTargetClass");
        Debug.logger("fetched");
        DemoDescriptor I = DescriptorImplBuilder.createHelperImpl(DemoDescriptor.class);
        Debug.logger(I);
        I.c();
        Debug.logger(I.dSetter(333));
        Debug.logger(I.newInstance());
        Debug.logger(I.newInstance(114));
        DemoTargetClass targetClass = new DemoTargetClass();
        Debug.logger(targetClass.b);
        I.bSetter(targetClass, 114514);
        Assert(targetClass.b == 114514);
        //        Assert(int.class.isAssignableFrom(int.class));
        //        Object t = new DemoTargetClass();
        //        DemoTargetClass answer = (DemoTargetClass) t;
        //        DemoDescriptor I = DescriptorImplBuilder.createHelperImplAt(t.getClass(), DemoDescriptor.class);
        //        Debug.logger(I);
        //        Debug.logger(I.newInstance(114));
        //
        //        Debug.logger("Starting invocation tests");
        //        Assert(I.getTargetClass() == DemoTargetClass.class);
        //        //fields
        //        Assert(I.aGetter(t) == 114);
        //        I.bSetter(t, 10);
        //        Assert(answer.b == 10);
        //        Assert(I.cccGetter(t) == 514);
        //        I.dSetter(Float.valueOf(3.15f));
        //        Assert(DemoTargetClass.d == 3.15f);
        //        answer.e = answer;
        //        Assert(I.eGetter(t) == t);
        //        I.fSetter(t, "cnmd");
        //        AssertEq("cnmd",answer.f);
        //        AssertEq(I.gGetter(t) ,"www");
        //        AssertEq(I.hGetter(t), "http");
        //        Assert(I.iGetter() == -114);
        //        //methods
        //        Assert(I.a(t) == 0);
        //        I.b(t);
        //        I.c();
        //        Assert(I.d(t).length == 0);
        //        Assert(I.e(t) == null);
        //        I.f(t);
        //        I.g(t);
        //        I.newInstance();
        //        int start = new Random().nextInt();
        //        //test invocation cost;
        //        long a = System.nanoTime();
        //        for (int i=0; i< 1_000_000; ++i){
        //            I.bSetter(t, i + start);
        //            //using a volatile field to stop it from optimizing for-loop
        //            tmp = i;
        //        }
        //        long b = System.nanoTime();
        //        Assert(answer.b  == 999_999+ start);
        //        Debug.logger("Field set cost",b-a);
        //        a = System.nanoTime();
        //        for (int i=0; i< 1_000_000; ++i){
        //            answer.b = i + start;
        //            tmp = i;
        //        }
        //        b = System.nanoTime();
        //        Assert(answer.b == 999_999 + start);
        //        Debug.logger("Field direct set cost",b-a);
        //        a = System.nanoTime();
        //        for (int i=0; i<1_000_000; ++i){
        //            I.aGetter(t);
        //            tmp = i;
        //        }
        //        b = System.nanoTime();
        //        Debug.logger("Private field get cost ",b-a);
        //        Debug.logger(ReflectUtils.getAllFieldsRecursively(I.getClass()).get(2).get(null).getClass());
        //        Field f = this.getClass().getDeclaredField("first");
        //        VarHandle handle = MethodHandles.privateLookupIn(this.getClass(),
        // MethodHandles.lookup()).unreflectVarHandle(f);
        //        Debug.logger(handle.getClass());
        //        Debug.logger(handle.withInvokeExactBehavior().getClass());
    }

    private boolean first;

    @OnlineTest(name = "test class building")
    public void test_classbuilder() throws Throwable {
        // shit it fails
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(
                Opcodes.V21,
                ACC_PUBLIC,
            "me/matl114/matlib/unitTest/samples/114514DemoImpl",
                null,
                Type.getInternalName(DemoTargetSuper.class),
                new String[] {Type.getInternalName(DemoTargetInterface.class)});
        String parentCls = Type.getInternalName(DemoTargetSuper.class);
        var methodVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        {
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(
                    INVOKESPECIAL, Type.getInternalName(DemoTargetSuper.class), "<init>", "()V", false);
            //            methodVisitor.visitMethodInsn(
            //                Opcodes.INVOKESPECIAL,
            //                parentCls == null?"java/lang/Object":parentCls.replace(".","/"),
            //                "<init>",
            //                "()V",
            //                false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        methodVisitor = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "invokeTest",
                "(Lme/matl114/matlib/unitTest/samples/DemoTargetInterface;)V",
                null,
                null);

        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL, "me/matl114/matlib/unitTest/samples/DemoTargetInterface", "r", "()V", false);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        //        mv = cw.visitMethod(ACC_PUBLIC, "test","()"+ ByteCodeUtils.toJvmType(DemoTargetSuper.class), null,
        // null);
        //        mv.visitCode();
        //        mv.visitTypeInsn(NEW,"me/matl114/matlib/unitTest/demo/DemoImpl");
        //        mv.visitInsn(ARETURN);
        //        mv.visitMaxs(0,0);
        //        mv.visitEnd();
        cw.visitEnd();
        byte[] cls = cw.toByteArray();
        var cl = CustomClassLoader.getInstance()
                .defineAccessClass("me.matl114.matlib.unitTest.samples.114514DemoImpl", cls);
        Object val = cl.getConstructor().newInstance();
        Debug.logger(val.getClass());
        Debug.logger(val);
        ((DemoTargetInterface) val).abs();
        ((DemoTargetSuper) val).invokeTest(new DemoTargetClass());
    }

    @OnlineTest(name = "test class debug")
    public void test_classdebug() throws Throwable {
        Debug.logger(SlimefunUtils.class);
        String path = SlimefunUtils.class.getName().replace(".", "/") + ".class";
        ClassReader reader =
                new ClassReader(SlimefunUtils.class.getClassLoader().getResourceAsStream(path));
        ClassVisitor debugger = (ClassVisitor) new DebugClassReader(ASM9);
        reader.accept(debugger, 0);
        for (var node : ((DebugClassReader)debugger).getMethodInfo().entrySet()) {
            //            Debug.logger("In method",node.getKey());
            DebugClassReader.printInfo(node.getValue().instructions, (str) -> {
                // Debug.logger(str);
            });
        }
    }

    public static String bytecodeToString(byte[] bytecode) {
        return null;
    }

    @Descriptive(target = "me.matl114.matlib.unitTest.samples.DemoTargetClass")
    public static interface DemoDescriptor extends TargetDescriptor {
        // field tests
        @Note("test get, test RedirectType")
        @FieldTarget
        @RedirectType("I")
        public int aGetter(Object v);

        @Note("test set")
        @FieldTarget
        public void bSetter(Object v, int val);

        @Note("test more primitive, test redirect name")
        @FieldTarget
        @RedirectName("cGetter")
        public long cccGetter(Object v);

        @Note("test object default return, test primitive cast, test static fields")
        @FieldTarget(isStatic = true)
        public Object dSetter(float v);

        @Note("test Object return value")
        @FieldTarget
        public Object eGetter(Object v);

        @Note("test int default return, test value cast")
        @FieldTarget
        public int fSetter(Object v, Object str);

        @Note("test super field")
        @FieldTarget
        public String gGetter(Object v);

        @Note("test super static field, test argument passed")
        @FieldTarget(isStatic = true)
        public String hGetter(Object v);

        @Note("test interface static field")
        @FieldTarget(isStatic = true)
        public int iGetter();
        // method tests

        @Note("test method, test type match")
        @MethodTarget
        @RedirectType("V")
        public int a(Object v);

        @Note("test private method")
        @MethodTarget
        public void b(Object v);

        @Note("test static method")
        @MethodTarget(isStatic = true)
        public void c();

        @Note("test return value cast")
        @MethodTarget
        @RedirectType("Ljava/lang/Object")
        public byte[] d(Object v);

        @Note("test thrown error")
        @MethodTarget
        public Object e(Object v);

        @Note("test interface default method")
        @MethodTarget
        public void f(Object e);

        @Note("test super method")
        @MethodTarget
        public void g(Object g);

        // constructor tests;
        @ConstructorTarget()
        public Object newInstance();

        @ConstructorTarget
        public Object newInstance(int x);
    }
}
