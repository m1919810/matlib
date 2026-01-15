package me.matl114.matlib.utils.crafting.agents;

public interface CraftingProgress {
    public int getTotalTicks();

    public int getProgress();

    public void addProgress(int progress);

    default int getRemainingTicks() {
        return this.getTotalTicks() - this.getProgress();
    }

    default boolean isFinished() {
        return this.getRemainingTicks() <= 0;
    }

    public void tickProgress();

    CraftingOperation getOperation();
}
