package me.matl114.matlib.utils.reflect.classBuild;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.FailHard;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.classBuild.internel.MethodNotCompleteError;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorBuildException;
import me.matl114.matlib.utils.reflect.internal.SimpleObfManager;
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

    public static List<Method> matchMethods(
            Method methodAccess,
            List<Method> methods,
            boolean static1,
            @Note("this determines whether to consider the first param as 'this'") boolean selfAsParam) {
        String targetName;
        var redirect1 = methodAccess.getAnnotation(RedirectName.class);
        if (redirect1 != null) {
            targetName = redirect1.value();
        } else {
            targetName = methodAccess.getName();
        }
        boolean shouldDebug = false;
        //        if(targetName.equals("createSerializationContext")){
        //            shouldDebug = true;
        //        }
        if (shouldDebug) {
            Debug.logger("matching methods", methodAccess, "from", methods);
        }
        // what returns does not matter
        //            String returnType;
        //            if(redirect2 != null){
        //                returnType = ObfManager.getManager().reobfClassName( redirect2.value() );
        //            }else {
        //                returnType = ByteCodeUtils.toJvmType( methodAccess.getReturnType() );
        //            }
        var arguCount =
                static1 ? methodAccess.getParameterCount() : methodAccess.getParameterCount() - (selfAsParam ? 1 : 0);
        var startArgument = static1 ? 0 : 1;
        Method tar = null;
        String[] paramI = new String[arguCount];
        Parameter[] params = methodAccess.getParameters();
        Class[] paramsCls = methodAccess.getParameterTypes();
        for (int i = 0; i < arguCount; ++i) {
            var redirect3 = params[i + startArgument].getAnnotation(RedirectType.class);
            if (redirect3 != null) {
                paramI[i] = redirect3.value();
            } else {
                paramI[i] = SimpleObfManager.getManager().deobfToJvm(paramsCls[i + startArgument]);
            }
        }
        //        if(Debug.isDebugMod()){
        //            Debug.logger("matching methodAccess",methodAccess, methods);
        //        }
        return methods.stream()
                .filter(m -> Modifier.isStatic(m.getModifiers()) == static1)
                .filter(m -> m.getParameterCount() == arguCount)
                .filter(test -> {
                    //                if(Debug.isDebugMod() &&
                    // ObfManager.getManager().deobfMethod(test).equals(targetName))
                    //
                    // Debug.logger(test,ObfManager.getManager().deobfMethod(test),"matches",targetName);
                    return SimpleObfManager.getManager().deobfMethod(test).equals(targetName);
                })
                .filter(test -> {
                    // match every type after deobf
                    var paramTypes = test.getParameterTypes();
                    for (int i = 0; i < arguCount; ++i) {
                        //                    if(Debug.isDebugMod()){
                        //                        Debug.logger("match param
                        // ",ObfManager.getManager().deobfToJvm(paramTypes[i]),paramI[i]);
                        //
                        //                    }
                        if (!SimpleObfManager.getManager()
                                .deobfToJvm(paramTypes[i])
                                .equals(paramI[i])) {
                            return false;
                        }
                    }
                    return true;
                })
                .peek(c -> c.setAccessible(true))
                .toList();
    }

    public static List<Constructor<?>> matchConstructors(
            Method constructorAccess, Constructor<?>[] targetConstructors) {
        var arguCount = constructorAccess.getParameterCount();
        Method tar = null;
        String[] paramI = new String[arguCount];
        Parameter[] params = constructorAccess.getParameters();
        Class[] paramsCls = constructorAccess.getParameterTypes();
        for (int i = 0; i < arguCount; ++i) {
            var redirect3 = params[i].getAnnotation(RedirectType.class);
            if (redirect3 != null) {
                paramI[i] = redirect3.value();
            } else {
                paramI[i] = SimpleObfManager.getManager().deobfToJvm(paramsCls[i]);
            }
        }
        return Arrays.stream(targetConstructors)
                .filter(c -> c.getParameterCount() == arguCount)
                .filter(c -> {
                    var paramsTypes = c.getParameterTypes();
                    for (int i = 0; i < arguCount; ++i) {
                        if (!SimpleObfManager.getManager()
                                .deobfToJvm(paramsTypes[i])
                                .equals(paramI[i])) {
                            return false;
                        }
                    }
                    return true;
                })
                .peek(c -> c.setAccessible(true))
                .toList();
    }

    public static Pair<Field, Boolean> matchFields(Method fieldAccess, List<Field> fields, boolean static1) {
        String targetName;
        boolean isGetter;
        var redirect1 = fieldAccess.getAnnotation(RedirectName.class);
        String name1;
        if (redirect1 != null) {
            name1 = redirect1.value();
        } else {
            name1 = fieldAccess.getName();
        }
        if (name1.endsWith("Getter")) {
            isGetter = true;
        } else if (name1.endsWith("Setter")) {
            isGetter = false;
        } else {
            throw new DescriptorBuildException(
                    "Illegal field target name " + name1 + ", can not resolve Getter or Setter");
        }
        targetName = name1.substring(0, name1.length() - "Netter".length());
        var type = fieldAccess.getAnnotation(RedirectType.class);
        Field tar = matchFields(targetName, type, fields, static1);
        return tar == null ? null : Pair.of(tar, isGetter);
    }

    public static Field matchFields(
            String targetName, @Nullable RedirectType type, List<Field> fields, boolean static1) {
        Field tar = null;
        for (Field test : fields) {
            // filter type not match
            if (Modifier.isStatic(test.getModifiers()) != static1) {
                continue;
            }
            String deobfName = SimpleObfManager.getManager().deobfField(test);
            if (!deobfName.equals(targetName)) {
                continue;
            }
            if (type != null) {
                String typeName = SimpleObfManager.getManager().deobfToJvm(test.getType());
                if (type.value().equals(typeName)) {
                    tar = test;
                    break;
                }
            } else {
                tar = test;
                break;
            }
        }
        if (tar != null) {
            tar.setAccessible(true);
            return tar;
        } else return null;
    }
}
