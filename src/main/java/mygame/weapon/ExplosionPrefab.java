package mygame.weapon;

import com.capdevon.control.TimerControl;
import com.jme3.app.Application;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ExplosionPrefab extends PrefabComponent {
	
	String assetName;
	float lifeTimeVFX = 5f;

    public ExplosionPrefab(Application app) {
    	super(app);
    }

	@Override
	public Spatial getAssetModel() {
		// TODO Auto-generated method stub
		return app.getAssetManager().loadModel(assetName);
	}

	@Override
	public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
		// TODO Auto-generated method stub
		Node model = (Node) getAssetModel();
		model.setName("Explosion-" + nextSeqId());
        model.setLocalTranslation(position);
        parent.attachChild(model);
        
        EmitterData emitter = new EmitterData(model);
        model.addControl(new TimerControl(lifeTimeVFX) {
			@Override
			public void onTrigger() {
				// TODO Auto-generated method stub
				emitter.stop();
	            spatial.removeFromParent();
	            System.out.println("Explosion removed: " + model);
			}
        });
        
        emitter.play();
        
        return model;
	}
}
