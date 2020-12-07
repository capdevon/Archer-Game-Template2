/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animation3;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public class PlayerControl extends AdapterControl {
	
	private final Logger logger = Logger.getLogger(PlayerControl.class.getName());

    Camera camera;
    AudioNode footsteps;
    
    private Animator animator;
    private BetterCharacterControl bcc;
    private PlayerWeaponManager m_PlayerWeaponManager;
    
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 1);

    private final Quaternion dr = new Quaternion();
    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    private final Vector2f velocity = new Vector2f();

    float m_RunSpeed = 5.5f;
    float m_MoveSpeed = 4.5f;
    float m_TurnSpeed = 10f;

    boolean _MoveForward, _MoveBackward, _MoveLeft, _MoveRight;
    boolean isRunning, isAiming;

    @Override
	public void setSpatial(Spatial sp) {
		super.setSpatial(sp);
		if (spatial != null) {
			this.animator 	= getComponent(Animator.class);
			this.bcc 		= getComponent(BetterCharacterControl.class);
			this.m_PlayerWeaponManager = getComponent(PlayerWeaponManager.class);

			logger.log(Level.INFO, "Initialized");
		}
	}

    @Override
    protected void controlUpdate(float tpf) {

        camera.getDirection(camDir).setY(0);
        camera.getLeft(camLeft).setY(0);

        walkDirection.set(0, 0, 0);

        if (m_PlayerWeaponManager.isAiming) {
            bcc.setWalkDirection(walkDirection);
            bcc.setViewDirection(camDir);
            footsteps.stop();

        } else {
            if (_MoveForward) {
                walkDirection.addLocal(camDir);
            } else if (_MoveBackward) {
                walkDirection.addLocal(camDir.negateLocal());
            }

            if (_MoveLeft) {
                walkDirection.addLocal(camLeft);
            } else if (_MoveRight) {
                walkDirection.addLocal(camLeft.negateLocal());
            }

            walkDirection.normalizeLocal();

            if (walkDirection.lengthSquared() > 0) {
                float angle = FastMath.atan2(walkDirection.x, walkDirection.z);
                dr.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
                spatial.getWorldRotation().slerp(dr, 1 - (tpf * m_TurnSpeed));
                spatial.getWorldRotation().mult(Vector3f.UNIT_Z, viewDirection);
                bcc.setViewDirection(viewDirection);
            }

            float xSpeed = isRunning ? m_RunSpeed : m_MoveSpeed;
            bcc.setWalkDirection(walkDirection.multLocal(xSpeed));

            Vector3f v = bcc.getVelocity(null);
            velocity.set(v.x, v.z);
            boolean isMoving = (velocity.length() / xSpeed) > .2f;

            if (isMoving) {
                setAnimTrigger(isRunning ? AnimDefs.Running_2 : AnimDefs.Running);
                footsteps.setVolume(isRunning ? 2f : .4f);
                footsteps.setPitch(isRunning ? 1f : .85f);
                footsteps.play();

            } else {
                setAnimTrigger(AnimDefs.Idle);
                footsteps.stop();
            }
        }
    }
    
    private void setAnimTrigger(Animation3 newAnim) {
        if (checkTransition(newAnim, AnimDefs.Running, AnimDefs.Running_2)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getAnimationName();
        return (newAnim.equals(a) && b.name.equals(curAnim)) || (newAnim.equals(b) && a.name.equals(curAnim));
    }
	
}
