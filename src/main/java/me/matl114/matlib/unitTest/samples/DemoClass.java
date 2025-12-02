package me.matl114.matlib.unitTest.samples;

public class DemoClass {
    public int a;
    public Integer a1;
    private String b;
    public String b1;
    protected Object c;
    public static final String aaa = "111";
    public static final Object bbb = null;
    public void a(){

    }
    public int b(){
        return -1;
    }
    public void c(String a){
        System.out.println(a);
    }
    public boolean d(boolean a){
        final int w = 336;
        if(a){
            a = false;
            a();
        }else {
            c( ""+ b.length() + w);
        }
        return a;
    }
}
