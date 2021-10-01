package com.capdevon.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.SkeletonDebugger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author capdevon
 */
public class AnimUtils {

    /**
     * @param from
     * @param to
     */
    public static void copyAnimation(Spatial from, Spatial to) {
        AnimControl acFrom = getAnimControl(from);
        AnimControl acTo = getAnimControl(to);

        for (String animName : acFrom.getAnimationNames()) {
            if (!acTo.getAnimationNames().contains(animName)) {
                System.out.println("Copying Animation: " + animName);
                Animation anim = acFrom.getAnim(animName);
                acTo.addAnim(anim);
            }
        }
    }
        
    /**
     * Making the skeleton visible inside animated models can be handy for
     * debugging animations
     *
     * @param spatial
     */
    public static void addSkeletonDebugger(AssetManager asm, Spatial sp) {
        SkeletonControl skControl = getSkeletonControl(sp);
        addSkeletonDebugger(asm, skControl);
    }
 
    public static void addSkeletonDebugger(AssetManager asm, SkeletonControl skControl) {
        Node owner = (Node) skControl.getSpatial();
        SkeletonDebugger skDebugger = new SkeletonDebugger(owner.getName() + "_Skeleton", skControl.getSkeleton());
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setDepthTest(false);
        skDebugger.setMaterial(mat);
        owner.attachChild(skDebugger);
    }
    
    public static List<String> listBones(Spatial sp) {
        return listBones( getSkeletonControl(sp) );
    }
    
    public static List<String> listBones(SkeletonControl skControl) {
        Skeleton skeleton = skControl.getSkeleton();
        int boneCount = skeleton.getBoneCount();
        
        List<String> lst = new ArrayList<>(boneCount);
        for (int i = 0; i < boneCount; ++i) {
            lst.add(skeleton.getBone(i).getName());
        }
        return lst;
    }
    
    public static Bone getBone(Spatial sp, String boneName) {
        SkeletonControl skControl = getSkeletonControl(sp);
        Bone bone = skControl.getSkeleton().getBone(boneName);
        return Objects.requireNonNull(bone, "Bone not found: " + boneName);
    }

    public static Node getAttachments(Spatial sp, String boneName) {
        SkeletonControl skControl = getSkeletonControl(sp);
        Node attachedNode = skControl.getAttachmentsNode(boneName);
        return Objects.requireNonNull(attachedNode, "AttachedNode not found: " + boneName);
    }

    public static SkeletonControl getSkeletonControl(Spatial sp) {
        SkeletonControl control = findControl(sp, SkeletonControl.class);
        return Objects.requireNonNull(control, "SkeletonControl not found: " + sp);
    }

    public static AnimControl getAnimControl(Spatial sp) {
        AnimControl control = findControl(sp, AnimControl.class);
        return Objects.requireNonNull(control, "AnimControl not found: " + sp);
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
