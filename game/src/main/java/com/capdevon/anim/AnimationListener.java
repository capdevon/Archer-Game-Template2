package com.capdevon.anim;

import com.jme3.anim.AnimComposer;

/**
 *
 * @author capdevon
 */
public interface AnimationListener {

    /**
     * For non-looping animations, this event is invoked when the animation is
     * finished playing. For looping animations, this event is invoked each time
     * the animation is restarted.
     */
    public void onAnimCycleDone(AnimComposer animComposer, String animName, boolean loop);

    /**
     * Invoked when an animation is set to play by the user.
     */
    public void onAnimChange(AnimComposer animComposer, String animName);
}
