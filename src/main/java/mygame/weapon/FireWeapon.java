package mygame.weapon;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author capdevon
 */
public class FireWeapon extends Weapon {

    private static final Logger logger = Logger.getLogger(FireWeapon.class.getName());

    float distance = 30f;
    AudioNode impactSFX;
    ParticleEmitter impactVFX;
    RaycastHit shootHit = new RaycastHit();

    public FireWeapon() {
        // default empty.
    }

    /**
     * @param name
     * @param model
     */
    public FireWeapon(String name, Node model) {
        super(name, model);
    }

    public void handleShoot(Vector3f origin, Vector3f direction) {
        if (Physics.doRaycast(origin, direction, shootHit, distance)) {
            logger.log(Level.INFO, " * You shot: " + shootHit);
            applyExplosion(shootHit);

        } else {
            logger.log(Level.INFO, "Target not in range...");
        }

        if (impactSFX != null) {
            impactSFX.playInstance();
        }
    }

    /**
     * @param hit
     */
    private void applyExplosion(RaycastHit hit) {
        float explosionRadius = 5;
        float baseStrength = 10f;
        int layerMask = PhysicsCollisionObject.COLLISION_GROUP_01;
        ColorRGBA color = ColorRGBA.randomColor();

        Function<PhysicsCollisionObject, Boolean> dynamicBodies = new Function<PhysicsCollisionObject, Boolean>() {
            @Override
            public Boolean apply(PhysicsCollisionObject pco) {
                if (pco instanceof PhysicsRigidBody) {
                    PhysicsRigidBody rb = (PhysicsRigidBody) pco;
                    return rb.getMass() > 0;
                }
                return false;
            }
        };

        int maxColliders = 10;
        PhysicsCollisionObject[] hitColliders = new PhysicsCollisionObject[maxColliders];
        int numColliders = Physics.overlapSphereNonAlloc(hit.point, explosionRadius, hitColliders, layerMask, dynamicBodies);
        System.out.println("numColliders=" + numColliders);

        for (int i = 0; i < numColliders; i++) {

            PhysicsCollisionObject pco = hitColliders[i];
            if (pco instanceof PhysicsRigidBody) {

                PhysicsRigidBody rb = (PhysicsRigidBody) pco;
                Physics.addExplosionForce(rb, baseStrength, hit.point, explosionRadius);

                Spatial userObj = (Spatial) pco.getUserObject();
                applyDamage(userObj, color);
            }
        }
    }

    /**
     * @param hit
     */
    private void applyImpulse(RaycastHit hit, Weapon weapon) {
        RigidBodyControl rgb = hit.userObject.getControl(RigidBodyControl.class);
        if (rgb != null && rgb.getMass() > 0) {

            Vector3f force = rgb.getGravity(null).negateLocal().multLocal(rgb.getMass());
            rgb.applyImpulse(force, Vector3f.ZERO);

            ColorRGBA color = ColorRGBA.randomColor();
            applyDamage(hit.userObject, color);
        }
    }

    private void applyDamage(Spatial sp, ColorRGBA color) {
        if (sp instanceof Geometry) {
            Geometry geom = (Geometry) sp;
            geom.getMaterial().setColor("Color", color);
        }
    }

}
