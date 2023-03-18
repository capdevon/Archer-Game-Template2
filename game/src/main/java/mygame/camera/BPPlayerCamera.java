package mygame.camera;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.input.CameraInput;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.CameraControl;
import jme3utilities.math.MyVector3f;
import mygame.Main;

/**
 *
 * @author capdevon
 */
public class BPPlayerCamera extends AbstractControl implements AnalogListener {

    private static final Logger logger = Logger.getLogger(BPPlayerCamera.class.getName());

    protected Camera camera;
    protected InputManager inputManager;
    protected Node yawNode;
    protected Node pitchNode;
    protected CameraNode camNode;

    protected float xOffset = 0f;
    protected float yHeight = 1.5f;
    protected float minDistance = 1f;
    protected float maxDistance = 10f;
    protected float rotationSpeed = 1f;
    protected float zoomSensitivity = 12f;
    protected float minVerticalRotation = 0f;
    protected float maxVerticalRotation = FastMath.PI / 2;
    protected boolean invertYaxis = false;

    private boolean canRotate = true;
    private float horizontalRotation = 0f;
    private float verticalRotation = 0f;
    private final Quaternion qHRotation = new Quaternion();
    private final Quaternion qVRotation = new Quaternion();
    private Vector3f upVector;
    private Vector3f leftVector;

    protected float targetDistance;
    private final Vector3f camOffset = new Vector3f(0, 0, 1);

    /**
     *
     * @param camera
     * @param inputManager
     */
    public BPPlayerCamera(Camera camera, InputManager inputManager) {
        this.camera = camera;
        this.upVector = camera.getUp().clone();
        this.leftVector = camera.getLeft().clone();
        registerWithInput(inputManager);
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            initCamera();
            logger.log(Level.INFO, "Initialized");
        }
    }

    private void initCamera() {
        yawNode = new Node("Yaw");
        pitchNode = new Node("Pitch");
        camNode = new CameraNode("MainCamera", camera);

        yawNode.attachChild(pitchNode);
        pitchNode.attachChild(camNode);

        targetDistance = maxDistance;

        yawNode.setLocalTranslation(spatial.getWorldTranslation());
        pitchNode.setLocalTranslation(xOffset, yHeight, 0);
        camNode.setLocalTranslation(0, 0, -targetDistance);

        camNode.lookAt(pitchNode.getWorldTranslation(), upVector);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        rotateCamera();
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!canRotate || !enabled) {
            return;
        }

        if (name.equals(CameraInput.CHASECAM_MOVELEFT)) {
            horizontalRotation -= value * rotationSpeed;
            rotateCamera();

        } else if (name.equals(CameraInput.CHASECAM_MOVERIGHT)) {
            horizontalRotation += value * rotationSpeed;
            rotateCamera();

        } else if (name.equals(CameraInput.CHASECAM_UP)) {
            verticalRotation += value * rotationSpeed;
            rotateCamera();

        } else if (name.equals(CameraInput.CHASECAM_DOWN)) {
            verticalRotation -= value * rotationSpeed;
            rotateCamera();
        }
    }

    /**
     * rotate the camera around the target
     */
    protected void rotateCamera() {
        //rotate the camera around the target on the vertical plane
        verticalRotation = FastMath.clamp(verticalRotation, minVerticalRotation, maxVerticalRotation);
        qVRotation.fromAngleNormalAxis(verticalRotation, leftVector);
        pitchNode.setLocalRotation(qVRotation);

        //rotate the camera around the target on the horizontal plane
        qHRotation.fromAngleNormalAxis(horizontalRotation, upVector);
        yawNode.setLocalRotation(qHRotation);
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateCamera(tpf);
    }

    /**
     * @param tpf
     */
    protected void updateCamera(float tpf) {
        // handle translations
        yawNode.setLocalTranslation(spatial.getWorldTranslation());

        // handle zooming
        if (camNode.getLocalTranslation().z != targetDistance) {
            camOffset.z = -targetDistance;
            camNode.getLocalTranslation().interpolateLocal(camOffset, tpf * zoomSensitivity);
        }

        yawNode.updateLogicalState(tpf);
        yawNode.updateGeometricState();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * Registers inputs with the input manager
     *
     * @param inputManager
     */
    protected void registerWithInput(InputManager inputManager) {

        this.inputManager = inputManager;
        inputManager.setCursorVisible(false);
        initVerticalAxisInputs();
        initHorizontalAxisInput();
        mapJoystick();
    }

    private void initVerticalAxisInputs() {
        if (!invertYaxis) {
            addMapping(CameraInput.CHASECAM_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
            addMapping(CameraInput.CHASECAM_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        } else {
            addMapping(CameraInput.CHASECAM_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
            addMapping(CameraInput.CHASECAM_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        }
    }

    private void initHorizontalAxisInput() {
        addMapping(CameraInput.CHASECAM_MOVELEFT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        addMapping(CameraInput.CHASECAM_MOVERIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
    }

    private void addMapping(String bindingName, Trigger... triggers) {
        inputManager.addMapping(bindingName, triggers);
        inputManager.addListener(this, bindingName);
    }

    protected void mapJoystick() {
        Joystick[] joysticks = inputManager.getJoysticks();

        if (joysticks == null || joysticks.length == 0) {
            logger.log(Level.INFO, "Joystick not found");
            return;
        }

        for (Joystick joypad : joysticks) {
            logger.log(Level.INFO, "Joystick:{0}, id:{1}", new Object[]{joypad.getName(), joypad.getJoyId()});

            // Use right stick to control the camera
            JoystickAxis zRotation = joypad.getAxis(JoystickAxis.Z_ROTATION);
            JoystickAxis zAxis = joypad.getAxis(JoystickAxis.Z_AXIS);

            if (zRotation != null && zAxis != null) {
                zAxis.assignAxis(CameraInput.CHASECAM_MOVELEFT, CameraInput.CHASECAM_MOVERIGHT);
                zRotation.assignAxis(CameraInput.CHASECAM_UP, CameraInput.CHASECAM_DOWN);
            }
        }
    }

    /**
     * Cleans up the input mappings from the input manager. Undoes the work of
     * registerWithInput().
     *
     * @param inputManager the InputManager to clean up
     */
    public void cleanupWithInput(InputManager inputManager) {
        inputManager.deleteMapping(CameraInput.CHASECAM_DOWN);
        inputManager.deleteMapping(CameraInput.CHASECAM_UP);
        inputManager.deleteMapping(CameraInput.CHASECAM_MOVELEFT);
        inputManager.deleteMapping(CameraInput.CHASECAM_MOVERIGHT);
        inputManager.removeListener(this);
    }

    /**
     * invert the vertical axis movement of the mouse
     *
     * @param invertYaxis
     */
    public void setInvertVerticalAxis(boolean invertYaxis) {
        this.invertYaxis = invertYaxis;
        if (inputManager != null) {
            inputManager.deleteMapping(CameraInput.CHASECAM_DOWN);
            inputManager.deleteMapping(CameraInput.CHASECAM_UP);
            initVerticalAxisInputs();
        }
    }

    /**
     * Returns the min zoom distance of the camera (default is 1)
     * @return minDistance
     */
    public float getMinDistance() {
        return minDistance;
    }

    /**
     * Sets the min zoom distance of the camera (default is 1)
     * @param minDistance
     */
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
//        if (minDistance > targetDistance) {
//            zoomCamera(targetDistance - minDistance);
//        }
    }

    /**
     * Returns the max zoom distance of the camera (default is 40)
     * @return maxDistance
     */
    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * Sets the max zoom distance of the camera (default is 40)
     * @param maxDistance
     */
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
//        if (maxDistance < targetDistance) {
//            zoomCamera(maxDistance - targetDistance);
//        }
    }

    /**
     * return the current distance from the camera to the target
     * @return the distance
     */
    public float getDistanceToTarget() {
        return targetDistance;
    }

    public void setDistanceToTarget(float targetDistance) {
        this.targetDistance = targetDistance;
    }

    /**
     * The maximal vertical rotation angle in radian of the camera around the target.
     * @return maxVerticalRotation
     */
    public float getMaxVerticalRotation() {
        return maxVerticalRotation;
    }

    /**
     * Sets the maximal vertical rotation angle in radian of the camera around the target. Default is Pi/2;
     * @param maxVerticalRotation
     */
    public void setMaxVerticalRotation(float maxVerticalRotation) {
        this.maxVerticalRotation = maxVerticalRotation;
    }

    /**
     * The minimal vertical rotation angle in radian of the camera around the target.
     * @return minVerticalRotation
     */
    public float getMinVerticalRotation() {
        return minVerticalRotation;
    }

    /**
     * Sets the minimal vertical rotation angle in radian of the camera around the target default is 0;
     * @param minHeight
     */
    public void setMinVerticalRotation(float minHeight) {
        this.minVerticalRotation = minHeight;
    }

    /**
     * Returns the rotation speed when the mouse is moved.
     * @return rotationSpeed
     */
    public float getRotationSpeed() {
        return rotationSpeed;
    }

    /**
     * Sets the rotate amount when user moves his mouse, the lower the value,
     * the slower the camera will rotate. default is 1.
     *
     * @param rotationSpeed Rotation speed on mouse movement, default is 1.
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getXOffset() {
        return xOffset;
    }

    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getYHeight() {
        return yHeight;
    }

    public void setYHeight(float yHeight) {
        this.yHeight = yHeight;
    }

    public float getZoomSensitivity() {
        return zoomSensitivity;
    }

    public void setZoomSensitivity(float zoomSensitivity) {
        this.zoomSensitivity = zoomSensitivity;
    }

    /**
     * Returns the location of the aiming point for ranged weapons. The aiming
     * point is always centered in the camera's viewport.
     *
     * @return a new location vector (in world coordinates) or {@code null} if
     * the raycast doesn't find anything to shoot
     */
    public Vector3f locateAimingPoint() {
        /*
         * Cast a ray from the center of the near clipping plane
         * to the center of the far clipping plane.
         */
        Vector2f viewportCenter = new Vector2f(
                camera.getWidth() / 2f, camera.getHeight() / 2f);
        Vector3f rayBegin = camera.getWorldCoordinates(viewportCenter, 0f);
        Vector3f rayEnd = camera.getWorldCoordinates(viewportCenter, 1f);
        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        List<PhysicsRayTestResult> results
                = physicsSpace.rayTestRaw(rayBegin, rayEnd);

        // Find the closest result in the default collision group.
        float minFraction = 9f;
        for (PhysicsRayTestResult result : results) {
            PhysicsCollisionObject pco = result.getCollisionObject();
            if (pco.getCollisionGroup() == Main.DEFAULT_GROUP) {
                float hitFraction = result.getHitFraction();
                if (hitFraction < minFraction) {
                    minFraction = hitFraction;
                }
            }
        }

        Vector3f result;
        if (minFraction > 1f) { // no results in the default group
            result = null;
        } else {
            result = MyVector3f.lerp(minFraction, rayBegin, rayEnd, null);
        }
        return result;
    }
}
