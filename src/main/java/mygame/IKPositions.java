/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;

/**
 *
 * @author chris
 */
public class IKPositions {

    public static final Transform[] NONE = new Transform[1];
    public static final Transform[] M4 = new Transform[1];
    public static final Transform[] SHOTGUN = new Transform[1];
    public static final Transform[] RIFLE = new Transform[1];
    public static final Transform[] BOW = new Transform[3];
    
    static {
        NONE[0] = new Transform();
    }

    static {
        // M4 weapon configuration
        float x = FastMath.DEG_TO_RAD * -85;
        float y = FastMath.DEG_TO_RAD * -180;
        M4[0] = new Transform();
        M4[0].setRotation(new Quaternion().fromAngles(x, y, 0).normalizeLocal());
        M4[0].setTranslation(-2, 34, 2.5f);
        M4[0].setScale(3.6f);
    }

    static {
        // Shotgun weapon configuration
        float x = FastMath.DEG_TO_RAD * -85;
        float y = FastMath.DEG_TO_RAD * -180;
        SHOTGUN[0] = new Transform();
        SHOTGUN[0].setRotation(new Quaternion().fromAngles(x, y, 0).normalizeLocal());
        SHOTGUN[0].setTranslation(-2, 32, 0.8f);
        SHOTGUN[0].setScale(3.6f);
    }

    static {
        // Hunting Rifle weapon configuration
        float x = FastMath.DEG_TO_RAD * -85;
        float y = FastMath.DEG_TO_RAD * -180;
        RIFLE[0] = new Transform();
        RIFLE[0].setRotation(new Quaternion().fromAngles(x, y, 0).normalizeLocal());
        RIFLE[0].setTranslation(-2, 35, 1);
        RIFLE[0].setScale(3.8f);
    }

    static {
        // Bow weapon configuration
        float x = FastMath.DEG_TO_RAD * -95;
        float z = FastMath.DEG_TO_RAD * 6;
        BOW[0] = new Transform();
//        BOW[0].setRotation(new Quaternion().fromAngles(x, 0, z).normalizeLocal());
//        BOW[0].setTranslation(-8, 50, -2);
        BOW[0].setScale(100f);

        x = FastMath.DEG_TO_RAD * -90;
        BOW[1] = new Transform();
//        BOW[1].setRotation(new Quaternion().fromAngles(x, 0, 0).normalizeLocal());
//        BOW[1].setTranslation(-5f, -25f, 0f);
        BOW[1].setScale(100f);

        z = FastMath.DEG_TO_RAD * -15;
        BOW[2] = new Transform();
//        BOW[2].setRotation(new Quaternion().fromAngles(0, 0, z).normalizeLocal());
//        BOW[2].setTranslation(-10f, -10f, -30f);
        BOW[2].setScale(100f);
    }

}
