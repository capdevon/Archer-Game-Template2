package com.capdevon.physx;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class RaycastHit {

	public PhysicsCollisionObject rigidbody;
	public CollisionShape collider;
	public Spatial userObject;
	public float distance;
	public Vector3f normal = new Vector3f();
	public Vector3f point = new Vector3f();

	public void clear() {
		rigidbody = null;
		collider = null;
		userObject = null;
		distance = Float.NaN;
		point.set(Vector3f.NAN);
		normal.set(Vector3f.NAN);
	}
    
	@Override
	public String toString() {
		return "RaycastHit [rigidbody=" + toHexString(rigidbody) 
				+ ", collider=" + toHexString(collider)
				+ ", userObject=" + toHexString(userObject)
				+ ", distance=" + distance 
				+ ", normal=" + normal 
				+ ", point=" + point 
				+ "]";
	}

	private String toHexString(Object obj) {
		if (obj != null)
			return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
		return null;
	}

}
