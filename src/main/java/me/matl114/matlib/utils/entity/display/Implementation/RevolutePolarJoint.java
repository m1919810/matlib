package me.matl114.matlib.utils.entity.display.Implementation;

import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.utils.entity.display.Joint;
import me.matl114.matlib.utils.entity.display.RobotConfigure;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * this kind of joint can rotate in x and y
 * when rotation , it will first rotate around axisY (横向转动) then around axisX (纵向转动)
 */
public class RevolutePolarJoint extends Joint {
    Vector3f axisX;
    Vector3f axisY;
    String idX;
    String idY;

    public RevolutePolarJoint(String id, Vector3f bias, Vector3f axisX, Vector3f axisY) {
        super(id, bias);
        this.axisX = axisX;
        this.axisY = axisY;
        this.idX = id + "_x";
        this.idY = id + "_y";
    }

    @Override
    public Quaternionf getRotation(RobotConfigure config) {
        return TransformationUtils.fromAxisAngle(axisY, (float) config.getRotationAngle(idY))
                .mul(TransformationUtils.fromAxisAngle(axisX, (float) config.getRotationAngle(idX)));
    }
}
