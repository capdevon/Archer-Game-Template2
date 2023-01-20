package com.capdevon.physx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

/**
 * 
 * @author capdevon
 */
public class Physics {
    
    private static final Logger logger = Logger.getLogger(Physics.class.getName());

    /**
     * Default gravity
     */
    public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0, -9.81f, 0).multLocal(2);
    /**
     * DefaultRaycastLayers
     */
    public static final int ALL_LAYERS = ~0;

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Physics() {}

    /**
     * Applies a force to a rigidbody that simulates explosion effects.
     * 
     * @param rb
     * @param explosionForce	- The force of the explosion (which may be modified by distance).
     * @param explosionPosition	- The centre of the sphere within which the explosion has its effect.
     * @param explosionRadius	- The radius of the sphere within which the explosion has its effect.
     */
    public static void addExplosionForce(PhysicsRigidBody rb, float explosionForce, Vector3f explosionPosition, float explosionRadius) {
        Vector3f expCenter2Body = rb.getPhysicsLocation().subtract(explosionPosition);
        float distance = expCenter2Body.length();
        if (distance < explosionRadius) {
            // apply proportional explosion force
            float strength = (1.f - FastMath.clamp(distance / explosionRadius, 0, 1)) * explosionForce;
            rb.setLinearVelocity(expCenter2Body.normalize().mult(strength));
        }
    }
    
    /**
     * Casts a ray through the scene and returns all hits.
     * 
     * @param origin      The starting point of the ray in world coordinates.
     * @param direction   The direction of the ray.
     * @param maxDistance The max distance the rayhit is allowed to be from the
     *                    start of the ray.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders
     *                    when casting a ray.
     * @return
     */
    public static List<RaycastHit> raycastAll(Vector3f origin, Vector3f direction, float maxDistance, int layerMask) {

        List<RaycastHit> lstResults = new ArrayList<>();

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTestRaw(beginVec, finalVec);

        for (PhysicsRayTestResult ray : results) {
            PhysicsCollisionObject pco = ray.getCollisionObject();

            if (applyMask(layerMask, pco.getCollisionGroup())) {

                RaycastHit hitInfo = new RaycastHit();
                hitInfo.set(beginVec, finalVec, ray);
                lstResults.add(hitInfo);
            }
        }

        t.release();
        return lstResults;
    }
     
    /**
     * Casts a ray through the scene and returns all hits.
     */
    public static List<RaycastHit> raycastAll(Vector3f origin, Vector3f direction, float maxDistance) {
        return raycastAll(origin, direction, maxDistance, ALL_LAYERS);
    }
    
    /**
     * Casts a ray, from point origin, in direction direction, of length
     * maxDistance, against all colliders in the scene. You may optionally
     * provide a LayerMask, to filter out any Colliders you aren't interested in
     * generating collisions with.
     *
     * @param origin        - The starting point of the ray in world coordinates. (not null, unaffected)
     * @param direction     - The direction of the ray. (not null, unaffected)
     * @param hitInfo       - If true is returned, hitInfo will contain more information about where the closest collider was hit. (See Also: RaycastHit).
     * @param maxDistance   - The max distance the ray should check for collisions.
     * @param layerMask     - A Layer mask that is used to selectively ignore Colliders when casting a ray.
     * @return Returns true if the ray intersects with a Collider, otherwise
     * false.
     */
    public static boolean raycast(Vector3f origin, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {
        
        hitInfo.clear();
        boolean collision = false;

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTest(beginVec, finalVec);

        for (PhysicsRayTestResult ray : results) {
            PhysicsCollisionObject pco = ray.getCollisionObject();

//            if (pco instanceof GhostControl) {
//                logger.log(Level.FINE, "Skipping GhostControl for gameObject={0}", GameObject.findGameObject(pco);
//                continue;
//            }

            if (applyMask(layerMask, pco.getCollisionGroup())) {
                hitInfo.set(beginVec, finalVec, ray);
                collision = true;
                break;
            }
        }

        t.release();
        return collision;
    }
    
    public static boolean raycast(Vector3f origin, Vector3f direction, RaycastHit hitInfo, float maxDistance) {
        return raycast(origin, direction, hitInfo, maxDistance, ALL_LAYERS);
    }
    
    public static boolean raycast(Ray ray, RaycastHit hitInfo, float maxDistance, int layerMask) {
        return raycast(ray.origin, ray.direction, hitInfo, maxDistance, layerMask);
    }
    
    public static boolean raycast(Ray ray, RaycastHit hitInfo, float maxDistance) {
        return raycast(ray.origin, ray.direction, hitInfo, maxDistance, ALL_LAYERS);
    }

    /**
     * Returns true if there is any collider intersecting the line between start and end.
     * 
     * @param beginVec  - Start point (not null, unaffected).
     * @param finalVec  - End point (not null, unaffected).
     * @param hitInfo   - If true is returned, hitInfo will contain more information about where the closest collider was hit. (See Also: RaycastHit).
     * @param layerMask - A Layer mask that is used to selectively ignore Colliders when casting a ray.
     * @return Returns true if the ray intersects with a Collider, otherwise
     * false.
     */
    public static boolean linecast(Vector3f beginVec, Vector3f finalVec, RaycastHit hitInfo, int layerMask) {

        hitInfo.clear();
        boolean collision = false;

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTest(beginVec, finalVec);
        for (PhysicsRayTestResult ray : results) {

            PhysicsCollisionObject pco = ray.getCollisionObject();

//            if (pco instanceof GhostControl) {
//                logger.log(Level.FINE, "Skipping GhostControl for gameObject={0}", GameObject.findGameObject(pco));
//                continue;
//            }

            if (applyMask(layerMask, pco.getCollisionGroup())) {
                hitInfo.set(beginVec, finalVec, ray);
                collision = true;
                break;
            }
        }

        return collision;
    }
        
    /**
     * Returns true if there is any collider intersecting the line between start and end.
     */
    public static boolean linecast(Vector3f beginVec, Vector3f finalVec, RaycastHit hitInfo) {
        return linecast(beginVec, finalVec, hitInfo, ALL_LAYERS);
    }
    
    /**
     * Casts the box along a ray and returns detailed information on what was hit.
     * https://docs.unity3d.com/ScriptReference/Physics.BoxCast.html
     * 
     * @param center      Center of the box.
     * @param halfExtents Half the size of the box in each dimension.
     * @param direction   The direction in which to cast the box.
     * @param hitInfo     If true is returned, hitInfo will contain more information
     *                    about where the collider was hit.
     * @param maxDistance The max length of the cast.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders
     *                    when casting a capsule.
     * @return True, if any intersections were found.
     */
    public static boolean boxCast(Vector3f center, Vector3f halfExtents, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {
        BoxCollisionShape shape = new BoxCollisionShape(halfExtents);
        return sweepTest(center, direction, shape, hitInfo, maxDistance, layerMask);
    }

    public static List<RaycastHit> boxCastAll(Vector3f center, Vector3f halfExtents, Vector3f direction, float maxDistance, int layerMask) {
        BoxCollisionShape shape = new BoxCollisionShape(halfExtents);
        return sweepTestAll(center, direction, shape, maxDistance, layerMask);
    }
    
    /**
     * Casts a sphere along a ray and returns detailed information on what was hit.
     * https://docs.unity3d.com/ScriptReference/Physics.SphereCast.html
     * 
     * @param center      The center of the sphere at the start of the sweep.
     * @param radius      The radius of the sphere.
     * @param direction   The direction into which to sweep the sphere.
     * @param hitInfo     If true is returned, hitInfo will contain more information
     *                    about where the collider was hit.
     * @param maxDistance The max length of the cast.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders
     *                    when casting a capsule.
     * @return True, if any intersections were found.
     */
    public static boolean sphereCast(Vector3f center, float radius, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        return sweepTest(center, direction, shape, hitInfo, maxDistance, layerMask);
    }

    public static List<RaycastHit> sphereCastAll(Vector3f center, float radius, Vector3f direction, float maxDistance, int layerMask) {
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        return sweepTestAll(center, direction, shape, maxDistance, layerMask);
    }

    private static boolean sweepTest(Vector3f center, Vector3f direction, ConvexShape shape, RaycastHit hitInfo, float maxDistance, int layerMask) {

        hitInfo.clear();
        boolean collision = false;
        float hf = maxDistance;

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(center);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, center);

        float penetration = 0f; // physics-space units
        List<PhysicsSweepTestResult> results = new LinkedList<>();

        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        physicsSpace.sweepTest(shape, new Transform(beginVec), new Transform(finalVec), results, penetration);

        for (PhysicsSweepTestResult tr : results) {
            PhysicsCollisionObject pco = tr.getCollisionObject();

            if (tr.getHitFraction() < hf && applyMask(layerMask, pco.getCollisionGroup())) {
            	hf = tr.getHitFraction();
                hitInfo.set(beginVec, finalVec, tr);
                collision = true;
            }
        }

        t.release();
        return collision;
    }
	
    private static List<RaycastHit> sweepTestAll(Vector3f origin, Vector3f direction, ConvexShape shape, float maxDistance, int layerMask) {

        List<RaycastHit> lstResults = new ArrayList<>();

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        float penetration = 0f; // physics-space units
        List<PhysicsSweepTestResult> results = new LinkedList<>();

        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        physicsSpace.sweepTest(shape, new Transform(beginVec), new Transform(finalVec), results, penetration);

        for (PhysicsSweepTestResult tr : results) {
            PhysicsCollisionObject pco = tr.getCollisionObject();

            if (applyMask(layerMask, pco.getCollisionGroup())) {

                RaycastHit hitInfo = new RaycastHit();
                hitInfo.set(beginVec, finalVec, tr);
                lstResults.add(hitInfo);
            }
        }

        t.release();
        return lstResults;
    }
   
    /**
     * Computes and stores colliders inside the sphere.
     * https://docs.unity3d.com/ScriptReference/Physics.OverlapSphere.html
     *
     * @param position  Center of the sphere.
     * @param radius    Radius of the sphere.
     * @param layerMask A Layer mask defines which layers of colliders to include in
     *                  the query.
     * @return Returns all colliders that overlap with the given sphere.
     */
    public static Set<PhysicsCollisionObject> overlapSphere(Vector3f position, float radius, int layerMask) {

        Set<PhysicsCollisionObject> overlappingObjects = new HashSet<>(5);
        PhysicsGhostObject ghost = new PhysicsGhostObject(new SphereCollisionShape(radius)); //MultiSphere
        ghost.setPhysicsLocation(position);

        contactTest(ghost, overlappingObjects, layerMask);
        return overlappingObjects;
    }

    public static Set<PhysicsCollisionObject> overlapSphere(Vector3f position, float radius) {
        return overlapSphere(position, radius, ALL_LAYERS);
    }
    
    /**
     * Find all colliders touching or inside of the given box.
     * https://docs.unity3d.com/ScriptReference/Physics.OverlapBox.html
     * 
     * @param center      Center of the box.
     * @param halfExtents Half of the size of the box in each dimension.
     * @param rotation    Rotation of the box.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders
     *                    when casting a ray.
     * @return Returns all colliders that overlap with the given box.
     */
    public static Set<PhysicsCollisionObject> overlapBox(Vector3f center, Vector3f halfExtents, Quaternion rotation, int layerMask) {

        Set<PhysicsCollisionObject> overlappingObjects = new HashSet<>(5);
        PhysicsGhostObject ghost = new PhysicsGhostObject(new BoxCollisionShape(halfExtents));
        ghost.setPhysicsLocation(center);
        ghost.setPhysicsRotation(rotation);

        contactTest(ghost, overlappingObjects, layerMask);
        return overlappingObjects;
    }

    public static Set<PhysicsCollisionObject> overlapBox(Vector3f center, Vector3f halfExtents, Quaternion rotation) {
        return overlapBox(center, halfExtents, rotation, ALL_LAYERS);
    }

    /**
     * Perform a contact test. This will not detect contacts with soft bodies.
     */
    private static int contactTest(PhysicsGhostObject ghost, final Set<PhysicsCollisionObject> overlappingObjects, int layerMask) {

        overlappingObjects.clear();

        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        int numContacts = physicsSpace.contactTest(ghost, (PhysicsCollisionEvent event) -> {

            if (event.getDistance1() > 0f) {
                // bug: Discard contacts with positive distance between the colliding objects.
            } else {
                PhysicsCollisionObject pco = event.getNodeA() != null ? event.getObjectA() : event.getObjectB();

//                logger.log(Level.INFO, "NodeA={0} NodeB={1} CollGroup={2}",
//                        new Object[]{event.getNodeA(), event.getNodeB(), pco.getCollisionGroup()});

                if (applyMask(layerMask, pco.getCollisionGroup())) {
                    overlappingObjects.add(pco);
                }
            }
        });

        //logger.log(Level.INFO, "numContacts={0}", numContacts);
        return numContacts;
    }
    
    //TODO: https://docs.unity3d.com/ScriptReference/Physics.OverlapBoxNonAlloc.html
    //TODO: https://docs.unity3d.com/ScriptReference/Physics.OverlapSphereNonAlloc.html
    //TODO: https://docs.unity3d.com/ScriptReference/Physics.BoxCastNonAlloc.html
    //TODO: https://docs.unity3d.com/ScriptReference/Physics.SphereCastNonAlloc.html
    
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
