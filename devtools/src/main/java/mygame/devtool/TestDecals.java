package mygame.devtool;

import com.capdevon.engine.WireAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

import jme3utilities.DecalManager;
import mygame.camera.MainCamera;
import mygame.decals.DecalProjector;

/**
 *
 * @author capdevon
 */
public class TestDecals extends SimpleApplication implements ActionListener {

    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
        TestDecals app = new TestDecals();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    /**
     * The size of the projector influence box. 
     * The projector scales the decal to match the Width (along the local x-axis) 
     * and Height (along the local y-axis) components of the Size.
     */
    private Vector2f projectionSize = new Vector2f(1f, 1f);
    /**
     * The depth of the projector influence box. 
     * The projector scales the decal to match Projection Depth. 
     * The Decal Projector component projects decals along the local z-axis.
     */
    private float projectionDepth = 1f;
    // The Material to project.
    private Material decalMat;
    private Material wireMat;
    private boolean showDebugNode = true;
    private Node scene = new Node("Scene");
    private Node debugNode = new Node("DebugNode");
    private DecalManager decalManager = new DecalManager();

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(25);
        flyCam.setDragToRotate(true);

        // Set the viewport's background color to light blue.
        ColorRGBA skyColor = new ColorRGBA(0.1f, 0.2f, 0.4f, 1f);
        viewPort.setBackgroundColor(skyColor);

        decalMat = createDecalMaterial();
        wireMat = createWireMaterial(ColorRGBA.White);

        configureCamera();
        setupScene();
        addLighting();
        setupKeys();

        rootNode.attachChild(decalManager.getNode());
        stateManager.attach(new WireAppState());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        if (showDebugNode) {
            debugNode.updateLogicalState(0);
            debugNode.updateGeometricState();
            rm.renderScene(debugNode, viewPort);
        }
    }

    private void configureCamera() {
        float aspect = (float) cam.getWidth() / (float) cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.001f, 200);
        cam.setLocation(new Vector3f(0, 2, 10));
    }

    private void setupScene() {

        Box box = new Box(10f, 0.1f, 10f);
        box.scaleTextureCoordinates(new Vector2f(5, 5));
        Geometry floor = new Geometry("Floor", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture("Textures/default_grid.png");
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", tex);
        floor.setMaterial(mat);
        floor.setLocalTranslation(0, -0.1f, 0);
        scene.attachChild(floor);

        Spatial monkeyHead = assetManager.loadModel("Models/MonkeyHead/MonkeyHead.mesh.xml");
        monkeyHead.setLocalTranslation(0, 2, 0);
        scene.attachChild(monkeyHead);

        Geometry cube = createCube(new Vector3f(4, 2, 0), new Vector3f(1, 1, 1));
        scene.attachChild(cube);

        rootNode.attachChild(scene);
    }

    private Geometry createCube(Vector3f position, Vector3f size) {
        Box mesh = new Box(size.x, size.y, size.z);
        Geometry geom = new Geometry("Cube", mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(position);
        return geom;
    }

    private void addLighting() {
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
        rootNode.addLight(ambient);

        // add a PBR probe.
        Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
        lightProbe.getArea().setRadius(100);
        //rootNode.addLight(lightProbe);

        // skylight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.2f, -1, -0.3f).normalizeLocal());
        rootNode.addLight(sun);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.4f);
        dlsf.setShadowZExtend(256);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
    }

    private void setupKeys() {
        addMapping("splat", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        addMapping("showDebugNode", new KeyTrigger(KeyInput.KEY_SPACE));
        addMapping("removeAll", new KeyTrigger(KeyInput.KEY_R));
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("splat") && isPressed) {
            Vector3f position = getLocationOnMap();
            if (position != null) {
                Vector3f cameraLocation = cam.getLocation(); // alias
                Vector3f offsetFromCamera = position.subtract(cameraLocation);
                if (offsetFromCamera.length() < 0.001f) {
                    return; // too close to apply a decal
                }
                Vector3f projectionDir = offsetFromCamera.normalize();
                Quaternion rotation = new Quaternion().lookAt(projectionDir, Vector3f.UNIT_Y);
                projectDecal(position, rotation, scene);
            }
        } else if (name.equals("showDebugNode") && isPressed) {
            showDebugNode = !showDebugNode;

        } else if (name.equals("removeAll") && isPressed) {
            decalManager.removeAll();
            debugNode.detachAllChildren();
        }
    }

    private Vector3f getLocationOnMap() {
        Ray ray = MainCamera.screenPointToRay(cam, inputManager.getCursorPosition());
        CollisionResults collResults = new CollisionResults();
        scene.collideWith(ray, collResults);

        if (collResults.size() > 0) {
            return collResults.getClosestCollision().getContactPoint();
        } else {
            return null;
        }
    }

    private void projectDecal(Vector3f position, Quaternion rotation, Spatial subtree) {

        Vector3f projectionBox = new Vector3f(projectionSize.x, projectionSize.y, projectionDepth);
        DecalProjector projector = new DecalProjector(subtree, position, rotation, projectionBox);
        projector.setSeparation(0.001f);

        Geometry decal = projector.project();
        decal.setMaterial(decalMat);
        decal.setQueueBucket(Bucket.Transparent);
        decalManager.addDecal(decal);

        // debug
        Geometry box = new Geometry("ProjectionBox", new Box(projectionBox.x / 2f, projectionBox.y / 2f, projectionBox.z / 2f));
        box.setMaterial(wireMat);
        box.setShadowMode(ShadowMode.Off);
        box.setLocalTranslation(position);
        box.setLocalRotation(rotation);
        debugNode.attachChild(box);

        Geometry arrow = new Geometry("ProjectionLine", new Arrow(new Vector3f(0, -1, 0)));
        arrow.setMaterial(wireMat);
        arrow.setShadowMode(ShadowMode.Off);
        arrow.setLocalTranslation(position.add(0, 1, 0));
        debugNode.attachChild(arrow);
    }

    private Material createWireMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setWireframe(true);
        return mat;
    }

    private Material createDecalMaterial() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        //Texture tex = assetManager.loadTexture("Textures/blood-splatter-png-44461.png");
        Texture tex = assetManager.loadTexture("Textures/blood-png-7145.png");
        mat.setTexture("BaseColorMap", tex);
        mat.setColor("BaseColor", ColorRGBA.White);
        mat.setFloat("Metallic", 0.2f);
        mat.getAdditionalRenderState().setPolyOffset(-1f, -1f);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        return mat;
    }

}
