/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.physx;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author capdevon
 */
public class PhysxQuery {
    
    private static final Logger logger = Logger.getLogger(PhysxQuery.class.getName());
  
    /**
     * DefaultRaycastLayers ALL LAYERS
     */
    private static final int DefaultRaycastLayers = ~0;
    /**
     * IdentityFunction
     */
    private static final Function<PhysicsRigidBody, Boolean> IdentityFunction = x -> true;

    private PhysxQuery() {
    	// private constructor.
    }
    
    /**
     * Computes and stores colliders inside the sphere.
     *
     * @param position	- Center of the sphere.
     * @param radius	- Radius of the sphere.
     * @param layerMask	- A Layer mask defines which layers of colliders to include in the query.
     * @return
     */
    public static Set<Spatial> contactTest(Vector3f position, float radius, int layerMask) {

        Set<Spatial> overlappingObjects = new HashSet<>(5);
        PhysicsGhostObject ghost = new PhysicsGhostObject(new SphereCollisionShape(radius));
        ghost.setPhysicsLocation(position);

        int numContacts = PhysicsSpace.getPhysicsSpace().contactTest(ghost, new PhysicsCollisionListener() {
            @Override
            public void collision(PhysicsCollisionEvent event) {

                // ghost is not linked to any Spatial, so one of the two nodes A and B is null.
                PhysicsCollisionObject pco = event.getNodeA() != null ? event.getObjectA() : event.getObjectB();
                logger.log(Level.INFO, "NodeA={0}, NodeB={1}, CollGroup={2}", new Object[]{event.getNodeA(), event.getNodeB(), pco.getCollisionGroup()});

                if (applyMask(layerMask, pco.getCollisionGroup())) {
                    Spatial userObj = (Spatial) pco.getUserObject();
                    overlappingObjects.add(userObj);
                }
            }
        });

        System.out.println("numContacts: " + numContacts);
        return overlappingObjects;
    }
    
    public static Set<Spatial> contactTest(Vector3f position, float radius) {
        return contactTest(position, radius, DefaultRaycastLayers);
    }

    /**
     * Computes and stores colliders inside the sphere.
     *
     * @param position	- Center of the sphere.
     * @param radius	- Radius of the sphere.
     * @param layerMask	- A Layer mask defines which layers of colliders to include in the query.
     * @param func		- Specifies a function to filter colliders.
     * @return Returns an array with all PhysicsRigidBody touching or inside the
     * sphere.
     */
    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius, int layerMask, Function<PhysicsRigidBody, Boolean> func) {

        List<PhysicsRigidBody> results = new ArrayList<>(10);
        for (PhysicsRigidBody pco : PhysicsSpace.getPhysicsSpace().getRigidBodyList()) {

            if (applyMask(layerMask, pco.getCollisionGroup()) && func.apply(pco)) {
                Vector3f distance = pco.getPhysicsLocation().subtract(position);

                if (distance.length() < radius) {
                    results.add(pco);
                }
            }
        }
        return results;
    }
    
    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius, int layerMask) {
        return overlapSphere(position, radius, layerMask, IdentityFunction);
    }

    public static List<PhysicsRigidBody> overlapSphere(Vector3f position, float radius) {
        return overlapSphere(position, radius, DefaultRaycastLayers, IdentityFunction);
    }
  
    /**
     * Computes and stores colliders inside the sphere into the provided buffer.
     * Does not attempt to grow the buffer if it runs out of space.
     *
     * @param position  - Center of the sphere.
     * @param radius    - Radius of the sphere.
     * @param results   - The buffer to store the results into.
     * @param layerMask - A Layer mask defines which layers of colliders to include in the query.
     * @param func      - Specifies a function to filter colliders.
     * @return Returns the amount of colliders stored into the results buffer.
     */
    public static int overlapSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results, int layerMask, Function<PhysicsRigidBody, Boolean> func) {

        int numColliders = 0;
        for (PhysicsRigidBody pco : PhysicsSpace.getPhysicsSpace().getRigidBodyList()) {

            if (applyMask(layerMask, pco.getCollisionGroup()) && func.apply(pco)) {
                Vector3f distance = pco.getPhysicsLocation().subtract(position);

                if (distance.length() < radius) {
                    results[numColliders++] = pco;
                    if (numColliders == results.length) {
                        break;
                    }
                }
            }
        }
        return numColliders;
    }

    public static int overlapSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results, int layerMask) {
        return overlapSphereNonAlloc(position, radius, results, layerMask, IdentityFunction);
    }

    public static int overlapSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results) {
        return overlapSphereNonAlloc(position, radius, results, DefaultRaycastLayers, IdentityFunction);
    }

    /**
     * Check if a collisionGroup is in a layerMask
     *
     * @param layerMask
     * @param collisionGroup
     * @return
     */
    private static boolean applyMask(int layerMask, int collisionGroup) {
        return layerMask == (layerMask | collisionGroup);
    }

}
