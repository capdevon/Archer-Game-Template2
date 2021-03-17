/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.math.FastMath;

/**
 * @author capdevon
 */
public class MathUtils {

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @returna
     */
    public static int clamp01(int value) {
        return clamp(value, 0, 1);
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static long clamp01(long value) {
        return clamp(value, 0, 1);
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static float clamp01(float value) {
        return clamp(value, 0, 1);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static double clamp01(double value) {
        return clamp(value, 0, 1);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Linearly interpolates between fromValue to toValue on progress position.
     * 
     * @param fromValue
     * @param toValue
     * @param progress
     * @return 
     */
    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    /**
     * Return a random float number between min [inclusive] and max [inclusive].
     * 
     * @param min
     * @param max
     * @return 
     */
    public static float range(float min, float max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return FastMath.nextRandomFloat() * (max - min) + min;
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified value.
     * @param chance
     * @return 
     */
    public static boolean randomBoolean(float chance) {
        return FastMath.nextRandomFloat() < chance;
    }

}
