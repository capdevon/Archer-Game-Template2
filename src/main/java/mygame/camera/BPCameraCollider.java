package mygame.camera;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.capdevon.engine.GameObject;
import com.capdevon.physx.RaycastHit;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.MultiSphere;
import com.jme3.input.InputManager;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;

import jme3utilities.math.MyVector3f;

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

    private float cameraRadius = 0.3f;
    
    private final RaycastHit hitInfo = new RaycastHit();
    private final List<PhysicsSweepTestResult> sweepTestResults = new LinkedList<>();

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
        testSightLine();
        updateCamera(tpf);
    }
    
    /**
     * handle collisions
     */
    protected void testSightLine() {

        if (avoidObstacles) {

            Vector3f origin = pitchNode.getWorldTranslation();
            Vector3f dirToCamera = pitchNode.getWorldRotation().mult(Vector3f.UNIT_Z).negateLocal();

            float distance = -getMaxDistance();
            //The sphereCast goes from the pitchNode towards the camera
            if (sphereCast(origin, cameraRadius, dirToCamera, hitInfo, getMaxDistance(), collideWithGroups)) {
                distance = -hitInfo.distance;
            }

            setDistanceToTarget(distance);
        }
    }
    
    private boolean sphereCast(Vector3f origin, float radius, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        boolean collision = false;
        hitInfo.clear();
        
        float penetration = 0f; // physics-space units

        ConvexShape shape = new MultiSphere(radius);
        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        
        //The order of sweep-test results is arbitrary.
        physicsSpace.sweepTest(shape, new Transform(beginVec), new Transform(finalVec), sweepTestResults, penetration);
        sweepTestResults.sort(hitFractionComparator);
        
        for (PhysicsSweepTestResult tr : sweepTestResults) {
        	
            PhysicsCollisionObject pco = tr.getCollisionObject();
            if (!(pco.getUserObject() instanceof Spatial)) {
            	continue;
            }
            
            Spatial userObject = (Spatial) pco.getUserObject();
            
            boolean isObstruction = applyMask(layerMask, pco.getCollisionGroup()) 
                  && !GameObject.compareTag(userObject, ignoreTag);

            if (isObstruction) {
            	
            	hitInfo.rigidBody = pco;
            	hitInfo.collider = pco.getCollisionShape();
            	hitInfo.gameObject = userObject;
                MyVector3f.lerp(tr.getHitFraction(), beginVec, finalVec, hitInfo.point);
                tr.getHitNormalLocal(hitInfo.normal);
                hitInfo.distance = beginVec.distance(hitInfo.point);
                
                collision = true;
                break;
            }
        }
        
        t.release();
        return collision;
    }
 
    private static final Comparator<PhysicsSweepTestResult> hitFractionComparator = new Comparator<>() {
        @Override
        public int compare(PhysicsSweepTestResult r1, PhysicsSweepTestResult r2) {
            float r1Fraction = r1.getHitFraction();
            float r2Fraction = r2.getHitFraction();
            int result = Float.compare(r1Fraction, r2Fraction);
            return result;
        }
    };
    
    // Check if a collisionGroup is in a layerMask
    private boolean applyMask(int layerMask, int collisionGroup) {
        return layerMask == (layerMask | collisionGroup);
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

    public float getCameraRadius() {
        return cameraRadius;
    }

    public void setCameraRadius(float cameraRadius) {
        this.cameraRadius = cameraRadius;
    }

}
