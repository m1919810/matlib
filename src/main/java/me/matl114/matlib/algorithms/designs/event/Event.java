package me.matl114.matlib.algorithms.designs.event;

public class Event {
    private boolean cancel = false;

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean var1) {
        this.cancel = var1;
    }
}
