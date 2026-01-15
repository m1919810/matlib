package me.matl114.matlib.core;

import java.lang.reflect.Method;
import java.util.logging.Logger;
import lombok.Getter;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlibAdaptor.algorithms.interfaces.Initialization;
import org.bukkit.plugin.Plugin;

public class UtilInitialization implements Initialization {
    protected final Plugin plugin;

    protected final String name;

    @Getter
    protected String displayName;

    @Getter
    protected boolean testMode = false;

    public UtilInitialization testMode(boolean testMode) {
        this.testMode = testMode;
        return this;
    }

    public UtilInitialization displayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    /**
     * load as a Util lib
     * when entering from this entry ,final jar file may be smaller
     * @param loader
     * @param loaderName
     */
    public UtilInitialization(Plugin loader, String loaderName) {
        this.plugin = loader;
        this.name = loaderName;
    }

    public UtilInitialization onEnable() {
        Manager.onEnable();
        try {
            Method init = Debug.class.getDeclaredMethod("init", String.class);
            init.setAccessible(true);
            init.invoke(null, name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        //        Debug.setDebugMod(this.testMode);

        //        else{
        //            //in test
        //        }
        return this;
    }

    public UtilInitialization onDisable() {
        Manager.onDisable();
        return this;
    }

    @Override
    public Logger getLogger() {
        return Debug.getLog();
    }
}
