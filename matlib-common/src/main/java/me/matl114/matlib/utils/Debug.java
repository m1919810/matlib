package me.matl114.matlib.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.core.AutoInit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

@AutoInit(level = "Util")
public class Debug {

    @Getter
    private static Logger log = Logger.getLogger("Debug");

    private static Logger testlog = Logger.getLogger("Test");
    private static boolean start = false;
    private static boolean pos = false;

    @Getter
    public static boolean debugMod = false;

    private static boolean interceptorRegistered = false;
    private static boolean interceptLogger = false;
    private static List<String> interceptionString = new ArrayList<>();

    public static void debug(Throwable debug) {
        if (debugMod) {
            logger(debug);
        }
    }

    public static void debug(Object debug) {
        if (debugMod) {
            logger(debug);
        }
    }

    public static void debug(Object... debug) {
        if (debugMod) {
            logger(debug);
        }
    }
    //    public static void setBreakPoint(int i, boolean b){
    //        synchronized(lock){
    //            AtomicBoolean boo= getBreakPoint(i);
    //            setValues[i].set(false);
    //            breakValues[i]=null;
    //            breakPoints[i].set(b);
    //        }
    //    }
    //    public static Object getBreakValue(int i){
    //        synchronized(lock){
    //            return breakValues[i];
    //        }
    //    }
    //    public static boolean getHasSetValue(int i){
    //        synchronized(lock){
    //            return setValues[i].get();
    //        }
    //    }
    //    public static void setBreakValue(int i,Object value){
    //        synchronized(lock){
    //            breakValues[i]=value;
    //            setValues[i].set(true);
    //        }
    //    }

    public static void logger(String message) {
        log.info(s(message));
    }

    public static void warn(String message) {
        log.warning(s(message));
    }

    public static void severe(String message) {
        log.severe(s(message));
    }

    public static void logger(int message) {
        logger(Integer.toString(message));
    }

    private static String s(Object o) {
        return o != null ? o.toString() : "null";
    }

    public static void logger(Object... msgs) {
        logger(String.join(" ", Arrays.stream(msgs).map(Debug::s).toArray(String[]::new)));
    }

    public static void warn(Object... msgs) {
        warn(String.join(" ", Arrays.stream(msgs).map(Debug::s).toArray(String[]::new)));
    }

    public static void severe(Object... msgs) {
        severe(String.join(" ", Arrays.stream(msgs).map(Debug::s).toArray(String[]::new)));
    }

    public static void logger(Throwable t) {
        log.log(java.util.logging.Level.WARNING, (String) "Exception caught while running:", (Throwable) t);
    }

    public static void logger(Throwable t, Object... msg) {
        // t.printStackTrace();
        log.log(
                java.util.logging.Level.WARNING,
                (String) String.join(" ", Arrays.stream(msg).map(Debug::s).toArray(String[]::new)),
                (Throwable) t);
    }

    public static void severe(Throwable t) {
        log.log(java.util.logging.Level.SEVERE, (String) "Error while running:", (Throwable) t);
    }

    public static void logger(Supplier<String> str) {
        logger(s(str.get()));
    }

    public static void test(Object... msgs) {
        if (start) {
            logger(msgs);
        }
    }

    public static void stackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            Debug.logger(stackTraceElement.toString());
        }
    }

    public static void stackTrace(int k) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < Math.min(k, stackTraceElements.length - 2); ++i) {
            Debug.logger(stackTraceElements[i + 2].toString());
        }
    }

    public static void stackTrace(int from, int to) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = from; i < Math.min(to, stackTraceElements.length - 2); ++i) {
            Debug.logger(stackTraceElements[i + 2].toString());
        }
    }

    public static String catchAllOutputs(Runnable r, boolean returnCatched) {
        if (!interceptorRegistered) {
            Debug.warn("Warning: No interceptor registered !");
        }
        interceptLogger = true;
        String value = null;
        try {
            r.run();

        } finally {
            interceptLogger = false;
            //            Debug.logger("Run Run Run ");
            //            Debug.logger(interceptionString);
            if (returnCatched) {
                value = String.join("\n", interceptionString.toArray(String[]::new));
                interceptionString.clear();
            }
            interceptionString.clear();
        }
        return value;
    }

    private static final Consumer<String> NULL = (st) -> {};

    public static <C> C interceptAllOutputs(Supplier<C> r) {
        return interceptAllOutputs(r, NULL);
    }

    private static void startInterception() {
        interceptLogger = true;
    }

    private static void endInterception() {
        interceptLogger = false;
    }

    public static <C> C interceptAllOutputs(Supplier<C> r, Consumer<String> callback) {
        if (!interceptorRegistered) {
            Debug.warn("Warning: No interceptor registered !");
        }
        interceptLogger = true;
        try {
            return r.get();
        } finally {
            interceptLogger = false;
            String value = String.join("\n", interceptionString.toArray(String[]::new));
            interceptionString.clear();
            callback.accept(value);
        }
    }

    public static void trace0(String val) {}

    public static void trace(Object... arguments) {
        trace0(String.join(" ", Arrays.stream(arguments).map(Debug::s).toArray(String[]::new)));
    }

    protected static void init(String name) {
        log = Logger.getLogger(name);
        try {
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger())
                    .addFilter(new org.apache.logging.log4j.core.Filter() {
                        public Result checkMessage(String message) {
                            if (interceptLogger) {
                                interceptionString.add(message);
                                return Result.DENY;
                            } else return Result.NEUTRAL;
                        }

                        @Override
                        public Result getOnMismatch() {
                            return Result.NEUTRAL;
                        }

                        @Override
                        public Result getOnMatch() {
                            return Result.NEUTRAL;
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String msg,
                                Object... params) {
                            return checkMessage(msg);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4,
                                Object p5) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4,
                                Object p5,
                                Object p6) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4,
                                Object p5,
                                Object p6,
                                Object p7) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4,
                                Object p5,
                                Object p6,
                                Object p7,
                                Object p8) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                String message,
                                Object p0,
                                Object p1,
                                Object p2,
                                Object p3,
                                Object p4,
                                Object p5,
                                Object p6,
                                Object p7,
                                Object p8,
                                Object p9) {
                            return checkMessage(message);
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                Object msg,
                                Throwable t) {
                            return checkMessage(msg.toString());
                        }

                        @Override
                        public Result filter(
                                org.apache.logging.log4j.core.Logger logger,
                                Level level,
                                Marker marker,
                                Message msg,
                                Throwable t) {
                            return checkMessage(msg.getFormattedMessage());
                        }

                        @Override
                        public Result filter(LogEvent event) {
                            return checkMessage(event.getMessage().getFormattedMessage());
                        }

                        @Override
                        public State getState() {
                            try {
                                return State.STARTED;
                            } catch (Exception var2) {
                                return null;
                            }
                        }

                        @Override
                        public void initialize() {}

                        @Override
                        public void start() {}

                        @Override
                        public void stop() {}

                        @Override
                        public boolean isStarted() {
                            return true;
                        }

                        @Override
                        public boolean isStopped() {
                            return false;
                        }
                    });
            interceptorRegistered = true;
        } catch (Throwable e) {
            logger(e, "Error while injecting log4j-core Filter:");
        }
    }

    public static void threadDump() {
        ExecutorUtils.dumpAllThreads(log);
    }
}
