/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.anim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimTrack;
import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.TransformTrack;
import com.jme3.anim.util.HasLocalTransform;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.SafeArrayList;

/**
 *
 * @author capdevon
 */
public class AnimUtils3 {

    /**
     * @param from
     * @param to 
     */
	public static void copyAnimation(Spatial from, Spatial to) {

		AnimComposer source = getAnimControl(from);
		AnimComposer target = getAnimControl(to);
		Armature targetArmature = getSkeletonControl(to).getArmature();

		copyAnimation(source, target, targetArmature);
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param targetArmature
	 */
	public static void copyAnimation(AnimComposer source, AnimComposer target, Armature targetArmature) {
		for (String animName : source.getAnimClipsNames()) {
			if (!target.getAnimClipsNames().contains(animName)) {
				System.out.println("Copying Animation: " + animName);

				AnimClip clip = new AnimClip(animName);
				clip.setTracks( copyAnimTracks(source.getAnimClip(animName), targetArmature) );
				target.addAnimClip(clip);
			}
		}
	}
	
	/**
	 * 
	 * @param sourceClip
	 * @param targetArmature
	 * @return
	 */
	private static AnimTrack[] copyAnimTracks(AnimClip sourceClip, Armature targetArmature) {
		
		SafeArrayList<AnimTrack> tracks = new SafeArrayList<>(AnimTrack.class);
		
		for (AnimTrack track : sourceClip.getTracks()) {

			TransformTrack tt = (TransformTrack) track;
			
			if (tt.getTarget() instanceof Joint) {
				Joint joint = (Joint) tt.getTarget();
				HasLocalTransform target = targetArmature.getJoint(joint.getName());
//				TransformTrack newTrack = new TransformTrack(target, tt.getTimes(), tt.getTranslations(), tt.getRotations(), tt.getScales());
				TransformTrack newTrack = tt.jmeClone(); // optimization
				newTrack.setTarget(target);
				tracks.add(newTrack);
			}
		}

		return tracks.getArray();
	}
    
    public static AnimComposer getAnimControl(Spatial sp) {
    	AnimComposer control = findControl(sp, AnimComposer.class);
        if (control == null) {
            throw new IllegalArgumentException("AnimComposer not found: " + sp);
        }
        return control;
    }
    
    public static SkinningControl getSkeletonControl(Spatial sp) {
    	SkinningControl control = findControl(sp, SkinningControl.class);
        if (control == null) {
            throw new IllegalArgumentException("SkinningControl not found: " + sp);
        }
        return control;
    }
    
    public static Joint findBone(Spatial sp, String boneName) {
    	SkinningControl skControl = getSkeletonControl(sp);
    	Joint bone = skControl.getArmature().getJoint(boneName);
    	if (bone == null) {
    		throw new IllegalArgumentException("Armature Joint not found: " + boneName);
    	}
    	return bone;
    }
    
	public static Node getAttachments(Spatial sp, String boneName) {
		SkinningControl skControl = getSkeletonControl(sp);
		Node attachedNode = skControl.getAttachmentsNode(boneName);
		if (attachedNode == null) {
			throw new IllegalArgumentException("AttachedNode not found: " + boneName);
		}
		return attachedNode;
	}
	
	public static List<String> listBones(Spatial sp) {
		SkinningControl skControl = getSkeletonControl(sp);
		List<String> lst = listBones(skControl.getArmature());
		Collections.sort(lst);
		return lst;
	}
	
	public static List<String> listBones(Armature skeleton) {
		int boneCount = skeleton.getJointCount();
		List<String> lst = new ArrayList<>(boneCount);

		for (Joint bone : skeleton.getJointList()) {
			lst.add(bone.getName());
		}

		return lst;
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
