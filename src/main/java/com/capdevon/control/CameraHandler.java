/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;

/**
 *
 */
public abstract class CameraHandler {

    /**
     * disable the default 1st-person flyCam!
     * 
     * @param cam
     * @param target
     * @param inputManager
     * @param useJoystick
     * @return 
     */
    public static ChaseCamera bindChaseCamera(Camera cam, Spatial target, InputManager inputManager, boolean useJoystick) {

        TPSChaseCamera chaseCam = new TPSChaseCamera(cam, target);
        chaseCam.registerWithInput(inputManager, useJoystick);
        
        chaseCam.setUpVector(Vector3f.UNIT_Y.clone());
        chaseCam.setLookAtOffset(new Vector3f(0f, 2f, 0f));
        chaseCam.setMaxDistance(4f);
        chaseCam.setMinDistance(1.5f);
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setMinVerticalRotation(-FastMath.QUARTER_PI);
        chaseCam.setRotationSpeed(2f);
        chaseCam.setRotationSensitivity(1.5f);
        chaseCam.setZoomSensitivity(4f);
        //chaseCam.setSmoothMotion(true);
//        chaseCam.setDragToRotate(false);
        chaseCam.setDownRotateOnCloseViewOnly(false);

        return chaseCam;
    }

    /**
     * @param cam
     * @param target
     * @param location
     * @param lookAtOffset 
     */
    public static void bindCameraNode(Camera cam, Node target, Vector3f location, Vector3f lookAtOffset) {
        // creating camera node
        CameraNode camNode = new CameraNode("Camera.Node", cam);
        // setting the direction, the camera will copy the movements of the node
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        // attaching the camNode to the targetNode
        target.attachChild(camNode);
        // setting the local translation of the camNode to move it away from the targetNode a bit
        camNode.setLocalTranslation(location);
        // setting the camNode to look at the targetNode
        camNode.lookAt(lookAtOffset, Vector3f.UNIT_Y);
    }

}
