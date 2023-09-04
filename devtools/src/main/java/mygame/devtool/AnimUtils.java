package mygame.devtool;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class AnimUtils {

    private static final Logger logger = Logger.getLogger(AnimUtils.class.getName());

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private AnimUtils() {}

    /**
     *
     * @param newClipName desired name for the new clip
     * @param sourceClip
     * @param targetArmature
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static AnimClip retargetClip(String newClipName, AnimClip sourceClip, Armature targetArmature) {
        AnimClip copy = new AnimClip(newClipName);
        AnimTrack[] tracks = copyAnimTracks(sourceClip, targetArmature);
        copy.setTracks(tracks);
        
        logger.log(Level.INFO, "Copy AnimClip {0} Tracks {1}", new Object[]{newClipName, tracks.length});
        return copy;
    }

    /**
     *
     * @param sourceClip
     * @param targetArmature
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static AnimTrack[] copyAnimTracks(AnimClip sourceClip, Armature targetArmature) {

        SafeArrayList<AnimTrack> tracks = new SafeArrayList<>(AnimTrack.class);

        for (AnimTrack track : sourceClip.getTracks()) {

            TransformTrack tt = (TransformTrack) track;
            HasLocalTransform target = null;

            if (tt.getTarget() instanceof Node) {
                Node node = (Node) tt.getTarget();
                target = targetArmature.getJoint(node.getName());

            } else if (tt.getTarget() instanceof Joint) {
                Joint joint = (Joint) tt.getTarget();
                target = targetArmature.getJoint(joint.getName());
            }

            if (target != null) {
                TransformTrack newTrack = tt.jmeClone(); //optimization
                newTrack.setTarget(target);
                tracks.add(newTrack);

            } else {
                logger.log(Level.WARNING, "Joint not found in the target Armature: {0}", tt.getTarget());
            }
        }

        return tracks.getArray();
    }

    public static AnimComposer getAnimComposer(Spatial sp) {
        AnimComposer control = findControl(sp, AnimComposer.class);
        return Objects.requireNonNull(control, "AnimComposer not found: " + sp);
    }

    public static SkinningControl getSkinningControl(Spatial sp) {
        SkinningControl control = findControl(sp, SkinningControl.class);
        return Objects.requireNonNull(control, "SkinningControl not found: " + sp);
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
