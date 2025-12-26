package me.matl114.matlib.utils.reflect.classBuild;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.FailHard;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.internel.MethodNotCompleteError;
import me.matl114.matlib.utils.version.Version;

public class ClassBuildingUtils {
    public static boolean processFailure(Method method, Class<?> main) {
        var re0 = main.getAnnotation(IgnoreFailure.class);
        if (re0 != null) {
            var version = re0.thresholdInclude();
            boolean below = re0.below();
            if (Version.getVersionInstance().isAtLeast(version) != below) {
                return true;
            }
        }
        var re1 = method.getAnnotation(IgnoreFailure.class);
        if (re1 != null) {
            var version = re1.thresholdInclude();
            boolean below = re1.below();
            if (Version.getVersionInstance().isAtLeast(version) != below) {
                return true;
            }
        }
        var re2 = main.getAnnotation(FailHard.class);
        if (re2 != null) {
            var version = re2.thresholdInclude();
            boolean below = re2.below();
            if (Version.getVersionInstance().isAtLeast(version) != below) {
                throw new MethodNotCompleteError("Fail Hard in class " + main + " at method " + method);
            }
        }
        var re3 = method.getAnnotation(FailHard.class);
        if (re3 != null) {
            var version = re3.thresholdInclude();
            boolean below = re3.below();
            if (Version.getVersionInstance().isAtLeast(version) != below) {
                throw new MethodNotCompleteError("Fail Hard in class " + main + " at method " + method);
            }
        }
        return false;
    }

    /**
     * remove default impl, print absent, or fail hard
     * @param uncompletedMethod
     * @param main
     */
    public static void checkUncompleted(List<Method> uncompletedMethod, Class<?> main) {
        uncompletedMethod.removeIf(m -> {
            if (!Modifier.isAbstract(m.getModifiers())) {
                if (!processFailure(m, main)) {
                    Debug.warn("Target absent for method:", m, ",fallback to its default Impl!");
                }
                return true;
            } else {
                if (!processFailure(m, main)) {
                    Debug.warn("Target absent for method", m);
                }
            }
            return false;
        });
    }
}
