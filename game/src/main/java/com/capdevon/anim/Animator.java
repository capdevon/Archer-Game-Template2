package com.capdevon.anim;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimationMask;
import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugger;

/**
 *
 * @author capdevon
 */
public class Animator extends AdapterControl {

    private static final Logger logger = Logger.getLogger(Animator.class.getName());

    private AnimComposer animComposer;
    private SkinningControl skinningControl;
    private String currentAnim;
    private ArrayList<ActionAnimEventListener> listeners = new ArrayList<>();
    private ArmatureDebugger debugger;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);

        if (spatial != null) {
            animComposer = getComponentInChildren(AnimComposer.class);
            skinningControl = getComponentInChildren(SkinningControl.class);
        }
    }

    public void createDefaultActions() {
        for (AnimClip clip : animComposer.getAnimClips()) {
            actionCycleDone(clip.getName(), true);
        }
    }

    /**
     * @param anim (not null)
     */
    public void actionCycleDone(Animation3 anim) {
        String animName = anim.getName();
        boolean isLooping = anim.isLooping();
        actionCycleDone(animName, isLooping).setSpeed(anim.getSpeed());
    }

    public Action actionCycleDone(String animName, boolean loop) {
        // Get action registered with specified name. It will make a new action if there isn't any.
        Action action = animComposer.action(animName);
        Tween doneTween = Tweens.callMethod(this, "notifyAnimCycleDone", animName, loop);
        // Register custom action with specified name.
        animComposer.actionSequence(animName, action, doneTween);
        return action;
    }

    /**
     * Run an action with specified anim params.
     */
    public void setAnimation(Animation3 anim) {
        setAnimation(anim.getName(), anim.getLayer());
    }

    /**
     * Run an action on specified layer.
     */
    public void setAnimation(String animName, String layerName) {
        if (!animName.equals(currentAnim)) {
            currentAnim = animName;
            animComposer.setCurrentAction(currentAnim, layerName);
            notifyAnimChange(currentAnim);
        }
    }

    public void crossFade(Animation3 anim) {
        crossFade(anim.getName(), anim.getLayer());
    }

    public void crossFade(String animName, String layerName) {
        currentAnim = animName;
        double dt = animComposer.getTime(layerName);
        animComposer.setCurrentAction(currentAnim, layerName);
        animComposer.setTime(layerName, dt);
        notifyAnimChange(currentAnim);
    }

    public String getCurrentAnimName() {
        return currentAnim;
    }

    public Spatial getAnimRoot() {
        return animComposer.getSpatial();
    }

    public Joint getJoint(String name) {
        return skinningControl.getArmature().getJoint(name);
    }

    public Node getAttachments(String jointName) {
        return skinningControl.getAttachmentsNode(jointName);
    }
    
    public Armature getArmature() {
        return skinningControl.getArmature();
    }

    /**
     * Set mask with specified layer name. 
     * It will make a new layer if there isn't any.
     *
     * @param layerName the desired name for the new layer
     * @param mask the desired mask for the new layer (alias created)
     */
    public void setAnimMask(String layerName, AnimationMask mask) {
        animComposer.makeLayer(layerName, mask);
    }

    public void disableArmatureDebug() {
        debugger.removeFromParent();
        debugger = null;
    }

    public void enableArmatureDebug(AssetManager asm) {
        if (debugger == null) {
            Node animRoot = (Node) skinningControl.getSpatial();
            String name = animRoot.getName() + "_Armature";
            Armature armature = skinningControl.getArmature();
            debugger = new ArmatureDebugger(name, armature, armature.getJointList());
            debugger.setMaterial(createWireMaterial(asm));
            animRoot.attachChild(debugger);
        }
    }

    private Material createWireMaterial(AssetManager asm) {
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        return mat;
    }

    /**
     * Adds a new listener to receive animation related events.
     */
    public void addListener(ActionAnimEventListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("The given listener is already registered at this Animator");
        }

        listeners.add(listener);
    }

    /**
     * Removes the given listener from listening to events.
     */
    public void removeListener(ActionAnimEventListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException("The given listener is not registered at this Animator");
        }
    }

    /**
     * Clears all the listeners added to this <code>Animator</code>
     */
    public void clearListeners() {
        listeners.clear();
    }

    void notifyAnimChange(String name) {
        for (ActionAnimEventListener listener : listeners) {
            listener.onAnimChange(animComposer, name);
        }
    }

    void notifyAnimCycleDone(String name, boolean loop) {
        for (ActionAnimEventListener listener : listeners) {
            listener.onAnimCycleDone(animComposer, name, loop);
        }
    }

}
