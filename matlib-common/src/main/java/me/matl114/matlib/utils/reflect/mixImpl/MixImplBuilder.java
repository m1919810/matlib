package me.matl114.matlib.utils.reflect.mixImpl;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.reflect.mixImpl.annotations.MixImpl;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixBase;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixContent;
import me.matl114.matlib.utils.reflect.mixImpl.buildTools.MixImplBuildException;

@SuppressWarnings("all")
public class MixImplBuilder {
    private static final Map<MixImplArgument, MixContent> CACHE = new HashMap<>();

    public static <W extends MixBase> MixContent<W> createMixinImplDefault(Class<W> mixBases, String fullname) {
        MixImpl defaultArgument = mixBases.getAnnotation(MixImpl.class);
        Preconditions.checkNotNull(
                defaultArgument, "Invalid Argument passed mixBases: @MixImpl absent for target class");
        Class mixSuperClass;
        List<Class<?>> extraItf = new ArrayList<>();
        try {
            mixSuperClass = ObfManager.getManager().reobfClass(defaultArgument.subClass());
            for (String name : defaultArgument.interfaces()) {
                extraItf.add(ObfManager.getManager().reobfClass(name));
            }

        } catch (Throwable e) {
            throw new MixImplBuildException(e);
        }
        return createMixImplAt(mixBases, mixSuperClass, extraItf, fullname);
    }

    /**
     * use mixInterface as main body to create a class <ClassName> overriding mixSuperClass with extra interfaces <extraItf>
     * @param mixBases
     * @param mixSuperClass
     * @param extraItf
     * @param className
     * @return
     * @param <T>
     * @param <R>
     * @param <W>
     */
    public static <R, W extends MixBase> MixContent<W> createMixImplAt(
            Class<W> mixBases, Class<R> mixSuperClass, List<Class<?>> extraItf, String className) {
        MixImplArgument argument = new MixImplArgument(mixBases, mixSuperClass, extraItf, className);
        return CACHE.computeIfAbsent(argument, (arg) -> {
            try {
                return createMixImplInternel(arg.mixBase, arg.superClass, arg.extraItf, arg.name);
            } catch (Throwable e) {
                throw new MixImplBuildException(e);
            }
        });
    }

    private static synchronized <R, W extends MixBase> MixContent<W> createMixImplInternel(
            Class<W> mixInterface, Class<R> mixSuperClass, Set<Class<?>> extraItf, String fullname) throws Throwable {
        Preconditions.checkArgument(
                !CustomClassLoader.getInstance().isClassPresent(fullname),
                "Invalid new ClassPath :" + fullname + " ,because class with this path is present !");
        // todo maybe we will support mix methods /fields conflict priority system
        throw new NotImplementedYet();
        // return null;
    }

    @Data
    static class MixImplArgument {
        public MixImplArgument(
                Class<? extends MixBase> mixins, Class<?> superClass, List<Class<?>> extraItf, String name) {
            this.mixBase = mixins;
            ;
            this.superClass = superClass;
            this.extraItf = extraItf.stream()
                    .distinct()
                    .filter(
                            // 考虑itf是否被mixBase囊括了,如果囊括了,就drop it
                            t -> !mixBase.isAssignableFrom(t))
                    .collect(Collectors.toUnmodifiableSet());
            this.name = name;
        }

        Class<? extends MixBase> mixBase;
        Class<?> superClass;
        Set<Class<?>> extraItf;
        String name;
    }
}
