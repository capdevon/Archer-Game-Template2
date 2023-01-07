package com.capdevon.anim;

import com.jme3.anim.Joint;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author capdevon
 */
public class IKControl {

    private final AvatarMask mask;
    private final Joint joint;
    private final Vector3f ikPosition = new Vector3f();
    private final Quaternion ikRotation = new Quaternion();
    private final Vector3f ikScale = new Vector3f();
    private float weight = 1f;
    private boolean userControl;

    /**
     * Instantiate an IKControl.
     *
     * @param mask
     * @param joint
     */
    protected IKControl(AvatarMask mask, Joint joint) {
        this.mask = mask;
        this.joint = joint;
        Transform tr = joint.getInitialTransform();
        tr.getTranslation(ikPosition);
        tr.getRotation(ikRotation);
        tr.getScale(ikScale);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Returns true if this joint can be directly manipulated by the user.
     *
     * @return true if it can be manipulated
     */
    public boolean hasUserControl() {
        return userControl;
    }

    /**
     * If enabled, user can control joint transform. Animation transforms are
     * not applied to this bone when enabled.
     *
     * @param enable true for direct control, false for canned animations
     */
    public void setUserControl(boolean enable) {
        this.userControl = enable;
        if (enable) {
            mask.removeJoints(joint.getName());
        } else {
            mask.addJoints(joint.getName());
        }
    }

    public Vector3f getIKPosition() {
        return ikPosition;
    }

    public void setIKPosition(Vector3f ikPosition) {
        this.ikPosition.set(ikPosition);
    }

    public Quaternion getIKRotation() {
        return ikRotation;
    }

    public void setIKRotation(Quaternion ikRotation) {
        this.ikRotation.set(ikRotation);
    }

    public Vector3f getIKScale() {
        return ikScale;
    }

    public void setIKScale(Vector3f ikScale) {
        this.ikScale.set(ikScale);
    }

    protected void update() {
        if (userControl) {
            Transform tr = joint.getInitialTransform();
            joint.getLocalRotation().slerp(tr.getRotation(), ikRotation, weight);
            joint.getLocalTranslation().interpolateLocal(tr.getTranslation(), ikPosition, weight);
            joint.getLocalScale().interpolateLocal(tr.getScale(), ikScale, weight);
        }
    }

}
