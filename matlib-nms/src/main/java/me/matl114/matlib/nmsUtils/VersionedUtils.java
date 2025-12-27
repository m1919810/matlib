package me.matl114.matlib.nmsUtils;

public class VersionedUtils {
    public static RuntimeException removal() {
        return new UnsupportedOperationException("removal");
    }

    public static RuntimeException versionLow() {
        return new UnsupportedOperationException("low version");
    }

    public static void checkVersionAnnotations(Class<?> constValueClass) {
        // todo complete, check @DependOnVersion
    }
}
