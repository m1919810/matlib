package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

import java.util.stream.Stream;


public class NumberOps implements DynamicOps<Number> {
    public static final NumberOps I = new NumberOps();

    @Override
    public Number empty() {
        return 0;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, Number number) {
        return dynamicOps.createNumeric(number);
    }

    @Override
    public DataResult<Number> getNumberValue(Number number) {
        return DataHelper.A.I.success(number);
    }

    @Override
    public Number createNumeric(Number number) {
        return number;
    }

    @Override
    public DataResult<String> getStringValue(Number number) {
        return number != null ? DataHelper.A.I.success(number.toString()) : DataHelper.A.I.error(() -> "Not a number");
    }

    @Override
    public Number createString(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public DataResult<Number> mergeToList(Number number, Number t1) {
        return DataHelper.A.I.error(() -> "Collection not support");
    }

    @Override
    public DataResult<Number> mergeToMap(Number number, Number t1, Number t2) {
        return DataHelper.A.I.error(() -> "Collection not support");
    }

    @Override
    public DataResult<Stream<Pair<Number, Number>>> getMapValues(Number number) {
        return DataHelper.A.I.error(() -> "Collection not support");
    }

    @Override
    public Number createMap(Stream<Pair<Number, Number>> stream) {
        return 0;
    }

    @Override
    public DataResult<Stream<Number>> getStream(Number number) {
        return DataHelper.A.I.error(() -> "Collection not support");
    }

    @Override
    public Number createList(Stream<Number> stream) {
        return 0;
    }

    @Override
    public Number remove(Number number, String s) {
        return 0;
    }
}
