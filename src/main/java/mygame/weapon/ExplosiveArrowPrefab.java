package mygame.weapon;

import com.jme3.app.Application;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ExplosiveArrowPrefab extends RangedBullet {

    private float radius = 0.04f;
    private ExplosionPrefab explosionPrefab;

    public ExplosiveArrowPrefab(Application app, String name, float mass, ExplosionPrefab explosionPrefab) {
        super(app);
        this.name = name;
        this.mass = mass;
        this.explosionPrefab = explosionPrefab;
    }

    @Override
    public Spatial loadModel() {
        //		Mesh mesh = new Sphere(16, 16, 0.05f);
        //		Geometry geo = new Geometry("Arrow", mesh);
        //		Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        //		mat.setColor("Color", ColorRGBA.Green.clone());
        //		geo.setMaterial(mat);
        //		return geo;

        return getAssetManager().loadModel("Models/Arrow/arrow.glb");
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = loadModel();
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

}
