package me.matl114.matlib.unitTest.samples;

import me.matl114.matlib.utils.Debug;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class DemoFinal implements DemoInterface {
    public static DemoFinal ins(){
        return new DemoFinal();

    }
    static final VarHandle varhandle = null;
    static final MethodHandle methodHandle = null;
    static final Field field1 ;
    static{
        try {
            field1 = DemoFinal.class.getDeclaredField("varhandle");
            field1.set(null, null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    {

        Iterable[] c = new Iterable[111];
        List[] d = (List[]) c;


//        if(Debug.isDebugMod()){
//            a= MixBase.castInsn(getClass(), "Lnet/");
//            a =MixBase.castInsn(a, "Lnet/");
//            a = MixBase.castInsn(a, "Lnet/");
//            a = MixBase.castInsn(a, "Lnet/");
//            a = MixBase.castInsn(a, "Lnet/");
//        }else {
//            a= MixBase.castInsn(getClass(), "Lnet/");
//            if( MixBase.instanceofInsn(a, "Lnet/")){
//                Object debug = MixBase.castInsn(a, "Lnet/");
//                Debug.logger(debug);
//            }
//
//            Debug.logger("Lnet/");
//        }

    }
    public void setA(boolean a){

    }
    public void invokeB(Object a, Object b){

    }
    public void b(DemoFinal s, int t){

    }
    public void shit(){
        Debug.stackTrace(4);
    }
    private boolean getBoolean(){
        Debug.stackTrace(4);
        return true;
    }
//    public Predicate<DemoFinal> getPredicate(){
//        return ;
//    }

    public int assssss= 114514;
    boolean a;
    boolean a4;
    @Override
    public Boolean a(Object instance) throws InvocationTargetException {
        return instance instanceof DemoFinal;
    }
    static{
//        varhandle = DescriptorImplBuilder.initVarHandle(0,"111");
//        methodHandle = DescriptorImplBuilder.initMethodHandle(0,"122");
    }
    public Class  acc1(String a, boolean b){
       return (Class)  varhandle.get(a);

    }
    public void task1(){

    }
//    public int retint(){
//        return 0;
//    }
//    public long retLong(){
//        return 0L;
//    }
//    public double retDouble(){
//        return 0D;
//    }
//    public float retFloat(){
//        return 0F;
//    }
//    public void retVoid(){
//    }

    public int acc2(Object c){
        return (int)varhandle.get(c);
    }
}
