package me.matl114.matlib.unitTest.autoTests.reflectionTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.samples.DemoBase;
import me.matl114.matlib.unitTest.samples.DemoDerivative;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.WorldUtils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.*;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.reflect.proxy.ProxyBuilder;
import me.matl114.matlib.utils.reflect.proxy.invocation.AdaptorInvocation;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtMost;
import me.matl114.matlibAdaptor.algorithms.dataStructures.LockFactory;
import me.matl114.matlibAdaptor.algorithms.interfaces.Initialization;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.objectweb.asm.*;

public class ReflectionUtilTests implements TestCase {
    @OnlineTest(name = "CraftUtils VarHandle test")
    public void testDisplayVarHandle() {
        ItemStack item = new CleanItemStack(Material.BOOK, "这是&a一个&c书", "这&e是一本&r书", "这并&6不是两&3本书");
        AddUtils.addGlow(item);
        ItemMeta meta = item.getItemMeta();
        Debug.logger(CraftUtils.getDisplayNameHandle().get(meta));
        Debug.logger(CraftUtils.getLoreHandle().get(meta));
        item = CraftUtils.getCraftCopy(item);
        Debug.logger(item);
        Assert(CraftUtils.isCraftItemStack(item));
        Debug.logger(CraftUtils.getHandled(item));
        ItemMeta meta2 = item.getItemMeta();
        Assert(CraftUtils.matchDisplayNameField(meta, meta2));
        Assert(CraftUtils.matchLoreField(meta, meta2));
        Assert(CraftUtils.matchEnchantmentsFields(meta, meta2));
        Debug.logger(CraftUtils.getEnchantmentsHandle().get(meta2));
        Assert(((Map) CraftUtils.getEnchantmentsHandle().get(meta)).size() == 1);
        Assert(CraftUtils.matchItemStack(item, item, true));
        ItemStack blockStateItem = new CleanItemStack(Material.SPAWNER);
        ItemMeta blockStateMeta = blockStateItem.getItemMeta();
        Assert(blockStateMeta instanceof BlockStateMeta);
        BlockStateMeta blockState = (BlockStateMeta) blockStateMeta;
        BlockState blockStateThis = blockState.getBlockState();
        Assert(blockStateThis instanceof CreatureSpawner);
        CreatureSpawner spawner = (CreatureSpawner) blockStateThis;
        spawner.setSpawnedType(EntityType.ZOMBIE);
        spawner.setSpawnRange(114);
        blockState.setBlockState(spawner);
        Assert(CraftUtils.matchBlockStateMetaField(blockState, blockState));
        Debug.logger(blockState.getClass(), blockState.getClass().getClassLoader(), ClassLoader.getSystemClassLoader());
    }

    @OnlineTest(name = "CraftUtils Invoker test")
    public void testCraftInvoker() {
        ItemStack item = new CleanItemStack(Material.BOOK);
        ItemStack citem = CraftUtils.getCraftCopy(item);
        Debug.logger(citem);
        Debug.logger(citem.getClass());
        //        watch me.matl114.matlib.UnitTest.AutoTests.ReflectionUtilTests testCraftInvoker
        // '{params,returnObj,throwExp}'  -n 5  -x 3
        //        watch net.minecraft.server.network.PlayerConnection a '{params}' -n 5
        Assert(CraftUtils.isCraftItemStack(citem));
        Object nmsItem = CraftUtils.getNMSCopy(item);
        Debug.logger(nmsItem);
        Debug.logger(nmsItem.getClass());
        Debug.logger(nmsItem.getClass().getClassLoader());
        Assert(CraftUtils.isNMSItemStack(nmsItem));
        Debug.logger("Test Success");
    }

    //    @OnlineTest(name = "Paper Obf utils test")
    //    public void test_paperobf() throws Throwable{
    //        Class obfClass = Class.forName("io.papermc.paper.util.ObfHelper");
    //        var instance =  Enum.valueOf(obfClass, "INSTANCE");
    //        Map<String,?> re = (Map<String, ?>)
    // FieldAccess.ofName(obfClass,"mappingsByObfName").ofAccess(instance).getRaw();
    //        //Debug.logger(re);
    //        Debug.logger(re.size());
    //        Object classMapper = re.get( WorldUtils.getTileEntityClass().getName());
    //        Debug.logger(classMapper.getClass());
    //        Debug.logger(ReflectUtils.getAllFieldsRecursively(classMapper.getClass()));
    //        //Debug.logger( classMapper);
    //        String des = ByteCodeUtils.getMethodDescriptor(
    // WorldUtils.getTileEntitySetChangeAccess().getMethodOrDefault(()->null));
    //        Debug.logger(des);
    //        FieldAccess methodObf = FieldAccess.ofName(classMapper.getClass(),"methodsByObf");
    //        Debug.logger( ((Map<String,?>)(methodObf.getValue(classMapper))).get(des));
    //        var recls = Class.forName("io.papermc.paper.util.ObfHelper$ClassMapping");
    //        Debug.logger(recls);
    //        Assert(recls == classMapper.getClass());
    //        Debug.logger((Object[]) recls.getDeclaredConstructors());
    //        Debug.logger(ReflectUtils.getAllFieldsRecursively(recls));
    //        Debug.logger(ReflectUtils.getAllMethodsRecursively(recls));
    //        Debug.logger(recls.getDeclaredConstructors()[0].newInstance("a","b",Map.of()));
    //    }
    @OnlineTest(name = "MatlibAdaptor Test")
    public void testAPI() throws Throwable {
        disableTest();
        Class logiTech = Class.forName("me.matl114.logitech.MyAddon");
        Debug.logger(logiTech.getName());
        FieldAccess initAccess = FieldAccess.ofName(logiTech, "matlibInstance");
        Object instance = initAccess.initWithNull().getValue(null);
        Debug.logger(instance.getClass().getName());
        Debug.logger(instance instanceof Initialization);
        Initialization init = ProxyBuilder.buildMatlibAdaptorOf(
                Initialization.class, instance, (set) -> AdaptorInvocation.createASM(instance.getClass(), set));

        long start = System.nanoTime();
        String value = null;
        for (int i = 0; i < 1_000; ++i) {
            value = init.getDisplayName();
        }
        long end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 invocation", end - start, value);
        Method method = instance.getClass().getMethod("getDisplayName");
        start = System.nanoTime();
        value = null;
        for (int i = 0; i < 1_000; ++i) {
            method.invoke(instance);
        }
        end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 reflection", end - start, value);

        // DO NOT CALL METHOD WITH OUR CLASS RETURN VALUE ,OTHERWISE CLASS CAST EXCEPTION WILL OCCURS
        Debug.logger(init.isTestMode());
        Object access =
                MethodAccess.reflect("getCargoLockFactory", Slimefun.class).invoke(null);
        Debug.logger(access);
        LockFactory<Location> locationLockFactory = ProxyBuilder.buildMatlibAdaptorOf(
                LockFactory.class, access, (set) -> AdaptorInvocation.createASM(access.getClass(), set));

        Debug.logger(locationLockFactory.checkThreadStatus(new Location(testWorld(), 0, 0, 0)));
    }

    @OnlineTest(name = "ASM Test")
    public void testASM() throws Throwable {
        Class<?> clazz = Class.forName("io.papermc.paper.util.ObfHelper");
        Debug.logger(clazz);
        String classResourcePath = clazz.getName().replace('.', '/') + ".class";
        ClassReader reader = new ClassReader(clazz.getClassLoader().getResourceAsStream(classResourcePath));
        // starting our class
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        String generatedName =
                DemoBase.class.getName().replace("DemoBase", "DemoDerivative" + new Random().nextInt(336));
        cw.visit(
                reader.readShort(6),
                Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN | Opcodes.ACC_SUPER,
                generatedName.replace(".", "/"),
                null, // 泛型签名
                DemoBase.class.getName().replace(".", "/"), // 父类
                null // 接口
                );

        var methodVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        {
            // 若没有构造函数必须显示提供构造函数
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL, DemoBase.class.getName().replace(".", "/"), "<init>", "()V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            // 即使自动计算也得写
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }

        ClassVisitor writer = new MyClassVisitor(
                589824, cw, "loadMappingsIfPresent", DemoBase.class.getName().replace(".", "/"));
        //        String val0 = new String( writer.toByteArray() );
        //        Debug.logger("before accept");
        //        Debug.logger(val0);
        reader.accept(writer, 0);
        // important
        cw.visitEnd();
        Debug.logger("end");

        byte[] bytecode = cw.toByteArray();
        var re = CustomClassLoader.getInstance().defineAccessClass(generatedName, bytecode);
        Debug.logger(re);
        Debug.logger("start invocation test");
        Assert(re != DemoDerivative.class);
        Object inst = re.getConstructor().newInstance();
        Debug.logger(inst);
        if (Version.getVersionInstance().isAtLeast(Version.v1_20_R4)) {
            return;
        }
        Method newDefined = re.getDeclaredMethod("loadMappingsIfPresent");
        newDefined.setAccessible(true);
        Debug.logger(newDefined);
        newDefined.invoke(inst);
        // MethodVisitor method =

    }

    @OnlineTest(name = "ObfManager Test")
    public void testObfManager() throws Throwable {
        var helper = ObfManager.getManager();

        String obfClass = WorldUtils.getTileEntityClass().getName();
        String mojClass = helper.deobfClassName(obfClass);
        AssertEq(mojClass, "net.minecraft.world.level.block.entity.BlockEntity");
        String des = ByteCodeUtils.getMethodDescriptor(WorldUtils.getTileEntitySetChangeAccess());
        AssertEq(helper.deobfMethodInClass(mojClass, des), "setChanged");
    }
    //    public static class DynamicClassLoader extends ClassLoader{
    //        public Class<?> defineClass0(ClassLoader other ,String className, byte[] bytecode){
    //            return this.defineClass(className, bytecode, 0, bytecode.length);
    //        }
    //        public static Class<?> loadClass0(ClassLoader loader, String className, byte[] bytecode){
    //
    //        }
    //    }

    public static class MyClassVisitor extends ClassVisitor {
        ClassWriter target;
        String targetMethod;
        String baseClass;

        public MyClassVisitor(int api, ClassWriter writer, String targetMethod, String baseClass) {
            super(api);
            this.target = writer;
            this.targetMethod = targetMethod;
            this.baseClass = baseClass;
        }

        @Override
        public MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
            //            Debug.logger("visiting method ", access, name, descriptor, signature);
            if (!targetMethod.equals(name)) {
                // return super.visitMethod(access, name, descriptor, signature, exceptions);
                // ignore other method
                return null;
            }
            // create a method writer for target ClassWriter
            // changing access from private static to public and not static
            var re = target.visitMethod(
                    (access & (~Opcodes.ACC_STATIC) & (~Opcodes.ACC_PRIVATE)) | Opcodes.ACC_PUBLIC,
                    name,
                    descriptor,
                    signature,
                    exceptions);
            // do injections using default constructor of method Visitor
            return new MethodVisitor(Opcodes.ASM9, re) {
                // MethodVisitor这个构造器中的行为是每个方法都顺次的调用传入mv的对应方法（大致
                // 我们传入构造的mv拥有构造字节码的能力
                // 所以我们如果想在对应的地方插入字节码， 我们可以覆写指定方法,增加判定条件和增加对mv的字节码处理
                // 更多的，我们通过super顺次调用被拦截的mv,实际上我们可以通过嵌套实现一个带优先级的插入
                // 我们可以通过覆盖方法
                @Override
                public void visitCode() {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, baseClass, "start", "()V", false);
                    super.visitCode();
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN || opcode == Opcodes.ARETURN) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, baseClass, "end", "()V", false);
                    }
                    super.visitInsn(opcode);
                }

                @VersionAtMost(Version.v1_20_R4)
                public String replaceClassPath(String classPath) {
                    if ("io/papermc/paper/util/ObfHelper$StringPool".equals(classPath)) {
                        classPath = StringPool.class.getName().replace(".", "/");
                    } else if ("Lio/papermc/paper/util/ObfHelper$StringPool".equals(classPath)) {
                        classPath = "L" + StringPool.class.getName().replace(".", "/");
                    } else if ("net/fabricmc/mappingio/tree/MappingTree$MethodMapping".equals(classPath)) {
                        classPath = "net/fabricmc/mappingio/tree/MappingTree$FieldMapping";
                    } else if ("Lnet/fabricmc/mappingio/tree/MappingTree$MethodMapping".equals(classPath)) {
                        classPath = "Lnet/fabricmc/mappingio/tree/MappingTree$FieldMapping";
                    }
                    return classPath;
                }

                public void replaceClassPath(Object[] vars) {
                    for (int i = 0; i < vars.length; ++i) {
                        if (vars[i] instanceof String cls) {
                            vars[i] = replaceClassPath(cls);
                        }
                    }
                }

                public void visitTypeInsn(int code, String classPath) {
                    classPath = replaceClassPath(classPath);
                    super.visitTypeInsn(code, classPath);
                    //
                }

                public void visitMethodInsn(int code, String a, String b, String c, boolean fa) {
                    a = replaceClassPath(a);
                    if (b.equals("getMethods")) {
                        b = "getFields";
                    }
                    super.visitMethodInsn(code, a, b, c, fa);
                }

                public void visitFrame(int code, int a, Object[] b, int c, Object[] d) {
                    replaceClassPath(b);
                    replaceClassPath(d);
                    super.visitFrame(code, a, b, c, d);
                }

                public void visitVarInsn(int opcode, int index) {
                    // 留出index = 0从static转非static的，
                    super.visitVarInsn(opcode, index + 1);
                }

                @Override
                public void visitLocalVariable(
                        String name, String descriptor, String signature, Label start, Label end, int index) {
                    // 留出index= 0 因为是非static方法，需要给this留地方
                    super.visitLocalVariable(name, replaceClassPath(descriptor), signature, start, end, index + 1);
                }
            };
        }
    }

    public static class MyMethodVisitor extends MethodVisitor {
        public MyMethodVisitor(int api) {
            super(api);
        }
    }

    public static final class StringPool {
        private final Map<String, String> pool = new HashMap<>();

        public String string(final String string) {
            return this.pool.computeIfAbsent(string, Function.identity());
        }
    }
}
