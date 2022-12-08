package com.capdevon.engine;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class FRotator {
	
    private FRotator() {}

    /**
     * Creates a rotation which rotates from fromDirection to toDirection.
     * (Default fromDirection = Vector3f.UNIT_Y)
     *
     * @param toDirection
     * @return
     */
    public static Quaternion fromToRotation(Vector3f toDirection) {
        return fromToRotation(toDirection, Vector3f.UNIT_Y);
    }

    /**
     * Creates a rotation which rotates from fromDirection to toDirection.
     *
     * @param direction
     * @param axis
     * @return
     */
    public static Quaternion fromToRotation(Vector3f direction, Vector3f axis) {
        float angle = FastMath.atan2(direction.x, direction.z);
        return new Quaternion().fromAngleNormalAxis(angle, axis);
    }

    /**
     * Creates a rotation with the specified forward and upwards directions.
     * (Default upwards = Vector3f.UNIT_Y)
     *
     * @param direction
     * @return
     */
    public static Quaternion lookRotation(Vector3f direction) {
        Quaternion q = new Quaternion();
        q.lookAt(direction, Vector3f.UNIT_Y);
        return q;
    }

    /**
     * Spherically interpolates between quaternions a and b by ratio t. The
     * parameter t is clamped to the range [0, 1].
     *
     * @param from
     * @param to
     * @param smoothTime
     * @param viewDirection
     * @return
     */
    public static Vector3f smoothDamp(Quaternion from, Quaternion to, float smoothTime, Vector3f viewDirection) {
        if (viewDirection == null) {
            viewDirection = new Vector3f();
        }
        from.slerp(to, FastMath.clamp(smoothTime, 0, 1));
        return from.mult(Vector3f.UNIT_Z, viewDirection);
    }

    public static Vector3f eulerAngles(Spatial sp) {
        float[] angles = new float[3];
        sp.getWorldRotation().toAngles(angles);
        return new Vector3f(angles[0], angles[1], angles[2]);
    }

    public static Vector3f localEulerAngles(Spatial sp) {
        float[] angles = new float[3];
        sp.getLocalRotation().toAngles(angles);
        return new Vector3f(angles[0], angles[1], angles[2]);
    }

}
