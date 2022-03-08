package com.capdevon.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.SkeletonDebugger;
import java.util.Objects;

/**
 *
 * @author capdevon
 */
public class AnimUtils {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private AnimUtils() {
    }

    public static AnimControl getAnimControl(Spatial sp) {
        AnimControl control = findControl(sp, AnimControl.class);
        return Objects.requireNonNull(control, "AnimControl not found: " + sp);
    }

    public static SkeletonControl getSkeletonControl(Spatial sp) {
        SkeletonControl control = findControl(sp, SkeletonControl.class);
        return Objects.requireNonNull(control, "SkeletonControl not found: " + sp);
    }

    public static void copyAnimation(Spatial source, Spatial target) {
        AnimControl from = getAnimControl(source);
        AnimControl to = getAnimControl(target);
        copyAnimation(from, to);
    }

    public static void copyAnimation(AnimControl source, AnimControl target) {
        for (String animName : source.getAnimationNames()) {
            if (!target.getAnimationNames().contains(animName)) {
                System.out.println("Copying Animation: " + animName);
                Animation anim = source.getAnim(animName);
                target.addAnim(anim);
            }
        }
    }

    /**
     * Making the skeleton visible inside animated models can be handy for
     * debugging animations
     */
    public static void addSkeletonDebugger(AssetManager asm, SkeletonControl skControl) {
        Node animRoot = (Node) skControl.getSpatial();
        SkeletonDebugger skDebugger = new SkeletonDebugger(animRoot.getName() + "_Skeleton", skControl.getSkeleton());
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setDepthTest(false);
        skDebugger.setMaterial(mat);
        animRoot.attachChild(skDebugger);
    }

    /**
     * @param <T>
     * @param sp
     * @param clazz
     * @return
     */
    private static <T extends Control> T findControl(Spatial sp, Class<T> clazz) {
        T control = sp.getControl(clazz);
        if (control != null) {
            return control;
        }
        if (sp instanceof Node) {
            for (Spatial child : ((Node) sp).getChildren()) {
                control = findControl(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }
        return null;
    }

}
