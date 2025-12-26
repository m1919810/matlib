package me.matl114.matlib.algorithms.algorithm;

import lombok.NoArgsConstructor;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.Debug;
import org.bukkit.util.Transformation;
import org.joml.*;
import org.joml.Math;

/**
 * Utility class for 3D transformations and quaternion operations.
 * This class provides methods for working with Bukkit Transformations, quaternions,
 * vectors, and various transformation builders and factories.
 */
public class TransformationUtils {
    /** Identity quaternion (no rotation) */
    public static final Quaternionf R = new Quaternionf(0, 0, 0, 1);

    /** Quaternion representing rotation around X-axis */
    public static final Quaternionf I = new Quaternionf(1, 0, 0, 0);

    /** Quaternion representing rotation around Y-axis */
    public static final Quaternionf J = new Quaternionf(0, 1, 0, 0);

    /** Quaternion representing rotation around Z-axis */
    public static final Quaternionf K = new Quaternionf(0, 0, 1, 0);

    /** Zero quaternion */
    public static final Quaternionf ZERO = new Quaternionf(0, 0, 0, 0);

    /** Identity scale vector (1,1,1) */
    public static final Vector3f ID_SCALE = new Vector3f(1, 1, 1);

    /** Zero vector (0,0,0) */
    public static final Vector3f ZERO_VEC = new Vector3f(0, 0, 0);

    /** Unit vector in X direction (1,0,0) */
    public static final Vector3f X = new Vector3f(1, 0, 0);

    /** Unit vector in Y direction (0,1,0) */
    public static final Vector3f Y = new Vector3f(0, 1, 0);

    /** Unit vector in Z direction (0,0,1) */
    public static final Vector3f Z = new Vector3f(0, 0, 1);

    /** Default transformation (no translation, no rotation, identity scale) */
    private static final Transformation DEFAULT_TRANSFORMATION = new Transformation(ZERO_VEC, R, ID_SCALE, R);

    /** Identity 4x4 matrix */
    private static final Matrix4f Id4 = new Matrix4f().identity();

    /**
     * Converts a quaternion to a vector by extracting the vector part.
     *
     * @param q The quaternion to convert
     * @return A vector containing the x, y, z components of the quaternion
     */
    public static Vector3f q2v(Quaternionf q) {
        return new Vector3f(q.x, q.y, q.z);
    }

    /**
     * Converts a vector to a quaternion with zero scalar part.
     *
     * @param v The vector to convert
     * @return A quaternion with the vector components and zero scalar
     */
    public static Quaternionf v2q(Vector3f v) {
        return new Quaternionf(v.x, v.y, v.z, 0);
    }

    /**
     * Creates a deep copy of a vector.
     *
     * @param v The vector to clone
     * @return A new vector with the same components
     * @throws RuntimeException if cloning is not supported
     */
    public static Vector3f cloneVec(Vector3f v) {
        try {
            return (Vector3f) v.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates a deep copy of a quaternion.
     *
     * @param q The quaternion to clone
     * @return A new quaternion with the same components
     * @throws RuntimeException if cloning is not supported
     */
    public static Quaternionf cloneQ(Quaternionf q) {
        try {
            return (Quaternionf) q.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Performs element-wise multiplication of two vectors.
     *
     * @param v1 The first vector
     * @param v2 The second vector
     * @return A new vector with components (v1.x*v2.x, v1.y*v2.y, v1.z*v2.z)
     */
    public static Vector3f mulElem(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    /**
     * Creates a quaternion from an axis and angle representation.
     * The axis should be normalized, and the angle is in radians.
     *
     * @param axis The rotation axis (should be normalized)
     * @param angle The rotation angle in radians
     * @return A quaternion representing the rotation
     */
    public static Quaternionf fromAxisAngle(Vector3f axis, float angle) {
        if (axis.x == 0 && axis.y == 0 && axis.z == 0) {
            return R;
        }
        Vector3f normedAxis = cloneVec(axis).normalize();
        angle = angle * 0.5f;
        float sin = Math.sin(angle);
        return new Quaternionf(normedAxis.x * sin, normedAxis.y * sin, normedAxis.z * sin, Math.cosFromSin(sin, angle));
    }

    /**
     * Creates a quaternion from axis-angle representation using cosine value.
     * The theta value of cosValue should be less than PI and greater than 0.
     *
     * @param x The x component of the rotation axis
     * @param y The y component of the rotation axis
     * @param z The z component of the rotation axis
     * @param cosValue The cosine of the half angle
     * @param isSinValuePositve Whether the sine value is positive
     * @return A quaternion representing the rotation
     */
    @Note("thetaValue of cosValue should be less than PI and greater than 0,or you should use -x -y -z")
    private static Quaternionf axisAngleWithCos(float x, float y, float z, float cosValue, boolean isSinValuePositve) {
        // IMPORTANT
        // cos2θ=2cosθ^2 -1
        float halfAngleCos = Math.sqrt(0.5f * (cosValue + 1));
        float sin = Math.sqrt(1.0f - halfAngleCos * halfAngleCos) * (isSinValuePositve ? 1 : -1);
        return new Quaternionf(x * sin, y * sin, z * sin, halfAngleCos);
    }

    /**
     * Converts degrees to radians.
     *
     * @param degree The angle in degrees
     * @return The angle in radians
     */
    public static float angleDegreeToRadian(float degree) {
        return Math.toRadians(degree);
    }

    /**
     * Returns the default transformation (identity transformation).
     *
     * @return A transformation with no translation, no rotation, and identity scale
     */
    public static Transformation defaultTrans() {
        return DEFAULT_TRANSFORMATION;
    }

    /**
     * Linear Conformal Transformation record representing a composition of rotation, scale, and translation.
     * LC means Linear Conformal, which includes rotation, scale, and translation operations.
     *
     * @param q The rotation quaternion (should satisfy ||q||=1)
     * @param c The scale factor
     * @param b The translation quaternion (vector part represents translation)
     */
    @Note("LC means Linear Conformal, it is a composition of rotation, scale and translation")
    public static record LCTransformation(@Note("q should satisfy ||q||=1") Quaternionf q, float c, Quaternionf b) {
        /**
         * Composes this transformation with another transformation.
         * f.compositionWith(g) means f(g(x)).
         *
         * @param other The transformation to compose with
         * @return The composed transformation
         */
        @Note("f.compose(g) means f·g ,which f·g(x) = f(g(x))")
        public LCTransformation compositionWith(LCTransformation other) {
            Quaternionf newQ = cloneQ(q).mul(other.q);
            float newC = c * other.c;
            Quaternionf newB =
                    cloneQ(q).mul(c).mul(other.b).mul(cloneQ(q).invert()).add(b);
            return new LCTransformation(newQ, newC, newB);
        }

        /**
         * Creates an LCTransformation from a Bukkit Transformation.
         *
         * @param transformation The Bukkit transformation to convert
         * @return An LCTransformation representing the same transformation
         */
        public static LCTransformation ofTransformation(Transformation transformation) {
            return new LCTransformation(
                    cloneQ(transformation.getLeftRotation()), 1.0f, v2q(transformation.getTranslation()));
        }

        /**
         * Transforms an origin transformation by this LCTransformation.
         *
         * @param origin The origin transformation to transform
         * @return The transformed transformation
         */
        public Transformation transformOrigin(Transformation origin) {
            LCTransformation tran = this.compositionWith(ofTransformation(origin));
            return new Transformation(
                    q2v(tran.b),
                    cloneQ(tran.q),
                    cloneVec(origin.getScale()).mul(tran.c),
                    cloneQ(origin.getRightRotation()));
        }

        /** Identity LCTransformation */
        private static final LCTransformation Id = new LCTransformation(R, 1.0f, ZERO);

        /**
         * Returns the identity LCTransformation.
         *
         * @return The identity transformation
         */
        public static LCTransformation ofIdentical() {
            return Id;
        }

        /**
         * Returns this transformation without the bias (translation).
         *
         * @return An LCTransformation with zero bias
         */
        public LCTransformation noBias() {
            return new LCTransformation(cloneQ(q), c, ZERO);
        }

        /**
         * Returns the bias (translation) vector of this transformation.
         *
         * @return The translation vector
         */
        public Vector3f bias() {
            return q2v(b);
        }
    }

    /**
     * Factory class for building Linear Conformal Transformations.
     * Provides a fluent API for creating LCTransformations.
     */
    public static class LinearTransFactory {
        Quaternionf q = R;
        Vector3f b = ZERO_VEC;
        float c = 1.0f;

        /**
         * Sets the scale factor.
         *
         * @param c The scale factor
         * @return This factory for chaining
         */
        public LinearTransFactory C(float c) {
            this.c = c;
            return this;
        }

        /**
         * Sets the rotation quaternion.
         *
         * @param q The rotation quaternion
         * @return This factory for chaining
         */
        public LinearTransFactory A(Quaternionf q) {
            this.q = q;
            return this;
        }

        /**
         * Sets the rotation using axis-angle representation with radians.
         *
         * @param x The x component of the rotation axis
         * @param y The y component of the rotation axis
         * @param z The z component of the rotation axis
         * @param delta The rotation angle in radians
         * @return This factory for chaining
         */
        public LinearTransFactory A(float x, float y, float z, float delta) {
            this.q = fromAxisAngle(new Vector3f(x, y, z), delta);
            return this;
        }

        /**
         * Sets the rotation using axis-angle representation with degrees.
         *
         * @param x The x component of the rotation axis
         * @param y The y component of the rotation axis
         * @param z The z component of the rotation axis
         * @param degree The rotation angle in degrees
         * @return This factory for chaining
         */
        public LinearTransFactory A(float x, float y, float z, int degree) {
            this.q = fromAxisAngle(new Vector3f(x, y, z), Math.toRadians(degree));
            return this;
        }

        /**
         * Sets the rotation using axis-angle representation with a vector and radians.
         *
         * @param v The rotation axis vector
         * @param delta The rotation angle in radians
         * @return This factory for chaining
         */
        public LinearTransFactory A(Vector3f v, float delta) {
            this.q = fromAxisAngle(v, delta);
            return this;
        }

        /**
         * Sets the bias (translation) vector.
         *
         * @param b The translation vector
         * @return This factory for chaining
         */
        public LinearTransFactory bias(Vector3f b) {
            this.b = b;
            return this;
        }

        /**
         * Builds the LCTransformation.
         *
         * @return The constructed LCTransformation
         */
        public LCTransformation build() {
            return new LCTransformation(q, c, v2q(b));
        }
    }

    /**
     * Factory class for building Bukkit Transformations.
     * Provides a fluent API for creating complex transformations.
     */
    @NoArgsConstructor
    public static class TransFactory {
        Quaternionf q1 = R;
        Vector3f s = ID_SCALE;
        Quaternionf q2 = R;
        Vector3f d = ZERO_VEC;

        /**
         * Sets the pre-rotation using axis-angle representation with radians.
         *
         * @param x The x component of the rotation axis
         * @param y The y component of the rotation axis
         * @param z The z component of the rotation axis
         * @param angle The rotation angle in radians
         * @return This factory for chaining
         */
        public TransFactory preRotation(float x, float y, float z, float angle) {
            this.q1 = fromAxisAngle(new Vector3f(x, y, z), angle);
            return this;
        }

        /**
         * Sets the pre-rotation quaternion.
         *
         * @param q The pre-rotation quaternion
         * @return This factory for chaining
         */
        public TransFactory preRotation(Quaternionf q) {
            this.q1 = q;
            return this;
        }

        /**
         * Sets the scale vector.
         *
         * @param x The x scale factor
         * @param y The y scale factor
         * @param z The z scale factor
         * @return This factory for chaining
         */
        public TransFactory scale(float x, float y, float z) {
            this.s = new Vector3f(x, y, z);
            return this;
        }

        /**
         * Sets the post-rotation using axis-angle representation with degrees.
         *
         * @param x The x component of the rotation axis
         * @param y The y component of the rotation axis
         * @param z The z component of the rotation axis
         * @param angleDegree The rotation angle in degrees
         * @return This factory for chaining
         */
        public TransFactory postRotation(float x, float y, float z, int angleDegree) {
            return postRotation(x, y, z, Math.toRadians(angleDegree));
        }

        /**
         * Sets the post-rotation using axis-angle representation with radians.
         *
         * @param x The x component of the rotation axis
         * @param y The y component of the rotation axis
         * @param z The z component of the rotation axis
         * @param angle The rotation angle in radians
         * @return This factory for chaining
         */
        public TransFactory postRotation(float x, float y, float z, float angle) {
            this.q2 = fromAxisAngle(new Vector3f(x, y, z), angle);
            return this;
        }

        /**
         * Sets the post-rotation quaternion.
         *
         * @param q The post-rotation quaternion
         * @return This factory for chaining
         */
        public TransFactory postRotation(Quaternionf q) {
            this.q2 = q;
            return this;
        }

        /**
         * Sets the translation vector.
         *
         * @param x The x translation
         * @param y The y translation
         * @param z The z translation
         * @return This factory for chaining
         */
        public TransFactory translate(float x, float y, float z) {
            this.d = new Vector3f(x, y, z);
            return this;
        }

        /**
         * Adds to the current translation vector.
         *
         * @param x The x translation to add
         * @param y The y translation to add
         * @param z The z translation to add
         * @return This factory for chaining
         */
        public TransFactory addTranslate(float x, float y, float z) {
            this.d = this.d == null ? new Vector3f(x, y, z) : cloneVec(d).add(x, y, z);
            return this;
        }

        /**
         * Builds the Transformation.
         *
         * @return The constructed Transformation
         */
        public Transformation build() {
            return new Transformation(cloneVec(d), cloneQ(q2), cloneVec(s), cloneQ(q1));
        }

        /**
         * Creates a copy of this factory.
         *
         * @return A new TransFactory with the same settings
         */
        public TransFactory copy() {
            var re = new TransFactory();
            re.q1 = q1;
            re.s = s;
            re.q2 = q2;
            re.d = d;
            return re;
        }
    }

    /**
     * Creates a new TransFactory instance.
     *
     * @return A new TransFactory
     */
    public static TransFactory builder() {
        return new TransFactory();
    }

    /**
     * Creates a new LinearTransFactory instance.
     *
     * @return A new LinearTransFactory
     */
    public static LinearTransFactory linearBuilder() {
        return new LinearTransFactory();
    }

    /**
     * Creates a linear transformation with only translation (bias).
     *
     * @param bias The translation vector
     * @return An LCTransformation representing only translation
     */
    public static LCTransformation linearBias(Vector3f bias) {
        return new LCTransformation(R, 1.0f, v2q(bias));
    }

    /**
     * Creates a linear transformation with only rotation.
     *
     * @param axis The rotation axis
     * @param angle The rotation angle in radians
     * @return An LCTransformation representing only rotation
     */
    public static LCTransformation linearAxisAngle(Vector3f axis, float angle) {
        return new LCTransformation(fromAxisAngle(axis, angle), 1.0f, ZERO);
    }

    /**
     * Shrinks a transformation by element-wise multiplication with a vector.
     *
     * @param trans The transformation to shrink
     * @param vec The shrinking vector
     * @return A new transformation with modified scale
     */
    public static Transformation shrink(Transformation trans, Vector3f vec) {
        return new Transformation(
                trans.getTranslation(),
                trans.getLeftRotation(),
                mulElem(vec, trans.getScale()),
                trans.getRightRotation());
    }

    /**
     * Creates a transformation representing only rotation.
     *
     * @param f The rotation quaternion
     * @return A transformation with the specified rotation and no translation
     */
    public static Transformation rotation(Quaternionf f) {
        return new Transformation(ZERO_VEC, f, ID_SCALE, R);
    }

    /**
     * Creates a linear transformation representing only rotation.
     *
     * @param f The rotation quaternion
     * @return An LCTransformation representing only rotation
     */
    public static LCTransformation rotationAsLinear(Quaternionf f) {
        return new LCTransformation(f, 1.0f, ZERO);
    }

    /**
     * Creates a flat block transformation with bottom and top parallel to XZ plane.
     * The block has relative translation (0,0,0)->(dx,dy,dz), shape (shrinkX,shrinkY,shrinkZ),
     * and rotation degree from default rotation.
     *
     * @param dx The x translation
     * @param dy The y translation
     * @param dz The z translation
     * @param shrinkX The x scale factor
     * @param shrinkY The y scale factor
     * @param shrinkZ The z scale factor
     * @param degree The rotation angle in degrees
     * @return A TransFactory configured for the flat block
     */
    public static TransFactory buildFlatBlockAt(
            float dx, float dy, float dz, float shrinkX, float shrinkY, float shrinkZ, float degree) {
        return builder()
                .scale(shrinkX, shrinkY, shrinkZ)
                .postRotation(0, 1, 0, degree)
                .translate(dx, dy, dz);
    }

    /**
     * Creates a flat block transformation with no rotation (origin placement).
     *
     * @param dx The x translation
     * @param dy The y translation
     * @param dz The z translation
     * @param shrinkX The x scale factor
     * @param shrinkY The y scale factor
     * @param shrinkZ The z scale factor
     * @return A TransFactory configured for the flat block
     */
    public static TransFactory buildCubeAt(float dx, float dy, float dz, float shrinkX, float shrinkY, float shrinkZ) {
        return buildFlatBlockAt(dx, dy, dz, shrinkX, shrinkY, shrinkZ, 0);
    }

    /**
     * Creates a cube transformation centered at the origin.
     *
     * @param shapeX The x dimension
     * @param shapeY The y dimension
     * @param shapeZ The z dimension
     * @return A TransFactory configured for the centered cube
     */
    public static TransFactory buildCubeAtCenter(float shapeX, float shapeY, float shapeZ) {
        return buildCubeAt(-shapeX / 2.0f, -shapeY / 2.0f, -shapeZ / 2.0f, shapeX, shapeY, shapeZ);
    }

    /**
     * Creates a rotation quaternion that rotates the directed line from (0,0,0) to (0,1,0)
     * to the directed line from (0,0,0) to the given vector.
     * The shouldRotateXZ parameter determines whether to rotate XZ to make XY plane-> X vec plane.
     *
     * @param vec The target vector
     * @param shouldRotateXZ Whether to perform additional XZ rotation
     * @return A quaternion representing the rotation
     */
    public static Quaternionf rotateOriginTo(Vector3f vec, boolean shouldRotateXZ) {
        // 首先我们求出他的旋转轴
        float thetaCos = vec.y / vec.length();
        float invlen = Math.invsqrt(vec.x * vec.x + vec.z * vec.z);
        Vector3f rotationVec = new Vector3f(vec.z * invlen, 0, -vec.x * invlen).normalize();
        // y should be 0,xz
        Quaternionf rotate = axisAngleWithCos(rotationVec.x, 0, rotationVec.z, thetaCos, true);
        if (shouldRotateXZ) {
            //
            Debug.logger(rotate);
            // 右手旋转,按y+旋转的话需要旋转-theta度数
            Quaternionf rotateXZ = axisAngleWithCos(0, -1, 0, vec.x * invlen, (vec.z > 0));
            Debug.logger(rotateXZ);
            rotate = rotate.mul(rotateXZ);
            Debug.logger(rotate);
        }
        return rotate;
    }
    // todo need a rotate from Direction to Direction
}
