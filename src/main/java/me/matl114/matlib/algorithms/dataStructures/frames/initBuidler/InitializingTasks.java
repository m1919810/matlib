package me.matl114.matlib.algorithms.dataStructures.frames.initBuidler;

public class InitializingTasks {

    public InitializingTasks(Runnable r) {
        r.run();
    }

    public static InitializingTasks of(Runnable task) {
        task.run();
        return null;
    }
}
