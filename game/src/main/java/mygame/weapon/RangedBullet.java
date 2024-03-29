package mygame.weapon;

import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;

/**
 *
 * @author capdevon
 */
public abstract class RangedBullet extends PrefabComponent {

    /**
     * The name in GUI
     */
    public String name;
    /**
     * Mass of the bullet being shot. Affects velocity.
     */
    public float mass = 0.1f;

    /**
     * A bullet for a RangedWeapon
     */
    public RangedBullet(Application app) {
    	super(app);
    }

}