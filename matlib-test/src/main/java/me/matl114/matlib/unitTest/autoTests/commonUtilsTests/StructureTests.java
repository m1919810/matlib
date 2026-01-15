package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.*;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.ConfigUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.serialization.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

public class StructureTests implements TestCase {

    private static final Gson GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private static final Yaml YAML = new Yaml();

    @OnlineTest(name = "test yaml readwrite")
    public void test_yaml_readwrite_map() throws Throwable {
        disableTest();
        Debug.logger("=== YAML Map<String, Object> Type Test ===");

        // 1. 创建包含所有原始类型和边界值的 Map
        Map<String, Object> originalMap = createTestMap();

        Debug.logger("Original Map types:");
        Object mapTransfer = TypeOps.I.convertTo(StringOps.I, originalMap);
        // 2. 序列化为 YAML
        String yamlString = YAML.dump(mapTransfer);
        Debug.logger("Serialized YAML:");
        Debug.logger(yamlString);

        // 3. 反序列化回 Map
        Map<String, Object> deserializedOriginMap = YAML.load(yamlString);
        Map<String, Object> deserializedMap =
                (Map<String, Object>) StringOps.I.convertTo(TypeOps.I, deserializedOriginMap);
        Debug.logger("Deserialized Map types:");
        logMapTypes(deserializedMap);

        // 4. 验证所有值
        boolean allPassed = true;

        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Object originalValue = entry.getValue();
            Object deserializedValue = deserializedMap.get(key);

            String typeInfo = getTypeInfo(originalValue);
            Debug.logger("Testing: " + key + " (" + typeInfo + ")");

            boolean passed = compareValues(key, originalValue, deserializedValue, true);
            if (!passed) {
                allPassed = false;
            }
        }

        Debug.logger("Test bukkit yaml loader");
        Object map1 = TypeOps.I.convertTo(StringOps.I, originalMap);
        ConfigurationSection configuration = CodecUtils.decode(BukkitCodecs.MEMORY_SECTION, TypeOps.I, map1);
        YamlConfiguration config = new YamlConfiguration();
        ConfigUtils.copySection(config, configuration);
        String bukkitString = config.saveToString();
        Debug.logger("Serialized YAML:");
        Debug.logger(bukkitString);
        YamlConfiguration deserializedConfig = new YamlConfiguration();
        deserializedConfig.loadFromString(bukkitString);
        Map<String, Object> deserializedMap2Origin =
                (Map<String, Object>) CodecUtils.encode(BukkitCodecs.MEMORY_SECTION, TypeOps.I, deserializedConfig);
        Map<String, Object> deserializedMap2 =
                (Map<String, Object>) StringOps.I.convertTo(TypeOps.I, deserializedMap2Origin);
        Debug.logger("Deserialized Map types:");
        logMapTypes(deserializedMap2);
        // 5. 测试嵌套结构
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Object originalValue = entry.getValue();
            Object deserializedValue = deserializedMap2.get(key);

            String typeInfo = getTypeInfo(originalValue);
            Debug.logger("Testing: " + key + " (" + typeInfo + ")");

            boolean passed = compareValues(key, originalValue, deserializedValue, true);
            if (!passed) {
                allPassed = false;
            }
        }
        if (allPassed) {
            Debug.logger("✓ YAML Map<String, Object> test passed!\n");
        } else {
            Debug.logger("✗ YAML Map<String, Object> test failed!\n");
        }
    }

    @OnlineTest(name = "test json readwrite")
    public void test_json_readwrite_jsonobject() {
        disableTest();
        Debug.logger("=== JSON JsonObject Type Test ===");

        // 1. 创建包含所有原始类型的 JsonObject
        JsonObject originalJson = createTestJsonObject();

        Debug.logger("Original JsonObject:");
        Debug.logger(originalJson.toString());

        // 2. 序列化为 JSON
        String jsonString = GSON.toJson(originalJson);
        Debug.logger("\nSerialized JSON:");
        Debug.logger(jsonString);

        // 3. 反序列化回 JsonObject
        JsonObject deserializedJson = GSON.fromJson(jsonString, JsonObject.class);

        Debug.logger("\nDeserialized JsonObject:");
        Debug.logger(deserializedJson.toString());

        // 4. 验证所有值
        boolean allPassed = true;

        for (Map.Entry<String, JsonElement> entry : originalJson.entrySet()) {
            String key = entry.getKey();
            JsonElement originalElement = entry.getValue();
            JsonElement deserializedElement = deserializedJson.get(key);

            String typeInfo = getJsonTypeInfo(originalElement);
            Debug.logger("\nTesting: " + key + " (" + typeInfo + ")");

            Object originalValue = getValueFromJsonElement(originalElement);
            Object deserializedValue = getValueFromJsonElement(deserializedElement);

            boolean passed = compareValues(key, originalValue, deserializedValue, true);
            if (!passed) {
                allPassed = false;
            }
        }

        // 5. 测试 JSON 类型标签（Gson 的原始类型处理）
        Debug.logger("\n--- Testing JSON Type Preservation ---");
        testJsonTypePreservation();

        if (allPassed) {
            Debug.logger("✓ JSON JsonObject test passed!\n");
        } else {
            Debug.logger("✗ JSON JsonObject test failed!\n");
        }
    }

    @OnlineTest(name = "test stringify codec json")
    public void test_stringify_codec_json() {
        Debug.logger("=== JSON Stringify Test ===");
        Map<String, Object> testMap = createTestMap();
        DynamicOps<JsonElement> jsonOp = new StringifyOps<>(JsonOps.INSTANCE);
        JsonElement jsonElement = TypeOps.I.convertTo(jsonOp, testMap);
        Debug.logger("\nSerialized JSON:");
        String stringifiedJson = GSON.toJson(jsonElement);
        Debug.logger(stringifiedJson);
        JsonElement deserializedJson = GSON.fromJson(stringifiedJson, JsonObject.class);
        Debug.logger("\nDeserialized JSON:");
        Map<String, Object> deserializationResult = (Map<String, Object>) jsonOp.convertTo(TypeOps.I, deserializedJson);
        Debug.logger(deserializedJson.toString());
        logMapTypes(deserializationResult);
        for (Map.Entry<String, Object> entry : testMap.entrySet()) {
            String key = entry.getKey();
            Object originalValue = entry.getValue();
            Object deserializedValue = deserializationResult.get(key);

            String typeInfo = getTypeInfo(originalValue);
            Debug.logger("Testing: " + key + " (" + typeInfo + ")");

            compareValues(key, originalValue, deserializedValue, true);
        }
    }

    private Map<String, Object> createTestMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        // 整数类型
        map.put("byte", (byte) 123);
        map.put("short", (short) 12345);
        map.put("int", 1234567890);
        map.put("integer", Integer.valueOf(987654321));
        map.put("long", 1234567890123456789L);
        map.put("longObject", Long.valueOf(987654321098765432L));

        // 浮点类型
        map.put("float", 123.456789f);
        map.put("floatObject", Float.valueOf(987.654321f));
        map.put("double", 123456.789012345);
        map.put("doubleObject", Double.valueOf(987654.321098765));

        // 边界值
        map.put("minByte", Byte.MIN_VALUE);
        map.put("maxByte", Byte.MAX_VALUE);
        map.put("minShort", Short.MIN_VALUE);
        map.put("maxShort", Short.MAX_VALUE);
        map.put("minInt", Integer.MIN_VALUE);
        map.put("maxInt", Integer.MAX_VALUE);
        map.put("minLong", Long.MIN_VALUE);
        map.put("maxLong", Long.MAX_VALUE);
        map.put("minFloat", Float.MIN_VALUE);
        map.put("maxFloat", Float.MAX_VALUE);
        map.put("nanFloat", Float.NaN);
        map.put("posInfFloat", Float.POSITIVE_INFINITY);
        map.put("negInfFloat", Float.NEGATIVE_INFINITY);
        map.put("minDouble", Double.MIN_VALUE);
        map.put("maxDouble", Double.MAX_VALUE);
        map.put("nanDouble", Double.NaN);
        map.put("posInfDouble", Double.POSITIVE_INFINITY);
        map.put("negInfDouble", Double.NEGATIVE_INFINITY);

        // 其他类型
        map.put("string", "Hello World!");
        map.put("boolean", true);
        map.put("booleanObject", Boolean.FALSE);

        // 列表类型测试 - 新增
        map.put("intList", Arrays.asList(1, 2, 3, 4, 5));
        map.put("byteList", Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4));
        map.put("longList", Arrays.asList(1000L, 2000L, 3000L));
        map.put("floatList", Arrays.asList(1.1f, 2.2f, 3.3f));
        map.put("doubleList", Arrays.asList(1.11, 2.22, 3.33));
        map.put("stringList", Arrays.asList("A", "B", "C"));
        map.put("booleanList", Arrays.asList(true, false, true));

        // 混合类型列表
        map.put("mixedList", Arrays.asList(1, "two", 3.0, true));

        return map;
    }

    private JsonObject createTestJsonObject() {
        JsonObject json = new JsonObject();

        // 整数类型
        json.addProperty("byte", (byte) 123);
        json.addProperty("short", (short) 12345);
        json.addProperty("int", 1234567890);
        json.addProperty("integer", Integer.valueOf(987654321));
        json.addProperty("long", 1234567890123456789L);
        json.addProperty("longObject", Long.valueOf(987654321098765432L));

        // 浮点类型
        json.addProperty("float", 123.456789f);
        json.addProperty("floatObject", Float.valueOf(987.654321f));
        json.addProperty("double", 123456.789012345);
        json.addProperty("doubleObject", Double.valueOf(987654.321098765));

        // 边界值
        json.addProperty("minByte", Byte.MIN_VALUE);
        json.addProperty("maxByte", Byte.MAX_VALUE);
        json.addProperty("minShort", Short.MIN_VALUE);
        json.addProperty("maxShort", Short.MAX_VALUE);
        json.addProperty("minInt", Integer.MIN_VALUE);
        json.addProperty("maxInt", Integer.MAX_VALUE);
        json.addProperty("minLong", Long.MIN_VALUE);
        json.addProperty("maxLong", Long.MAX_VALUE);
        json.addProperty("minFloat", Float.MIN_VALUE);
        json.addProperty("maxFloat", Float.MAX_VALUE);
        json.addProperty("nanFloat", Float.NaN);
        json.addProperty("posInfFloat", Float.POSITIVE_INFINITY);
        json.addProperty("negInfFloat", Float.NEGATIVE_INFINITY);
        json.addProperty("minDouble", Double.MIN_VALUE);
        json.addProperty("maxDouble", Double.MAX_VALUE);
        json.addProperty("nanDouble", Double.NaN);
        json.addProperty("posInfDouble", Double.POSITIVE_INFINITY);
        json.addProperty("negInfDouble", Double.NEGATIVE_INFINITY);

        // 其他类型
        json.addProperty("string", "Hello World!");
        json.addProperty("boolean", true);
        json.addProperty("booleanObject", Boolean.FALSE);

        // 列表类型测试 - 新增
        // 注意：Gson 需要将列表转换为 JsonArray
        // 这里我们使用 Gson 的便捷方法创建数组
        json.add("intList", GSON.toJsonTree(Arrays.asList(1, 2, 3, 4, 5)));
        json.add("byteList", GSON.toJsonTree(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
        json.add("longList", GSON.toJsonTree(Arrays.asList(1000L, 2000L, 3000L)));
        json.add("floatList", GSON.toJsonTree(Arrays.asList(1.1f, 2.2f, 3.3f)));
        json.add("doubleList", GSON.toJsonTree(Arrays.asList(1.11, 2.22, 3.33)));
        json.add("stringList", GSON.toJsonTree(Arrays.asList("A", "B", "C")));
        json.add("booleanList", GSON.toJsonTree(Arrays.asList(true, false, true)));

        // 混合类型列表
        json.add("mixedList", GSON.toJsonTree(Arrays.asList(1, "two", 3.0, true)));

        return json;
    }

    // 其他方法保持不变...
    private void logMapTypes(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String type = value == null ? "null" : value.getClass().getName();
            Debug.logger(String.format("%-20s: %-30s (type: %s)", entry.getKey(), String.valueOf(value), type));
        }
    }

    private String getTypeInfo(Object value) {
        if (value == null) return "null";
        return value.getClass().getSimpleName() + " (" + value.getClass().getName() + ")";
    }

    private String getJsonTypeInfo(JsonElement element) {
        if (element == null || element.isJsonNull()) return "null";
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) return "boolean";
            if (primitive.isNumber()) return "number";
            if (primitive.isString()) return "string";
        }
        if (element.isJsonObject()) return "object";
        if (element.isJsonArray()) return "array";
        return "unknown";
    }

    private Object getValueFromJsonElement(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            if (primitive.isNumber()) {
                return primitive.getAsNumber();
                // 尝试保持精度
            }
            if (primitive.isString()) return primitive.getAsString();
        }
        if (element.isJsonArray()) {
            // 将 JsonArray 转换为 List
            List<Object> list = new ArrayList<>();
            for (JsonElement item : element.getAsJsonArray()) {
                list.add(getValueFromJsonElement(item));
            }
            return list;
        }
        return element;
    }

    private boolean compareValues(String key, Object original, Object deserialized, boolean checkType) {
        // 处理 null 值
        if (original == null) {
            if (deserialized == null) {
                Debug.logger("  ✓ null value preserved");
                return true;
            } else {
                Debug.logger("  ✗ null value changed to: " + deserialized);
                return false;
            }
        }

        // 检查类型
        if (checkType) {
            String originalType = original.getClass().getName();
            String deserializedType =
                    deserialized == null ? "null" : deserialized.getClass().getName();

            Debug.logger("  Type check - Original: " + originalType + ", Deserialized: " + deserializedType);

            // YAML 可能无法完全保留原始类型（如 byte 可能变成 int）
            // 所以我们只记录类型变化，但不作为失败条件
            if (!originalType.equals(deserializedType)) {
                Debug.logger("  ⚠ Type changed from " + originalType + " to " + deserializedType);
            }
        }

        // 特殊浮点值处理
        if (original instanceof Float) {
            Float f1 = (Float) original;
            if (deserialized instanceof Number) {
                Float f2 = ((Number) deserialized).floatValue();
                if (Float.isNaN(f1)) {
                    boolean isNan = Float.isNaN(f2);
                    Debug.logger("  " + (isNan ? "✓" : "✗") + " NaN preserved: " + isNan);
                    return isNan;
                } else if (Float.isInfinite(f1)) {
                    boolean isInf = Float.isInfinite(f2) && Float.compare(f1, f2) == 0;
                    Debug.logger("  " + (isInf ? "✓" : "✗") + " Infinity preserved: " + isInf);
                    return isInf;
                }
            }
        } else if (original instanceof Double) {
            Double d1 = (Double) original;
            if (deserialized instanceof Number) {
                Double d2 = ((Number) deserialized).doubleValue();
                if (Double.isNaN(d1)) {
                    boolean isNan = Double.isNaN(d2);
                    Debug.logger("  " + (isNan ? "✓" : "✗") + " NaN preserved: " + isNan);
                    return isNan;
                } else if (Double.isInfinite(d1)) {
                    boolean isInf = Double.isInfinite(d2) && Double.compare(d1, d2) == 0;
                    Debug.logger("  " + (isInf ? "✓" : "✗") + " Infinity preserved: " + isInf);
                    return isInf;
                }
            }
        }

        // 列表比较
        if (original instanceof List && deserialized instanceof List) {
            List<?> list1 = (List<?>) original;
            List<?> list2 = (List<?>) deserialized;

            if (list1.size() != list2.size()) {
                Debug.logger("  ✗ List size mismatch: " + list1.size() + " vs " + list2.size());
                return false;
            }

            boolean allItemsMatch = true;
            for (int i = 0; i < list1.size(); i++) {
                Object item1 = list1.get(i);
                Object item2 = list2.get(i);
                boolean itemMatch = compareValues(key + "[" + i + "]", item1, item2, false);
                if (!itemMatch) {
                    allItemsMatch = false;
                }
            }

            Debug.logger("  " + (allItemsMatch ? "✓" : "✗") + " List comparison: " + list1 + " vs " + list2);
            return allItemsMatch;
        }

        // 数值比较
        if (original instanceof Number && deserialized instanceof Number) {
            Number n1 = (Number) original;
            Number n2 = (Number) deserialized;

            // 对于浮点数，使用容差
            if (original instanceof Float
                    || original instanceof Double
                    || deserialized instanceof Float
                    || deserialized instanceof Double) {
                double diff = Math.abs(n1.doubleValue() - n2.doubleValue());
                double tolerance = Math.max(Math.abs(n1.doubleValue()), Math.abs(n2.doubleValue())) * 1e-15;
                boolean equal = diff <= tolerance || diff <= 1e-15;
                Debug.logger("  " + (equal ? "✓" : "✗") + " Floating point comparison: " + n1.doubleValue() + " vs "
                        + n2.doubleValue() + " (diff: " + diff + ")");
                return equal;
            }

            // 对于整数，精确比较
            boolean equal = n1.longValue() == n2.longValue();
            Debug.logger(
                    "  " + (equal ? "✓" : "✗") + " Integer comparison: " + n1.longValue() + " vs " + n2.longValue());
            return equal;
        }

        // 字符串和其他类型比较
        boolean equal = original.equals(deserialized);
        Debug.logger("  " + (equal ? "✓" : "✗") + " Object comparison: " + original + " vs " + deserialized);
        return equal;
    }

    private void testJsonTypePreservation() {
        Debug.logger("Testing JSON type preservation strategies...");

        // 测试 Gson 的类型处理
        JsonObject typeTest = new JsonObject();

        // 添加各种类型的值
        typeTest.addProperty("smallInt", 123);
        typeTest.addProperty("largeInt", 1234567890123L);
        typeTest.addProperty("floatVal", 123.456f);
        typeTest.addProperty("doubleVal", 123456.789012);
        typeTest.addProperty("stringNum", "123.456"); // 字符串形式的数字

        String json = GSON.toJson(typeTest);
        Debug.logger("\nType test JSON:");
        Debug.logger(json);

        // 解析并检查类型
        JsonObject parsed = GSON.fromJson(json, JsonObject.class);
        Debug.logger("\nParsed types:");
        for (Map.Entry<String, JsonElement> entry : parsed.entrySet()) {
            JsonElement element = entry.getValue();
            String typeInfo = getJsonTypeInfo(element);
            String value =
                    element.isJsonPrimitive() ? element.getAsJsonPrimitive().getAsString() : element.toString();
            Debug.logger(entry.getKey() + ": " + value + " (" + typeInfo + ")");
        }
    }
}
