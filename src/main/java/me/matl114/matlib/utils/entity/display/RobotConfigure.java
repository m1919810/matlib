package me.matl114.matlib.utils.entity.display;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.bukkit.Location;
import org.joml.Vector3f;

/**
 * this represents the current state of robot,
 * it should contains core location, root RobotPart, and Joint datas
 * used in fk
 */
public interface RobotConfigure {
    public Location getRootLocation();

    public void setRootLocation(Location loc);

    public double getRotationAngle(String val);

    public Vector3f getRotationAxis(String val);

    public void updateConfiguration();

    public void appendAngleConfiguration(Object2DoubleOpenHashMap<String> jointArguments);

    public void applyAngleConfiguration(Object2DoubleOpenHashMap<String> config);

    public void killGroup();
}
