package mygame.player;

import com.jme3.math.Transform;

import mygame.weapon.Weapon;

public class WeaponDefs {
	
	public class WeaponAssets {

	    public Class<? extends Weapon> clazz;
	    public String name;
	    public Transform[] ik;
	    public String model;
	    public String crosshairText;
	}

}
