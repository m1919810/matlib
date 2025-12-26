package me.matl114.matlib.nmsUtils.serialize;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class StringCodec<S> implements Codec<S> {
    private final Codec<S> codec;

    public StringCodec(
            S[] values,
            Function<S, String> identifiableToId,
            Function<String, S> idToIdentifiable,
            ToIntFunction<S> identifiableToOrdinal) {
        this.codec = Codecs.orCompressed(
                Codec.stringResolver(identifiableToId, idToIdentifiable),
                Codecs.idResolverCodec(
                        identifiableToOrdinal,
                        ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null,
                        -1));
    }

    public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> dynamicOps, T object) {
        return this.codec.decode(dynamicOps, object);
    }

    public <T> DataResult<T> encode(S stringRepresentable, DynamicOps<T> dynamicOps, T object) {
        return this.codec.encode(stringRepresentable, dynamicOps, object);
    }
}
