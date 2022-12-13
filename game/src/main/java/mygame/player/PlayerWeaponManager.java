package mygame.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.anim.HumanBodyBones;
import com.capdevon.control.AdapterControl;
import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.capdevon.util.LineRenderer;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Joint;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3utilities.math.MyVector3f;
import mygame.AnimDefs.Archer;
import mygame.camera.BPCameraCollider;
import mygame.camera.MainCamera;
import mygame.weapon.FireWeapon;
import mygame.weapon.RangedWeapon;
import mygame.weapon.Weapon;

/**
 * @author capdevon
 */
public class PlayerWeaponManager extends AdapterControl implements ActionAnimEventListener {

    private static final Logger logger = Logger.getLogger(PlayerWeaponManager.class.getName());

    AssetManager assetManager;
    Camera camera;
    AudioNode shootSFX;
    AudioNode reloadSFX;

    private WeaponUIManager weaponUI;
    private BPCameraCollider bpCamera;
    private Animator animator;

    private MainCamera mainCamera;
    private float nearClipPlane = 0.01f;
    private float farClipPlane = 100f;
    private float fov = 0;
    private float aimingSpeed = 5f;
    private float aimFOV = 45;
    private float defaultFOV = 60;
    
    boolean isAiming;
    boolean canShooting;
    private float m_CurrentLaunchForce;

    // weapon hook
    private Node r_wh; // right hand
    private Node l_wh; // left hand
    // runtime weapon
    private Weapon currentWeapon;
    // current weapon index
    private int index = -1;
    // weapons list
    private List<Weapon> lstWeapons = new ArrayList<>();

    private final String pfxMixamo = "mixamorig:";
    private Joint spineBone;
    private Quaternion tempRotation = new Quaternion();
    private float[] angles = new float[3];

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bpCamera = getComponent(BPCameraCollider.class);
            this.weaponUI = getComponent(WeaponUIManager.class);
            this.animator = getComponent(Animator.class);
            this.lr = getComponent(LineRenderer.class);

            mainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);

            r_wh = createHook(HumanBodyBones.RightHand);
            l_wh = createHook(HumanBodyBones.LeftHand);
            spineBone = setupBoneIK(HumanBodyBones.Spine1);

            configureAnimClips();
            switchWeapon();

            logger.log(Level.INFO, "Initialized");
        }
    }

    private void configureAnimClips() {
        animator.actionCycleDone(Archer.Idle);
        animator.actionCycleDone(Archer.Running);
        animator.actionCycleDone(Archer.Sprinting);
        animator.actionCycleDone(Archer.AimIdle);
        animator.actionCycleDone(Archer.AimOverdraw);
        animator.actionCycleDone(Archer.AimRecoil);
        animator.actionCycleDone(Archer.DrawArrow);
        animator.addListener(this);
    }

    private Node createHook(String jointName) {
        Node wh = new Node("Ref-" + jointName);
        animator.getAttachments(pfxMixamo + jointName).attachChild(wh);
        System.out.println("--Setup Hook: " + wh);
        return wh;
    }

    private Joint setupBoneIK(String jointName) {
    	Joint joint = animator.getJoint(pfxMixamo + jointName);
        System.out.println("--Setup BoneIK: " + joint.getId() + " " + joint.getName());
        return joint;
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateBoneIK(tpf);
        updateWeaponCharge(tpf);
        updateWeaponAiming(tpf);
    }

    private void updateBoneIK(float tpf) {
    	//TODO: To be converted into the new animation system (How ???).
//        if (isAiming) {
//            camera.getRotation().toAngles(angles);
//            float rx = FastMath.clamp(angles[0], -0.1f, 0.75f);
//            //System.out.println("updateBoneIK: " + angles[0] + " " + rx);
//            tempRotation.fromAngles(0, 0, -rx);
//            spineBone.setUserControl(true);
//            spineBone.setUserTransforms(Vector3f.ZERO, tempRotation, Vector3f.UNIT_XYZ);
//
//        } else {
//            spineBone.setUserControl(false);
//        }
    }

    private void updateWeaponCharge(float tpf) {
        if (isAiming && currentWeapon instanceof RangedWeapon) {
            RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
            m_CurrentLaunchForce = Math.min(m_CurrentLaunchForce + rWeapon.m_ChargeSpeed * tpf, rWeapon.m_MaxLaunchForce);
            //predictPOI(aimNode.getWorldTranslation(), camera.getDirection().mult(m_CurrentLaunchForce));
        }
    }

    private void updateWeaponAiming(float tpf) {
        if (isAiming) {
            fov += tpf * aimingSpeed;
        } else {
            fov -= tpf * aimingSpeed;
        }

        fov = FastMath.clamp(fov, 0, 1);
        mainCamera.setFieldOfView(FastMath.interpolateLinear(fov, defaultFOV, aimFOV));
    }

    private void resetWeaponCharge() {
        if (currentWeapon instanceof RangedWeapon) {
            RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
            m_CurrentLaunchForce = rWeapon.m_MinLaunchForce;
        }
    }

    public void setAiming(boolean isAiming) {
        this.isAiming = isAiming;
        float distance = isAiming ? bpCamera.getMinDistance() : bpCamera.getMaxDistance();
        bpCamera.setDistanceToTarget(distance);
        //bpCamera.setAvoidObstacles(!isAiming);
        bpCamera.setRotationSpeed(isAiming ? 0.5f : 1);
        currentWeapon.crosshair.setEnabled(isAiming);
        animator.setAnimation(Archer.DrawArrow);
    }

    public void shooting() {
        if (isAiming && canShooting) {

            // Aim the ray from character location in camera direction.
            Vector3f origin = camera.getLocation(); //aimNode.getWorldTranslation();
            Vector3f dir = camera.getDirection();

            if (currentWeapon instanceof FireWeapon) {
                FireWeapon fWeapon = (FireWeapon) currentWeapon;
                fWeapon.handleShoot(origin, dir);

            } else if (currentWeapon instanceof RangedWeapon) {
                RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
                rWeapon.handleShoot(origin, dir, m_CurrentLaunchForce);
                logger.log(Level.INFO, "m_CurrentLaunchForce: {0}", m_CurrentLaunchForce);
            }

            shootSFX.playInstance();
            animator.setAnimation(Archer.AimRecoil);
        }
    }

    @Override
    public void onAnimCycleDone(AnimComposer control, String animName, boolean loop) {
        if (animName.equals(Archer.AimRecoil.name)) {
            animator.setAnimation(Archer.DrawArrow);

        } else if (animName.equals(Archer.DrawArrow.name)) {
            animator.setAnimation(Archer.AimOverdraw);
            
        } else if (!loop) {
        	control.removeCurrentAction();
        }
    }

    @Override
    public void onAnimChange(AnimComposer control, String animName) {
        showRightHandItem(false);

        if (animName.equals(Archer.AimRecoil.name)) {
            setWeaponCharging();

        } else if (animName.equals(Archer.DrawArrow.name)) {
            setWeaponCharging();
            showRightHandItem(true);

        } else if (animName.equals(Archer.AimOverdraw.name)) {
            setWeaponReady();
            showRightHandItem(true);
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        currentWeapon.crosshair.setColor(ColorRGBA.Green);
        reloadSFX.play();
    }

    private void setWeaponCharging() {
        canShooting = false;
        currentWeapon.crosshair.setColor(ColorRGBA.Red);
        reloadSFX.stop();
        resetWeaponCharge();
    }

    private void bindWeapon(Node wh, Transform ik, Spatial model) {
        wh.detachAllChildren();
        wh.setLocalTransform(ik);

        if (model != null)
            wh.attachChild(model);

        String msg = String.format("\n\nModel: %s \nParent: %s \nPos: %s \nRot: %s \nScale: %s",
            model, wh.getName(),
            wh.getLocalTranslation().toString(),
            wh.getLocalRotation().toString(),
            wh.getLocalScale().toString());

        logger.log(Level.INFO, msg);
    }

    public boolean addWeapon(Weapon w) {
        return lstWeapons.add(w);
    }

    public boolean removeWeapon(Weapon w) {
        return lstWeapons.remove(w);
    }

    public Weapon getActiveWeapon() {
        return currentWeapon;
    }

    private void showRightHandItem(boolean show) {
        r_wh.setCullHint(show ? Spatial.CullHint.Never : Spatial.CullHint.Always);
    }

    private void showLeftHandItem(boolean show) {
        l_wh.setCullHint(show ? Spatial.CullHint.Never : Spatial.CullHint.Always);
    }

    public void switchWeaponBullet() {
        currentWeapon.switchBullet();
        onWeaponChanged();
    }

    public void switchWeapon() {
        if (lstWeapons.isEmpty()) {
            return;
        }

        int newIndex = (index + 1) % lstWeapons.size();
        if (newIndex == index) {
            return;
        }

        index = newIndex;
        currentWeapon = lstWeapons.get(index);
        Node spWeapon;

        switch (currentWeapon.weaponType) {
            case Normal:
                //spWeapon = currentWeapon.getSpatial();
                bindWeapon(r_wh, currentWeapon.ik[0], null);
                bindWeapon(l_wh, Transform.IDENTITY, null);
                break;

            case Bow:
                //spWeapon       = currentWeapon.getSpatial();
                //Spatial arrow  = spWeapon.getChild("Arrow");
                //Spatial bow    = spWeapon.getChild("Bow");
                //bindWeapon(r_wh, currentWeapon.ik[0], arrow);
                //bindWeapon(l_wh, currentWeapon.ik[1], bow);

                Spatial arrow = assetManager.loadModel("Models/Arrow/arrow.j3o");
                Spatial bow = assetManager.loadModel("Models/Bow/bow.j3o");
                bindWeapon(r_wh, IKPositions.Arrow.getTransform(), arrow);
                bindWeapon(l_wh, IKPositions.Bow.getTransform(), bow);
                break;

            default:
                logger.log(Level.SEVERE, "WeaponType unknow");
                break;
        }

        onWeaponChanged();
    }

    private void onWeaponChanged() {
        weaponUI.changeWeapon(currentWeapon);
    }

    private RaycastHit hitInfo = new RaycastHit();
    private LineRenderer lr;
    private List<Vector3f> points = new LinkedList<>();
    private boolean drawPoints = true;

    /**
     * point-of-impact prediction.
     * 
     * @param launchLocation
     * @param launchVelocity
     */
    private void predictPOI(Vector3f launchLocation, Vector3f launchVelocity) {
        Vector3f gravity = PhysicsSpace.getPhysicsSpace().getGravity(null);
        Vector3f velocity = launchVelocity.clone();
        Vector3f location = launchLocation.clone();
        Vector3f previousLocation = new Vector3f();

        float timeStep = 0.02f; // seconds per step
        //logger.log(Level.INFO, "Start simulation. Position=" + location + ", Velocity=" + launchVelocity + ", timeStep=" + timeStep); 

        points.clear();

        // Predict impact for next 2 seconds.
        for (int stepIndex = 0; stepIndex < 100; ++stepIndex) {

            previousLocation.set(location);
            MyVector3f.accumulateScaled(location, velocity, timeStep);
            MyVector3f.accumulateScaled(velocity, gravity, timeStep);

            points.add(location.clone());

            if (Physics.linecast(previousLocation, location, hitInfo)) {
                //logger.log(Level.INFO, hitInfo.toString());
                //logger.log(Level.INFO, "Stop simulation. stepIndex=" + stepIndex);
                break;
            }
        }

        if (drawPoints) {
            lr.setPoints(points);
            lr.updateGeometry();
        }

        //logger.log(Level.INFO, "End simulation.");
    }

}
