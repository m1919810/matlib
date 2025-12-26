package me.matl114.matlib;

import lombok.Getter;

public class TestClass {
    public Object a;
    public Object b;
    public static Object c;
    public int ttt = 1;

    @Getter
    public Object d = null;

    @Getter
    private static Object e;

    public static final Object f = new Object();

    @Getter
    public static final Object s = 1;

    public String m1() {
        System.out.println("method 1 called");
        return "6b";
    }

    public String m2(String m2) {
        System.out.println("method 2 called");
        System.out.println("message " + m2);
        return "6b";
    }

    private void m3() {
        System.out.println("method 3 called");
    }

    public static void m4() {
        System.out.println("method 4 called");
    }

    private static void m5() {
        System.out.println("method 5 called");
    }

    public static void m6(String m6) {
        System.out.println("method 6 called");
        System.out.println("message " + m6);
    }

    public static void m6(Object m6) {
        System.out.println("method 6 called");
        System.out.println("message " + m6);
    }

    public static String m7(Object m6) {
        System.out.println("method 7 called");
        System.out.println("message " + m6);
        return m6.toString();
    }

    public static void m8(Object m6) {
        System.out.println("method 8 called");
        System.out.println("message " + m6);
        return;
    }
}
