package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.*;
import me.matl114.matlib.algorithms.algorithm.StringUtils;
import me.matl114.matlib.common.lang.exceptions.DecodeException;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import org.jetbrains.annotations.Nullable;

/**
 * 设计目的：
 * 我们希望像StringOps一样 将可能引发歧义的类型全部写入为字符串
 * 但是我希望和某个delegate DynamicOps直接结合
 */
public class StringifyOps<U> implements DynamicOps<U> {
    private final DynamicOps<U> delegate;

    public DynamicOps<U> getDelegate() {
        return delegate;
    }

    public StringifyOps(DynamicOps<U> delegate) {
        this.delegate = delegate;
    }

    @Override
    public U empty() {
        return this.delegate.empty();
    }

    private static final Object NOT_A_STRING = new Object();

    public Object dispatchStringAsNbt(U object) {
        DataResult<String> dataResult = delegate.getStringValue(object);
        if (DataHelper.A.I.isSuccess(dataResult)) {
            String string = DataHelper.A.I.getOrThrow(dataResult, DecodeException::new);
            return StringOps.dispatchStringAsNbt(string);
        }
        return NOT_A_STRING;
    }

    public U unwrapString(U obj) {
        DataResult<String> dataResult = delegate.getStringValue(obj);
        if (DataHelper.A.I.isSuccess(dataResult)) {
            String str = DataHelper.A.I.getOrThrow(dataResult, DecodeException::new);
            if (str.length() > 1) {
                if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
                    return this.delegate.createString(str.substring(1, str.length() - 1));
                }
            }
        }
        return obj;
    }

    public U wrapString(U obj) {
        DataResult<String> dataResult = delegate.getStringValue(obj);
        if (DataHelper.A.I.isSuccess(dataResult)) {
            String str = DataHelper.A.I.getOrThrow(dataResult, DecodeException::new);
            return this.delegate.createString("\"" + str + "\"");
        }
        return obj;
    }

    public Object dispatchListAsNbt(List<?> list) {
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

    @Override
    public <V> V convertTo(DynamicOps<V> outOps, U input) {
        if (input == null) {
            return outOps.empty();
        }
        return delegate.convertTo(new UnStringifyOps<V>(outOps), input);
    }

    @Override
    public DataResult<Number> getNumberValue(U object) {
        Object val = dispatchStringAsNbt(object);
        return val instanceof Number num ? DataHelper.A.I.success(num) : this.delegate.getNumberValue(object);
    }

    public DataResult<Boolean> getBooleanValue(Object input) {
        return input instanceof Boolean bool
                ? DataHelper.A.I.success(bool)
                : DataHelper.A.I.error(() -> "not a boolean");
    }

    public U createBoolean(boolean value) {
        return this.delegate.createBoolean(value);
    }

    @Override
    public U createNumeric(Number number) {
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

    public U createByte(byte b) {
        return this.delegate.createString(b + "b");
    }

    public U createShort(short s) {
        return this.delegate.createString(s + "s");
    }

    public U createInt(int i) {
        return this.delegate.createString(i + "i");
    }

    public U createLong(long l) {
        return this.delegate.createString(l + "l");
    }

    public U createFloat(float f) {
        return this.delegate.createString(f + "f");
    }

    public U createDouble(double d) {
        return this.delegate.createString(d + "d");
    }

    @Override
    public DataResult<String> getStringValue(U object) {
        Object obj = dispatchStringAsNbt(object);
        return obj instanceof String str ? DataHelper.A.I.success(str) : this.delegate.getStringValue(object);
    }

    @Override
    public U createString(String s) {
        return this.delegate.createString("\"" + s + "\"");
    }

    @Override
    public DataResult<U> mergeToList(U object, U t1) {
        return this.delegate.mergeToList(object, t1);
    }

    @Override
    public DataResult<U> mergeToList(U object, List<U> values) {
        return this.delegate.mergeToList(object, values);
    }

    @Override
    public DataResult<U> mergeToMap(U object, U t1, U t2) {
        return this.delegate.mergeToMap(object, unwrapString(t1), t2);
    }

    @Override
    public DataResult<U> mergeToMap(U object, Map<U, U> values) {
        Map<U, U> map = new LinkedHashMap<>();
        values.forEach((k, v) -> map.put(unwrapString(k), v));
        return this.delegate.mergeToMap(object, map);
    }

    @Override
    public DataResult<Stream<Pair<U, U>>> getMapValues(U object) {
        return DataHelper.A.I.map(
                this.delegate.getMapValues(object),
                pairStream -> pairStream.map(p -> Pair.of(wrapString(p.getFirst()), p.getSecond())));
        //        return object instanceof Map<?, ?> map0
        //            ? DataHelper.A.I.success(
        //            map0.entrySet().stream().map(entry -> Pair.of(this.createString((String) entry.getKey()),
        // entry.getValue())))
        //            : DataHelper.A.I.error(() -> "not a map");
    }

    @Override
    public DataResult<U> mergeToMap(U object, MapLike<U> values) {
        return this.delegate.mergeToMap(object, new MapLike<U>() {
            @Nullable @Override
            public U get(U key) {
                return values.get(wrapString(key));
            }

            @Nullable @Override
            public U get(String key) {
                return values.get(StringOps.wrapString(key));
            }

            @Override
            public Stream<Pair<U, U>> entries() {
                return values.entries().map(pair -> Pair.of(unwrapString(pair.getFirst()), pair.getSecond()));
            }
        });
    }

    @Override
    public U createMap(Stream<Pair<U, U>> stream) {
        //
        return this.delegate.createMap(
                stream.map(uuPair -> Pair.of(unwrapString(uuPair.getFirst()), uuPair.getSecond())));
        //        Map<Object, Object> map0 = new LinkedHashMap<>();
        //        stream.forEach(pair -> map0.put(unwrapString(pair.getFirst()), pair.getSecond()));
        //        return map0;
    }

    @Override
    public DataResult<Stream<U>> getStream(U object) {
        return this.delegate.getStream(object);
    }

    @Override
    public U createList(Stream<U> stream) {
        return this.delegate.createList(stream);
    }

    public U createByteList(ByteBuffer input) {
        byte[] byteArray = input.array();
        List<U> list0 = new ArrayList<>(byteArray.length + 1);
        list0.add(this.delegate.createString("B"));
        for (var b : byteArray) {
            list0.add(this.delegate.createByte(b));
        }
        return this.delegate.createList(list0.stream());
    }

    public U createIntList(IntStream input) {
        return this.delegate.createList(Stream.concat(
                Stream.of("I").map(this.delegate::createString), input.mapToObj(this.delegate::createInt)));
    }

    public U createLongList(LongStream input) {
        return this.delegate.createList(Stream.concat(
                Stream.of("L").map(this.delegate::createString), input.mapToObj(this.delegate::createLong)));
    }

    @Override
    public U remove(U object, String s) {
        return this.delegate.remove(object, s);
    }

    private static class UnStringifyOps<U> implements DynamicOps<U> {
        private final DynamicOps<U> delegate;

        public UnStringifyOps(DynamicOps<U> delegate) {
            this.delegate = delegate;
            this.primitiveArrayMark.put(delegate.createString("B"), 'B');
            this.primitiveArrayMark.put(delegate.createString("I"), 'I');
            this.primitiveArrayMark.put(delegate.createString("L"), 'L');
        }

        public U dispatchStringAsNbt(String string) {
            if (string == null) {
                return delegate.empty();
            }
            char chr = string.charAt(string.length() - 1);
            String valuePart = string.substring(0, string.length() - 1);
            if (valuePart.isEmpty()) {
                return delegate.createString(string);
            }
            switch (chr) {
                case 'b':
                    if (StringUtils.isDigit(valuePart, 10)) {
                        return delegate.createByte(Byte.parseByte(valuePart));
                    }
                    break;
                case 's':
                    if (StringUtils.isDigit(valuePart, 10)) {
                        return delegate.createShort(Short.parseShort(valuePart));
                    }
                    break;

                case 'i':
                    if (StringUtils.isDigit(valuePart, 10)) {
                        return delegate.createInt(Integer.parseInt(valuePart));
                    }
                    break;

                case 'l':
                    if (StringUtils.isDigit(valuePart, 10)) {
                        return delegate.createLong(Long.parseLong(valuePart)); // Long.valueOf(valuePart);
                    }
                    break;

                case 'f':
                    if (StringUtils.isDouble(valuePart)) {
                        return delegate.createFloat(Float.parseFloat(valuePart));
                    }
                    break;
                case 'd':
                    if (StringUtils.isDouble(valuePart)) {
                        return delegate.createDouble(Double.parseDouble(valuePart));
                    }
                    break;
                case '\"':
                    if (valuePart.charAt(0) == '\"') {
                        return delegate.createString(valuePart.substring(1));
                    }
                    break;
            }
            return delegate.createString(string);
        }

        private final Map<U, Character> primitiveArrayMark = new HashMap<>();

        public U dispatchListAsNbt(Stream<U> stream) {
            Iterator<U> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                return delegate.createList(Stream.empty());
            }
            U first = iterator.next();
            if (!iterator.hasNext()) {
                return delegate.createList(Stream.of(first));
            }
            List<U> list = new ArrayList<>();
            list.add(first);

            if (primitiveArrayMark.containsKey(first)) {
                char chr = primitiveArrayMark.get(first).charValue();
                sw:
                switch (chr) {
                    case 'B': {
                        ByteArrayList byteArrayList = new ByteArrayList();
                        while (iterator.hasNext()) {
                            U next = iterator.next();
                            list.add(next);
                            Number by = this.delegate.getNumberValue(next, null);
                            if (by == null) {
                                break sw;
                            }
                            byteArrayList.add(by.byteValue());
                        }
                        return this.delegate.createByteList(ByteBuffer.wrap(byteArrayList.toByteArray()));
                    }
                    case 'I': {
                        IntArrayList byteArrayList = new IntArrayList();
                        while (iterator.hasNext()) {
                            U next = iterator.next();
                            list.add(next);
                            Number by = this.delegate.getNumberValue(next, null);
                            if (by == null) {
                                break sw;
                            }
                            byteArrayList.add(by.intValue());
                        }
                        return this.delegate.createIntList(byteArrayList.intStream());
                    }
                    case 'L': {
                        LongArrayList byteArrayList = new LongArrayList();
                        while (iterator.hasNext()) {
                            U next = iterator.next();
                            list.add(next);
                            Number by = this.delegate.getNumberValue(next, null);
                            if (by == null) {
                                break sw;
                            }
                            byteArrayList.add(by.longValue());
                        }
                        return this.delegate.createLongList(byteArrayList.longStream());
                    }
                }
            }
            return this.delegate.createList(Stream.concat(
                    list.stream(),
                    StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)));
        }

        @Override
        public U empty() {
            return delegate.empty();
        }

        @Override
        public <V> V convertTo(DynamicOps<V> outOps, U input) {
            return delegate.convertTo(outOps, input);
        }

        @Override
        public DataResult<Number> getNumberValue(U input) {
            return delegate.getNumberValue(input);
        }

        @Override
        public U createNumeric(Number i) {
            return delegate.createNumeric(i);
        }

        @Override
        public DataResult<Boolean> getBooleanValue(U input) {
            return delegate.getBooleanValue(input);
        }

        @Override
        public DataResult<String> getStringValue(U input) {
            return delegate.getStringValue(input);
        }

        @Override
        public U createString(String value) {
            // 在这里添加解析字符串的逻辑
            return dispatchStringAsNbt(value);
        }

        @Override
        public DataResult<U> mergeToList(U list, U value) {
            return delegate.mergeToList(list, value);
        }

        @Override
        public DataResult<U> mergeToMap(U map, U key, U value) {
            return delegate.mergeToMap(map, key, value);
        }

        @Override
        public DataResult<Stream<Pair<U, U>>> getMapValues(U input) {
            return delegate.getMapValues(input);
        }

        @Override
        public U createMap(Stream<Pair<U, U>> map) {
            return delegate.createMap(map);
        }

        @Override
        public DataResult<Stream<U>> getStream(U input) {
            return delegate.getStream(input);
        }

        @Override
        public U createList(Stream<U> input) {
            return dispatchListAsNbt(input);
        }

        @Override
        public U remove(U input, String key) {
            return delegate.remove(input, key);
        }

        @Override
        public boolean compressMaps() {
            return delegate.compressMaps();
        }

        // 以下是默认方法的实现，直接调用delegate的对应方法

        @Override
        public U emptyMap() {
            return delegate.emptyMap();
        }

        @Override
        public U emptyList() {
            return delegate.emptyList();
        }

        @Override
        public Number getNumberValue(U input, Number defaultValue) {
            return delegate.getNumberValue(input, defaultValue);
        }

        @Override
        public U createByte(byte value) {
            return delegate.createByte(value);
        }

        @Override
        public U createShort(short value) {
            return delegate.createShort(value);
        }

        @Override
        public U createInt(int value) {
            return delegate.createInt(value);
        }

        @Override
        public U createLong(long value) {
            return delegate.createLong(value);
        }

        @Override
        public U createFloat(float value) {
            return delegate.createFloat(value);
        }

        @Override
        public U createDouble(double value) {
            return delegate.createDouble(value);
        }

        @Override
        public U createBoolean(boolean value) {
            return delegate.createBoolean(value);
        }

        @Override
        public DataResult<U> mergeToList(U list, List<U> values) {
            return delegate.mergeToList(list, values);
        }

        @Override
        public DataResult<U> mergeToMap(U map, Map<U, U> values) {
            return delegate.mergeToMap(map, values);
        }

        @Override
        public DataResult<U> mergeToMap(U map, MapLike<U> values) {
            return delegate.mergeToMap(map, values);
        }

        @Override
        public DataResult<U> mergeToPrimitive(U prefix, U value) {
            return delegate.mergeToPrimitive(prefix, value);
        }

        @Override
        public DataResult<Consumer<BiConsumer<U, U>>> getMapEntries(U input) {
            return delegate.getMapEntries(input);
        }

        @Override
        public DataResult<MapLike<U>> getMap(U input) {
            return delegate.getMap(input);
        }

        @Override
        public U createMap(Map<U, U> map) {
            return delegate.createMap(map);
        }

        @Override
        public DataResult<Consumer<Consumer<U>>> getList(U input) {
            return delegate.getList(input);
        }

        @Override
        public DataResult<ByteBuffer> getByteBuffer(U input) {
            return delegate.getByteBuffer(input);
        }

        @Override
        public U createByteList(ByteBuffer input) {
            return delegate.createByteList(input);
        }

        @Override
        public DataResult<IntStream> getIntStream(U input) {
            return delegate.getIntStream(input);
        }

        @Override
        public U createIntList(IntStream input) {
            return delegate.createIntList(input);
        }

        @Override
        public DataResult<LongStream> getLongStream(U input) {
            return delegate.getLongStream(input);
        }

        @Override
        public U createLongList(LongStream input) {
            return delegate.createLongList(input);
        }

        @Override
        public DataResult<U> get(U input, String key) {
            return delegate.get(input, key);
        }

        @Override
        public DataResult<U> getGeneric(U input, U key) {
            return delegate.getGeneric(input, key);
        }

        @Override
        public U set(U input, String key, U value) {
            return delegate.set(input, key, value);
        }

        @Override
        public U update(U input, String key, Function<U, U> function) {
            return delegate.update(input, key, function);
        }

        @Override
        public U updateGeneric(U input, U key, Function<U, U> function) {
            return delegate.updateGeneric(input, key, function);
        }

        @Override
        public ListBuilder<U> listBuilder() {
            return delegate.listBuilder();
        }

        @Override
        public RecordBuilder<U> mapBuilder() {
            return delegate.mapBuilder();
        }

        @Override
        public <E> Function<E, DataResult<U>> withEncoder(Encoder<E> encoder) {
            return delegate.withEncoder(encoder);
        }

        @Override
        public <E> Function<U, DataResult<Pair<E, U>>> withDecoder(Decoder<E> decoder) {
            return delegate.withDecoder(decoder);
        }

        @Override
        public <E> Function<U, DataResult<E>> withParser(Decoder<E> decoder) {
            return delegate.withParser(decoder);
        }

        @Override
        public <V> V convertList(DynamicOps<V> outOps, U input) {
            return delegate.convertList(outOps, input);
        }

        @Override
        public <V> V convertMap(DynamicOps<V> outOps, U input) {
            return delegate.convertMap(outOps, input);
        }
    }
}
