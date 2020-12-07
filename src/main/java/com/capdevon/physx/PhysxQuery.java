/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.physx;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 */
public class PhysxQuery {
    
    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius) {
        return overlapSphere(position, radius, (x) -> x.getMass() > 0);
    }

    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius, int layerMask) {
        return overlapSphere(position, radius, (x) -> (x.getMass() > 0 && x.getCollisionGroup() == layerMask));
    }

    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius, Function<PhysicsRigidBody, Boolean> func) {
        System.out.println("\n ---OverlapSphere[position: " + position + ", radius: " + radius + "]");

        List<PhysicsRigidBody> lst = new ArrayList<>();
        for (PhysicsRigidBody rgb : PhysicsSpace.getPhysicsSpace().getRigidBodyList()) {
            if (func.apply(rgb)) {
                Vector3f distance = rgb.getPhysicsLocation().subtract(position);
                if (distance.length() < radius) {
                    lst.add(rgb);
                }
            }
        }
        return lst;
    }

}
