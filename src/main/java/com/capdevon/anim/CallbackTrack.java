package com.capdevon.anim;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme3.anim.tween.Tween;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Track;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

/**
 *
 * @author capdevon
 */
public class CallbackTrack implements Track, JmeCloneable {

    private static final Logger logger = Logger.getLogger(CallbackTrack.class.getName());
    
    private Tween tween;
    private float startOffset = 0;
    private float length = 0;
    private boolean initialized = false;
    private boolean started = false;

    //Animation listener to reset the tween when the animation ends or is changed
    private class OnEndListener implements AnimEventListener {

        @Override
        public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
            stop();
        }

        @Override
        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        }
    }

    /**
     * constructor for serialization only
     */
    protected CallbackTrack() {
    }

    /**
     * Creates an ActionTrack
     *
     * @param tween the Tween
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     */
    public CallbackTrack(Tween tween, float length) {
        this.tween = tween;
        this.length = length;
    }

    /**
     * Creates an ActionTrack
     *
     * @param tween the Tween
     * @param length the length of the track (usually the length of the
     * animation you want to add the track to)
     * @param startOffset the time in second when the tween will be played after
     * the animation starts (default is 0)
     */
    public CallbackTrack(Tween tween, float length, float startOffset) {
        this(tween, length);
        this.startOffset = startOffset;
    }

    /**
     * Internal use only
     *
     * @see Track#setTime(float, float, com.jme3.animation.AnimControl,
     * com.jme3.animation.AnimChannel, com.jme3.util.TempVars)
     */
    @Override
    public void setTime(float time, float weight, AnimControl control, AnimChannel channel, TempVars vars) {

        if (time >= length) {
            return;
        }
        if (!initialized) {
            control.addListener(new OnEndListener());
            initialized = true;
        }
        if (!started && time >= startOffset) {
            started = true;
            tween.interpolate(1);
        }
    }

    private void stop() {
        started = false;
    }

    /**
     * Return the length of the track
     *
     * @return length of the track
     */
    @Override
    public float getLength() {
        return length;
    }

    @Override
    public float[] getKeyFrameTimes() {
        return new float[] { startOffset };
    }
    
    /**
     * Clone this track
     *
     * @return a new track
     */
    @Override
    public Track clone() {
        return new CallbackTrack(tween, length, startOffset);
    }

    @Override   
    public Object jmeClone() {
        try {
            return super.clone();
        } catch( CloneNotSupportedException e ) {
            throw new RuntimeException("Error cloning", e);
        }
    }     

    @Override   
    public void cloneFields( Cloner cloner, Object original ) {
        // Duplicating the old cloned state from cloneForSpatial()
        this.initialized = false;
        this.started = false;
        this.tween = cloner.clone(tween);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
