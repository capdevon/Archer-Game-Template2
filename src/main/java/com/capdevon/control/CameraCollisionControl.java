package com.capdevon.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.capdevon.physx.RaycastHit;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.TempVars;

/**
 * requires ChaseCamera
 * 
 * @author capdevon
 */
public class CameraCollisionControl extends AbstractControl {

    private Camera camera;
    private ChaseCamera chaseCam;

    // The layer mask against which the collider will raycast, default all layers.
    private int collideWithGroups = ~0;
    // Obstacles with this tag will be ignored. It is a good idea to set this field to the target's tag.
    private String ignoreTag = "";
    // When enabled, will attempt to resolve situations where the line of sight to the target is blocked by an obstacle.
    private boolean avoidObstacles = true;
    // Upper limit on how many obstacle hits to process. Higher numbers may impact performance. In most environments, 4 is enough.
    private int maxEffort = 4;
    // Obstacles closer to the target than this will be ignored
    private float minDistanceFromTarget = 0.02f;

    private final Vector3f targetLocation = new Vector3f();
    private final Vector3f targetToCamDirection = new Vector3f();
    private final RaycastHit hitInfo = new RaycastHit();
    private final List<PhysicsRayTestResult> collResults = new ArrayList<>(10);

    /**
     * 
     * @param camera
     */
    public CameraCollisionControl(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.chaseCam = spatial.getControl(ChaseCamera.class);
            Objects.requireNonNull(chaseCam, "ChaseCamera is required to use this control");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (avoidObstacles) {

            float maxDistance = chaseCam.getMaxDistance();
            float zSensitivity = chaseCam.getZoomSensitivity();

            targetLocation.set(spatial.getWorldTranslation()).addLocal(chaseCam.getLookAtOffset());
            targetToCamDirection.set(camera.getLocation()).subtractLocal(targetLocation).normalizeLocal();

            if (Raycast(targetLocation, targetToCamDirection, hitInfo, maxDistance, collideWithGroups)) {
                if (chaseCam.getDistanceToTarget() + hitInfo.normal.length() > hitInfo.distance) {
                    chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMIN, tpf * zSensitivity, tpf);
                }
            } else if (chaseCam.getDistanceToTarget() < maxDistance) {
                chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMOUT, tpf * zSensitivity, tpf);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //do nothing
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

}
