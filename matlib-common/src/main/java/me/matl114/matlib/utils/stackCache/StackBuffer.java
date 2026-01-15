package me.matl114.matlib.utils.stackCache;

import me.matl114.matlib.common.lang.annotations.MustCheckRetValue;

public interface StackBuffer {

    public long getMaxStackCnt();

    public boolean isNull();

    public boolean isFull();

    public boolean isEmpty();

    public long getAmount();

    public void setAmount(long amount);

    public boolean isDirty();

    public void setDirty(boolean t);

    public void syncData();

    public void syncAmount();

    public void updateSource();

    @MustCheckRetValue
    public boolean setFrom(StackBuffer source);

    @MustCheckRetValue
    public long consume(long cnt2);

    @MustCheckRetValue
    public long grab(long grab);
}
