package com.capdevon.physx;

import com.capdevon.engine.GameObject;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import jme3utilities.math.MyVector3f;

/**
 * @author capdevon
 */
public class RaycastHit {

    public PhysicsCollisionObject rigidBody;
    public CollisionShape collider;
    public Spatial gameObject;
    public Vector3f normal;
    public Vector3f point;
    public float distance;
    
    public RaycastHit() {
        this.normal = new Vector3f();
        this.point  = new Vector3f();
    }
    
    protected void set(Vector3f beginVec, Vector3f finalVec, PhysicsRayTestResult ray) {
        PhysicsCollisionObject pco = ray.getCollisionObject();
        float hf = ray.getHitFraction();

        rigidBody = pco;
        collider = pco.getCollisionShape();
        gameObject = GameObject.findGameObject(pco);
        distance = finalVec.subtract(beginVec).length() * hf;
        point.interpolateLocal(beginVec, finalVec, hf);
        ray.getHitNormalLocal(normal);
    }
    
    protected void set(Vector3f beginVec, Vector3f finalVec, PhysicsSweepTestResult tr) {
        PhysicsCollisionObject pco = tr.getCollisionObject();

        rigidBody = pco;
        collider = pco.getCollisionShape();
        gameObject = GameObject.findGameObject(pco);
        MyVector3f.lerp(tr.getHitFraction(), beginVec, finalVec, point);
        tr.getHitNormalLocal(normal);
        distance = beginVec.distance(point);
    }
    
    public void clear() {
        rigidBody = null;
        gameObject = null;
        distance = Float.NaN;
        point.set(Vector3f.NAN);
        normal.set(Vector3f.NAN);
    }

    @Override
    public String toString() {
        return "RaycastHit [rigidbody=" + toHexString(rigidBody)
                + ", collider=" + toHexString(collider)
                + ", gameObject=" + gameObject
                + ", distance=" + distance
                + ", normal=" + normal
                + ", point=" + point
                + "]";
    }

    private String toHexString(Object obj) {
        if (obj != null) {
            return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
        }
        return null;
    }

}
