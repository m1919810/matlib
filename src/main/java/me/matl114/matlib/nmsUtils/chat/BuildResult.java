package me.matl114.matlib.nmsUtils.chat;

public interface BuildResult<R> {
    public R toNMS();

    public Object clone();

    default boolean isImmutable() {
        return false;
    }

    public <T extends BuildResult<R>> T toImmutable();
}
