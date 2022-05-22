package com.capdevon.anim;

import com.jme3.anim.tween.Tween;
import com.jme3.animation.Animation;
import com.jme3.animation.AudioTrack;
import com.jme3.animation.EffectTrack;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;

/**
 *
 * @author capdevon
 */
public class TrackUtils {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private TrackUtils() {}

    /**
     * 
     * @param anim
     * @param audio
     * @param startOffset
     */
    @SuppressWarnings("deprecation")
    public static void addAudioTrack(Animation anim, AudioNode audio, float startOffset) {
        AudioTrack track = new AudioTrack(audio, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    @SuppressWarnings("deprecation")
    public static void addAudioTrack(Animation anim, AudioNode audio) {
        addAudioTrack(anim, audio, 0f);
    }

    /**
     * 
     * @param anim
     * @param emitter
     * @param startOffset
     */
    @SuppressWarnings("deprecation")
	public static void addEffectTrack(Animation anim, ParticleEmitter emitter, float startOffset) {
        EffectTrack track = new EffectTrack(emitter, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    @SuppressWarnings("deprecation")
    public static void addEffectTrack(Animation anim, ParticleEmitter emitter) {
        addEffectTrack(anim, emitter, 0f);
    }

    /**
     * 
     * @param anim
     * @param tween
     * @param startOffset
     */
    @SuppressWarnings("deprecation")
    public static void addCallbackTrack(Animation anim, Tween tween, float startOffset) {
        CallbackTrack track = new CallbackTrack(tween, anim.getLength(), startOffset);
        anim.addTrack(track);
    }

    @SuppressWarnings("deprecation")
    public static void addCallbackTrack(Animation anim, Tween tween) {
        addCallbackTrack(anim, tween, 0f);
    }

}