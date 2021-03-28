/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.camera;

import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;

/**
 *
 * @author capdevon
 */
public class MainCamera {
    
    private Camera cam;
    private float fieldOfView;
    private float near;
    private float far;
    
    /**
     * Creates a camera state that will initialize the application camera to a
     * 45 degree fov, 0.1 near plane, and 1000 far plane.
     * 
     * @param cam
     */
    public MainCamera(Camera cam) {
        this(cam, 45, 0.1f, 1000); // 45 is the default JME fov
    }

    /**
     * Creates a camera state that will initialize the specified camera to the
     * specified parameters. If the specified camera is null then the
     * application's main camera will be used.
     * 
     * @param cam
     * @param fov
     * @param near
     * @param far
     */
    public MainCamera(Camera cam, float fov, float near, float far) {
        this.cam = cam;
        this.fieldOfView = fov;
        this.near = near;
        this.far = far;
        resetCamera();
    }
    
    public void setFieldOfView(float f) {
        if (this.fieldOfView == f) {
            return;
        }
        this.fieldOfView = f;
        resetCamera();
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setNear(float f) {
        if (this.near == f) {
            return;
        }
        this.near = f;
        resetCamera();
    }

    public float getNear() {
        return near;
    }

    public void setFar(float f) {
        if (this.far == f) {
            return;
        }
        this.far = f;
        resetCamera();
    }

    public float getFar() {
        return far;
    }
    
    private void resetCamera() {
        float aspect = (float) cam.getWidth() / (float) cam.getHeight();
        cam.setFrustumPerspective(fieldOfView, aspect, near, far);
    }
    
    public static float fieldOfView(Camera camera) {
        float yTangent = yTangent(camera);
        float fovY = 2f * FastMath.atan(yTangent);
        float yDegrees = fovY * FastMath.RAD_TO_DEG;
        return yDegrees;
    }

    private static float yTangent(Camera camera) {
        float near = camera.getFrustumNear();
        float height = camera.getFrustumTop() - camera.getFrustumBottom();
        float halfHeight = height / 2.0F;
        float yTangent = halfHeight / near;
        return yTangent;
    }
    
}
