package com.capdevon.anim;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.anim.tween.Tween;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AudioTrack;
import com.jme3.animation.EffectTrack;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author capdevon
 */
public class TrackUtils {

    private static final Logger logger = Logger.getLogger(TrackUtils.class.getName());
    
    private TrackUtils() {
    	// private constructor.
    }

    public static void addAudioTrack(Spatial sp, AudioNode audio, String animName) {
        addAudioTrack(sp, audio, animName, 0f);
    }

    /**
     * @param sp
     * @param audio
     * @param animName
     * @param startOffset
     */
    public static void addAudioTrack(Spatial sp, AudioNode audio, String animName, float startOffset) {
        Animation anim = getAnimation(sp, animName);
        AudioTrack track = new AudioTrack(audio, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    public static void addEffectTrack(Spatial sp, ParticleEmitter emitter, String animName) {
        addEffectTrack(sp, emitter, animName, 0f);
    }

    /**
     *
     * @param sp
     * @param emitter
     * @param animName
     * @param startOffset
     */
    public static void addEffectTrack(Spatial sp, ParticleEmitter emitter, String animName, float startOffset) {
        Animation anim = getAnimation(sp, animName);
        EffectTrack track = new EffectTrack(emitter, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    public static void addActionTrack(Spatial sp, Tween tween, String animName) {
        addCallbackTrack(sp, tween, animName, 0f);
    }

    /**
     *
     * @param sp
     * @param tween
     * @param animName
     * @param startOffset
     */
    public static void addCallbackTrack(Spatial sp, Tween tween, String animName, float startOffset) {
        Animation anim = getAnimation(sp, animName);
        CallbackTrack track = new CallbackTrack(tween, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    private static Animation getAnimation(Spatial sp, String animName) {
        AnimControl control = findControl(sp, AnimControl.class);
        if (control == null) {
            throw new IllegalArgumentException("AnimControl not found: " + sp);
        }
        Animation anim = control.getAnim(animName);
        if (anim == null) {
            throw new IllegalArgumentException("Animation not found: " + sp);
        }

        logger.log(Level.INFO, "Anim: {0}, length: {1}", new Object[] {
            animName,
            anim.getLength()
        });
        return anim;
    }

    /**
     * @param <T>
     * @param sp
     * @param clazz
     * @return
     */
    private static <T extends Control> T findControl(Spatial sp, Class <T> clazz) {
        T control = sp.getControl(clazz);
        if (control != null) {
            return control;
        }
        if (sp instanceof Node) {
            for (Spatial child: ((Node) sp).getChildren()) {
                control = findControl(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }
        return null;
    }

}
