package mygame.weapon;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.control.AdapterControl;
import com.capdevon.control.TimerControl;
import com.jme3.anim.SkinningControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.animation.BoneLink;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ArrowControl extends AdapterControl implements PhysicsCollisionListener {
	
	private static final Logger logger = Logger.getLogger(ArrowControl.class.getName());
	
	// the rigid body of the arrow.
	private RigidBodyControl rigidBody;
	private PhysicsSpace m_PhysicsSpace;
	private boolean hasCollided;
	private float maxFlyingTime = 10f;
	private float timer = 0;
	private Quaternion flyingRotation = new Quaternion();

	@Override
	public void setSpatial(Spatial sp) {
		super.setSpatial(sp);
		if (spatial != null) {
			rigidBody = getComponent(RigidBodyControl.class);
			m_PhysicsSpace = rigidBody.getPhysicsSpace();
			m_PhysicsSpace.addCollisionListener(this);
		}
	}
	
    @Override
    protected void controlUpdate(float tpf) {
        // TODO Auto-generated method stub
    	timer += tpf;
    	
    	if (!hasCollided) {
    		flyingRotation.lookAt(rigidBody.getLinearVelocity(), Vector3f.UNIT_Y);
    		rigidBody.setPhysicsRotation(flyingRotation);
    		// this is to cleanup old bullets that hasn't collided yet (lived more than maxTime)
    		if (timer > maxFlyingTime) {
    			destroy();
    		}
    	}
    }
    
	@Override
	public void collision(PhysicsCollisionEvent event) {
		if (hasCollided) {
			return;
		}
		
		if ( event.getObjectA() == rigidBody || event.getObjectB() == rigidBody ) {
			
			hasCollided = true;

			// Stop the rigidBody in position
//			rigidBody.setLinearVelocity(Vector3f.ZERO);
//			rigidBody.setKinematic(true);
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
			stick(other, hitPoint);
			
			spatial.addControl(new TimerControl(15f) {
				@Override
				public void onTrigger() {
					destroy();
				}
			});
		}
	}
	
	/**
	 * 
	 * @param other
	 * @param hitPoint
	 */
	private void stick(PhysicsCollisionObject other, Vector3f hitPoint) {
		
		if (other.getUserObject() instanceof Node) {
            
			Node gameObject = (Node) other.getUserObject();
			gameObject.worldToLocal(hitPoint, hitPoint);
			gameObject.attachChild(spatial);
        	spatial.setLocalTranslation(hitPoint);
        	
		} else if (other.getUserObject() instanceof BoneLink) {
			BoneLink link = (BoneLink) other.getUserObject();
        	Spatial animRoot = link.getControl().getSpatial();
        	Node attachNode = animRoot.getControl(SkinningControl.class).getAttachmentsNode(link.boneName());
        	System.out.println(link.boneName() + " " + animRoot + "; " + attachNode);
        	
        	attachNode.worldToLocal(hitPoint, hitPoint);
        	attachNode.attachChild(spatial);
        	spatial.setLocalTranslation(hitPoint);
        	
		} else {
			logger.log(Level.WARNING, "Unable to attach the arrow to the hit object: " + other.getUserObject());
		}
	}
	
	//  Destroy everything
	private void destroy() {
		rigidBody.setEnabled(false);
		m_PhysicsSpace.removeCollisionListener(this);
		spatial.removeFromParent();
		logger.log(Level.INFO, "Object Destroyed: {0}", spatial);
	}

}
