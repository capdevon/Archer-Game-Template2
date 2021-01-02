package mygame.weapon;

import java.util.List;

import com.capdevon.control.AdapterControl;
import com.capdevon.control.MaterializeTimer;
import com.capdevon.control.MaterializerListener;
import com.capdevon.engine.GameObject;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;

public class Damageable extends AdapterControl {
	
	private boolean hasAppliedDamage;
    
	public void applyDamage() {
		
		if (hasAppliedDamage)
			return;
		
		hasAppliedDamage = true;
		List<Geometry> lstGeometries = GameObject.listGeometries(spatial);
		
		for (Geometry geo : lstGeometries) {
			Material mat = geo.getMaterial();

			if ("MaterializePBR".equals(mat.getName())) {

				MaterializeTimer mTimer = new MaterializeTimer(mat, false, 0.4f);
				mTimer.setMaterializerListener(new MaterializerListener() {
					@Override
					public void onCompleteEvent() {
						mTimer.setMaterializerListener(null);
						geo.removeControl(mTimer);
						destroy();
					}
				});
				geo.addControl(mTimer);
				System.out.println(spatial + "; MaterializeTimer added");
			}
		}
	}
	
	private void destroy() {
		PhysicsSpace.getPhysicsSpace().removeAll(spatial);
		spatial.removeFromParent();
		System.out.println("Destroyed: " + spatial);
	}

}
