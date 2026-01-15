package me.matl114.matlib.utils.experimential;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;
import me.matl114.matlib.common.lang.annotations.*;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;
import org.bukkit.Bukkit;

@Experimental
@Deprecated
@DoNotCall
@NotRecommended
@UnsafeOperation
@VersionAtLeast(Version.v1_20_R2)
public class FakeSchedular {
    private static boolean enabled = false;
    private static Constructor<? extends Thread> threadConstructor;

    @ForceOnMainThread
    public static void init() {
        Version v = FakeSchedular.class.getAnnotation(VersionAtLeast.class).value();
        if (!Version.getVersionInstance().isAtLeast(v)) {
            enabled = false;
            Debug.logger("Fake Schedular thread not enabled");
            //            throw new UnsupportedOperationException("Version should be at least " +
            // FakeSchedular.class.getAnnotation(VersionAtLeast.class).value());
        } else {
            try {
                Preconditions.checkState(Bukkit.isPrimaryThread());
                threadConstructor = Thread.currentThread().getClass().getConstructor(Runnable.class, String.class);
                threadConstructor.setAccessible(true);
                enabled = true;
                Debug.logger("Fake Schedular thread enabled");
            } catch (NoSuchMethodException e) {
                enabled = false;
                Debug.logger("Fake Schedular thread not enabled");
            }
        }
    }

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * create a fake server thread to run the task,
     * the thread will already be started when return
     * @param runnable
     * @return
     */
    public static Thread runSync(Runnable runnable) {
        if (enabled) {
            try {
                Thread thread = threadConstructor.newInstance(
                        runnable, "Fake Server Thread - " + taskCounter.incrementAndGet());
                thread.start();
                return thread;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            Debug.logger("Fake Schedular thread not enabled");
            return null;
        }
    }
}
