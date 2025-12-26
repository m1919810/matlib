package me.matl114.matlib.utils.entity.display;

import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.algorithms.dataStructures.complex.MatrixStack;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.Protected;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * a joint represents a transformation linkage between parent Link and child Link
 * when applying transformation in forward kinematics, Joint will infect current transformation :
 * trans = trans · (joint translation(should be the relative position from parent link core location to child link core location in parent's view)) · (joint rotation (if joint is fixed, this is a fix value, or Identity trans, else itis a rotation through its axis))
 */
public abstract class Joint implements RobotPart {
    protected Joint(String id, Vector3f bias) {
        this.translation = TransformationUtils.cloneVec(bias);
        this.precalTranslation = TransformationUtils.linearBias(bias);
        this.id = id;
    }

    final Vector3f translation;
    final TransformationUtils.LCTransformation precalTranslation;

    @Getter
    protected final String id;

    @Protected
    @Note("this method should return a translation ,but returning a pre-calculated translation LC object is better")
    @ConstVal
    public TransformationUtils.LCTransformation getTranslationInternal() {
        return precalTranslation;
    }

    @ConstVal
    public Vector3f getTranslation() {
        return translation;
    }

    public abstract Quaternionf getRotation(RobotConfigure config);

    public void forwardKinematics(MatrixStack currentTransformation, RobotConfigure configure) {
        currentTransformation.composition(getTranslationInternal());
        currentTransformation.composition(
                TransformationUtils.linearBuilder().A(getRotation(configure)).build());
    }
}
