package me.matl114.matlib.utils.version;

import javax.annotation.Nonnull;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Experimental;
import me.matl114.matlib.utils.Debug;
import org.bukkit.Bukkit;

public enum Version {
    unknown("unknown", Integer.MAX_VALUE),
    v1_20_R1("v1_20_R1", 15),
    v1_20_R2("v1_20_R2", 18),
    v1_20_R3("v1_20_R3", 26),
    v1_20_R4("v1_20_R4", 41),
    v1_21_R1("v1_21_R1", 48),
    v1_21_R2("v1_21_R2", 57),
    @Experimental
    //1.21.4
    v1_21_R3("v1_21_R3", 61),
    //1.21.5
    v1_21_R4("v1_21_R4", 71),
    v1_21_R5("v1_21_R5", 80),
    v1_21_R6("v1_21_R6", 81),
    v1_21_R7("v1_21_R7", 88);

    private Version(String name, int datapackNumber) {
        this.name = name;
        this.datapackNumber = datapackNumber;
    }

    private String name;

    @Getter
    private int datapackNumber;

    static Version INSTANCE;

    public static Version getVersionInstance() {
        if (INSTANCE == null) {
            INSTANCE = getVersionInstance0();
        }
        return INSTANCE;
    }

    @Nonnull
    private static Version getVersionInstance0() {
        String version = null;
        try {
            String[] path = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
            if (path.length >= 4) {
                version = path[3].trim();
            } else {
                version = Bukkit.getServer().getBukkitVersion().split("-")[0].trim();
            }
            for (Version v : Version.values()) {
                if (v.name.equals(version)) {
                    Debug.logger("Using version", version);
                    return v;
                }
            }
            switch (version) {
                case "1.20.5":
                case "1.20.6":
                    Debug.logger("Using version", v1_20_R4.name);
                    return v1_20_R4;
                case "1.21":
                case "1.21.1":
                    Debug.logger("Using version", v1_21_R1.name);
                    return v1_21_R1;
                case "1.21.2":
                case "1.21.3":
                    Debug.logger("Using version", v1_21_R2.name);
                    return v1_21_R2;
                case "1.21.4":
                    Debug.logger("Using version", v1_21_R3.name);
                    return v1_21_R3;
                case "1.21.5":
                    Debug.logger("Using version", v1_21_R4.name);
                    return v1_21_R4;
                case "1.21.6":
                case "1.21.7":
                    Debug.logger("Using version", v1_21_R5.name);
                    return v1_21_R5;
                case "1.21.8":
                    Debug.logger("Using version", v1_21_R6.name);
                    return v1_21_R6;
                case "1.21.9":
                    Debug.logger("Using version", v1_21_R7.name);
                    return v1_21_R7;
                default:
            }
            throw new RuntimeException("Version not supported for " + version);
        } catch (Throwable e) {
            Debug.logger(e, "Fail to create version specific feature :", version);
            Debug.logger("Using default versiond feature ");
            return unknown;
        }
    }

    public boolean isAtLeast(Version v2) {
        return versionAtLeast(this, v2);
    }

    public static boolean versionAtLeast(Version v1, Version v2) {
        return v1.datapackNumber >= v2.datapackNumber;
    }

    public static boolean isDataComponentVersion() {
        return getVersionInstance().isAtLeast(v1_20_R4);
    }
}
