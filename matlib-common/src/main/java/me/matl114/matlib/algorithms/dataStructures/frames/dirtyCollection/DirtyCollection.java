package me.matl114.matlib.algorithms.dataStructures.frames.dirtyCollection;

public interface DirtyCollection<DE> {
    default void setDirty() {
        setDirty(true);
    }

    public void setDirty(boolean dirty);

    public boolean isDirty();

    public DE getHandle();
}
