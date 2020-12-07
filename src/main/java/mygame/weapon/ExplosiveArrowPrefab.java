package mygame.weapon;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

public class ExplosiveArrowPrefab extends RangedBullet {
	
	ExplosionPrefab explosionPrefab;
	
	public ExplosiveArrowPrefab(Application app, String name, String effectName) {
		super(app);
		this.name = name;
		mass = 6f;
		explosionPrefab = new ExplosionPrefab(app);
		explosionPrefab.assetName = effectName;
	}

	@Override
	public Spatial getAssetModel() {
		// TODO Auto-generated method stub
		Mesh mesh = new Sphere(16, 16, 0.05f);
		Geometry geo = new Geometry("Arrow", mesh);
		Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green.clone());
		geo.setMaterial(mat);
		return geo;
	}
	
	@Override
	public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
    	Spatial model = getAssetModel();
    	model.setName(name + "-" + nextSeqId());
    	model.setLocalTranslation(position);
    	model.setLocalRotation(rotation);
    	parent.attachChild(model);
    	
    	// Add Physics.
        RigidBodyControl rgb = new RigidBodyControl(mass);
        model.addControl(rgb);
        PhysicsSpace.getPhysicsSpace().add(rgb);
        rgb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        rgb.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        rgb.setCcdMotionThreshold(0.001f);
        
        ExplosiveArrowControl arrow = new ExplosiveArrowControl();
        arrow.explosionPrefab = explosionPrefab;
        model.addControl(arrow);
    	
    	return model;
    }

}
