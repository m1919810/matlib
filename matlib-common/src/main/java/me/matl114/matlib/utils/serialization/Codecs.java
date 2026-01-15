package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class Codecs {
    public static <S extends Enum<?>> Codec<S> enumLowerCase(Class<S> enumClass) {
        S[] enumConstants = enumClass.getEnumConstants();
        Function<S, String> nameGetter =
                createToNameMap(enumConstants, Enum::name, name -> name.toLowerCase(Locale.ROOT));
        Function<String, S> nameLookup =
                createNameLookup(enumConstants, Enum::name, name -> name.toLowerCase(Locale.ROOT));
        ToIntFunction<S> indexLookup = Enum::ordinal;
        return new StringCodec<>(enumConstants, nameGetter, nameLookup, indexLookup);
    }

    public static <T> Function<T, String> createToNameMap(
            T[] values, Function<T, String> nameGetter, Function<String, String> valueNameTransformer) {
        Map<T, String> map = Arrays.stream(values)
                .collect(Collectors.toMap(
                        value -> (T) value, value -> valueNameTransformer.apply(nameGetter.apply(value))));
        return name -> name == null ? null : map.get(name);
    }

    public static <T> Function<String, T> createNameLookup(
            T[] values, Function<T, String> nameGetter, Function<String, String> valueNameTransformer) {
        Map<String, T> map = Arrays.stream(values)
                .collect(Collectors.toMap(
                        value -> valueNameTransformer.apply(nameGetter.apply(value)), value -> (T) value));
        return name -> name == null ? null : map.get(name);
    }

    public static <T> ToIntFunction<T> createIndexLookup(List<T> values) {
        int i = values.size();
        if (i < 3) {
            return values::indexOf;
        } else {
            Object2IntMap<T> object2IntMap = new Object2IntOpenHashMap<>(i);
            object2IntMap.defaultReturnValue(-1);

            for (int j = 0; j < i; j++) {
                object2IntMap.put(values.get(j), j);
            }
            return object2IntMap;
        }
    }

    public static <E> Codec<E> idResolverCodec(
            ToIntFunction<E> elementToRawId, IntFunction<E> rawIdToElement, int errorRawId) {
        return Codec.INT.flatXmap(
                rawId -> Optional.ofNullable(rawIdToElement.apply(rawId))
                        .map(DataHelper.A.I::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + rawId)),
                element -> {
                    int j = elementToRawId.applyAsInt((E) element);
                    return j == errorRawId
                            ? DataResult.error(() -> "Element with unknown id: " + element)
                            : DataHelper.A.I.success(j);
                });
    }

    public static <E> Codec<E> orCompressed(Codec<E> uncompressedCodec, Codec<E> compressedCodec) {
        return new Codec<E>() {
            public <T> DataResult<T> encode(E object, DynamicOps<T> dynamicOps, T object2) {
                return dynamicOps.compressMaps()
                        ? compressedCodec.encode(object, dynamicOps, object2)
                        : uncompressedCodec.encode(object, dynamicOps, object2);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                return dynamicOps.compressMaps()
                        ? compressedCodec.decode(dynamicOps, object)
                        : uncompressedCodec.decode(dynamicOps, object);
            }

            @Override
            public String toString() {
                return uncompressedCodec + " orCompressed " + compressedCodec;
            }
        };
    }
}
