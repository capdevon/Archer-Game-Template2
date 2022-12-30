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
    private boolean userControl;

    /**
     * Constructor.
     *
     * @param mask
     * @param joint
     */
    public IKControl(AvatarMask mask, Joint joint) {
        this.mask = mask;
        this.joint = joint;
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
     * not applied to this joint when enabled.
     *
     * @param enable true for direct control, false for canned animations
     */
    public void setUserControl(boolean enable) {
        this.userControl = enable;

        if (enable && mask.contains(joint)) {
            mask.removeJoints(joint.getName());

        } else if (!enable && !mask.contains(joint)) {
            mask.addJoints(joint.getName());
        }
    }

    public void setIKRotation(Quaternion rotation) {
        joint.setLocalRotation(rotation);
    }

    public void setIKPosition(Vector3f position) {
        joint.setLocalTranslation(position);
    }

    public void setIKScale(Vector3f scale) {
        joint.setLocalScale(scale);
    }

    public void setIKTransform(Transform transform) {
        joint.setLocalTransform(transform);
    }

}
