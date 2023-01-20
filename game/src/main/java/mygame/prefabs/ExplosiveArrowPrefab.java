package mygame.prefabs;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.Main;
import mygame.weapon.ExplosiveArrowControl;
import mygame.weapon.RangedBullet;

/**
 * 
 * @author capdevon
 */
public class ExplosiveArrowPrefab extends RangedBullet {

    public float radius = 0.04f;
    public ExplosionPrefab explosionPrefab;

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
        Spatial model = assetManager.loadModel(ArrowPrefab.ASSET_PATH);
        model.setName(name + "-" + nextSeqId());
        model.setLocalTranslation(position);
        model.setLocalRotation(rotation);
        parent.attachChild(model);

        // Add Physics
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        model.addControl(rgb);
        getPhysicsSpace().add(rgb);

        rgb.setCcdMotionThreshold(radius);
        rgb.setCcdSweptSphereRadius(radius);
        rgb.setCollisionGroup(Main.AMMO_GROUP);
        rgb.setCollideWithGroups(Main.DEFAULT_GROUP); // ignore BCCs and other ammo

        ExplosiveArrowControl arrow = new ExplosiveArrowControl();
        arrow.explosionPrefab = explosionPrefab;
        model.addControl(arrow);

        return model;
    }

}
