package mygame.devtool;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.capdevon.anim.AvatarMask;
import com.capdevon.engine.GameObject;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.props.PropertyPanel;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author capdevon
 */
public class TestAnimMask extends SimpleApplication implements ActionListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        TestAnimMask app = new TestAnimMask();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    private String ARCHER = "Models/Erika/Erika.j3o";

    private AnimComposer animComposer;
    private SkinningControl skControl;
    private final Queue<String> animsQueue = new LinkedList<>();
    private JointWidget widget;
    private BitmapText animUI;
    private ArmatureDebugAppState armatureDebug;

    @Override
    public void simpleInitApp() {

        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));

        armatureDebug = new ArmatureDebugAppState();
        stateManager.attach(armatureDebug);

        int xPos = 20;
        int yPos = 15;
        int yOffset = 0;
        animUI = getTextUI(ColorRGBA.Blue, xPos, ++yOffset * yPos);

        setupScene();
        setupCharacter();
        setupCamera();
        setupLights();
        setupKeys();
        initLemur();
    }

    private void setupLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.2f, -1, -0.3f).normalizeLocal());
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
        rootNode.addLight(ambient);

        PointLight point = new PointLight();
        point.setRadius(10f);
        rootNode.addLight(point);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 2);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.4f);
        dlsf.setShadowZExtend(256);
        fpp.addFilter(dlsf);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

    private void setupScene() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.DarkGray);

        Geometry floor = new Geometry("Stage", new Quad(10, 10));
        floor.rotate(-FastMath.HALF_PI, 0, 0);
        floor.center();
        floor.setMaterial(mat);
        rootNode.attachChild(floor);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    private void setupCamera() {
        // disable the default 1st-person flyCam!
        flyCam.setEnabled(false);

        Node target = new Node("CamTarget");
        target.move(0, 1, 0);

        ChaseCameraAppState chaseCam = new ChaseCameraAppState();
        chaseCam.setTarget(target);
        stateManager.attach(chaseCam);
        chaseCam.setInvertHorizontalAxis(true);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setMinDistance(2);
        chaseCam.setMaxDistance(6);
        chaseCam.setZoomSpeed(0.5f);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setRotationSpeed(3);
        chaseCam.setDefaultDistance(3);
        chaseCam.setDefaultVerticalRotation(0.3f);
    }

    private void setupCharacter() {
        Node myModel = (Node) assetManager.loadModel(ARCHER);
        rootNode.attachChild(myModel);

        animComposer = GameObject.getComponentInChildren(myModel, AnimComposer.class);
        animsQueue.addAll(Arrays.asList("StandingAimIdle", "StandingDrawArrow", "StandingAimOverdraw", "StandingAimRecoil", "StandingAimWalkForward"));

        skControl = GameObject.getComponentInChildren(myModel, SkinningControl.class);
        armatureDebug.addArmatureFrom(skControl);

        Armature armature = skControl.getArmature();

        // Override the default layer mask.
        AvatarMask avatarMask = new AvatarMask(armature).addAllJoints();
        animComposer.makeLayer(AnimComposer.DEFAULT_LAYER, avatarMask);

        Joint spine = armature.getJoint("mixamorig:Spine");
        widget = new JointWidget(avatarMask, spine);

        Node axes = createTransformWidget();
        axes.addControl(widget);
        skControl.getAttachmentsNode(spine.getName()).attachChild(axes);
    }

    private Node createTransformWidget() {
        Node node = new Node("Axes");
        node.attachChild(createArrow("X", Vector3f.UNIT_X, ColorRGBA.Red));
        node.attachChild(createArrow("Y", Vector3f.UNIT_Y, ColorRGBA.Green));
        node.attachChild(createArrow("Z", Vector3f.UNIT_Z, ColorRGBA.Blue));
        node.setShadowMode(ShadowMode.Off);
        node.setQueueBucket(Bucket.Transparent);
        node.setCullHint(CullHint.Never);
        node.scale(0.5f);
        return node;
    }

    private Geometry createArrow(String name, Vector3f dir, ColorRGBA color) {
        Arrow arrow = new Arrow(dir);
        Geometry geo = new Geometry(name, arrow);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setDepthTest(false);
        geo.setMaterial(mat);
        return geo;
    }

    private void initLemur() {
        // init Lemur
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        Container window = new Container();
        window.addChild(new Label("Joint Widget", new ElementId("title")));

        PropertyPanel panel = new PropertyPanel("glass");
        panel.addFloatProperty("RX", widget, "x", -180, 180, 0.05f);
        panel.addFloatProperty("RY", widget, "y", -180, 180, 0.05f);
        panel.addFloatProperty("RZ", widget, "z", -180, 180, 0.05f);
        panel.addFloatProperty("Weight", widget, "weight", 0, 1, 0.05f);
        panel.addBooleanProperty("User Control", widget, "userControl");
        window.addChild(panel);

        window.setLocalTranslation(10, cam.getHeight() - 100f, 0);
        guiNode.attachChild(window);

        nextAnim();
    }

    @Override
    public void simpleUpdate(float tpf) {
        // do something...
    }

    private void setupKeys() {
        addMapping("toggleArmature", new KeyTrigger(KeyInput.KEY_0));
        addMapping("nextAnim", new KeyTrigger(KeyInput.KEY_RIGHT));
        addMapping("stopAnim", new KeyTrigger(KeyInput.KEY_P));
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            return;
        }

        if (name.equals("nextAnim")) {
            nextAnim();

        } else if (name.equals("stopAnim")) {
            // Reset all layers to t=0 with no current action.
            animComposer.reset();
            skControl.getArmature().applyBindPose();

        } else if (name.equals("toggleArmature")) {
            armatureDebug.setEnabled(!armatureDebug.isEnabled());
        }
    }

    private void nextAnim() {
        String currAnimName = animsQueue.poll();
        animsQueue.add(currAnimName);
        animUI.setText("Anim: " + currAnimName);

        // Run an action on the default layer.
        animComposer.setCurrentAction(currAnimName);
    }

    private BitmapText getTextUI(ColorRGBA color, float xPos, float yPos) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setSize(font.getCharSet().getRenderedSize());
        bmp.setLocalTranslation(xPos, settings.getHeight() - yPos, 0);
        bmp.setColor(color);
        guiNode.attachChild(bmp);
        return bmp;
    }

    protected class JointWidget extends AbstractControl {

        private final AvatarMask mask;
        private final Joint joint;
        private final Quaternion targetRotation = new Quaternion();
        private float x, y, z;
        private boolean userControl;
        private float weight = 1f;

        public JointWidget(AvatarMask mask, Joint joint) {
            this.mask = mask;
            this.joint = joint;
            targetRotation.set(joint.getInitialTransform().getRotation());
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
            updateJointRotation();
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
            updateJointRotation();
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
            updateJointRotation();
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public boolean isUserControl() {
            return userControl;
        }

        /**
         * If enabled, user can control joint transform. Animation transforms
         * are not applied to this bone when enabled.
         *
         * @param enable true for direct control, false for canned animations
         */
        public void setUserControl(boolean enable) {
            this.userControl = enable;
            if (userControl) {
                mask.removeJoints(joint.getName());
            } else {
                mask.addJoints(joint.getName());
            }
        }

        private void updateJointRotation() {
            float qx = FastMath.DEG_TO_RAD * x;
            float qy = FastMath.DEG_TO_RAD * y;
            float qz = FastMath.DEG_TO_RAD * z;
            targetRotation.fromAngles(qx, qy, qz);
        }

        @Override
        protected void controlUpdate(float tpf) {
            if (userControl) {
                Transform t = joint.getInitialTransform();
                joint.getLocalRotation().slerp(t.getRotation(), targetRotation, weight);
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

    }

}
