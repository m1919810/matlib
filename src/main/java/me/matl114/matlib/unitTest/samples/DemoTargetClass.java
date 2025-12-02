package me.matl114.matlib.unitTest.samples;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import me.matl114.matlib.utils.Debug;

@Accessors(chain = true,fluent = true)
public class DemoTargetClass extends DemoTargetSuper implements DemoTargetInterface{
    public DemoTargetClass(){
        super();
        Debug.logger("Instance create");
        b =-13;
    }
    DemoTargetClass(int a){
        super();
        Debug.logger("Private Instance create:",a);
        b =-13;
        val s = 1;

        //setTest("wc").setTest("lombok").setTest("真的").setTest("牛皮").setTest("桀桀桀");
        test("wc").test("还能蒸").test("六百六十六");
    }
    @Setter
    private String test;

    private int a =114;
    public final int b;
    public long c =514;
    public static double d;
    public Object e;
    public String f;
    public void a(){
        Debug.logger("Target.a called");
    }
    private void b(){
        Debug.logger("Target.b called");
    }
    public static void c(){
        Debug.logger("Target.c called");
    }
    public Object d(){
        Debug.logger("Target.d called");
        return new byte[0];
    }
    public Object e() throws Throwable {
        return null;
    }
    static{
        Debug.logger("Demo Target Class Init");
    }
//    public void privateMethod(){
//        Debug.logger("Super.privateMethod called");
//    }

    public void testMethod(){

        ((DemoTargetInterface)this).privateMethod();
        //DemoTargetClass.super.privateMethod();
        DemoTargetInterface.super.privateMethod();
        try{
            privateMethod();
        }catch (Throwable e){

        }
    }



    @Override
    public void abs() {
        DemoTargetInterface.super.abs();
        super.abs();
        Debug.logger("Target.abs called");
    }
//    public void f(){
//        Debug.logger("Target.f called");
//    }
}
