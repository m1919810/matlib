package me.matl114.matlib.nmsUtils.network;

public enum ConnectionLifeCycle {
    HAND_SHAKE,
    LOGIN,
    CONFIGURATION,
    // PLAY, but gameJoinEvent is not fired yet
    PREPLAY,
    PLAY,
    DISCONNECT;

    public boolean isValid() {
        return this != HAND_SHAKE && this != DISCONNECT;
    }

    public boolean isCommonState() {
        return this == PLAY;
    }

    public static boolean isCommonState(ConnectionLifeCycle c) {
        return c == PLAY;
    }
}
