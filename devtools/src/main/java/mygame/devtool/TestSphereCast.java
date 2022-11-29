package mygame.devtool;

import java.util.LinkedList;
import java.util.List;

import com.capdevon.engine.GameObject;
import com.capdevon.physx.RaycastHit;
import com.jme3.anim.AnimComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.MultiSphere;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.DebugTools;
import com.jme3.environment.util.BoundingSphereDebug;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.util.TempVars;

import jme3utilities.math.MyVector3f;

/**
 *
 * @author capdevon
 */
public class TestSphereCast extends SimpleApplication implements ActionListener {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        TestSphereCast app = new TestSphereCast();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    private BulletAppState physics;
    // The layer mask against which the collider will raycast
    private int collideWithGroups = ~0;
    // Obstacles with this tag will be ignored. It is a good idea to set this field to the target's tag
    private String ignoreTag = "";
    private float maxDistance = 10f;
    private float cameraRadius = 0.4f;
    private boolean useMultiSphere = true;
    private final RaycastHit hitInfo = new RaycastHit();
    private final List<PhysicsSweepTestResult> sweepTestResults = new LinkedList<>();

    private DebugTools debugTools;
    private Geometry marker;

    @Override
    public void simpleInitApp() {
        configCamera();
        initPhysics();
        createGrid();
        addModels();
        createCrosshair();
        setupSky();
        setupLights();
        setupKeys();

        guiNode.attachChild(createCrosshair());

        debugTools = new DebugTools(assetManager);
        marker = createMarker();
        debugTools.debugNode.attachChild(marker);
    }

    private void configCamera() {
        cam.setLocation(new Vector3f(0, 2f, 5f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(20f);

        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(60, aspect, 0.1f, 100f);
    }

    /**
     * Initialize the physics simulation
     */
    private void initPhysics() {
        physics = new BulletAppState();
        stateManager.attach(physics);
        physics.setDebugEnabled(true);
    }

    private void setupSky() {
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    /**
     * An ambient light and a directional sun light
     */
    private void setupLights() {
        // change the viewport background color.
//        viewPort.setBackgroundColor(ColorRGBA.White);
//        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-1f, -1f, -1f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }

    /* A centered plus sign to help the player aim. */
    private BitmapText createCrosshair() {
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+");
        ch.setColor(ColorRGBA.White);
        float width = settings.getWidth() / 2 - ch.getLineWidth() / 2;
        float height = settings.getHeight() / 2 + ch.getLineHeight() / 2;
        ch.setLocalTranslation(width, height, 0);
        return ch;
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

    private void addModels() {

        float radius = 0.5f;
        float height = 1.8f;

        for (int i = 0; i < 4; i++) {
            Node myModel = (Node) assetManager.loadModel("Models/Erika/Erika.j3o");
            myModel.setName("Erika." + i);
            myModel.setLocalTranslation(0, 0, -2 * i);

            AnimComposer composer = GameObject.getComponentInChildren(myModel, AnimComposer.class);
            composer.setCurrentAction("Idle");

            if (i % 2 == 0) {
                BoxCollisionShape boxShape = new BoxCollisionShape(radius, (height - (2 * radius)), radius);
                CompoundCollisionShape collShape = new CompoundCollisionShape();
                Vector3f position = new Vector3f(0, (height / 2f), 0);
                collShape.addChildShape(boxShape, position);

                RigidBodyControl rbc = new RigidBodyControl(collShape, 0f);
                myModel.addControl(rbc);

            } else {
                CapsuleCollisionShape capsule = new CapsuleCollisionShape(radius, (height - (2 * radius)));
                CompoundCollisionShape collShape = new CompoundCollisionShape();
                Vector3f position = new Vector3f(0, (height / 2f), 0);
                collShape.addChildShape(capsule, position);

                RigidBodyControl rbc = new RigidBodyControl(collShape, 0f);
                myModel.addControl(rbc);
            }

            rootNode.attachChild(myModel);
            physics.getPhysicsSpace().add(myModel);
        }
    }

    private Geometry createMarker() {
        Geometry geo = new Geometry("WireSphere", new BoundingSphereDebug());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        mat.getAdditionalRenderState().setWireframe(true);
        geo.setMaterial(mat);
        geo.setLocalTranslation(0, -1000, 0);
        geo.setLocalScale(cameraRadius);
        return geo;
    }

    private void setupKeys() {
        addMapping("FIRE_ACTION", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        addMapping("TOGGLE_PHYSICS_DEBUG", new KeyTrigger(KeyInput.KEY_0));
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("FIRE_ACTION") && isPressed) {
            fire();

        } else if (name.equals("TOGGLE_PHYSICS_DEBUG") && isPressed) {
            boolean debugEnabled = physics.isDebugEnabled();
            physics.setDebugEnabled(!debugEnabled);
        }
    }

    private void fire() {
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        debugTools.setYellowArrow(ray.origin, ray.direction.mult(maxDistance));

        if (sphereCast(ray.origin, cameraRadius, ray.direction, hitInfo, maxDistance, collideWithGroups)) {
            marker.setLocalTranslation(hitInfo.point);
        } else {
            marker.setLocalTranslation(0, -1000, 0);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        debugTools.show(rm, viewPort);
    }

    private boolean sphereCast(Vector3f origin, float radius, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        boolean collision = false;
        hitInfo.clear();

        float penetration = 0f; // physics-space units
        ConvexShape shape = (useMultiSphere) ? new MultiSphere(radius) : new SphereCollisionShape(radius);

        // FIXME: the order of sweep-test results is arbitrary. 
        // Perhaps it is worth sorting objects by distance in ascending order.
        physics.getPhysicsSpace().sweepTest(shape, new Transform(beginVec), new Transform(finalVec), sweepTestResults, penetration);

        System.out.println("--Collisions: " + sweepTestResults.size());

        for (PhysicsSweepTestResult tr : sweepTestResults) {

            PhysicsCollisionObject pco = tr.getCollisionObject();
            Spatial userObject = (Spatial) pco.getUserObject();
            System.out.println(userObject);

            boolean isObstruction = applyMask(layerMask, pco.getCollisionGroup())
                    && !compareTag(userObject, ignoreTag);

            if (isObstruction) {

                hitInfo.rigidBody = pco;
                hitInfo.collider = pco.getCollisionShape();
                hitInfo.gameObject = userObject;
                MyVector3f.lerp(tr.getHitFraction(), beginVec, finalVec, hitInfo.point);
                tr.getHitNormalLocal(hitInfo.normal);
                hitInfo.distance = beginVec.distance(hitInfo.point);

                System.out.println("isObstruction " + userObject);
                collision = true;
                break;
            }
        }

        t.release();
        return collision;
    }

    private boolean compareTag(Spatial sp, String tagName) {
        return tagName.equals(sp.getUserData("TagName"));
    }

    // Check if a collisionGroup is in a layerMask
    private boolean applyMask(int layerMask, int collisionGroup) {
        return layerMask == (layerMask | collisionGroup);
    }

}
