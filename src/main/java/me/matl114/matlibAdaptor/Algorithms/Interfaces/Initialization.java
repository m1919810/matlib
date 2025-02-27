package me.matl114.matlibAdaptor.Algorithms.Interfaces;

import me.matl114.matlibAdaptor.Proxy.Annotations.AdaptorInterface;
import me.matl114.matlibAdaptor.Proxy.Annotations.InternalMethod;

import java.util.logging.Logger;

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
    public Initialization displayName(final String displayName) ;

    /**
     * control the progress's start/stop
     * @return
     */
    default void onStart(){
        onEnable();
    }
    @InternalMethod
    public Initialization onEnable();
    default void onStop(){
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
     * @param clazz
     * @return
     * @param <T>
     */
    @InternalMethod
    default  <T extends Initialization> T cast(Class<T> clazz) {
        return (T)this;
    }
}
