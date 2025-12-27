package me.matl114.matlib.common.lang.enums;

public enum Flags {
    // time unit
    UNIT_SEC(),
    UNIT_SFT(),
    UNIT_TICK(),
    // order
    SEQUENTIAL(),
    REVERSE(),
    // transport direction
    GRAB(),
    PUSH(),
    // lifecycle
    INIT(),
    RUN(),
    DECON(),
    // string utils
    PREFIX(),
    SUFFIX(),
    // menu view
    OUTPUT(),
    INPUT(),
    // reflect
    METHOD(),
    FIELD(),
    // match result
    REJECT(),
    IGNORED(),
    ACCEPT();
}
