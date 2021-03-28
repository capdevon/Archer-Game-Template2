package mygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animator;
import com.capdevon.anim.HumanBodyBones;
import com.capdevon.anim.TrackUtils;
import com.capdevon.control.AdapterControl;
import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.capdevon.util.LineRenderer;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
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

import mygame.camera.BPCameraCollider;
import mygame.camera.MainCamera;
import mygame.weapon.FireWeapon;
import mygame.weapon.RangedWeapon;
import mygame.weapon.Weapon;

/**
 * @author capdevon
 */
public class PlayerWeaponManager extends AdapterControl implements AnimEventListener {

    private static final Logger logger = Logger.getLogger(PlayerWeaponManager.class.getName());

    AssetManager assetManager;
    Camera camera;
    AudioNode shootSFX;
    AudioNode reloadSFX;

    private BPCameraCollider bpCamera;
    private Animator animator;
    private WeaponUIManager weaponUI;

    private MainCamera _MainCamera;
    public float nearClipPlane = 0.01f;
    public float farClipPlane = 100f;
    private float fov = 0;
    private float aimingSpeed = 5f;
    private float aimFOV = 45;
    private float defaultFOV = 60;
    boolean isAiming, canShooting;
    float m_CurrentLaunchForce;

    // weapon hook
    private Node r_wh; // right hand
    private Node l_wh; // left hand
    private Node s_wh; // spine
    // runtime weapon
    private Weapon currentWeapon;
    // current weapon index
    private int index = -1;
    // weapons list
    private List<Weapon> lstWeapons = new ArrayList<>();

    private final String pfxMixamo = "Armature_mixamorig:";
    private Bone spineBone;
    private Quaternion tempRotation = new Quaternion();
    private float[] angles = new float[3];

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            awake();
            logger.log(Level.INFO, "Initialized");
        }
    }

    private void awake() {
        this.bpCamera = getComponent(BPCameraCollider.class);
        this.weaponUI = getComponent(WeaponUIManager.class);
        this.animator = getComponent(Animator.class);
        this.lr = getComponent(LineRenderer.class);

        _MainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);

        r_wh = createHook(HumanBodyBones.RightHand);
        l_wh = createHook(HumanBodyBones.LeftHand);
        s_wh = createHook(HumanBodyBones.Spine2);

        animator.addAnimListener(this);
        TrackUtils.addAudioTrack(spatial, reloadSFX, AnimDefs.Aim_Overdraw.getName());

        switchWeapon();
        setupBoneIK();

        logger.log(Level.INFO, "Initialized");
    }

    private Node createHook(String boneName) {
        Node wh = new Node();
        wh.setName("Ref-" + boneName);
        animator.getAttachments(pfxMixamo + boneName).attachChild(wh);
        System.out.println("--Setup Hook: " + wh);
        return wh;
    }

    private void setupBoneIK() {
        spineBone = animator.getBone(pfxMixamo + HumanBodyBones.Spine1);
        System.out.println("--Setup BoneIK: " + spineBone.getName());
    }

    @Override
    protected void controlUpdate(float tpf) {
        // TODO Auto-generated method stub
        updateBoneIK(tpf);
        updateWeaponCharge(tpf);
        updateWeaponAiming(tpf);
    }

    private void updateBoneIK(float tpf) {
        if (isAiming) {
            camera.getRotation().toAngles(angles);
            float rx = FastMath.clamp(angles[0], -0.1f, 0.75f);
            //System.out.println("updateBoneIK: " + angles[0] + " " + rx);
            tempRotation.fromAngles(0, 0, -rx);
            spineBone.setUserControl(true);
            spineBone.setUserTransforms(Vector3f.ZERO, tempRotation, Vector3f.UNIT_XYZ);

        } else {
            spineBone.setUserControl(false);
        }
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
        _MainCamera.setFieldOfView(FastMath.interpolateLinear(fov, defaultFOV, aimFOV));
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
        bpCamera.setDistanceToTarget(-distance);
        bpCamera.setAvoidObstacles(!isAiming);
        bpCamera.setRotationSpeed(isAiming ? 0.5f : 1);
        currentWeapon.crosshair.setEnabled(isAiming);
        animator.setAnimation(AnimDefs.Draw_Arrow);
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
                logger.log(Level.INFO, "m_CurrentLaunchForce: " + m_CurrentLaunchForce);
            }

            shootSFX.playInstance();
            animator.setAnimation(AnimDefs.Aim_Recoil);
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //To change body of generated methods, choose Tools | Templates.
        if (animName.equals(AnimDefs.Aim_Recoil.name)) {
            animator.setAnimation(AnimDefs.Draw_Arrow);

        } else if (animName.equals(AnimDefs.Draw_Arrow.name)) {
            animator.setAnimation(AnimDefs.Aim_Overdraw);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        // To change body of generated methods, choose Tools | Templates.
        showRightHandItem(false);

        if (animName.equals(AnimDefs.Aim_Recoil.name)) {
            setWeaponCharging();

        } else if (animName.equals(AnimDefs.Draw_Arrow.name)) {
            setWeaponCharging();
            showRightHandItem(true);

        } else if (animName.equals(AnimDefs.Aim_Overdraw.name)) {
            setWeaponReady();
            showRightHandItem(true);
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        currentWeapon.crosshair.setColor(ColorRGBA.White);
        //reloadSFX.play();
    }

    private void setWeaponCharging() {
        canShooting = false;
        currentWeapon.crosshair.setColor(ColorRGBA.Red);
        //reloadSFX.stop();
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
        onChangeWeapon();
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
                //spWeapon = (Node) assetManager.loadModel(activeWeapon.fileModel);
                bindWeapon(r_wh, currentWeapon.ik[0], null);
                bindWeapon(l_wh, Transform.IDENTITY, null);
                break;

            case Bow:
                //spWeapon 		 = (Node) assetManager.loadModel(currentWeapon.fileModel);
                //Spatial arrow  = spWeapon.getChild("10490_arrow_v1");
                //Spatial bow 	 = spWeapon.getChild("10490_bow_v1");
                //Spatial quiver = spWeapon.getChild("10490_quiver_v1");
                //bindWeapon(r_wh, currentWeapon.ik[0], arrow);
                //bindWeapon(l_wh, currentWeapon.ik[1], bow);
                //bindWeapon(s_wh, currentWeapon.ik[2], quiver);

                //spWeapon = currentWeapon.model;
                Spatial arrow = assetManager.loadModel("Models/Arrow/arrow.glb");
                Spatial bow = assetManager.loadModel("Models/Bow/bow.gltf");
                bindWeapon(r_wh, IKPositions.ARCHER[0], arrow);
                bindWeapon(l_wh, IKPositions.ARCHER[1], bow);
                break;

            default:
                logger.log(Level.SEVERE, "WeaponType unknow");
                break;
        }

        onChangeWeapon();
    }

    private void onChangeWeapon() {
        weaponUI.changeWeapon(currentWeapon);
    }

    RaycastHit hitInfo = new RaycastHit();
    LineRenderer lr;
    List<Vector3f> points = new LinkedList<>();
    boolean drawPoints = true;

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
            accumulateScaled(location, velocity, timeStep);
            accumulateScaled(velocity, gravity, timeStep);

            points.add(location.clone());

            if (Physics.doLinecast(previousLocation, location, hitInfo)) {
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

    /**
     * Accumulate a linear combination of vectors.
     *
     * @param total sum of the scaled inputs so far (not null, modified)
     * @param input the vector to scale and add (not null, unaffected)
     * @param scale scale factor to apply to the input
     */
    private void accumulateScaled(Vector3f total, Vector3f input, float scale) {
        total.x += input.x * scale;
        total.y += input.y * scale;
        total.z += input.z * scale;
    }

}
