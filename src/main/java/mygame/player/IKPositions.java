package mygame.player;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author capdevon
 */
public enum IKPositions {

    Arrow {
        @Override
        public Transform getTransform() {
            Vector3f angles = new Vector3f(-90, 90, 10).multLocal(FastMath.DEG_TO_RAD);
            Transform t = new Transform();
            t.setRotation(new Quaternion().fromAngles(angles.x, angles.y, angles.z).normalizeLocal());
            t.setTranslation(0f, 1f, 0.18f);
            t.setScale(1f);
            return t;
        }
    },
    Bow {
        @Override
        public Transform getTransform() {
            Vector3f angles = new Vector3f(-90, 90, 0).multLocal(FastMath.DEG_TO_RAD);
            Transform t = new Transform();
            t.setRotation(new Quaternion().fromAngles(angles.x, angles.y, angles.z).normalizeLocal());
            t.setTranslation(-0.02f, 0.06f, 0.04f);
            t.setScale(1f);
            return t;
        }
    };

    public abstract Transform getTransform();

}
