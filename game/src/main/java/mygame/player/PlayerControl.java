package mygame.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animation3;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import jme3utilities.math.MyMath;
import jme3utilities.math.MyVector3f;

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

    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    private final Vector2f velocity = new Vector2f();

    boolean _MoveForward, _MoveBackward, _MoveLeft, _MoveRight;
    boolean isRunning;

    public float runSpeed = 5.5f;
    public float moveSpeed = 4.5f;
    public float turnSpeed = 10f;
    /**
     * control the overall rotation of the avatar via BetterCharacterControl
     */
    private final Damper bccTurner = new Damper(0.4f, -0.2f);

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

        camera.getDirection(camDir);
        camDir.setY(0);
        camDir.normalizeLocal();

        bcc.getViewDirection(viewDirection);
        float viewAzimuth = MyVector3f.azimuth(viewDirection);

        if (m_PlayerWeaponManager.isAiming) {
            // Never walk while you're aiming.
            bcc.setWalkDirection(Vector3f.ZERO);
            footstepsSFX.stop();

            float cameraAzimuth = MyVector3f.azimuth(camDir);
            if (m_PlayerWeaponManager.canShooting) {
                Vector3f weaponDir = m_PlayerWeaponManager.weaponDirection();
                float weaponAzimuth = MyVector3f.azimuth(weaponDir);
                float azimuthErr = cameraAzimuth - weaponAzimuth;
                azimuthErr = MyMath.standardizeAngle(azimuthErr);
                bccTurner.setNextError(azimuthErr);
                /*
                 * If the weapon's azimuth is nearly correct,
                 * then we adjust the weapon's elevation angle as well.
                 */
                if (FastMath.abs(azimuthErr) < 0.4f) {
                    float eaSetpoint = MyVector3f.altitude(camera.getDirection());
                    {
                        // Adjust the elevation angle (EA) by bending the spine.
                        float eaMeasured = MyVector3f.altitude(weaponDir);
                        float eaErr = eaSetpoint - eaMeasured;
                        Damper bender = m_PlayerWeaponManager.getSpineBender();
                        bender.setNextError(eaErr);
                    }
                }
            }

        } else { // not aiming a weapon
            camera.getLeft(camLeft);
            camLeft.setY(0f);
            camLeft.normalizeLocal();

            walkDirection.zero();

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

            if (!MyVector3f.isZero(walkDirection)) {
                walkDirection.normalizeLocal();
                float walkAzimuth = MyVector3f.azimuth(walkDirection);
                float azimuthErr = walkAzimuth - viewAzimuth;
                azimuthErr = MyMath.standardizeAngle(azimuthErr);
                bccTurner.setNextError(azimuthErr);
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

        // Update the avatar's view direction.
        float controlSignal = bccTurner.update();
        float x = FastMath.cos(controlSignal);
        float z = FastMath.sin(controlSignal);
        viewDirection.set(x, 0, z);
        bcc.setViewDirection(viewDirection);
    }

    private void setAnimTrigger(Animation3 newAnim) {
        if (checkTransition(newAnim, Archer.Running, Archer.Sprinting)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getCurrentAnimName();
        return (newAnim.equals(a) && b.getName().equals(curAnim))
                || (newAnim.equals(b) && a.getName().equals(curAnim));
    }

}
