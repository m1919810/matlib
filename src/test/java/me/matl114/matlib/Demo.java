package me.matl114.matlib;

import java.util.Arrays;

public class Demo {
    public int publicVar = 1;
    protected int protectedVar = 2;
    private int privateVar = 3;
    public int[] arrayData = new int[] {1, 2, 3};

    @Override
    public String toString() {
        return "Demo{" + "publicVar="
                + publicVar + ", protectedVar="
                + protectedVar + ", privateVar="
                + privateVar + ", arrayData="
                + Arrays.toString(arrayData) + '}';
    }
}
