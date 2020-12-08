package mygame;

import com.capdevon.engine.MathUtils;
import com.capdevon.engine.SimpleAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class CubeAppState extends SimpleAppState {
	
	private Node targetsNode = new Node("Targets");
    private Node cubePrefab;
    private float spawnTime = 2f;
	private float currTime = 0;
	private int objectId = 0;
	
	@Override
	protected void simpleInit() {
		rootNode.attachChild(targetsNode);
		initCubePrefab();
	}
	
	@Override
	public void update(float tpf) {
		currTime += tpf;
		if (currTime > spawnTime) {
			currTime = 0;
			
			objectId++;
			spawnObject();
		}
	}
	
	private void spawnObject() {
		// randomize 3D coordinates
		float dx = MathUtils.range(-20, 20);
        float dz = MathUtils.range(-20, 20);
        Vector3f position = new Vector3f(dx, 5, dz);
        
		Node cube = (Node) instantiate(cubePrefab, position, Quaternion.IDENTITY, targetsNode);
		cube.setName("Target-Cube-" + objectId);
		
		BoundingBox vol = (BoundingBox) cube.getWorldBound();
		CollisionShape shape = new BoxCollisionShape(vol.getExtent(null));
		RigidBodyControl rgb = new RigidBodyControl(shape, 30f);
		cube.addControl(rgb);
		physics.getPhysicsSpace().add(rgb);
	}
    
	private void initCubePrefab() {
		float size = .5f;
		cubePrefab = new Node("Box");
		Geometry geo = new Geometry("Box.GeoMesh", new Box(size, size, size));
		Material mat = getCubeMaterial();
		geo.setMaterial(mat);
		cubePrefab.attachChild(geo);
	}

	private Material getCubeMaterial() {
		Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
		mat.setTexture("BaseColorMap", assetManager.loadTexture("Textures/github-logo.png"));
		mat.setFloat("Metallic", 0);
		mat.setFloat("Roughness", 0.4f);
		return mat;
	}

}
