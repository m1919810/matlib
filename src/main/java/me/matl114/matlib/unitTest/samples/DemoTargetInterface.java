package me.matl114.matlib.unitTest.samples;

import me.matl114.matlib.utils.Debug;

public interface DemoTargetInterface {
    static int i = -114;
    public void r();
    default void f(){
        Debug.logger("Interface.f called");
    }
    default void abs(){
        Debug.logger("Interface.abs called");
    }
    default void privateMethod(){
        Debug.logger("Interface.privateMethod called");
    }
}
