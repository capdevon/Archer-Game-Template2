/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import com.capdevon.physx.RaycastHit;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 */
public class CameraCollisionControl extends AbstractControl {

    private final Spatial scene;
    private final Camera camera;
    private final ChaseCamera chaseCam;
    private final Vector3f targetLocation = new Vector3f();
    private final Vector3f targetToCamDirection = new Vector3f();
    private final RaycastHit hitInfo = new RaycastHit();
    private boolean isZooming;

    /**
     * 
     * @param camera
     * @param target
     * @param scene 
     */
    public CameraCollisionControl(Camera camera, Spatial target, Spatial scene) {
        this.scene = scene;
        this.camera = camera;
        this.chaseCam = target.getControl(ChaseCamera.class);
        target.addControl(this);
    }
    
    public void setZooming(boolean isZooming) {
        this.isZooming = isZooming;
        chaseCam.setRotationSpeed( isZooming ? .5f : 1 );
//        chaseCam.setDefaultDistance( isZooming ? chaseCam.getMinDistance() : chaseCam.getMaxDistance() );
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        float distMin = chaseCam.getMinDistance();
        float distMax = chaseCam.getMaxDistance();
        float zSensitivity = chaseCam.getZoomSensitivity();
        
        if (isZooming) {
            if (chaseCam.getDistanceToTarget() > distMin) {
                chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMIN, tpf * zSensitivity, tpf);
            }
            return;
        }

        targetLocation.set( spatial.getWorldTranslation() ).addLocal(chaseCam.getLookAtOffset());
        targetToCamDirection.set( camera.getLocation() ).subtractLocal(targetLocation).normalizeLocal();

        if (doRaycast(targetLocation, targetToCamDirection, distMax, hitInfo)) {
            if (chaseCam.getDistanceToTarget() + hitInfo.normal.length() > hitInfo.distance) {
                chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMIN, tpf * zSensitivity, tpf);
            }
        } else if (chaseCam.getDistanceToTarget() < distMax) {
            chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMOUT, tpf * zSensitivity, tpf);
        }
    }
    
    /**
     * perform simple raycast
     */
    private boolean doRaycast(Vector3f origin, Vector3f dir, float maxDistance, RaycastHit out) {
        Ray ray = new Ray(origin, dir);
        ray.setLimit(maxDistance); // FIXME: Bug!
        
        CollisionResults results = new CollisionResults();
        scene.collideWith(ray, results);
        
        boolean hit = false;
		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();
			out.userObject 	= closest.getGeometry();
			out.normal 		= closest.getContactNormal();
			out.point 		= closest.getContactPoint();
			out.distance 	= closest.getDistance();

			if (out.distance < maxDistance)
				hit = true;
		}
        
        return hit;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //To change body of generated methods, choose Tools | Templates.
    }

}
