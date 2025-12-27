package me.matl114.matlib.nmsUtils.v1_20_R4;

import com.mojang.serialization.Codec;
import me.matl114.matlib.nmsMirror.core.BuiltInRegistryEnum;
import me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4;
import me.matl114.matlib.nmsUtils.RegistryUtils;

public class DataComponentUtils {
    public static Codec<Object> getTypeCodec(String idd) {
        Object val = getDataType(idd);
        return getTypeCodec(val);
    }

    public static Codec<Object> getTypeCodec(Object type) {
        Codec<Object> codecValue =
                Env1_20_R4.DATA_TYPES.getDataTypeCodec(type); // ComponentCodecEnum.DATA_COMPONENT_CODEC_MAP.get(type);
        if (codecValue == null) {
            throw new IllegalArgumentException(
                    "This DataComponentType is temporary and can not be saved by this method");
        }
        return codecValue;
    }

    public static String typeId(Object string) {
        return RegistryUtils.getResourceLocation(BuiltInRegistryEnum.DATA_COMPONENT_TYPE, string)
                .toString();
    }

    public static String typeIdOrNull(Object string) {
        return string == null ? null : typeId(string);
    }

    public static Object getDataType(String idd) {
        Object val = RegistryUtils.getByKeyOrNull(BuiltInRegistryEnum.DATA_COMPONENT_TYPE, idd);
        if (val == null) {
            throw new IllegalArgumentException("No such DataComponentType" + idd);
        }
        return val;
    }
}
