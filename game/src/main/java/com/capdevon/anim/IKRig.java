package com.capdevon.anim;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public class IKRig extends AbstractControl {

    private final AvatarMask mask;
    private Map<String, IKControl> ikControls = new HashMap<>();

    /**
     * Instantiate an IKRig.
     *
     * @param mask
     */
    public IKRig(AvatarMask mask) {
        this.mask = mask;
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);

        if (spatial != null) {
            SkinningControl skControl = spatial.getControl(SkinningControl.class);
            Objects.requireNonNull(skControl, "SkinningControl not found: " + spatial);

            Armature armature = skControl.getArmature();
            for (Joint joint : armature.getJointList()) {
                ikControls.put(joint.getName(), new IKControl(mask, joint));
            }
        }
    }

    public void setAvatarIKRotation(String jointName, Quaternion rotation) {
        getIKControl(jointName).setIKRotation(rotation);
    }

    public void setAvatarIKPosition(String jointName, Vector3f position) {
        getIKControl(jointName).setIKPosition(position);
    }

    public void setAvatarIKScale(String jointName, Vector3f scale) {
        getIKControl(jointName).setIKScale(scale);
    }

    public void setAvatarIKWeight(String jointName, float weight) {
        getIKControl(jointName).setWeight(weight);
    }

    public void setAvatarIKActive(String jointName, boolean active) {
        getIKControl(jointName).setUserControl(active);
    }

    public IKControl getIKControl(String jointName) {
        IKControl result = ikControls.get(jointName);
        if (result == null) {
            throw new IllegalArgumentException("Unknown IKControl " + jointName);
        }
        return result;
    }

    public Set<String> getIKControlNames() {
        return Collections.unmodifiableSet(ikControls.keySet());
    }

    @Override
    protected void controlUpdate(float tpf) {
        for (IKControl ik : ikControls.values()) {
            ik.update();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
