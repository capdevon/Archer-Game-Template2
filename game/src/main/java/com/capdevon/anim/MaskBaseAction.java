package com.capdevon.anim;

import com.jme3.anim.AnimationMask;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BaseAction;

public class MaskBaseAction extends BaseAction {

    private boolean maskPropagationEnabled = true;

    public MaskBaseAction(Tween tween) {
        super(tween);
    }

    /**
     * @return true if mask propagation to child actions is enabled else returns
     * false
     */
    public boolean isMaskPropagationEnabled() {
        return maskPropagationEnabled;
    }

    /**
     *
     * @param maskPropagationEnabled If true, then mask set by AnimLayer will be
     * forwarded to all child actions (Default=true)
     */
    public void setMaskPropagationEnabled(boolean maskPropagationEnabled) {
        this.maskPropagationEnabled = maskPropagationEnabled;
    }

    @Override
    public void setMask(AnimationMask mask) {
        super.setMask(mask);

        if (maskPropagationEnabled) {
            for (Action action : actions) {
                action.setMask(mask);
            }
        }
    }

}
