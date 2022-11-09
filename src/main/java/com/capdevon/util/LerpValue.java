package com.capdevon.util;

import com.jme3.math.FastMath;

public class LerpValue {

    boolean change;
    float dt;
    float speed;
    float startValue;
    float endValue;

    public LerpValue(float speed, float startValue, float endValue) {
        this.speed = speed;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public void change(boolean change) {
        this.change = change;
    }

    public float interpolate(float tpf) {
        if (dt == 0 && !change) {
            return 0;
        }

        if (dt == 1 && change) {
            return 1;
        }

        float m = speed * tpf;
        dt = (change) ? (dt + m) : (dt - m);
        dt = FastMath.clamp(dt, 0, 1);
        return FastMath.interpolateLinear(dt, startValue, endValue);
    }

    /* getters & setters */
}
