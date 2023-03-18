package mygame.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.anim.HumanBodyBones;
import com.capdevon.anim.IKRig;
import com.capdevon.control.AdapterControl;
import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.capdevon.util.LineRenderer;
import com.jme3.anim.AnimComposer;
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
import mygame.prefabs.ArrowPrefab;
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
    private float currentLaunchForce;

    private Node ammoNode;

    // weapon hook
    private Node r_wh; // right hand
    private Node l_wh; // left hand
    // runtime weapon
    private Weapon currentWeapon;
    // current weapon index
    private int index = -1;
    // weapons list
    private List<Weapon> lstWeapons = new ArrayList<>();

    private static final String MIXAMO_PREFIX = "mixamorig:";
    private IKRig ikRig;
    private String ikSpine = MIXAMO_PREFIX + HumanBodyBones.Spine2;
    /**
     * control the side-to-side bending of the avatar's spine
     */
    final private Damper spineBender = new Damper(0.4f, -0.2f);
    private final Quaternion tempRotation = new Quaternion();
    private final float[] angles = new float[3];

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.lr         = getComponent(LineRenderer.class);
            this.bpCamera   = getComponent(BPCameraCollider.class);
            this.weaponUI   = getComponent(WeaponUIManager.class);
            this.ikRig      = getComponentInChildren(IKRig.class);
            this.animator   = getComponent(Animator.class);
            configureAnimClips(animator);
            animator.addListener(this);

            mainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);

            r_wh = createBoneHook(HumanBodyBones.RightHand);
            l_wh = createBoneHook(HumanBodyBones.LeftHand);

            switchWeapon();

            logger.log(Level.INFO, "Initialized");
        }
    }

    private Node createBoneHook(String jointName) {
        Node wh = new Node("Ref-" + jointName);
        animator.getAttachments(MIXAMO_PREFIX + jointName).attachChild(wh);
        return wh;
    }

    private void configureAnimClips(Animator animator) {
        animator.actionCycleDone(Archer.Idle);
        animator.actionCycleDone(Archer.Running);
        animator.actionCycleDone(Archer.Sprinting);
        animator.actionCycleDone(Archer.AimIdle);
        animator.actionCycleDone(Archer.AimOverdraw);
        animator.actionCycleDone(Archer.AimRecoil);
        animator.actionCycleDone(Archer.DrawArrow);
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateBoneIK();
        updateWeaponCharge(tpf);
        updateWeaponAiming(tpf);
    }

    private void updateBoneIK() {
        if (isAiming) {
            float spineBendAngle = spineBender.update();
            tempRotation.fromAngles(0f, 0f, spineBendAngle);
            ikRig.setAvatarIKRotation(ikSpine, tempRotation);
        }
    }

    private void updateWeaponCharge(float tpf) {
        if (isAiming && currentWeapon instanceof RangedWeapon) {
            RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
            currentLaunchForce = Math.min(currentLaunchForce + rWeapon.m_ChargeSpeed * tpf, rWeapon.m_MaxLaunchForce);
            //predictPOI(aimNode.getWorldTranslation(), camera.getDirection().mult(currentLaunchForce));
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
            currentLaunchForce = rWeapon.m_MinLaunchForce;
        }
    }

    /**
     * Estimates the elevation angle to hit a target at the specified offset
     * from the weapon.
     *
     * @param targetOffset the target offset (in world coordinates, not null,
     * unaffected)
     * @return the angle above the horizontal (in radians) or null if no
     * solution found
     */
    Float elevationAngle(Vector3f targetOffset) {
        Float elevationAngle = null;
        if (currentWeapon instanceof FireWeapon) {
            elevationAngle = MyVector3f.altitude(targetOffset);

        } else if (currentWeapon instanceof RangedWeapon) {
            double g = -PhysicsSpace.getPhysicsSpace().getGravity(null).y;
            double rxz = Math.hypot(targetOffset.x, targetOffset.z);
            double v0 = currentLaunchForce; // in world units per second
            assert v0 > 0.0 : "v0 = " + v0;
            double rxzOverV0 = rxz / v0;

            // Solve a quadratic equation for the tangent of the launch angle.
            double a = g * rxzOverV0 * rxzOverV0 / 2.0;
            assert a > 0.0 : "a = " + a;
            double b = -rxz;
            double c = targetOffset.y + a;
            double discriminant = b * b - 4.0 * a * c;
            if (discriminant >= 0.0) {
                /*
                 * To minimize flight time, select the more negative root,
                 * which yields the lower elevation angle.
                 */
                double tangent = (-b - Math.sqrt(discriminant)) / (2.0 * a);
                elevationAngle = (float) Math.atan(tangent);
            }
        }

        return elevationAngle;
    }

    /**
     * Accesses the control system for side-to-side bending of the avatar's
     * spine.
     *
     * @return the pre-existing instance (not null)
     */
    Damper getSpineBender() {
        return spineBender;
    }

    /**
     * Returns the location of the aiming point for ranged weapons. The aiming
     * point is always centered in the camera's viewport.
     * <p>
     * The crosshair color is updated to indicate the aiming status:
     * <ul>
     * <li> Red - weapon isn't ready to shoot</li>
     * <li> Yellow - weapon is ready, but doesn't see anything to shoot</li>
     * <li> Green - weapon is ready and sees something to shoot</li>
     * </ul>
     *
     * @return a new location vector (in world coordinates) or {@code null} if
     * the weapon isn't ready to shoot or doesn't see anything to shoot
     */
    Vector3f locateAimingPoint() {
        assert isAiming;

        ColorRGBA crosshairColor;
        Vector3f location;
        if (canShooting) { // weapon is ready
            location = bpCamera.locateAimingPoint();

            if (location != null) {
                float range = locateWeapon().distance(location);
                if (range < 0.8f) { // too close!
                    location = null;
                }
            }

            if (location == null) { // weapon doesn't see anything to shoot
                crosshairColor = ColorRGBA.Yellow;
            } else { // found a target
                crosshairColor = ColorRGBA.Green;
            }

        } else { // weapon isn't ready to shoot
            location = null;
            crosshairColor = ColorRGBA.Red;
        }
        currentWeapon.crosshair.setColor(crosshairColor);

        return location;
    }

    /**
     * Returns the location of the weapon.
     *
     * @return a new location vector in world coordinates
     */
    Vector3f locateWeapon() {
        Spatial cylinder = ammoNode.getChild(0);
        Vector3f location = cylinder.getWorldTranslation(); // alias

        return location.clone();
    }

    /**
     * Returns the direction in which the weapon is pointed.
     *
     * @return a new unit vector in world coordinates
     */
    Vector3f weaponDirection() {
        Spatial cylinder = ammoNode.getChild(0);
        Quaternion orientation = cylinder.getWorldRotation(); // alias
        Vector3f direction = orientation.mult(Vector3f.UNIT_Z, null);

        return direction;
    }

    public void setAiming(boolean enable) {
        this.isAiming = enable;
        float distance = (enable) ? bpCamera.getMinDistance() : bpCamera.getMaxDistance();
        bpCamera.setDistanceToTarget(distance);
        //bpCamera.setAvoidObstacles(!enable);
        bpCamera.setRotationSpeed(enable ? 0.5f : 1);
        currentWeapon.crosshair.setEnabled(enable);

        ikRig.setAvatarIKActive(ikSpine, enable);
        animator.setAnimation(Archer.DrawArrow);
    }

    public void shooting() {
        if (isAiming && canShooting) {
            if (currentWeapon instanceof FireWeapon) {
                FireWeapon fWeapon = (FireWeapon) currentWeapon;

                // Aim the ray from camera location in camera direction.
                Vector3f origin = camera.getLocation();
                Vector3f dir = camera.getDirection();
                fWeapon.handleShoot(origin, dir);

            } else if (currentWeapon instanceof RangedWeapon) {
                RangedWeapon rWeapon = (RangedWeapon) currentWeapon;

                rWeapon.shoot(ammoNode, currentLaunchForce);
                logger.log(Level.INFO, "currentLaunchForce: {0}", currentLaunchForce);
            }

            shootSFX.playInstance();
            animator.setAnimation(Archer.AimRecoil);
        }
    }

    @Override
    public void onAnimCycleDone(AnimComposer control, String animName, boolean loop) {
        if (animName.equals(Archer.AimRecoil.getName())) {
            animator.setAnimation(Archer.DrawArrow);

        } else if (animName.equals(Archer.DrawArrow.getName())) {
            animator.setAnimation(Archer.AimOverdraw);

        } else if (!loop) {
            control.removeCurrentAction();
        }
    }

    @Override
    public void onAnimChange(AnimComposer control, String animName) {
        showRightHandItem(false);

        if (animName.equals(Archer.AimRecoil.getName())) {
            setWeaponCharging();

        } else if (animName.equals(Archer.DrawArrow.getName())) {
            setWeaponCharging();
            showRightHandItem(true);

        } else if (animName.equals(Archer.AimOverdraw.getName())) {
            setWeaponReady();
            showRightHandItem(true);
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        // The crosshair color is updated in findTarget().
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

        String msg = String.format("%nModel: %s %nParent: %s %nPos: %s %nRot: %s %nScale: %s",
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
        this.ammoNode = null;

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

                this.ammoNode = (Node) assetManager.loadModel(ArrowPrefab.ASSET_PATH);
                Spatial bow = assetManager.loadModel("Models/Bow/bow.j3o");
                bindWeapon(r_wh, IKPositions.Arrow.getTransform(), ammoNode);
                bindWeapon(l_wh, IKPositions.Bow.getTransform(), bow);
                break;

            default:
                logger.log(Level.SEVERE, "unknown WeaponType: " + currentWeapon.weaponType);
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
