package mygame.weapon;

import com.jme3.app.Application;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author capdevon
 */
public class ArrowPrefab extends RangedBullet {

    private float radius = 0.04f;

    public ArrowPrefab(Application app, String name) {
        super(app);
        this.mass = 6f;
        this.name = name;
    }

    @Override
	public Spatial loadModel() {
//		Node model = new Node();
//
//		Geometry g1 = new Geometry("Arrow.GeoMesh", new Sphere(16, 16, radius));
//		Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//		mat1.setColor("Color", ColorRGBA.Green.clone());
//		g1.setMaterial(mat1);
//		model.attachChild(g1);
//
//		Geometry g2 = new Geometry("Axis.Z", new Arrow(Vector3f.UNIT_Z));
//		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//		mat2.setColor("Color", ColorRGBA.Blue.clone());
//		mat2.getAdditionalRenderState().setLineWidth(2f);
//		g2.setMaterial(mat2);
//		g2.setLocalTranslation(FVector.forward(g1).negate());
//		model.attachChild(g2);
//
//		return model;

		return assetManager.loadModel("Models/Arrow/arrow.glb");
	}

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = loadModel();
        model.setName(name + "-" + nextSeqId());
        model.setLocalTranslation(position);
        model.setLocalRotation(rotation);
        parent.attachChild(model);

        // Add Physics.
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        RigidBodyControl rbc = new RigidBodyControl(shape, mass);
        model.addControl(rbc);
        getPhysicsSpace().add(rbc);

        rbc.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        rbc.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        rbc.setCcdMotionThreshold(0.001f);
        // rbc.setCcdSweptSphereRadius(0.001f);

        ArrowControl arrow = new ArrowControl();
        model.addControl(arrow);

        return model;
    }

}
