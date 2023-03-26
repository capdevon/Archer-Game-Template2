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
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

import mygame.camera.MainCamera;
import mygame.decals.DecalManager;
import mygame.decals.DecalProjector;

/**
 *
 * @author capdevon
 */
public class TestDecals extends SimpleApplication implements ActionListener {

    /**
     *
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

    private float widht = 1f;
    private float height = 1f;
    private float depth = 1f;
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

        configureCamera();
        setupScene();
        addLighting();
        setupKeys();

        stateManager.attach(decalManager);
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

        Box box = new Box(10f, 0.02f, 10f);
        box.scaleTextureCoordinates(new Vector2f(5, 5));
        Geometry floor = new Geometry("Floor", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture("Textures/default_grid.png");
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", tex);
        floor.setMaterial(mat);
        floor.setLocalTranslation(0, -0.01f, 0);
        scene.attachChild(floor);

        Spatial monkeyHead = assetManager.loadModel("Models/MonkeyHead/MonkeyHead.mesh.xml");
        monkeyHead.setLocalTranslation(0, 2, 0);
        scene.attachChild(monkeyHead);

        rootNode.attachChild(scene);
    }

    private void addLighting() {
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
        rootNode.addLight(ambient);

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
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("splat") && isPressed) {
            Vector3f position = getLocationOnMap();
            Vector3f direction = new Vector3f(0, 0, -1);
            if (position != null) {
                Quaternion rotation = new Quaternion().lookAt(direction, Vector3f.UNIT_Y);
                projectDecal(position, rotation, scene, widht, height, depth);
            }
        } else if (name.equals("showDebugNode") && isPressed) {
            showDebugNode = !showDebugNode;
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

    private void projectDecal(Vector3f position, Quaternion rotation, Spatial subtree, float width, float height, float depth) {

        Vector3f projectionBox = new Vector3f(width, height, depth);
        DecalProjector projector = new DecalProjector(subtree, position, rotation, projectionBox);
        projector.setSeparation(0.01f);

        Geometry decal = projector.project();
        Material decalMat = createDecalMaterial();
        decal.setMaterial(decalMat);
        decal.setQueueBucket(Bucket.Transparent);
        decalManager.addDecal(decal);

        // debug projection box
        Geometry box = new Geometry(decal.getName() + "-ProjectionBox", new Box(projectionBox.x / 2f, projectionBox.y / 2f, projectionBox.z / 2f));
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("Color", ColorRGBA.White);
        box.setMaterial(boxMat);
        boxMat.getAdditionalRenderState().setWireframe(true);
        box.setShadowMode(ShadowMode.Off);
        box.setLocalTranslation(position);
        box.setLocalRotation(rotation);
        debugNode.attachChild(box);
    }

    private Material createDecalMaterial() {
        Material decalMat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
//        Texture tex = assetManager.loadTexture("Textures/blood-splatter-png-44461.png");
        Texture tex = assetManager.loadTexture("Textures/blood-png-7145.png");
        decalMat.setTexture("BaseColorMap", tex);
        decalMat.setColor("BaseColor", ColorRGBA.White);
        decalMat.setFloat("Metallic", 0.2f);
        decalMat.getAdditionalRenderState().setPolyOffset(-1f, -1f);
        decalMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        decalMat.getAdditionalRenderState().setDepthWrite(false);
        return decalMat;
    }

}
