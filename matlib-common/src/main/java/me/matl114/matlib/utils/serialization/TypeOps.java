package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class TypeOps implements DynamicOps<Object> {
    public static final TypeOps I = new TypeOps();

    @Override
    public Object empty() {
        return null;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, Object object) {
        if (dynamicOps instanceof TypeOps thisType) {
            return (U) object;
        }
        if (object instanceof Number number) {
            return dynamicOps.createNumeric(number);
        } else if (object instanceof Boolean bool) {
            return dynamicOps.createBoolean(bool);
        } else if (object instanceof String value) {
            return dynamicOps.createString(value);
        } else if (object instanceof List<?> list) {
            return this.convertList(dynamicOps, list);
        } else if (object instanceof Map<?, ?> map) {
            return this.convertMap(dynamicOps, map);
        } else if (object instanceof int[] intList) {
            return dynamicOps.createIntList(Arrays.stream(intList));
        } else if (object instanceof long[] longlist) {
            return dynamicOps.createLongList(Arrays.stream(longlist));
        } else if (object instanceof byte[] bytelist) {
            return dynamicOps.createByteList(ByteBuffer.wrap(bytelist));
        } else {
            throw new IllegalStateException("Unknown object type " + object.getClass());
        }
    }

    @Override
    public DataResult<Number> getNumberValue(Object object) {
        return object instanceof Number num ? DataHelper.A.I.success(num) : DataHelper.A.I.error(() -> "not a number");
    }

    public DataResult<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean bool
                ? DataHelper.A.I.success(bool)
                : DataHelper.A.I.error(() -> "not a boolean");
    }

    public Object createBoolean(boolean value) {
        return value;
    }

    @Override
    public Object createNumeric(Number number) {
        return number;
    }

    @Override
    public DataResult<String> getStringValue(Object object) {
        return object instanceof String str ? DataHelper.A.I.success(str) : DataHelper.A.I.error(() -> "not a string");
    }

    @Override
    public Object createString(String s) {
        return s;
    }

    @Override
    public DataResult<Object> mergeToList(Object object, Object t1) {
        List<Object> list0;
        if (object instanceof List<?> list00) {
            list0 = (List<Object>) list00;
        } else if (object == null) {
            list0 = new ArrayList<>();
        } else return DataHelper.A.I.error(() -> "Not a list");
        list0.add(t1);
        return DataHelper.A.I.success(list0);
    }

    @Override
    public DataResult<Object> mergeToList(Object object, List<Object> values) {
        List<Object> list0;
        if (object instanceof List<?> list00) {
            list0 = (List<Object>) list00;
        } else if (object == null) {
            list0 = new ArrayList<>();
        } else return DataHelper.A.I.error(() -> "Not a list");
        list0.addAll(values);
        return DataHelper.A.I.success(list0);
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, Object t1, Object t2) {
        if (!(object instanceof Map) && object != null) {
            return DataHelper.A.I.error(() -> "Not a Map");
        }
        Map<Object, Object> map0 = object != null ? (Map<Object, Object>) object : new HashMap<>();
        map0.put(t1, t2);
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, Map<Object, Object> values) {
        if (!(object instanceof Map) && object != null) {

            return DataHelper.A.I.error(() -> "Not a Map");
        }
        Map<Object, Object> map0 = object != null ? (Map<Object, Object>) object : new HashMap<>();
        map0.putAll(values);
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object object) {

        return object instanceof Map<?, ?> map0
                ? DataHelper.A.I.success(
                        map0.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())))
                : DataHelper.A.I.error(() -> "not a map");
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, MapLike<Object> values) {
        if (!(object instanceof Map) && object != null) {

            return DataHelper.A.I.error(() -> "Not a Map");
        }
        Map<Object, Object> map0 = object != null ? (Map<Object, Object>) object : new HashMap<>();
        values.entries().map(pr -> map0.put(pr.getFirst(), pr.getSecond()));
        return DataHelper.A.I.success(map0);
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> stream) {
        Map<Object, Object> map0 = new HashMap<>();
        stream.forEach(pair -> map0.put(pair.getFirst(), pair.getSecond()));
        return map0;
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object object) {
        return object instanceof List list0
                ? DataHelper.A.I.success(list0.stream())
                : DataHelper.A.I.error(() -> "Not a list");
    }

    @Override
    public Object createList(Stream<Object> stream) {
        List<Object> list0 = new ArrayList<>();
        stream.forEach(list0::add);
        return list0;
    }

    public Object createByteList(ByteBuffer input) {
        return ByteBuffer.wrap(input.array());
    }

    public Object createIntList(IntStream input) {
        return input.toArray();
    }

    public Object createLongList(LongStream input) {
        return input.toArray();
    }

    @Override
    public Object remove(Object object, String s) {
        if (object instanceof Map map0) {
            Map<Object, Object> map2 = new HashMap<>(map0);
            map2.remove(s);
            return map2;
        } else {
            return object;
        }
    }

    public String toString() {
        return "TYPE_OP";
    }
}
