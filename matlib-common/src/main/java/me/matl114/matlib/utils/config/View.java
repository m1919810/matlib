package me.matl114.matlib.utils.config;

import java.util.List;
import me.matl114.matlib.common.lang.exceptions.DoNotCallException;

public interface View {
    default boolean getAsBoolean() {
        throw new DoNotCallException();
    }

    default int getAsInt() {
        throw new DoNotCallException();
    }

    default byte getAsByte() {
        return (byte) getAsInt();
    }

    default short getAsShort() {
        return (short) getAsInt();
    }

    default long getAsLong() {
        throw new DoNotCallException();
    }

    default float getAsFloat() {
        return (float) getAsDouble();
    }

    default double getAsDouble() {
        throw new DoNotCallException();
    }

    default String getAsString() {
        throw new DoNotCallException();
    }

    default List<String> getAsList() {
        throw new DoNotCallException();
    }

    default boolean setBoolean(boolean val) {
        throw new DoNotCallException();
    }

    default boolean setInt(int val) {
        throw new DoNotCallException();
    }

    default boolean setLong(long val) {
        throw new DoNotCallException();
    }

    default boolean setDouble(double val) {
        throw new DoNotCallException();
    }

    default boolean setFloat(float val) {
        return setDouble(val);
    }

    default boolean setList(List<String> val) {
        throw new DoNotCallException();
    }
}
