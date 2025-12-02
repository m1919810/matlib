package me.matl114.matlib.unitTest.autoTests.reflectionTests;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.unitTest.samples.DemoLoad;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.reflect.proxy.ProxyBuilder;
import me.matl114.matlib.utils.reflect.proxy.invocation.SimpleRemappingInvocation;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import me.matl114.matlibAdaptor.algorithms.dataStructures.LockFactory;
import me.matl114.matlibAdaptor.algorithms.interfaces.Initialization;
import org.bukkit.Location;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class ProxyUtilTests implements TestCase {
    @OnlineTest(name = "Proxy descriptor test")
    public void test_proxy()throws Throwable{
        DemoProxy handle = DescriptorProxyBuilder.createHelperImpl(DemoProxy.class);
        Debug.logger(handle);
        Debug.logger(handle.getClass());
        Class cls = DemoLoad.initDemo();
        var constructor = cls.getConstructor();
        constructor.setAccessible(true);
        Object val = constructor.newInstance();
        handle.a(val);
        handle.notComplete(val, true);
        Debug.logger(ReflectUtils.getAllFieldsRecursively(handle.getClass()));
    }
    @OnlineTest(name = "Proxy builder test")
    public void test_simple_proxy()throws Throwable{
        Class logiTech = Class.forName("me.matl114.logitech.MyAddon");
        Debug.logger(logiTech.getName());
        FieldAccess initAccess = FieldAccess.ofName(logiTech,"matlibInstance");
        Object instance= initAccess.initWithNull().getValue(null);
        Debug.logger(instance.getClass().getName());
        Debug.logger(instance instanceof Initialization);
        Initialization init = ProxyBuilder.buildMatlibAdaptorOf(Initialization.class, instance, SimpleRemappingInvocation::new);
        long start = System.nanoTime();
        String value = null;
        for (int i=0;i<1_000;++i){
            value = init.getDisplayName();
        }
        long end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 invocation",end-start,value);
        Method method = instance.getClass().getMethod("getDisplayName");
        start = System.nanoTime();
        value = null;
        for (int i=0;i<1_000;++i){
            method.invoke(instance);
        }
        end = System.nanoTime();
        Debug.logger("time cost for 1_000_000 reflection",end-start,value);

        //DO NOT CALL METHOD WITH OUR CLASS RETURN VALUE ,OTHERWISE CLASS CAST EXCEPTION WILL OCCURS
        Debug.logger(init.isTestMode());
        Object access = MethodAccess.reflect("getCargoLockFactory", Slimefun.class)
            .invoke(null);
        Debug.logger(access);
        LockFactory<Location> locationLockFactory = ProxyBuilder.buildMatlibAdaptorOf(LockFactory.class, access,SimpleRemappingInvocation::new);
        Debug.logger(locationLockFactory.checkThreadStatus(new Location(testWorld(),0,0,0)));
    }

    @OnlineTest(name = "Proxy module test")
    public void test_proxyLoader() throws Throwable {
        //DID NOT WORK！
        // proxy itf should be visible from loader
//        ClassLoader loader = ProxyUtilTests.class.getClassLoader().getParent();
//        Class cls = DemoLoad.initDemo();
//        var constructor = cls.getConstructor();
//        constructor.setAccessible(true);
//        Object val = constructor.newInstance();
//        DemoProxy test = DescriptorProxyBuilder.createSingleInternel(cls, DemoProxy.class, loader);
//        Debug.logger(test.getClass().getClassLoader());
//        Debug.logger();
    }

    @Descriptive(target = "me.matl114.matlib.unitTest.demo.DemoTargetClass")
    public static interface DemoProxy extends TargetDescriptor {
        @MethodTarget
        void a(Object target);

        @MethodTarget
        default void notComplete(Object target, boolean val){
            Debug.logger("Default notComplete called",val);
            try{
                MethodHandles.Lookup lookup1 = MethodHandles.privateLookupIn(ObfManager.getManager().reobfClass("net.minecraft.world.item.ItemStack"),MethodHandles.lookup());
                Debug.logger( lookup1.hasFullPrivilegeAccess());
            }catch (Throwable e){
            }

        }
    }
}
