package mygame;

import com.capdevon.control.AdapterControl;
import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;

import mygame.weapon.Weapon;

public class WeaponUIManager extends AdapterControl {
	
	public BitmapText weaponText;
	PlayerControl m_PlayerControl;
	
	@Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
//        	m_PlayerControl = getComponent(PlayerControl.class);
        }
	}
	
	public void changeWeapon(Weapon weapon) {
		weaponText.setText(weapon.getDescription());
	}

}
