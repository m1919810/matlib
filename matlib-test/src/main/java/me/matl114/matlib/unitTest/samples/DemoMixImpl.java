package me.matl114.matlib.unitTest.samples;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.MixImpl;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.OverrideMethod;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.ShadowMethod;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.ShadowSuperMethod;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixBase;

@MixImpl(subClass = "me.matl114.matlib.unitTest.samples.DemoBase")
public abstract class DemoMixImpl extends MixBase {
    int flag;
    @ShadowMethod
    @RedirectName("overrideMethodA")
    public abstract void overrideMethodA(@RedirectType("me.matl114.matlib.unitTest.samples.DemoBase") Object c);


    @ShadowSuperMethod
    @RedirectName("overrideMethodA")
    public abstract void overrideMethodASuper(@RedirectType("me.matl114.matlib.unitTest.samples.DemoBase") Object c);

    @ShadowMethod
    @RedirectName("superMethodA")
    public abstract Object superMethodA(int a, double b, @RedirectType("me.matl114.matlib.unitTest.samples.DemoBase") Object c);

    @OverrideMethod
    @RedirectName("overrideMethodA")
    public void overrideMethodAOverride(@RedirectType("me.matl114.matlib.unitTest.samples.DemoBase") Object c){
        if(flag > 5){
            overrideMethodASuper(c);
        }else{
            ((DemoMixImpl)c).superMethodA(3, 5, c);
        }
        ((DemoMixImpl)c).superMethodA(1, 2, this);
    }
}
