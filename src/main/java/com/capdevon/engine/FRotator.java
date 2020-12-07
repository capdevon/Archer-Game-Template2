/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 */
public class FRotator {
    
    /**
     * 
     * @param direction
     * @return 
     */
    public static Quaternion fromToAngle(Vector3f direction) {
        float angle = FastMath.atan2(direction.x, direction.z);
        return new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y);
    }
    
    /**
     * 
     * @param direction
     * @return 
     */
    public static Quaternion lookAtRotation(Vector3f direction) {
        Quaternion q = new Quaternion();
        q.lookAt(direction, Vector3f.UNIT_Y);
        return q;
    }

    /**
     * 
     * @param sp
     * @param direction
     * @param changeAmnt 
     */
    public static void slerp(Spatial sp, Vector3f direction, float changeAmnt) {
        Quaternion q = fromToAngle(direction);
        sp.getLocalRotation().slerp(q, changeAmnt);
    }
    
}
