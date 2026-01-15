package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.stream.Collectors;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class TypeCodec<T> implements Codec<T> {
    private final DynamicOps<? super T> op;
    private final Class<T> clazz;

    public TypeCodec(final DynamicOps<? super T> ops, Class<T> clz) {
        this.op = ops;
        this.clazz = clz;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        var val = ops.convertTo(this.op, input);
        return this.clazz.isInstance(val)
                ? DataHelper.A.I.success(Pair.of((T) val, ops.empty()))
                : DataHelper.A.I.error(() -> "Not a " + this.clazz.getSimpleName() + ": " + val);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        if (input == this.op.empty()) {
            // nothing to merge, return rest
            return DataHelper.A.I.success(prefix);
        }

        final T1 casted = this.op.convertTo(ops, input);
        if (prefix == ops.empty()) {
            // no need to merge anything, return the old value
            return DataHelper.A.I.success(casted);
        }

        final DataResult<T1> toMap = ops.getMap(casted).flatMap(map -> ops.mergeToMap(prefix, map));
        return toMap.result().map(DataHelper.A.I::success).orElseGet(() -> {
            final DataResult<T1> toList = ops.getStream(casted)
                    .flatMap(stream -> ops.mergeToList(prefix, stream.collect(Collectors.toList())));
            return toList.result()
                    .map(DataHelper.A.I::success)
                    .orElseGet(
                            () -> DataHelper.A.I.error(() -> "Don't know how to merge " + prefix + " and " + casted));
        });
    }
}
