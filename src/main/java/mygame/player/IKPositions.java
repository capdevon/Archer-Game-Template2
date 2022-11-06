/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.player;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;

/**
 *
 * @author capdevon
 */
public class IKPositions {

    public static final Transform[] NONE = new Transform[1];
    public static final Transform[] RIFLE = new Transform[1];
    public static final Transform[] ARCHER = new Transform[3];
    
    static {
        NONE[0] = new Transform();
    }

    static {
        // Hunting Rifle
        float x = FastMath.DEG_TO_RAD * -85;
        float y = FastMath.DEG_TO_RAD * -180;
        RIFLE[0] = new Transform();
        RIFLE[0].setRotation(new Quaternion().fromAngles(x, y, 0).normalizeLocal());
        RIFLE[0].setTranslation(-2, 35, 1);
        RIFLE[0].setScale(3.8f);
    }

    static {
        // Arrow
        float x = FastMath.DEG_TO_RAD * -95;
        float z = FastMath.DEG_TO_RAD * 8;
        ARCHER[0] = new Transform();
        ARCHER[0].setRotation(new Quaternion().fromAngles(x, 0, z).normalizeLocal());
        ARCHER[0].setTranslation(-15f, 105f, -2f);
        ARCHER[0].setScale(100f);

        // Bow
        x = FastMath.DEG_TO_RAD * -90;
        ARCHER[1] = new Transform();
        ARCHER[1].setRotation(new Quaternion().fromAngles(x, 0, 0).normalizeLocal());
        ARCHER[1].setTranslation(-2f, 10f, -1f);
        ARCHER[1].setScale(100f);

        // Quiver
        z = FastMath.DEG_TO_RAD * -15;
        ARCHER[2] = new Transform();
//        BOW[2].setRotation(new Quaternion().fromAngles(0, 0, z).normalizeLocal());
//        BOW[2].setTranslation(-10f, -10f, -30f);
        ARCHER[2].setScale(100f);
    }

}
