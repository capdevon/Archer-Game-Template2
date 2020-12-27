package mygame.weapon;

import com.capdevon.control.TimerControl;
import com.jme3.app.Application;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ExplosionPrefab extends PrefabComponent {
	
	String assetName;
	ColorRGBA explosionColor;
	float lifeTimeVFX = 5f;

	private final PointLight lightVfx;

    public ExplosionPrefab(Application app) {
    	super(app);
    	lightVfx = new PointLight();
    }

	@Override
	public Spatial getAssetModel() {
		return app.getAssetManager().loadModel(assetName);
	}

	@Override
	public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
		Node model = (Node) getAssetModel();
		model.setName("Explosion-" + nextSeqId());
        model.setLocalTranslation(position);
        parent.attachChild(model);

        lightVfx.setPosition(position.add(0, 1f, 0));
        lightVfx.setRadius(13);
        lightVfx.setColor(explosionColor.mult(5));
        app.getRootNode().addLight(lightVfx);

        EmitterData emitter = new EmitterData(model);
        model.addControl(new TimerControl(lifeTimeVFX) {
        	//Gonna abuse an existing control a bit to fade out the light
	        @Override
	        protected void controlUpdate(float tpf) {
		        super.controlUpdate(tpf);
		        lightVfx.setColor(explosionColor.mult(5).mult(1 - time/maxTime));
	        }

	        @Override
			public void onTrigger() {
				emitter.stop();
	            spatial.removeFromParent();
	            app.getRootNode().removeLight(lightVfx);
	            System.out.println("Explosion removed: " + model);
			}
        });
        
        emitter.play();
        
        return model;
	}
}
