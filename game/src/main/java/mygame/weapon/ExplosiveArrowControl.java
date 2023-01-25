package mygame.weapon;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import com.capdevon.control.AdapterControl;
import com.capdevon.engine.GameObject;
import com.capdevon.physx.Physics;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import mygame.ai.AIControl;
import mygame.controls.Damageable;
import mygame.prefabs.ExplosionPrefab;

public class ExplosiveArrowControl extends AdapterControl implements PhysicsCollisionListener {

    private static final Logger logger = Logger.getLogger(ExplosiveArrowControl.class.getName());

    private RigidBodyControl rigidBody;
    private PhysicsGhostObject ghostObject;
    private PhysicsSpace m_PhysicsSpace;

    private boolean m_HasCollided;
    private float m_Timer;

    public float maxFlyingTime = 10f;
    public float explosionForce = 20f;
    public float explosionRadius = 4f;
    public ExplosionPrefab explosionPrefab;

    public ExplosiveArrowControl() {
        createGhostObject();
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            rigidBody = getComponent(RigidBodyControl.class);
            m_PhysicsSpace = rigidBody.getPhysicsSpace();
            m_PhysicsSpace.addCollisionListener(this);
        }
    }

    private void createGhostObject() {
        ghostObject = new PhysicsGhostObject(new SphereCollisionShape(explosionRadius));
    }

    @Override
    protected void controlUpdate(float tpf) {
        m_Timer += tpf;
        if (!m_HasCollided) {
            // this is to cleanup old objects that hasn't collided yet (lived more than maxTime)
            if (m_Timer > maxFlyingTime) {
                rigidBody.setEnabled(false);
                destroy();
                logger.log(Level.INFO, "Timeout, Object Destroyed: {0}", spatial);
            }
        } else {
            explode();
            destroy();
            logger.log(Level.INFO, "Object Destroyed: {0}", spatial);
        }
    }

    //  Destroy everything
    private void destroy() {
        m_PhysicsSpace.removeCollisionListener(this);
        if (m_HasCollided) {
            m_PhysicsSpace.remove(ghostObject);
        }
        spatial.removeFromParent();
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (m_HasCollided) {
            return;
        }

        if (event.getObjectA() == rigidBody || event.getObjectB() == rigidBody) {
            m_HasCollided = true;

            // Stop the rigidBody in position
            rigidBody.setEnabled(false);

            PhysicsCollisionObject other;
            Vector3f hitPoint;

            if (event.getObjectA() == rigidBody) {
                other = event.getObjectB();
                hitPoint = event.getPositionWorldOnB();
            } else {
                other = event.getObjectA();
                hitPoint = event.getPositionWorldOnA();
            }

            if (logger.isLoggable(Level.INFO)) {
                Spatial otherGameObject = GameObject.findGameObject(other);
                logger.log(Level.INFO, "Collided with: {0}", otherGameObject);
            }

            ghostObject.setPhysicsLocation(hitPoint);
            m_PhysicsSpace.add(ghostObject);
        }
    }

    private void explode() {
        explosionPrefab.instantiate(ghostObject.getPhysicsLocation(), Quaternion.IDENTITY, spatial.getParent());

        List<Spatial> affectedGameObjects = new ArrayList<>(8);
        for (PhysicsCollisionObject pco : ghostObject.getOverlappingObjects()) {
            Spatial gameObject = GameObject.findGameObject(pco);

            if (pco instanceof GhostControl) {
                System.out.println("skipping: " + gameObject);
                continue;
            }

            if (pco instanceof PhysicsRigidBody) {
                PhysicsRigidBody rb = (PhysicsRigidBody) pco;
                if (rb.getMass() > 0) {
                    logger.log(Level.INFO, "addExplosionForce to: {0}", gameObject);
                    Physics.addExplosionForce(rb, explosionForce, rigidBody.getPhysicsLocation(), explosionRadius);
                }
                if (!affectedGameObjects.contains(gameObject)) {
                    affectedGameObjects.add(gameObject);
                }
            }
        }

        for (Spatial gameObject : affectedGameObjects) {
            Damageable damageable = gameObject.getControl(Damageable.class);
            if (damageable != null) {
                damageable.applyDamage();
            }
            AIControl aiControl = gameObject.getControl(AIControl.class);
            if (aiControl != null) {
                aiControl.kill();
            }
        }
    }

}
