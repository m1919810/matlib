package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.util.Objects;
import java.util.Optional;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class ExtraCodecs {

    public static <A> Codec<Optional<A>> optionalEmptyMap(Codec<A> codec) {
        return new Codec<Optional<A>>() {
            public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> dynamicOps, T object) {
                return isEmptyMap(dynamicOps, object)
                        ? DataHelper.A.I.success(Pair.of(Optional.empty(), object))
                        : DataHelper.A.I.map(codec.decode(dynamicOps, object), pair -> pair.mapFirst(Optional::of));
            }

            private static <T> boolean isEmptyMap(DynamicOps<T> ops, T input) {
                Optional<MapLike<T>> optional = DataHelper.A.I.result(ops.getMap(input)); // .result();
                return optional.isPresent()
                        && optional.get().entries().findAny().isEmpty();
            }

            public <T> DataResult<T> encode(Optional<A> optional, DynamicOps<T> dynamicOps, T object) {
                return optional.isEmpty()
                        ? DataHelper.A.I.success(dynamicOps.emptyMap())
                        : codec.encode(optional.get(), dynamicOps, object);
            }
        };
    }

    public static <A> Codec<A> optionalEmptyMapElseGet(Codec<A> codec, A elseGet) {
        return new Codec<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicOps, T object) {
                return isEmptyMap(dynamicOps, object)
                        ? DataHelper.A.I.success(Pair.of(elseGet, object))
                        : codec.decode(dynamicOps, object);
            }

            private static <T> boolean isEmptyMap(DynamicOps<T> ops, T input) {
                Optional<MapLike<T>> optional = DataHelper.A.I.result(ops.getMap(input)); // .result();
                return optional.isPresent()
                        && optional.get().entries().findAny().isEmpty();
            }

            public <T> DataResult<T> encode(A optional, DynamicOps<T> dynamicOps, T object) {
                return Objects.equals(elseGet, optional)
                        ? DataHelper.A.I.success(dynamicOps.emptyMap())
                        : codec.encode(optional, dynamicOps, object);
            }
        };
    }
}
