package me.matl114.matlib.utils.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.function.Predicate;

public class CustomLogFilter implements org.apache.logging.log4j.core.Filter{
    Predicate<String> predicate;
    public CustomLogFilter(Predicate<String> predicate){
        this.predicate = predicate;
    }
    protected  Result checkMessage(String msg){
        return predicate.test(msg)? Result.DENY: Result.NEUTRAL;
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
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String msg, Object... params) {
        return checkMessage(msg);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return checkMessage(message);
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return checkMessage(msg.toString());
    }
    @Override
    public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Message msg, Throwable t) {
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
    public void initialize() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
    @Override
    public boolean isStarted() {
        return true;
    }


    @Override
    public boolean isStopped() {
        return false;
    }
}
