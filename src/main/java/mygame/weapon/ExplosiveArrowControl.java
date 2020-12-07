package mygame.weapon;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.control.AdapterControl;
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

public class ExplosiveArrowControl extends AdapterControl implements PhysicsCollisionListener {

	private static final Logger logger = Logger.getLogger(ExplosiveArrowControl.class.getName());
	
	private RigidBodyControl rigidBody;
	private PhysicsGhostObject ghostObject;
	private PhysicsSpace space;
	
	boolean hasCollided;
	float timer;
	float maxFlyingTime = 10f;
	float explosionForce = 20f;
	float explosionRadius = 5f;
	
	ExplosionPrefab explosionPrefab;
	
	public ExplosiveArrowControl() {
		createGhostObject();
	}

	@Override
	public void setSpatial(Spatial sp) {
		super.setSpatial(sp);
		if (spatial != null) {
			rigidBody = getComponent(RigidBodyControl.class);
			space = rigidBody.getPhysicsSpace();
			space.addCollisionListener(this);
		}
	}
	
	private void createGhostObject() {
		ghostObject = new PhysicsGhostObject(new SphereCollisionShape(explosionRadius));
	}
	
    @Override
    protected void controlUpdate(float tpf) {
        // TODO Auto-generated method stub
    	timer += tpf;
    	if (!hasCollided) {
    		// this is to cleanup old objects that hasn't collided yet (lived more than maxTime)
    		if (timer > maxFlyingTime) {
    			rigidBody.setEnabled(false);
    			destroy();
    		}
    	} else {
    		explode();
    		destroy();
    		logger.log(Level.INFO, "Object Destroyed: {0}", spatial);
    	}
    }
    
	//  Destroy everything
	private void destroy() {
		space.removeCollisionListener(this);
		space.remove(ghostObject);
		spatial.removeFromParent();
		logger.log(Level.INFO, "Timeout, Object Destroyed: {0}", spatial);
	}
    
	@Override
	public void collision(PhysicsCollisionEvent event) {
		if (hasCollided) {
			return;
		}
		
		if ( event.getObjectA() == rigidBody || event.getObjectB() == rigidBody ) {
			hasCollided = true;
			
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
			
			logger.log(Level.INFO, "Collided with: {0}", other.getUserObject().toString());
			
			ghostObject.setPhysicsLocation(hitPoint);
			space.add(ghostObject);
		}
	}

	private void explode() {
		explosionPrefab.instantiate(ghostObject.getPhysicsLocation(), Quaternion.IDENTITY, spatial.getParent());
		
		for (PhysicsCollisionObject pco : ghostObject.getOverlappingObjects()) {
			
			if (pco instanceof GhostControl) {
				System.out.println("skipping: " + pco.getUserObject());
				continue;
			}
			
			if (pco instanceof PhysicsRigidBody) {
				PhysicsRigidBody rb = (PhysicsRigidBody) pco;
				if (rb.getMass() > 0) {
					logger.log(Level.INFO, "addExplosionForce to: " + pco.getUserObject().toString());
					Physics.addExplosionForce(rb, explosionForce, rigidBody.getPhysicsLocation(), explosionRadius);
				}
				
//				if (pco.getUserObject() instanceof Spatial) {
//					Spatial gameObject = (Spatial) pco.getUserObject();
//					Damageable damageable = gameObject.getControl(Damageable.class);
//					if (damageable != null) {
//						damageable.destroy();
//					}
//				}
			}
		}
	}
	
}
