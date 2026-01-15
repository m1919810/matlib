package me.matl114.matlib.implement.bukkit.entity.display;

import static me.matl114.matlib.algorithms.algorithm.TransformationUtils.*;

import me.matl114.matlib.common.lang.annotations.Protected;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * this interface represents a group of display which can be rigidly transformed,
 * and can record present transformation data history
 */
public interface TransformApplicable {
    /**
     * should be like x->Ax +b which A = C*(rotation theta with axis rotationAxis/quaternion)
     *
     */
    /**
     * apply linear conformal transformation to display group
     * @param transformation transformation
     * @param forceEntityUpdate should EntitySource be force update,if force, will require entity load on main thread if entity reference is invalid,else will apply transformation on current thread
     */
    default void appendTransformation(LCTransformation transformation, boolean forceEntityUpdate) {
        setCurrentTransformation(getCurrentTransformation().compositionWith(transformation));
        updateStatus(forceEntityUpdate);
    }

    default void appendTransformation(Quaternionf unitRotationQ, float scaledC, Vector3f b, boolean forceEntityUpdate) {
        appendTransformation(new LCTransformation(unitRotationQ, scaledC, v2q(b)), forceEntityUpdate);
    }

    default void appendTransformation(
            Vector3f rotationAxis, float theta, float scaledC, Vector3f b, boolean forceEntityUpdate) {
        appendTransformation(
                new LCTransformation(fromAxisAngle(rotationAxis, theta), scaledC, v2q(b)), forceEntityUpdate);
    }

    default void appendTransformation(Vector3f rotationAxis, float theta, Vector3f b, boolean forceEntityUpdate) {
        appendTransformation(new LCTransformation(fromAxisAngle(rotationAxis, theta), 1.0f, v2q(b)), forceEntityUpdate);
    }

    default void appendTransformation(Vector3f rotationAxis, float theta, boolean forceEntityUpdate) {
        appendTransformation(new LCTransformation(fromAxisAngle(rotationAxis, theta), 1.0f, ZERO), forceEntityUpdate);
    }

    default void appendTransformation(Vector3f b, boolean forceEntityUpdate) {
        appendTransformation(new LCTransformation(R, 1.0f, v2q(b)), forceEntityUpdate);
    }

    /**
     * ignore current transformation state, override using argument
     * @param transformation
     * @param forceEntityUpdate
     */
    default void applyTransformation(LCTransformation transformation, boolean forceEntityUpdate) {
        setCurrentTransformation(transformation);
        updateStatus(forceEntityUpdate);
    }

    default void applyTransformation(Quaternionf unitRotationQ, float scaledC, Vector3f b, boolean forceEntityUpdate) {
        applyTransformation(new LCTransformation(unitRotationQ, scaledC, v2q(b)), forceEntityUpdate);
    }

    default void applyTransformation(
            Vector3f rotationAxis, float theta, float scaledC, Vector3f b, boolean forceEntityUpdate) {
        applyTransformation(
                new LCTransformation(fromAxisAngle(rotationAxis, theta), scaledC, v2q(b)), forceEntityUpdate);
    }

    default void applyTransformation(Vector3f rotationAxis, float theta, Vector3f b, boolean forceEntityUpdate) {
        applyTransformation(new LCTransformation(fromAxisAngle(rotationAxis, theta), 1.0f, v2q(b)), forceEntityUpdate);
    }

    default void applyTransformation(Vector3f rotationAxis, float theta, boolean forceEntityUpdate) {
        applyTransformation(new LCTransformation(fromAxisAngle(rotationAxis, theta), 1.0f, ZERO), forceEntityUpdate);
    }

    default void applyTransformation(Vector3f b, boolean forceEntityUpdate) {
        appendTransformation(new LCTransformation(R, 1.0f, v2q(b)), forceEntityUpdate);
    }

    default boolean isShrinkable() {
        return false;
    }

    default void reshapeBase(Vector3f scale, boolean forceEntityUpdate) {
        setCurrentReshape(scale);
        updateStatus(forceEntityUpdate);
    }

    default void reshapeBase(float x, float y, float z, boolean forceEntityUpdate) {
        reshapeBase(new Vector3f(x, y, z), forceEntityUpdate);
    }

    default void undo() {
        throw new UnsupportedOperationException();
    }

    default void undoAll() {
        setCurrentTransformation(LCTransformation.ofIdentical());
        if (isShrinkable()) {
            setCurrentReshape(ID_SCALE);
        }
        updateStatus(true);
    }

    @Protected
    public void updateStatus(boolean force);

    @Protected
    public LCTransformation getCurrentTransformation();

    @Protected
    public void setCurrentTransformation(LCTransformation transformation);

    default Vector3f getCurrentReshape() {
        return ID_SCALE;
    }

    @Protected
    default void setCurrentReshape(Vector3f reshape) {
        throw new UnsupportedOperationException();
    }
}
