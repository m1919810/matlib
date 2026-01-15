package me.matl114.matlib.implement.bukkit.entity.display.Implementation;

import me.matl114.matlib.implement.bukkit.entity.display.Joint;
import me.matl114.matlib.implement.bukkit.entity.display.RobotConfigure;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FixedJoint extends Joint {
    public FixedJoint(String id, Vector3f translation, Quaternionf fixedRotation) {
        super(id, translation);
        this.fixedRotation = fixedRotation;
    }

    final Quaternionf fixedRotation;

    @Override
    public Quaternionf getRotation(RobotConfigure config) {
        return fixedRotation;
    }
}
