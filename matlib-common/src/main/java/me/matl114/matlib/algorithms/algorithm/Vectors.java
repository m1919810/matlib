package me.matl114.matlib.algorithms.algorithm;

import org.joml.Vector3f;

/**
 * Utility class providing common 3D vector constants and operations.
 * This class contains predefined Vector3f constants for common directions
 * and transformations in 3D space.
 */
public class Vectors {
    /** Unit vector pointing in the positive X direction (1, 0, 0) */
    public static final Vector3f AXIS_X = new Vector3f(1, 0, 0);

    /** Unit vector pointing in the positive Y direction (0, 1, 0) */
    public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);

    /** Unit vector pointing in the positive Z direction (0, 0, 1) */
    public static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);

    /** Unit vector pointing in the negative X direction (-1, 0, 0) */
    public static final Vector3f AXIS_XN = new Vector3f(-1, 0, 0);

    /** Unit vector pointing in the negative Y direction (0, -1, 0) */
    public static final Vector3f AXIS_YN = new Vector3f(0, -1, 0);

    /** Unit vector pointing in the negative Z direction (0, 0, -1) */
    public static final Vector3f AXIS_ZN = new Vector3f(0, 0, -1);

    /** Zero vector (0, 0, 0) */
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);

    /** Identity scale vector (1, 1, 1) */
    public static final Vector3f ID_SCALE = new Vector3f(1, 1, 1);
}
