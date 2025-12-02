package me.matl114.matlib.nmsUtils.serialize;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import me.matl114.matlib.common.lang.exceptions.DecodeException;
import me.matl114.matlib.common.lang.exceptions.EncodeException;
import me.matl114.matlib.nmsMirror.fix.DataHelper;
import me.matl114.matlib.nmsMirror.impl.*;
import me.matl114.matlib.utils.version.Version;

public class CodecUtils {

    public static <R,T> T encode(Codec<R> codec, DynamicOps<T> ops, R input){
        return encodeEnd(codec.encode(input,ops, ops.empty()));
    }

    public static <R,T> R decode(Codec<R> codec, DynamicOps<T> ops, T input){
        return result(codec.decode(ops, input));
    }
    public static <R> R result(DataResult<? extends Pair<R,?>> encode){
        return DataHelper.A.I.getOrThrow(encode, (e)->{
            throw new DecodeException(e);
        }).getFirst();
    }


    public static <R> R encodeEnd(DataResult<R> encode){
       // return DataHelper.A.I.result(encode).get();
        return DataHelper.A.I.getOrThrow(encode, (e)->{
            throw new EncodeException(e);
        });
    }
    private static final boolean versionAt1_20_R4 = Version.getVersionInstance().isAtLeast(Version.v1_20_R4);
    private static final DynamicOps<Object> NBT_OP_1_20_R4_CACHE;
    private static final DynamicOps<Object> PRIMITIVE_OP_1_20_R4_CACHE;
    private static final DynamicOps<JsonElement> JSON_OP_1_20_R4_CACHE;
    static {
        if(versionAt1_20_R4){

            NBT_OP_1_20_R4_CACHE = NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, Env.NBT_OP);
            PRIMITIVE_OP_1_20_R4_CACHE =  NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, TypeOps.I);
            JSON_OP_1_20_R4_CACHE =  NMSCore.REGISTRIES.provideRegistryForDynamicOp(Env.REGISTRY_FROZEN, JsonOps.INSTANCE);
        }else {
            NBT_OP_1_20_R4_CACHE = Env.NBT_OP;
            PRIMITIVE_OP_1_20_R4_CACHE = TypeOps.I;
            JSON_OP_1_20_R4_CACHE = JsonOps.INSTANCE;
        }
    }
    public static DynamicOps<Object> nbtOp(){
        return NBT_OP_1_20_R4_CACHE;
    }
    public static DynamicOps<Object> primOp(){
        return PRIMITIVE_OP_1_20_R4_CACHE;
    }
    public static DynamicOps<JsonElement> jsonOp(){
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
