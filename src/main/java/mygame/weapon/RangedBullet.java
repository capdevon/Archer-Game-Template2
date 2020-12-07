package mygame.weapon;

import com.jme3.app.Application;

public abstract class RangedBullet extends PrefabComponent {
	
    /**
     * The name in GUI
     */
    String name;
    /**
     * Mass of the bullet being shot. Affects velocity.
     */
    float mass = 0.1f;

    /**
     * A bullet for a RangedWeapon
     */
    public RangedBullet(Application app) {
    	super(app);
    }
    
}