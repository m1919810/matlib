package me.matl114.matlib.unitTest.samples;

import me.matl114.matlib.utils.Debug;

public class DemoTargetSuper {
    public DemoTargetSuper(){
        Debug.logger("Super.init called");
    }
    private final String g = "www";
    private static final String h = "http";
    public void g(){
        Debug.logger("Super.g called");
    }
    private void privateMethod(){

    }
    public void abs(){
        Debug.logger("Super.abs called");
    }
    public void invokeTest(DemoTargetInterface itf){
        Debug.logger("Super.invokeTest called");
    }

    public void r() {
        Debug.logger("Super.r called");
    }
}
