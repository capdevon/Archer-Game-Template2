package mygame.states;

import com.capdevon.engine.MathUtils;
import com.capdevon.engine.SimpleAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import mygame.weapon.Damageable;

public class CubeAppState extends SimpleAppState {

    private Node targetsNode = new Node("Targets");
    private float spawnTime = 2f;
    private float currTime = 0;
    private int objectId = 0;

    private int maxCubes = 10;
    private float size = 0.5f;
    private float mass = 30f;

    @Override
    protected void simpleInit() {
        rootNode.attachChild(targetsNode);
    }

    @Override
    public void update(float tpf) {
        currTime += tpf;
        if (currTime > spawnTime && targetsNode.getQuantity() < maxCubes) {
            currTime = 0;

            objectId++;
            spawnObject();
            System.out.println("Spawn new objectId: " + objectId);

            //	System.out.println("debug RigidBodyList:--------------------");
            //	Collection<PhysicsRigidBody> lst = physics.getPhysicsSpace().getRigidBodyList();
            //	for (PhysicsRigidBody rb : lst) {
            //		System.out.println(rb.getUserObject());
            //	}
        }
    }

    private void spawnObject() {
        Node cube = createCube(size);
        cube.setLocalTranslation(getRandomPosition());
        targetsNode.attachChild(cube);

        BoundingBox bbox = (BoundingBox) cube.getWorldBound();
        CollisionShape shape = new BoxCollisionShape(bbox.getExtent(null));
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        cube.addControl(rgb);
        getPhysicsSpace().add(rgb);

        Damageable m_Damageable = new Damageable();
        cube.addControl(m_Damageable);
    }

    private Vector3f getRandomPosition() {
        // randomize 3D coordinates
        float dx = MathUtils.range(-20, 20);
        float dz = MathUtils.range(-20, 20);
        Vector3f position = new Vector3f(dx, 5, dz);
        return position;
    }

    private Node createCube(float size) {
        Node node = new Node("Box." + objectId);
        Geometry geo = new Geometry("Box.GeoMesh", new Box(size, size, size));
        Material mat = getMaterializePBR();
        geo.setMaterial(mat);
        geo.setQueueBucket(Bucket.Transparent);
        geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        node.attachChild(geo);
        return node;
    }

    private Material getPBRLighting() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setName("PBRLighting");
        mat.setTexture("BaseColorMap", assetManager.loadTexture("Textures/github-logo.png"));
        mat.setFloat("Metallic", 0);
        mat.setFloat("Roughness", 0.4f);
        return mat;
    }

    private Material getMaterializePBR() {
        Material mat = new Material(assetManager, "MatDefs/Materialize/MaterializePBR.j3md");
        mat.setName("MaterializePBR");
        mat.setTexture("BaseColorMap", assetManager.loadTexture("Textures/github-logo.png"));
        mat.setFloat("Roughness", 0.2f);
        mat.setFloat("Metallic", 0.001f);
        mat.setColor("EdgeColor", ColorRGBA.Red.mult(0.7f));
        mat.setFloat("EdgeThickness", 0.03f);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        // mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        return mat;
    }

}
