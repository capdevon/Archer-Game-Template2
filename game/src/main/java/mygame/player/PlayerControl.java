package mygame.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animation3;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.capdevon.engine.FRotator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import mygame.AnimDefs.Archer;

/**
 * 
 * @author capdevon
 */
public class PlayerControl extends AdapterControl {

    private final Logger logger = Logger.getLogger(PlayerControl.class.getName());

    Camera camera;
    AudioNode footstepsSFX;

    private Animator animator;
    private BetterCharacterControl bcc;
    private PlayerWeaponManager m_PlayerWeaponManager;

    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 1);

    private final Quaternion dr = new Quaternion();
    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    private final Vector2f velocity = new Vector2f();

    boolean _MoveForward, _MoveBackward, _MoveLeft, _MoveRight;
    boolean isRunning;
    
    public float runSpeed = 5.5f;
    public float moveSpeed = 4.5f;
    public float turnSpeed = 10f;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.animator = getComponent(Animator.class);
            this.bcc = getComponent(BetterCharacterControl.class);
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
            footstepsSFX.stop();

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
                
                float smoothTime = 1 - (tpf * turnSpeed);
                FRotator.smoothDamp(spatial.getWorldRotation(), dr, smoothTime, viewDirection);
                bcc.setViewDirection(viewDirection);
            }

            float xSpeed = isRunning ? runSpeed : moveSpeed;
            bcc.setWalkDirection(walkDirection.multLocal(xSpeed));

            Vector3f v = bcc.getVelocity(null);
            velocity.set(v.x, v.z);
            boolean isMoving = (velocity.length() / xSpeed) > .2f;

            if (isMoving) {
                setAnimTrigger(isRunning ? Archer.Sprinting : Archer.Running);
                footstepsSFX.setVolume(isRunning ? 2f : .4f);
                footstepsSFX.setPitch(isRunning ? 1f : .85f);
                footstepsSFX.play();

            } else {
                setAnimTrigger(Archer.Idle);
                footstepsSFX.stop();
            }
        }
    }

    private void setAnimTrigger(Animation3 newAnim) {
        if (checkTransition(newAnim, Archer.Running, Archer.Sprinting)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getCurrentAnimation();
        return (newAnim.equals(a) && b.name.equals(curAnim)) || (newAnim.equals(b) && a.name.equals(curAnim));
    }

}
