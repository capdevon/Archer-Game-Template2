package com.capdevon.physx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

/**
 * 
 * @author capdevon
 */
public class PhysxQuery {
    
    private static final Logger logger = Logger.getLogger(PhysxQuery.class.getName());
  
    /**
     * DefaultRaycastLayers
     */
    private static final int ALL_LAYERS = ~0;
    /**
     * IdentityFunction
     */
    private static final Predicate<PhysicsRigidBody> IdentityFunction = x -> true;

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private PhysxQuery() {}
    
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
    public static List<PhysicsRigidBody> checkSphere(Vector3f position, float radius, int layerMask, Predicate<PhysicsRigidBody> func) {

        List<PhysicsRigidBody> results = new ArrayList<>(10);
        for (PhysicsRigidBody pco : PhysicsSpace.getPhysicsSpace().getRigidBodyList()) {

            if (applyMask(layerMask, pco.getCollisionGroup()) && func.test(pco)) {
                float sqrDistance = pco.getPhysicsLocation().distanceSquared(position);

                if (sqrDistance < radius * radius) {
                    results.add(pco);
                }
            }
        }
        return results;
    }
    
    public static List<PhysicsRigidBody> checkSphere(Vector3f position, float radius, int layerMask) {
        return checkSphere(position, radius, layerMask, IdentityFunction);
    }

    public static List<PhysicsRigidBody> checkSphere(Vector3f position, float radius) {
        return checkSphere(position, radius, ALL_LAYERS, IdentityFunction);
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
    public static int checkSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results, int layerMask, Predicate<PhysicsRigidBody> func) {

        int numColliders = 0;
        for (PhysicsRigidBody pco : PhysicsSpace.getPhysicsSpace().getRigidBodyList()) {

            if (applyMask(layerMask, pco.getCollisionGroup()) && func.test(pco)) {
                float sqrDistance = pco.getPhysicsLocation().distanceSquared(position);

                if (sqrDistance < radius * radius) {
                    results[numColliders++] = pco;
                    if (numColliders == results.length) {
                        break;
                    }
                }
            }
        }
        return numColliders;
    }

    public static int checkSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results, int layerMask) {
        return checkSphereNonAlloc(position, radius, results, layerMask, IdentityFunction);
    }

    public static int checkSphereNonAlloc(Vector3f position, float radius, PhysicsRigidBody[] results) {
        return checkSphereNonAlloc(position, radius, results, ALL_LAYERS, IdentityFunction);
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
