package me.matl114.matlib.unitTest.samples;

import java.util.Optional;
import java.util.function.Function;

public interface DemoFunctional {
    Optional mapTo(String val);

    static DemoFunctional fromFunction(Function<String, Optional> val){
        return val::apply;
    }
}
