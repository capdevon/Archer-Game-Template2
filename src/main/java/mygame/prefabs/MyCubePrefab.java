package mygame.prefabs;

import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import mygame.weapon.Damageable;

/**
 *
 * @author capdevon
 */
public class MyCubePrefab extends PrefabComponent {

    public float size = 0.5f;
    public float mass = 30f;

    public MyCubePrefab(Application app) {
        super(app);
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Node cube = new Node("Box." + nextSeqId());
        Geometry geo = new Geometry("Box.GeoMesh", new Box(size, size, size));
        Material mat = getMaterializePBR();
        geo.setMaterial(mat);
        geo.setQueueBucket(Bucket.Transparent);
        geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        cube.attachChild(geo);

        cube.setLocalTranslation(position);
        cube.setLocalRotation(rotation);
        parent.attachChild(cube);

        BoundingBox bbox = (BoundingBox) cube.getWorldBound();
        CollisionShape shape = new BoxCollisionShape(bbox.getExtent(null));
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        cube.addControl(rgb);
        getPhysicsSpace().add(rgb);

        Damageable m_Damageable = new Damageable();
        cube.addControl(m_Damageable);

        return cube;
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
