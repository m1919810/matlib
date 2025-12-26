package me.matl114.matlib.unitTest.autoTests.reflectionTests;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeSafeProvider;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.samples.DemoFinal;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DynamicCodeTests implements TestCase {
    MethodHandle methodHandle = new InitializeSafeProvider<>(() -> {
                return MethodHandles.privateLookupIn(DemoFinal.class, MethodHandles.lookup())
                        .unreflect(DemoFinal.class.getDeclaredMethod("getBoolean"));
            })
            .v();

    @OnlineTest(name = "lambda factory tests")
    public void test_lambdafactory() throws Throwable {
        var testObj = new DemoFinal();
        Runnable task = testObj::shit;
        task.run();
        Method method = DemoFinal.class.getDeclaredMethod("shit");
        Function<DemoFinal, Runnable> lambdaBinder = LambdaUtils.createLambdaBinding(Runnable.class, method);
        //        CallSite callSite = LambdaMetafactory.metafactory(MethodHandles.lookup(), "run",
        // MethodType.methodType(Runnable.class,DemoFinal.class), MethodType.methodType(void.class),
        // MethodHandles.lookup().unreflect(method), MethodType.methodType(void.class));
        Debug.logger("test man-made lambda");
        Runnable meTask = lambdaBinder.apply(testObj);
        Debug.logger(meTask.getClass());
        meTask.run();
        Assert((boolean) methodHandle.invokeExact(testObj));
        Method method1 = DemoFinal.class.getDeclaredMethod("getBoolean");
        //        CallSite callSite1 = LambdaMetafactory.metafactory(MethodHandles.privateLookupIn(DemoFinal.class,
        // MethodHandles.lookup()), "getAsBoolean", MethodType.methodType(BooleanSupplier.class, DemoFinal.class),
        // MethodType.methodType(boolean.class),MethodHandles.privateLookupIn(DemoFinal.class,
        // MethodHandles.lookup()).unreflect(method1), MethodType.methodType(boolean.class));
        //        Debug.logger("test private lambda access");
        Function<DemoFinal, BooleanSupplier> lambdaBinder2 =
                LambdaUtils.createLambdaBinding(BooleanSupplier.class, method1);
        BooleanSupplier supplier = lambdaBinder2.apply(testObj);

        Assert(supplier.getAsBoolean());
        Debug.logger(
                supplier,
                supplier.getClass().getClassLoader(),
                supplier.getClass().getPackage());
        Debug.logger(
                ReflectUtils.getAllFieldsRecursively(supplier.getClass()),
                ReflectUtils.getAllMethodsRecursively(supplier.getClass()));
        Debug.logger("test static lambda");

        Supplier<DemoFinal> lambda3 = (Supplier<DemoFinal>)
                LambdaUtils.createLambdaForStaticMethod(Supplier.class, DemoFinal.class.getDeclaredMethod("ins"));
        AssertNN(lambda3.get());
        Debug.logger("test dynamic binding");
        Predicate<DemoFinal> lambda4 = (Predicate<DemoFinal>)
                LambdaUtils.createLambdaForMethod(Predicate.class, DemoFinal.class.getDeclaredMethod("getBoolean"));
        Assert(lambda4.test(testObj));
        Class<?> testClass = ObfManager.getManager().reobfClass("net.minecraft.world.item.ItemStack");
        var module = testClass.getModule();
        //        Debug.logger(module);
        //        Debug.logger(module.isOpen(testClass.getPackageName(), this.getClass().getModule()));
        //        Method methodPackaged =testClass.getDeclaredMethod("nextEntityId");
        Method methodPackaged = ReflectUtils.getAllMethodsRecursively(testClass).stream()
                .filter(s -> s.getReturnType() == boolean.class)
                .filter(s -> s.getParameterCount() == 0)
                .filter(s -> ObfManager.getManager().deobfMethod(s).equals("isEmpty"))
                .findAny()
                .orElseThrow();
        Object handle = ItemUtils.unwrapHandle(new ItemStack(Material.STONE));
        BooleanSupplier nextId = LambdaUtils.createLambdaBinding(BooleanSupplier.class, methodPackaged)
                .apply(handle);
        Debug.logger(
                nextId.getClass().getModule(),
                testClass.getModule(),
                nextId.getClass().getModule() == testClass.getModule(),
                nextId.getClass().getModule() == this.getClass().getModule());
        Assert(!nextId.getAsBoolean());
        Debug.logger("check code");

        // need test: lambda build for fields
        // make lambda factory util
        //        Debug.logger(new String(new ClassReader()));
    }
}
