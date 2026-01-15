package me.matl114.matlib.implement.bukkit.entity.display;

import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.algorithms.dataStructures.complex.MatrixStack;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.bukkit.Location;
import org.joml.Vector3f;

/**
 * this interface represent a LinkBody in a robot frame,
 * its transformation is driven by parent Link and parent Joint
 * it contains displayable part and child Joints
 * when applying transformation in forward kinematics, its core location is calculated via ROOT Link's location and current translation amount, its rotation is calculated via current rotation, and current transformation will pass down to child Joints
 */
public interface LinkBody extends RobotPart, TransformApplicable {
    public Location getCoreLocation();

    public LinkBody getParentLink();

    public void setCoreLocation(Location location);

    public Iterable<Pair<Joint, LinkBody>> getChildJoints();

    public void killGroup();

    default void forwardKinematics(MatrixStack currentTransformation, RobotConfigure configure) {
        // move self and update display
        TransformationUtils.LCTransformation trans = currentTransformation.peek();
        Vector3f bias = trans.bias();
        setCoreLocation(configure.getRootLocation().clone().add(bias.x, bias.y, bias.z));
        applyTransformation(trans.noBias(), true);
        // calculate child joints
        for (Pair<Joint, LinkBody> entry : getChildJoints()) {
            currentTransformation.push();
            entry.getA().forwardKinematics(currentTransformation, configure);
            entry.getB().forwardKinematics(currentTransformation, configure);
            currentTransformation.pop();
        }
    }
}
