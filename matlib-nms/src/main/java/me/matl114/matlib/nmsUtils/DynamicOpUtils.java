package me.matl114.matlib.nmsUtils;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.utils.serialization.TypeOps;
import me.matl114.matlib.utils.version.Version;

public class DynamicOpUtils {
    private static final boolean versionAt1_20_R4 = Version.getVersionInstance().isAtLeast(Version.v1_20_R4);
    private static final DynamicOps<Object> NBT_OP_1_20_R4_CACHE;
    private static final DynamicOps<Object> PRIMITIVE_OP_1_20_R4_CACHE;
    private static final DynamicOps<JsonElement> JSON_OP_1_20_R4_CACHE;

    static {
        if (versionAt1_20_R4) {

            NBT_OP_1_20_R4_CACHE = NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, Env.NBT_OP);
            PRIMITIVE_OP_1_20_R4_CACHE = NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, TypeOps.I);
            JSON_OP_1_20_R4_CACHE =
                NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, JsonOps.INSTANCE);
        } else {
            NBT_OP_1_20_R4_CACHE = Env.NBT_OP;
            PRIMITIVE_OP_1_20_R4_CACHE = TypeOps.I;
            JSON_OP_1_20_R4_CACHE = JsonOps.INSTANCE;
        }
    }

    public static DynamicOps<Object> nbtOp() {
        return NBT_OP_1_20_R4_CACHE;
    }

    public static DynamicOps<Object> primOp() {
        return PRIMITIVE_OP_1_20_R4_CACHE;
    }

    public static DynamicOps<JsonElement> jsonOp() {
        return JSON_OP_1_20_R4_CACHE;
    }
    //    public static DynamicOps<?> createDefaultNbtOp(){
    //        if(Version.getVersionInstance().isAtLeast(Version.v1_20_R4)){
    //            return NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, Env.NBT_OP);
    //        }else {
    //            return Env.NBT_OP;
    //        }
    //    }
}
