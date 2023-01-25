package mygame.prefabs;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.controls.Damageable;
import mygame.controls.TimerControl;
import mygame.weapon.Penetrator;
import mygame.weapon.RangedBullet;

/**
 * @author capdevon
 */
public class ArrowPrefab extends RangedBullet {

    public float radius = 0.04f;
    public static final String ASSET_PATH = "Models/Arrow/arrow.j3o";

    public ArrowPrefab(Application app) {
        super(app);
    }

//    private Spatial loadModel() {
//        Node model = new Node();
//
//        Geometry g1 = new Geometry("Arrow.GeoMesh", new Sphere(16, 16, radius));
//        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat1.setColor("Color", ColorRGBA.Green.clone());
//        g1.setMaterial(mat1);
//        model.attachChild(g1);
//
//        Geometry g2 = new Geometry("Axis.Z", new Arrow(Vector3f.UNIT_Z));
//        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat2.setColor("Color", ColorRGBA.Blue.clone());
//        mat2.getAdditionalRenderState().setLineWidth(2f);
//        g2.setMaterial(mat2);
//        g2.setLocalTranslation(FVector.forward(g1).negate());
//        model.attachChild(g2);
//
//        return model;
//    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = assetManager.loadModel(ASSET_PATH);
        model.setName(name + "-" + nextSeqId());
        model.setLocalTranslation(position);
        model.setLocalRotation(rotation);
        parent.attachChild(model);
        /*
         * The loaded model has its origin at the tip of the arrow.
         * Translate all 3 geometries foward so that the model origin
         * will be located at the arrow's center of mass.
         */
        float halfLength = 0.5f;
        Vector3f tipLocalOffset = new Vector3f(0f, 0f, halfLength);
        Node node = (Node) model;
        for (Spatial geometry : node.getChildren()) {
            geometry.setLocalTranslation(tipLocalOffset);
        }

        // Add Physics.
        float length = 2f * halfLength;
        CollisionShape shape = new CapsuleCollisionShape(
                radius, length, PhysicsSpace.AXIS_Z);
        float penetrationFraction = 0.35f;
        Penetrator penetrator = new Penetrator(
                shape, mass, tipLocalOffset, penetrationFraction);
        model.addControl(penetrator);
        getPhysicsSpace().add(penetrator);

        Damageable damageable = new Damageable();
        model.addControl(damageable);

        // Remove the arrow automatically in 15 seconds.
        TimerControl timeout = new TimerControl(15f) {
            @Override
            public void onTrigger() {
                penetrator.setEnabled(false);
                model.removeFromParent();
            }
        };
        timeout.setEnabled(true);
        model.addControl(timeout);

        return model;
    }

}
