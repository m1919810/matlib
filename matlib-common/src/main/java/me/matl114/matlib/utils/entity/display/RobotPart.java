package me.matl114.matlib.utils.entity.display;

import me.matl114.matlib.algorithms.dataStructures.complex.MatrixStack;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Note;

public interface RobotPart {
    String getId();

    @ForceOnMainThread
    @Note("this method requires location change of robot part, so it is forced on main thread")
    public void forwardKinematics(MatrixStack currentTransformation, RobotConfigure configure);
}
