package me.matl114.matlibAdaptor.algorithms.interfaces;

import java.util.logging.Logger;
import me.matl114.matlibAdaptor.proxy.annotations.AdaptorInterface;
import me.matl114.matlibAdaptor.proxy.annotations.DefaultMethod;
import me.matl114.matlibAdaptor.proxy.annotations.InternalMethod;

/**
 * Initialization contains the startup/shutdown information of a progress,
 * the management of Utils,PluginHelper,Plugin
 */
@AdaptorInterface
public interface Initialization {
    /**
     * return if this progress is in test
     * @return
     */
    public boolean isTestMode();

    public Initialization testMode(boolean testMode);

    /**
     * return the progress's displayName
     * @return
     */
    public String getDisplayName();

    @InternalMethod
    public Initialization displayName(final String displayName);

    /**
     * control the progress's start/stop
     * @return
     */
    @DefaultMethod
    default void onStart() {
        onEnable();
    }

    @InternalMethod
    public Initialization onEnable();

    @DefaultMethod
    default void onStop() {
        onDisable();
    }

    @InternalMethod
    public Initialization onDisable();

    /**
     * get the progress's logger
     * @return
     */
    public Logger getLogger();
    /**
     * builder method
     * @return
     * @param <T>
     */
    @InternalMethod
    default <T extends Initialization> T cast() {
        return (T) this;
    }
}
