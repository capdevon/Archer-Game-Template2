package mygame.devtool;

import java.util.LinkedList;
import java.util.Queue;

import com.capdevon.anim.HumanBodyBones;
import com.capdevon.engine.GameObject;
import com.jme3.anim.AnimComposer;
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
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import com.jme3.scene.shape.Line;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

import mygame.player.IKPositions;

/**
 * @author capdevon
 */
public class TestModel extends SimpleApplication implements ActionListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        TestModel app = new TestModel();
        AppSettings settings = new AppSettings(true);
        //settings.setResolution(1024, 768);
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.start();
    }

    private final String ARCHER = "Models/Erika/Erika.j3o";
    private Node myModel;
    private SkinningControl skinningControl;
    private AnimComposer animComposer;
    private final Queue<String> animsQueue = new LinkedList<>();
    private BitmapText animUI;
    private ArmatureDebugAppState armatureDebug;

    @Override
    public void simpleInitApp() {

//        String fileName = String.format("image_%d_", System.currentTimeMillis() / 1000);
//        stateManager.attach(new ScreenshotAppState("", fileName, 0));
        armatureDebug = new ArmatureDebugAppState();
        stateManager.attach(armatureDebug);

        int xPos = 20;
        int yPos = 15;
        int yOffset = 0;
        animUI = getTextUI(ColorRGBA.Blue, xPos, ++yOffset * yPos);

        configCamera();
        setupSky();
        setupLights();
        createGrid();
        setupCharacter();
        setupCamera();
        setupKeys();
    }

    private void configCamera() {
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.1f, 1000f);
    }

    /**
     * a sky as background
     */
    private void setupSky() {
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupLights() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.2f, -1, -0.3f).normalizeLocal());
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
        rootNode.addLight(ambient);

        // add a PBR probe.
        Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
        lightProbe.getArea().setRadius(100);
        rootNode.addLight(lightProbe);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.4f);
        dlsf.setShadowZExtend(256);
        fpp.addFilter(dlsf);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

    private void createGrid() {
        Geometry grid = new Geometry("DebugGrid", new Grid(21, 21, 1));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        grid.setMaterial(mat);
        grid.center().move(0, 0, 0);
        grid.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(grid);

        createLine("AX", new Vector3f(10, 0, 0), ColorRGBA.Red);
        createLine("AZ", new Vector3f(0, 0, 10), ColorRGBA.Blue);
    }

    private void createLine(String name, Vector3f dir, ColorRGBA color) {
        Geometry geom = new Geometry(name, new Line(dir.negate(), dir));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(2f);
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(geom);
    }

    private void setupCharacter() {
        myModel = (Node) assetManager.loadModel(ARCHER);
        rootNode.attachChild(myModel);

        animComposer = GameObject.getComponentInChildren(myModel, AnimComposer.class);
        animsQueue.addAll(animComposer.getAnimClipsNames());
        System.out.println("--Animations: " + animsQueue);

        skinningControl = GameObject.getComponentInChildren(myModel, SkinningControl.class);
        armatureDebug.addArmatureFrom(skinningControl);

        Node rightHand = createBoneHook(HumanBodyBones.RightHand);
        bindWeapon(rightHand, "Models/Arrow/arrow.glb", IKPositions.Arrow.getTransform());

        Node leftHand = createBoneHook(HumanBodyBones.LeftHand);
        bindWeapon(leftHand, "Models/Bow/bow.gltf", IKPositions.Bow.getTransform());
    }

    private void bindWeapon(Node parent, String assetName, Transform tr) {
        Spatial model = assetManager.loadModel(assetName);
        parent.setLocalTransform(tr);
        parent.attachChild(model);
    }

    private Node createBoneHook(String jointName) {
        Joint joint = skinningControl.getArmature().getJoint("mixamorig:" + jointName);
        Node ref = new Node("Joint-" + joint.getId() + "-" + jointName);
        skinningControl.getAttachmentsNode(joint.getName()).attachChild(ref);
        System.out.println("--Setup BoneHook: " + ref);
        return ref;
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
        chaseCam.setZoomSpeed(0.5f);
        chaseCam.setMinDistance(1);
        chaseCam.setMaxDistance(10);
        chaseCam.setDefaultDistance(3);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setRotationSpeed(3);
        chaseCam.setDefaultVerticalRotation(0.3f);
    }

    private void setupKeys() {
        addMapping("nextAnim", new KeyTrigger(KeyInput.KEY_RIGHT));
        addMapping("stopAnim", new KeyTrigger(KeyInput.KEY_RETURN));
        addMapping("toggleArmature", new KeyTrigger(KeyInput.KEY_SPACE));
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

        if (name.equals("stopAnim")) {
            animComposer.reset();

        } else if (name.equals("nextAnim")) {
            nextAnim();

        } else if (name.equals("toggleArmature")) {
            armatureDebug.setEnabled(!armatureDebug.isEnabled());
        }
    }

    private void nextAnim() {
        String actionName = animsQueue.poll();
        animsQueue.add(actionName);
        animComposer.setCurrentAction(actionName);
        animUI.setText("Anim: " + actionName);
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

    @Override
    public void simpleUpdate(float tpf) {
        // do something...
    }

}
