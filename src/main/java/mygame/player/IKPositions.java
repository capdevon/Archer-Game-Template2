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
            Vector3f position = new Vector3f(0f, 1f, 0.18f);
            Vector3f angles = new Vector3f(-90, 90, 10);
            float scale = 1f;
            return newTransform(position, angles, scale);
        }
    },
    Bow {
        @Override
        public Transform getTransform() {
            Vector3f position = new Vector3f(-0.02f, 0.06f, 0.04f);
            Vector3f angles = new Vector3f(-90, 90, 0);
            float scale = 1f;
            return newTransform(position, angles, scale);
        }
    };

    private static Transform newTransform(Vector3f position, Vector3f angles, float scale) {
        angles.multLocal(FastMath.DEG_TO_RAD);
        Transform t = new Transform();
        t.setRotation(new Quaternion().fromAngles(angles.x, angles.y, angles.z).normalizeLocal());
        t.setTranslation(position);
        t.setScale(scale);
        return t;
    }

    public abstract Transform getTransform();

}
