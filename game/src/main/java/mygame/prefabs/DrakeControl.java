package mygame.prefabs;

import com.jme3.bullet.RotationOrder;
import com.jme3.bullet.animation.CenterHeuristic;
import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.LinkConfig;
import com.jme3.bullet.animation.MassHeuristic;
import com.jme3.bullet.animation.RangeOfMotion;
import com.jme3.bullet.animation.ShapeHeuristic;
import com.jme3.math.Vector3f;

/**
 * A DynamicAnimControl configured for the Drake model.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class DrakeControl extends DynamicAnimControl {
    /**
     * Instantiate a new control.
     */
    public DrakeControl() {
        super();
        LinkConfig hull = new LinkConfig(1f, MassHeuristic.Density,
                ShapeHeuristic.VertexHull, new Vector3f(1f, 1f, 1f),
                CenterHeuristic.Mean, RotationOrder.XZY);

        super.setConfig(torsoName, hull);
        super.setIgnoredHops(2);

        super.link("mixamorig:Spine", hull,
                new RangeOfMotion(1f, -0.4f, 0.4f, -0.4f, 0.4f, -0.4f));
        super.link("mixamorig:Spine1", hull,
                new RangeOfMotion(0.4f, 0.2f, 0.2f));
        super.link("mixamorig:Spine2", hull,
                new RangeOfMotion(0.4f, 0.2f, 0.2f));

        super.link("mixamorig:Neck", hull,
                new RangeOfMotion(1f, 0.5f, 0.7f));
        super.link("mixamorig:Head", hull,
                new RangeOfMotion(1f, 0.5f, 0.7f));

        super.link("mixamorig:LeftShoulder", hull,
                new RangeOfMotion(0.5f, -0.5f, 0f, 0f, 0.6f, -0.3f));
        super.link("mixamorig:LeftArm", hull,
                new RangeOfMotion(1f, -1.6f, 1f, -1f, 1.6f, -1f));
        super.link("mixamorig:LeftForeArm", hull,
                new RangeOfMotion(0f, 0f, 1f, -1f, 2f, 0f));
        super.link("mixamorig:LeftHand", hull,
                new RangeOfMotion(0.8f, 0f, 0.2f));

        super.link("mixamorig:RightShoulder", hull,
                new RangeOfMotion(0.5f, -0.5f, 0f, 0f, 0.3f, -0.6f));
        super.link("mixamorig:RightArm", hull,
                new RangeOfMotion(1.6f, -1f, 1f, -1f, 1f, -1.6f));
        super.link("mixamorig:RightForeArm", hull,
                new RangeOfMotion(0f, 0f, 1f, -1f, 0f, -2f));
        super.link("mixamorig:RightHand", hull,
                new RangeOfMotion(0.8f, 0f, 0.2f));

        super.link("mixamorig:LeftUpLeg", hull,
                new RangeOfMotion(0.4f, -1f, 0.4f, -0.4f, 1f, -0.6f));
        super.link("mixamorig:LeftLeg", hull,
                new RangeOfMotion(0f, -2f, 0.6f, -0.6f, 0f, 0f));
        super.link("mixamorig:LeftFoot", hull,
                new RangeOfMotion(0.4f, 0.4f, 0f));

        super.link("mixamorig:RightUpLeg", hull,
                new RangeOfMotion(0.4f, -1f, 0.4f, -0.4f, 0.6f, -1f));
        super.link("mixamorig:RightLeg", hull,
                new RangeOfMotion(0f, -2f, 0.6f, -0.6f, 0f, 0f));
        super.link("mixamorig:RightFoot", hull,
                new RangeOfMotion(0.4f, 0.4f, 0f));
    }
}
