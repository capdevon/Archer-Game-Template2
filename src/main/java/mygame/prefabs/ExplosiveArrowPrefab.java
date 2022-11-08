package mygame.prefabs;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.weapon.ExplosiveArrowControl;
import mygame.weapon.RangedBullet;

/**
 * 
 * @author capdevon
 */
public class ExplosiveArrowPrefab extends RangedBullet {

    public float radius = 0.04f;
    public ExplosionPrefab explosionPrefab;
    final static String ASSET_PATH = "Models/Arrow/arrow.glb";

    public ExplosiveArrowPrefab(Application app) {
        super(app);
    }

//    private Spatial loadModel() {
//        Mesh mesh = new Sphere(16, 16, 0.05f);
//        Geometry geo = new Geometry("Arrow", mesh);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Green.clone());
//        geo.setMaterial(mat);
//        return geo;
//    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = assetManager.loadModel(ASSET_PATH);
        model.setName(name + "-" + nextSeqId());
        model.setLocalTranslation(position);
        model.setLocalRotation(rotation);
        parent.attachChild(model);

        // Add Physics
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        model.addControl(rgb);
        getPhysicsSpace().add(rgb);

        rgb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        rgb.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        rgb.setCcdMotionThreshold(0.001f);

        ExplosiveArrowControl arrow = new ExplosiveArrowControl();
        arrow.explosionPrefab = explosionPrefab;
        model.addControl(arrow);

        return model;
    }

    /**
     * Preload the assets used in this prefab.
     *
     * @param assetManager for loading assets (not null)
     */
    @Override
    public void load(AssetManager assetManager) {
        assetManager.loadModel(ASSET_PATH);
    }
}
