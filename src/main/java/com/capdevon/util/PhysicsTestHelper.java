/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.util;

import com.capdevon.engine.MathUtils;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;


/**
 *
 */
public class PhysicsTestHelper {

    public static Node createLightingBox(AssetManager am, int nClone) {
        Node root = new Node("Group.LightingBox");
        Geometry cube = getBoxLighting(am);
        getRandomCubes(root, cube, nClone);

        return root;
    }

    public static Node createUnshadedBox(AssetManager am, int nClone) {
        Node root = new Node("Group.UnshadedBox");
        Geometry cube = getBoxUnshaded(am);
        getRandomCubes(root, cube, nClone);

        return root;
    }
    
    private static Geometry getBoxUnshaded(AssetManager am) {
        Box box = new Box(.5f, .5f, .5f);
        Geometry g = new Geometry("GeoMesh", box);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setTexture("ColorMap", am.loadTexture("Common/Textures/MissingTexture.png"));
        mat.setTexture("ColorMap", am.loadTexture("Textures/github-logo.png"));
        mat.setColor("Color", ColorRGBA.randomColor());
        g.setMaterial(mat);
        return g;
    }

    private static Geometry getBoxLighting(AssetManager am) {
        Box box = new Box(.5f, .5f, .5f);
        TangentBinormalGenerator.generate(box);
        Geometry g = new Geometry("Box.GeoMesh", box);
        Material mat = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", am.loadTexture("Interface/Logo/Monkey.png"));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.randomColor());
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 12);
        g.setMaterial(mat);
        return g;
    }

    private static void getRandomCubes(Node root, Geometry cube, int nClone) {
        for (int i = 0; i < nClone; i++) {

            Geometry g = cube.clone(true);
            g.setName("Box.GeoMesh." + i);
            root.attachChild(g);

            float dx = MathUtils.range(-20, 20);
            float dz = MathUtils.range(-20, 20);
            Vector3f loc = new Vector3f(dx, 10, dz);
            g.setLocalTranslation(loc);

            CollisionShape collShape = CollisionShapeFactory.createBoxShape(g);
            RigidBodyControl rgb = new RigidBodyControl(collShape, 30f);
            g.addControl(rgb);
            PhysicsSpace.getPhysicsSpace().add(rgb);
        }
    }

    public static Spatial getMainScene(AssetManager am) {
        Spatial scene = am.loadModel("Scenes/ManyLights/Main.scene");
        scene.setName("MainScene");
        scene.scale(1f, .5f, 1f);
        scene.setLocalTranslation(0f, -10f, 0f);
        addStaticMeshCollider(scene);

        return scene;
    }
    
    public static Spatial getTownScene(AssetManager am) {
    	am.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = am.loadModel("main.scene");
//        Spatial scene = am.loadModel("Scenes/town/main.scene");
        scene.setName("MainScene");
        scene.setLocalTranslation(0, -5.2f, 0);
        addStaticMeshCollider(scene);

        return scene;
    }

    public static Geometry getQuadFloor(AssetManager am, ColorRGBA color) {
        Box mesh = new Box(20, 0.1f, 20);
        Geometry floor = new Geometry("Floor.GeoMesh", mesh);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        floor.setMaterial(mat);
        addStaticMeshCollider(floor);
        return floor;
    }

    public static void addStaticMeshCollider(Spatial sp) {
        addMeshCollider(sp, 0f);
    }

    public static void addMeshCollider(Spatial sp, float mass) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(sp);
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        sp.addControl(rgb);
        PhysicsSpace.getPhysicsSpace().add(rgb);
    }


    public enum ShapeType {
        NONE, 
        CAPSULE, 
        BOX, 
        SPHERE, 
        CYLINDER, 
        CONE, 
        PLANE, 
        COMPLEX, 
        HULL,
        MESH
    }

    /**
     *
     * @param type
     * @param sp
     * @param widthScale
     * @param heightScale
     * @return
     */
    public static CollisionShape createPhysicShape(ShapeType type, Spatial sp, float widthScale, float heightScale) {

        BoundingBox vol = (BoundingBox) sp.getWorldBound();
        CollisionShape collShape = null;

        if (type.equals(ShapeType.CAPSULE)) {
            float radius = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float height = vol.getYExtent() * heightScale;
            collShape = new CapsuleCollisionShape(radius, height);
        }
        if (type.equals(ShapeType.BOX)) {
            float x = vol.getXExtent() * widthScale;
            float z = vol.getZExtent() * widthScale;
            float y = vol.getYExtent() * heightScale;
            collShape = new BoxCollisionShape(new Vector3f(x, y, z));
        }
        if (type.equals(ShapeType.SPHERE)) {
            float radius = Math.max(Math.max(vol.getXExtent(), vol.getZExtent()), vol.getYExtent());
            collShape = new SphereCollisionShape(radius);
        }
        if (type.equals(ShapeType.CYLINDER)) {
            float x = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float z = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float y = vol.getYExtent() * heightScale;
            collShape = new CylinderCollisionShape(new Vector3f(x, y, z));
        }
        if (type.equals(ShapeType.CONE)) {
            float radius = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float height = vol.getYExtent();
            collShape = new ConeCollisionShape(radius, height);
        }
        if (type.equals(ShapeType.PLANE)) {
            Vector3f normal = sp.getWorldRotation().mult(Vector3f.UNIT_XYZ);
            float constant = Math.max(vol.getXExtent(), vol.getZExtent());
            collShape = new PlaneCollisionShape(new Plane(normal, constant));
        }
        if (type.equals(ShapeType.COMPLEX)) {
            collShape = CollisionShapeFactory.createMeshShape(sp);
        }
        if (type.equals(ShapeType.HULL)) {
            Geometry geo = (Geometry) sp;
            collShape = new HullCollisionShape(geo.getMesh());
        }
        if (type.equals(ShapeType.MESH)) {
            Geometry geo = (Geometry) sp;
            collShape = new MeshCollisionShape(geo.getMesh());
        }

        return collShape;
    }

}
