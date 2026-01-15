package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.algorithm.StringUtils;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class StringOps implements DynamicOps<Object> {

    public static final StringOps I = new StringOps();

    //    public static final Codec<Map> MAP = new TypeCodec<>(I, Map.class);

    @Override
    public Object empty() {
        return null;
    }

    public static Object dispatchStringAsNbt(String string) {
        if (string == null) {
            return null;
        }
        char chr = string.charAt(string.length() - 1);
        String valuePart = string.substring(0, string.length() - 1);
        if (valuePart.isEmpty()) {
            return string;
        }

        switch (chr) {
            case 'b':
                if (StringUtils.isDigit(valuePart, 10)) {
                    return Byte.valueOf(valuePart);
                }
                break;

            case 's':
                if (StringUtils.isDigit(valuePart, 10)) {
                    return Short.valueOf(valuePart);
                }
                break;

            case 'i':
                if (StringUtils.isDigit(valuePart, 10)) {
                    return Integer.valueOf(valuePart);
                }
                break;

            case 'l':
                if (StringUtils.isDigit(valuePart, 10)) {
                    return Long.valueOf(valuePart);
                }
                break;

            case 'f':
                if (StringUtils.isDouble(valuePart)) {
                    return Float.valueOf(valuePart);
                }
                break;
            case 'd':
                if (StringUtils.isDouble(valuePart)) {
                    return Double.valueOf(valuePart);
                }
                break;
            case '\"':
                if (valuePart.charAt(0) == '\"') {
                    return valuePart.substring(1);
                }
                break;
        }
        return string;
    }

    public static Object dispatchListAsNbt(List<?> list) {
        if (list.isEmpty() || list.size() == 1) {
            return list;
        }
        Object val = list.get(0);
        Object val2 = list.get(1);
        if (val instanceof String str && str.length() == 1 && val2 instanceof Number) {
            switch (str.charAt(0)) {
                case 'B': {
                    byte[] bytes = new byte[list.size() - 1];
                    for (int i = 0; i < bytes.length; i++) {
                        Object obj = list.get(i + 1);
                        if (obj instanceof Number number1) {
                            bytes[i] = number1.byteValue();
                        } else {
                            return list;
                        }
                    }
                    return ByteArrayList.of(bytes);
                }
                case 'I': {
                    int[] bytes = new int[list.size() - 1];
                    for (int i = 0; i < bytes.length; i++) {
                        Object obj = list.get(i + 1);
                        if (obj instanceof Number number1) {
                            bytes[i] = number1.intValue();
                        } else {
                            return list;
                        }
                    }
                    return IntArrayList.of(bytes);
                }
                case 'L': {
                    long[] bytes = new long[list.size() - 1];
                    for (int i = 0; i < bytes.length; i++) {
                        Object obj = list.get(i + 1);
                        if (obj instanceof Number number1) {
                            bytes[i] = number1.longValue();
                        } else {
                            return list;
                        }
                    }
                    return LongArrayList.of(bytes);
                }
            }
        }
        return list;
    }

    public static String unwrapString(Object obj) {
        if (obj instanceof String str && str.length() > 1) {
            if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
                return str.substring(1, str.length() - 1);
            }
        }
        return obj.toString();
    }

    public static String wrapString(String string) {
        return "\"" + string + "\"";
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input == null) {
            return outOps.empty();
        }
        if (input instanceof String str) {
            input = dispatchStringAsNbt(str);
        }
        if (input instanceof List list) {
            input = dispatchListAsNbt(list);
        }

        if (input == null) {
            return outOps.empty();
        } else if (input instanceof Map<?, ?>) {
            return this.convertMap(outOps, input);
        } else if (input instanceof ByteList value) {
            return outOps.createByteList(ByteBuffer.wrap(value.toByteArray()));
        } else if (input instanceof IntList value) {
            return outOps.createIntList(value.intStream());
        } else if (input instanceof LongList value) {
            return outOps.createLongList(value.longStream());
        } else if (input instanceof List) {
            return this.convertList(outOps, input);
        } else if (input instanceof String value) {
            return outOps.createString(value);
        } else if (input instanceof Boolean value) {
            return outOps.createBoolean(value);
        } else if (input instanceof Byte value) {
            return outOps.createByte(value);
        } else if (input instanceof Short value) {
            return outOps.createShort(value);
        } else if (input instanceof Integer value) {
            return outOps.createInt(value);
        } else if (input instanceof Long value) {
            return outOps.createLong(value);
        } else if (input instanceof Float value) {
            return outOps.createFloat(value);
        } else if (input instanceof Double value) {
            return outOps.createDouble(value);
        } else if (input instanceof Number value) {
            return outOps.createNumeric(value);
        } else {
            throw new IllegalStateException("Don't know how to convert " + input);
        }
    }

    @Override
    public DataResult<Number> getNumberValue(Object object) {
        if (object instanceof String str) {
            object = dispatchStringAsNbt(str);
        }
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
        if (number instanceof Byte b) {
            return createByte(b);
        }
        if (number instanceof Short s) {
            return createShort(s);
        }
        if (number instanceof Integer i) {
            return createInt(i);
        }
        if (number instanceof Long l) {
            return createLong(l);
        }
        if (number instanceof Float f) {
            return createFloat(f);
        }
        if (number instanceof Double d) {
            return createDouble(d);
        }
        return createDouble(number.doubleValue());
    }

    public Object createByte(byte b) {
        return b + "b";
    }

    public Object createShort(short s) {
        return s + "s";
    }

    public Object createInt(int i) {
        return i + "i";
    }

    public Object createLong(long l) {
        return l + "l";
    }

    public Object createFloat(float f) {
        return f + "f";
    }

    public Object createDouble(double d) {
        return d + "d";
    }

    @Override
    public DataResult<String> getStringValue(Object object) {
        if (object instanceof String str) {
            object = dispatchStringAsNbt(str);
        }
        return object instanceof String str ? DataHelper.A.I.success(str) : DataHelper.A.I.error(() -> "not a string");
    }

    @Override
    public Object createString(String s) {
        return wrapString(s);
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
        Map<Object, Object> map0 =
                object != null ? new LinkedHashMap<>((Map<Object, Object>) object) : new LinkedHashMap<>();
        map0.put(unwrapString(t1), t2);
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, Map<Object, Object> values) {
        if (!(object instanceof Map) && object != null) {

            return DataHelper.A.I.error(() -> "Not a Map");
        }
        Map<Object, Object> map0 =
                object != null ? new LinkedHashMap<>((Map<Object, Object>) object) : new LinkedHashMap<>();
        values.forEach((k, v) -> map0.put(unwrapString(k), v));
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object object) {

        return object instanceof Map<?, ?> map0
                ? DataHelper.A.I.success(map0.entrySet().stream()
                        .map(entry -> Pair.of(this.createString((String) entry.getKey()), entry.getValue())))
                : DataHelper.A.I.error(() -> "not a map");
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, MapLike<Object> values) {
        if (!(object instanceof Map) && object != null) {

            return DataHelper.A.I.error(() -> "Not a Map");
        }
        Map<Object, Object> map0 =
                object != null ? new LinkedHashMap<>((Map<Object, Object>) object) : new LinkedHashMap<>();
        values.entries().forEach(pr -> map0.put(unwrapString(pr.getFirst()), pr.getSecond()));
        return DataHelper.A.I.success(map0);
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> stream) {
        Map<Object, Object> map0 = new LinkedHashMap<>();
        stream.forEach(pair -> map0.put(unwrapString(pair.getFirst()), pair.getSecond()));
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
        byte[] byteArray = input.array();
        List<Object> list0 = new ArrayList<>(byteArray.length + 1);
        list0.add("B");
        for (var b : byteArray) {
            list0.add(b);
        }
        return list0;
    }

    public Object createIntList(IntStream input) {
        return Stream.concat(Stream.of("I"), input.boxed());
    }

    public Object createLongList(LongStream input) {
        return Stream.concat(Stream.of("L"), input.boxed());
    }

    @Override
    public Object remove(Object object, String s) {
        if (object instanceof Map map0) {
            Map<Object, Object> map2 = new LinkedHashMap<>(map0);
            map2.remove(s);
            return map2;
        } else {
            return object;
        }
    }

    public String toString() {
        return "STRING_OP";
    }
}
