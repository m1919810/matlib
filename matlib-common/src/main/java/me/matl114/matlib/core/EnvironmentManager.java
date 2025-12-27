package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionedFeature;

@Deprecated(forRemoval = true)
@AutoInit(level = "Util")
public class EnvironmentManager {
    @Getter
    private static EnvironmentManager manager = new EnvironmentManager();

    @Getter
    private Version version;

    private VersionedFeature versioned;

    public VersionedFeature getVersioned() {
        // lazily init
        if (versioned == null) {
            versioned = VersionedFeature.getFeature();
        }
        return versioned;
    }
}
