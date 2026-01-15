package me.matl114.matlib.utils.crafting.agents;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.stackCache.StackBuffer;

@Accessors(chain = true)
public abstract class StackAgent implements Cloneable, Comparable<StackAgent> {
    @Getter
    private long matchAmount = 0;

    @Getter
    private long matchStackAmount = 0;

    protected List<StackBuffer> related = null;

    public List<StackBuffer> getRelated() {
        if (related == null) {
            related = new ArrayList<>();
        }
        return related;
    }

    public void addRelated(StackBuffer related) {
        getRelated().add(related);
        related.setDirty(true);
    }

    public void setMatchAmount(long matchAmount) {
        this.matchAmount = matchAmount;
    }

    public void updateStackAmount() {
        long req = getAmountPerStack();
        this.setStackAmount(req == 0 ? Long.MAX_VALUE : (getMatchAmount() / req));
    }

    public final void setStackAmount(long stackAmount) {
        this.matchStackAmount = stackAmount;
    }

    public abstract long getAmountPerStack();

    public abstract void calculateAmountByStackAmount();

    public void resetMatchingInfo() {
        this.matchAmount = 0;
        this.matchStackAmount = 0;
        this.related = null;
    }

    public int compareTo(StackAgent o) {
        return Long.compare(this.matchStackAmount, o.matchStackAmount);
    }

    @Override
    protected StackAgent clone() {
        try {
            return (StackAgent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
