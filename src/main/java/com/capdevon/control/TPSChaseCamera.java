/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 */
public class TPSChaseCamera extends ChaseCamera {

    /**
     * Constructs the chase camera, but don't registers inputs!!!
     *
     * @param cam
     * @param target
     */
    public TPSChaseCamera(Camera cam, Spatial target) {
        super(cam, target);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setDragToRotate(false);
    }

    public void registerWithInput(InputManager inputManager, boolean useJoysticks) {
        registerWithInput(inputManager);
        setDragToRotate(false);
        
        if (useJoysticks) {
            Joystick[] joysticks = inputManager.getJoysticks();
            if (joysticks != null) {
                for (Joystick j : joysticks) {
                    mapJoysticks(j);
                }
            }
        }
    }

    private void mapJoysticks(Joystick joystick) {
        JoystickAxis zRotation = joystick.getAxis(JoystickAxis.Z_ROTATION);
        JoystickAxis zAxis = joystick.getAxis(JoystickAxis.Z_AXIS);

        if (zRotation != null && zAxis != null) {
            assignAxis(zRotation, CameraInput.CHASECAM_UP, CameraInput.CHASECAM_DOWN);
            assignAxis(zAxis, CameraInput.CHASECAM_MOVERIGHT, CameraInput.CHASECAM_MOVELEFT);
        }
    }

    private void assignAxis(JoystickAxis axis, String pMapping, String nMapping) {
        axis.assignAxis(pMapping, nMapping);
        inputManager.addListener(this, pMapping, nMapping);
    }

}
