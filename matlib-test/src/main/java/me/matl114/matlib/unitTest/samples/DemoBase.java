package me.matl114.matlib.unitTest.samples;

import com.google.common.base.Preconditions;
import me.matl114.matlib.utils.Debug;

public class DemoBase {
    public void start() {
        Debug.logger("start demo");
    }

    public void end() {
        Debug.logger("end demo");
    }

    public DemoBase superMethodA(int a, double b, DemoBase c) {
        Debug.logger("Super method A called", a, b);
        return this;
    }

    public void overrideMethodA(DemoBase c) {
        Debug.logger("Override method A super part called");
        this.superMethodA(1, 2, c);
    }

    public DemoBase() {
        Debug.logger("DemoBase constructor called");
    }

    public DemoBase(DemoBase c) {
        Preconditions.checkArgument(c == null);
        Debug.logger("DemoBase constructor called with argument");
    }
}
