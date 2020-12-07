/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class FVector {
    
    public static final Vector3f zero    = new Vector3f(0f, 0f, 0f);
    public static final Vector3f one     = new Vector3f(1f, 1f, 1f);
    public static final Vector3f up      = new Vector3f(0f, 1f, 0f);
    public static final Vector3f down    = new Vector3f(0f, -1f, 0f);
    public static final Vector3f left    = new Vector3f(-1f, 0f, 0f);
    public static final Vector3f right   = new Vector3f(1f, 0f, 0f);
    public static final Vector3f forward = new Vector3f(0f, 0f, 1f);
    public static final Vector3f back    = new Vector3f(0f, 0f, -1f);
    public static final Vector3f positiveInfinity = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    public static final Vector3f negativeInfinity = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    
    /**
     * Spherically interpolates between start vector and the end vector by
     * alpha which is in the range [0,1].
     *
     * @param start The start vector
     * @param end The end vector
     * @param alpha The interpolation coefficient
     * @return The result vector
     */
    public static Vector3f slerp(final Vector3f start, final Vector3f end, float alpha) {
        final float dot = start.dot(end);
        // If the inputs are too close for comfort, simply linearly interpolate.
        if (dot > 0.9995 || dot < -0.9995) {
            return new Vector3f().interpolateLocal(start, end, alpha);
        }
        // theta0 = angle between input vectors
        final float theta0 = (float) Math.acos(dot);
        // theta = angle between this vector and result
        final float theta = theta0 * alpha;
        final float st = (float) Math.sin(theta);
        final float tx = end.x - start.x * dot;
        final float ty = end.y - start.y * dot;
        final float tz = end.z - start.z * dot;
        final float l2 = tx * tx + ty * ty + tz * tz;
        final float dl = st * ((l2 < 0.0001f) ? 1f : 1f / (float) Math.sqrt(l2));
        return start.scaleAdd((float) Math.cos(theta), new Vector3f(tx * dl, ty * dl, tz * dl)).normalizeLocal();
    }
        
    /**
     * Sets the components from the given spherical coordinate
     *
     * @param azimuthalAngle The angle between x-axis in radians [0, 2pi]
     * @param polarAngle The angle between z-axis in radians [0, pi]
     * @return This vector for chaining
     */
    public static Vector3f setFromSpherical(float azimuthalAngle, float polarAngle) {
        float cosPolar = FastMath.cos(polarAngle);
        float sinPolar = FastMath.sin(polarAngle);

        float cosAzim = FastMath.cos(azimuthalAngle);
        float sinAzim = FastMath.sin(azimuthalAngle);

        return new Vector3f(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
    }

    public static Vector3f insideUnitSphere() {
        float u = FastMath.nextRandomFloat();
        float v = FastMath.nextRandomFloat();

        float theta = FastMath.TWO_PI * u; // azimuthal angle
        float phi = (float) Math.acos(2f * v - 1f); // polar angle

        return setFromSpherical(theta, phi);
    }

    public static float distanceTo(Spatial a, Spatial b) {
        return a.getWorldBound().distanceToEdge(b.getWorldTranslation());
    }

    public static float distanceTo(Spatial a, Spatial b, float radius) {
        float dist = FVector.distance(a, b);
        return Math.max(dist - radius, 0f);
    }

    //--------------------------------------------------------------------------
    public static Vector3f subtract(Spatial a, Spatial b) {
        return b.getWorldTranslation().subtract(a.getWorldTranslation());
    }

    public static float distance(Spatial a, Spatial b) {
        return b.getWorldTranslation().distance(a.getWorldTranslation());
    }

    public static float sqrDistance(Spatial a, Spatial b) {
        return b.getWorldTranslation().distanceSquared(a.getWorldTranslation());
    }
    
    //--------------------------------------------------------------------------
    public static float distanceFrom(Vector3f a, Vector3f b) {
        return a.subtract(b).length();
    }

    public static float sqrDistanceFrom(Vector3f a, Vector3f b) {
        return a.subtract(b).lengthSquared();
    }

    public static float angle(Vector3f v1, Vector3f v2) {
        return v1.angleBetween(v2);
    }

    public static Vector3f dirFromAngle(float angle) {
        return new Vector3f(FastMath.sin(angle), 0, FastMath.cos(angle));
    }

    //--------------------------------------------------------------------------
    public static Vector3f forward(Spatial sp) {
        return sp.getWorldRotation().mult(forward);
    }

    public static Vector3f up(Spatial sp) {
        return sp.getWorldRotation().mult(up);
    }

    public static Vector3f right(Spatial sp) {
        return sp.getWorldRotation().mult(right);
    }

    public static Vector3f left(Spatial sp) {
        return sp.getWorldRotation().mult(left);
    }

}
