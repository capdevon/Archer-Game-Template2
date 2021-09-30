package mygame.camera;

import java.util.ArrayList;
import java.util.List;

import com.capdevon.physx.RaycastHit;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;

/**
 *
 * @author capdevon
 */
public class BPCameraCollider extends BPPlayerCamera {

    // The layer mask against which the collider will raycast
    private int collideWithGroups = PhysicsCollisionObject.COLLISION_GROUP_01;
    // Obstacles with this tag will be ignored. It is a good idea to set this field to the target's tag
    private String ignoreTag = "";
    // When enabled, will attempt to resolve situations where the line of sight to the target is blocked by an obstacle
    private boolean avoidObstacles = true;
    // Upper limit on how many obstacle hits to process. Higher numbers may impact performance. In most environments, 4 is enough.
    private int maxEffort = 4;
    // Obstacles closer to the target than this will be ignored
    private float minDistanceFromTarget = 0.05f;
    
    private final Vector3f tempDirection = new Vector3f(0, 0, 1);
    private final RaycastHit hitInfo = new RaycastHit();
    private final List<PhysicsRayTestResult> collResults = new ArrayList<>(10);

    /**
     * 
     * @param camera
     * @param inputManager 
     */
    public BPCameraCollider(Camera camera, InputManager inputManager) {
        super(camera, inputManager);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        testSightLine(tpf);
        updateCamera(tpf);
    }
    
    /**
     * handle collisions
     * @param tpf 
     */
    protected void testSightLine(float tpf) {

        if (avoidObstacles) {

            Vector3f origin = pitchNode.getWorldTranslation();
            pitchNode.getWorldRotation().mult(Vector3f.UNIT_Z, tempDirection).negateLocal();
            
            float distance = -getMaxDistance();
            if (Raycast(origin, tempDirection, hitInfo, getMaxDistance(), collideWithGroups)) {
                distance = 0.01f - hitInfo.distance;
            }
            
            setDistanceToTarget(distance);
        }
    }
 
    private boolean Raycast(Vector3f origin, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).multLocal(maxDistance).addLocal(origin);

        PhysicsSpace.getPhysicsSpace().rayTest(beginVec, finalVec, collResults);

        int i = 0;
        float hf = maxDistance;
        boolean collision = false;
        
        for (PhysicsRayTestResult ray : collResults) {
            
            PhysicsCollisionObject pco = ray.getCollisionObject();
            Spatial userObj = (Spatial) pco.getUserObject();
            
            boolean isObstruction = between(ray.getHitFraction(), minDistanceFromTarget, hf)
                    && filter(layerMask, pco.getCollisionGroup()) 
                    && !hasTag(userObj, ignoreTag);
            
            if (isObstruction) {
                
                collision = true;
                hf = ray.getHitFraction();
                
                hitInfo.rigidbody   = pco;
                hitInfo.collider    = pco.getCollisionShape();
                hitInfo.userObject  = userObj;
                hitInfo.distance    = finalVec.subtract(beginVec, t.vect3).length() * hf;
                hitInfo.point.interpolateLocal(beginVec, finalVec, hf);
                ray.getHitNormalLocal(hitInfo.normal);
            }
            
            if (++i == maxEffort) {
                break;
            }
        }

        if (!collision) {
            hitInfo.clear();
        }

        t.release();
        return collision;
    }
    
    private boolean hasTag(Spatial sp, String tagName) {
        return tagName.equals(sp.getUserData("TagName"));
    }
    
    // Check if a collisionGroup is in a layerMask
    private boolean filter(int layerMask, int collisionGroup) {
        return layerMask == (layerMask | collisionGroup);
    }
    
    // Determine if value is between a range
    private boolean between(float value, float min, float max) {
        return (value > min && value < max);
    }

    public int getCollideWithGroups() {
        return collideWithGroups;
    }

    public void setCollideWithGroups(int collideWithGroups) {
        this.collideWithGroups = collideWithGroups;
    }

    public String getIgnoreTag() {
        return ignoreTag;
    }

    public void setIgnoreTag(String ignoreTag) {
        this.ignoreTag = ignoreTag;
    }

    public boolean isAvoidObstacles() {
        return avoidObstacles;
    }

    public void setAvoidObstacles(boolean avoidObstacles) {
        this.avoidObstacles = avoidObstacles;
    }

    public int getMaxEffort() {
        return maxEffort;
    }

    public void setMaxEffort(int maxEffort) {
        this.maxEffort = maxEffort;
    }

    public float getMinDistanceFromTarget() {
        return minDistanceFromTarget;
    }

    public void setMinDistanceFromTarget(float minDistanceFromTarget) {
        this.minDistanceFromTarget = minDistanceFromTarget;
    }

}
