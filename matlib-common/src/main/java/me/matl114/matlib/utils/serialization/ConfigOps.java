package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.matl114.matlib.utils.ConfigUtils;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class ConfigOps implements DynamicOps<Object> {
    public static final ConfigOps I = new ConfigOps();

    @Override
    public Object empty() {
        return null;
    }

    public Object emptyMap() {
        return new MemoryConfiguration();
    }

    public Object emptyList() {
        return List.of();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input == null) {
            return outOps.empty();
        } else if (input instanceof ConfigurationSection) {
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
        if (!(object instanceof ConfigurationSection) && object != null) {
            return DataHelper.A.I.error(() -> "Not a Map");
        }
        ConfigurationSection map0 =
                object != null ? ConfigUtils.copySection((ConfigurationSection) object) : new MemoryConfiguration();
        map0.set(t1.toString(), t2);
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, Map<Object, Object> values) {
        if (!(object instanceof Map) && object != null) {

            return DataHelper.A.I.error(() -> "Not a configuration");
        }
        ConfigurationSection map0 =
                object != null ? ConfigUtils.copySection((ConfigurationSection) object) : new MemoryConfiguration();
        for (Map.Entry<Object, Object> entry : values.entrySet()) {
            map0.set(entry.getKey().toString(), entry.getValue());
        }
        return DataHelper.A.I.success(map0);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object object) {

        return object instanceof ConfigurationSection map0
                ? DataHelper.A.I.success(map0.getKeys(false).stream()
                        .map(key -> Pair.of(
                                key, map0.get(key)))) // .entrySet().stream().map(entry -> Pair.of(entry.getKey(),
                // entry.getValue())))
                : DataHelper.A.I.error(() -> "not a configuration");
    }

    @Override
    public DataResult<Object> mergeToMap(Object object, MapLike<Object> values) {
        if (!(object instanceof ConfigurationSection) && object != null) {

            return DataHelper.A.I.error(() -> "Not a Map");
        }
        ConfigurationSection map0 = new MemoryConfiguration();
        if (object instanceof ConfigurationSection sec) {
            ConfigUtils.copySection(map0, sec);
        }
        values.entries().forEach(pr -> map0.set(pr.getFirst().toString(), pr.getSecond()));
        return DataHelper.A.I.success(map0);
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> stream) {
        ConfigurationSection map0 = new MemoryConfiguration();
        stream.forEach(pair -> map0.set(pair.getFirst().toString(), pair.getSecond()));
        return map0;
    }

    public Object createMap(Map<Object, Object> map) {
        ConfigurationSection map0 = new MemoryConfiguration();
        map.forEach((key, value) -> map0.set(key.toString(), value));
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
        if (object instanceof ConfigurationSection map0) {
            ConfigurationSection map2 = ConfigUtils.copySection(map0);
            map2.set(s, null);
            return map2;
        } else {
            return object;
        }
    }

    public String toString() {
        return "ConfigOp";
    }
}
