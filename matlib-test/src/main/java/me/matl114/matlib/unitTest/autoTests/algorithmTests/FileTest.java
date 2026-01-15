package me.matl114.matlib.unitTest.autoTests.algorithmTests;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.algorithms.algorithm.FileUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;

public class FileTest implements TestCase {

    @Data
    public static class A {
        int a;
        short b;
        long c;
        double d;
        float e;
        int[] f;
        B g;
        A[] h;
    }

    @Data
    public static class B {
        int a;
        short b;
        long c;
        double d;
        float e;
    }

    @Data
    public static class C {
        A a;
        B b;
        A c;
    }

    @Getter
    @Setter
    public static class PrimitiveData {
        // 整数类型
        byte byteValue;
        short shortValue;
        int intValue;
        long longValue;

        // 浮点类型
        float floatValue;
        double doubleValue;

        // 边界值
        byte minByte = Byte.MIN_VALUE;
        byte maxByte = Byte.MAX_VALUE;
        short minShort = Short.MIN_VALUE;
        short maxShort = Short.MAX_VALUE;
        int minInt = Integer.MIN_VALUE;
        int maxInt = Integer.MAX_VALUE;
        long minLong = Long.MIN_VALUE;
        long maxLong = Long.MAX_VALUE;
        float minFloat = Float.MIN_VALUE;
        float maxFloat = Float.MAX_VALUE;
        float nanFloat = Float.NaN;
        float posInfFloat = Float.POSITIVE_INFINITY;
        float negInfFloat = Float.NEGATIVE_INFINITY;
        double minDouble = Double.MIN_VALUE;
        double maxDouble = Double.MAX_VALUE;
        double nanDouble = Double.NaN;
        double posInfDouble = Double.POSITIVE_INFINITY;
        double negInfDouble = Double.NEGATIVE_INFINITY;

        // 构造方法
        public PrimitiveData() {
            this.byteValue = (byte) 123;
            this.shortValue = (short) 12345;
            this.intValue = 1234567890;
            this.longValue = 1234567890123456789L;
            this.floatValue = 123.456789f;
            this.doubleValue = 123456.789012345;
        }
    }

    @Data
    public static class AMap {
        Map<String, A> map;
    }

    public static class AMap2 extends HashMap<String, A> {}

    @OnlineTest(name = "test yaml utils")
    public void test_yaml_utils() throws Exception {
        PrimitiveData original = new PrimitiveData();
        // 2. 序列化为 YAML
        String yamlString = FileUtils.dumpYaml(original);
        Debug.logger("Serialized YAML:");
        Debug.logger(yamlString);

        // 3. 反序列化回对象
        PrimitiveData deserialized = FileUtils.readYamlString(yamlString);
        Debug.logger(deserialized);

        C c = FileUtils.readResourceYaml("tests/algorithm/yaml_data_class.yml", C.class);
        Debug.logger(c);
        String yamlStr = FileUtils.dumpYaml(c);
        Debug.logger(yamlStr);
        C c2 = FileUtils.readYamlString(yamlStr);
        AssertEq(c, c2);
        Debug.logger("Yaml object test success");

        AMap originalMap = FileUtils.readResourceYaml("tests/algorithm/yaml_data_class_map.yml", AMap.class);
        Assert(originalMap.map.values().stream().allMatch(s -> s.getClass() == A.class));

        //        AMap2 originMap2 = FileUtils.readResourceYaml("tests/algorithm/yaml_data_class_map_2.yml",
        // AMap2.class);
        //        Assert(originMap2.values().stream().noneMatch(s -> s.getClass() == A.class));
    }
}
