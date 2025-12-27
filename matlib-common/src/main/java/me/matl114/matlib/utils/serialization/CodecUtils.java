package me.matl114.matlib.utils.serialization;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import me.matl114.matlib.common.lang.exceptions.DecodeException;
import me.matl114.matlib.common.lang.exceptions.EncodeException;

import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import me.matl114.matlib.utils.version.Version;

public class CodecUtils {

    public static <R, T> T encode(Codec<R> codec, DynamicOps<T> ops, R input) {
        return encodeEnd(codec.encode(input, ops, ops.empty()));
    }

    public static <R, T> R decode(Codec<R> codec, DynamicOps<T> ops, T input) {
        return result(codec.decode(ops, input));
    }

    public static <R> R result(DataResult<? extends Pair<R, ?>> encode) {
        return DataHelper.A
                .I
                .getOrThrow(encode, (e) -> {
                    throw new DecodeException(e);
                })
                .getFirst();
    }

    public static <R> R encodeEnd(DataResult<R> encode) {
        // return DataHelper.A.I.result(encode).get();
        return DataHelper.A.I.getOrThrow(encode, (e) -> {
            throw new EncodeException(e);
        });
    }



}
