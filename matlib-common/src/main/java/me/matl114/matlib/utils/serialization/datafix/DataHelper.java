package me.matl114.matlib.utils.serialization.datafix;

import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import me.matl114.matlib.utils.chat.lan.i18n.ZhCNLocalizationHelper;
import me.matl114.matlib.utils.chat.lan.pinyinAdaptor.PinyinHelper;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorBuilder;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.service.CustomServiceLoader;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "com.mojang.serialization.DataResult")
public interface DataHelper extends TargetDescriptor {
    @MethodTarget
    public <R> Optional<R> result(com.mojang.serialization.DataResult<R> result);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    default <E extends Throwable, R> R getOrThrow(
            com.mojang.serialization.DataResult<R> result, Function<String, E> var1) {
        return _getOrThrow0(result, var1);
    }

    @MethodTarget(isStatic = true)
    @RedirectClass("net.minecraft.Util")
    @RedirectName("getOrThrow")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4)
    public <E extends Throwable, R> R _getOrThrow0(
            com.mojang.serialization.DataResult<R> result, Function<String, E> var1);

    @MethodTarget(isStatic = true)
    public <R> DataResult<R> success(R result);

    @MethodTarget(isStatic = true)
    public <R> DataResult<R> error(Supplier<String> message);

    public static interface A {
        DataHelper I = DescriptorBuilder.createASMMultiHelper(DataHelper.class);//DescriptorImplBuilder.createMultiHelper(DataHelper.class);
    }

    public static interface P {
        DataHelper I = DescriptorProxyBuilder.createMultiHelper(DataHelper.class);
    }
}
