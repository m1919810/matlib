package me.matl114.matlib.utils.chat.lan.pinyinAdaptor;

import java.util.List;
import java.util.Map;
import me.matl114.matlib.common.lang.annotations.EnumVal;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.FailHard;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorBuilder;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorProxyBuilder;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.service.CustomServiceLoader;
import me.matl114.matlib.utils.version.Version;

@FailHard(thresholdInclude = Version.v1_20_R1, below = false)
@MultiDescriptive(targetDefault = "com.github.houbb.pinyin.util.PinyinHelper")
public interface PinyinHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    default String toPinyin(String string) {
        return DEFAULT.toPinyin(string);
    }

    static final String PinyinStyleEnum = "Lcom/github/houbb/pinyin/constant/enums/PinyinStyleEnum;";

    @MethodTarget(isStatic = true)
    @RedirectClass("com.github.houbb.pinyin.constant.enums.PinyinStyleEnum")
    @RedirectName("valueOf")
    default Object getPinyinStyleEnum(
            @EnumVal({"NORMAL", "DEFAULT", "NUM_LAST", "FIRST_LETTER", "INPUT"}) String value) {
        return DEFAULT.getPinyinStyleEnum(value);
    }

    @MethodTarget(isStatic = true)
    default String toPinyin(String string, @RedirectType(PinyinStyleEnum) Object styleEnum) {
        return DEFAULT.toPinyin(string, styleEnum);
    }

    @MethodTarget(isStatic = true)
    default String toPinyin(String string, @RedirectType(PinyinStyleEnum) Object styleEnum, String connector) {
        return DEFAULT.toPinyin(string, styleEnum, connector);
    }

    @MethodTarget(isStatic = true)
    default List<String> toPinyinList(char chinese) {
        return DEFAULT.toPinyinList(chinese);
    }

    @MethodTarget(isStatic = true)
    default List<String> toPinyinList(char chinese, @RedirectType(PinyinStyleEnum) Object styleEnum) {
        return DEFAULT.toPinyinList(chinese, styleEnum);
    }

    @MethodTarget(isStatic = true)
    default List<String> samePinyinList(String pinyinNumLast) {
        return DEFAULT.samePinyinList(pinyinNumLast);
    }

    @MethodTarget(isStatic = true)
    default Map<String, List<String>> samePinyinMap(char hanzi) {
        return DEFAULT.samePinyinMap(hanzi);
    }

    PinyinHelper DEFAULT = new PinyinHelper() {
        @Override
        public String toPinyin(String string) {
            return string;
        }

        @Override
        public Object getPinyinStyleEnum(String value) {
            return null;
        }

        @Override
        public String toPinyin(String string, Object styleEnum) {
            return string;
        }

        @Override
        public String toPinyin(String string, Object styleEnum, String connector) {
            return string;
        }

        @Override
        public List<String> toPinyinList(char chinese) {
            return List.of();
        }

        @Override
        public List<String> toPinyinList(char chinese, Object styleEnum) {
            return List.of();
        }

        @Override
        public List<String> samePinyinList(String pinyinNumLast) {
            return List.of();
        }

        @Override
        public Map<String, List<String>> samePinyinMap(char hanzi) {
            return Map.of();
        }

        @Override
        public Class getTargetClass() {
            return null;
        }
    };

    public static PinyinHelper createDefaultImpl() {
        return DEFAULT;
    }

    public static final class A {
        public static final PinyinHelper I = DescriptorBuilder.createASMMultiHelper(PinyinHelper.class);


    }

    public static final class P {
        public static final PinyinHelper I;

        static {
            PinyinHelper helper;
            try {
                helper = DescriptorProxyBuilder.createMultiHelper(PinyinHelper.class);
            } catch (Throwable e) {
                Debug.logger(e, "Error while initializing PinyinHelper");
                Debug.logger("Use default PinyinHelper instead");
                helper = createDefaultImpl();
            }
            I = helper;
        }
    }
}
