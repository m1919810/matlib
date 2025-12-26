package me.matl114.matlib.utils.entity.display.Implementation;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.matl114.matlib.algorithms.algorithm.Vectors;
import me.matl114.matlib.algorithms.dataStructures.complex.MatrixStack;
import me.matl114.matlib.utils.entity.display.RobotConfigure;
import org.bukkit.Location;
import org.bukkit.entity.Marker;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RobotRoot extends LinkDisplayGroup implements RobotConfigure {
    public RobotRoot(String namespace, @NotNull Marker entityParent, String robotId) {
        super(namespace, entityParent, robotId + "_root");
        this.parentLink = null;
        this.robotId = robotId;
    }

    final String robotId;
    Object2DoubleOpenHashMap<String> jointAngleArguments = new Object2DoubleOpenHashMap<>();
    Object2ObjectOpenHashMap<String, Vector3f> jointAxisArguments = new Object2ObjectOpenHashMap<>();

    @Override
    public Location getRootLocation() {
        return this.getCoreLocation();
    }

    @Override
    public void setRootLocation(Location loc) {
        setCoreLocation(loc);
        updateConfiguration();
    }

    @Override
    public double getRotationAngle(String val) {
        return this.jointAngleArguments.getOrDefault(val, 0.0d);
    }

    @Override
    public Vector3f getRotationAxis(String val) {
        return jointAxisArguments.getOrDefault(val, Vectors.ZERO);
    }

    public void appendAngleConfiguration(Object2DoubleOpenHashMap<String> jointArguments) {
        this.jointAngleArguments.putAll(jointArguments);
        updateConfiguration();
    }

    public void applyAngleConfiguration(Object2DoubleOpenHashMap<String> jointArguments) {
        this.jointAngleArguments = jointArguments;
        updateConfiguration();
    }

    public void updateConfiguration() {
        var stack = new MatrixStack(getCurrentTransformation());
        this.forwardKinematics(stack, this);
    }

    public void appendAxisConfiguration(Object2ObjectOpenHashMap<String, Vector3f> jointAxisArguments) {
        this.jointAxisArguments.putAll(jointAxisArguments);
        updateConfiguration();
    }

    public void applyAxisConfiguration(Object2ObjectOpenHashMap<String, Vector3f> jointAxisArguments) {
        this.jointAxisArguments = jointAxisArguments;
        updateConfiguration();
    }
}
