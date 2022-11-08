package mygame.prefabs;

import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.controls.TimerControl;
import mygame.weapon.EmitterData;

/**
 * 
 * @author capdevon
 */
public class ExplosionPrefab extends PrefabComponent {

    public String assetName;
    public ColorRGBA explosionColor;
    public float lifeTimeVFX = 5f;

    public ExplosionPrefab(Application app) {
        super(app);
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {

        Node model = (Node) assetManager.loadModel(assetName);
        model.setName("Explosion-" + nextSeqId());
        model.setLocalTranslation(position);
        parent.attachChild(model);

        PointLight lightVFX = new PointLight();
        lightVFX.setPosition(position.add(0, 1f, 0));
        lightVFX.setRadius(13);
        lightVFX.setColor(explosionColor.mult(5));
        getRootNode().addLight(lightVFX);

        EmitterData emitter = new EmitterData(model);
        model.addControl(new TimerControl(lifeTimeVFX) {

            @Override
            protected void controlUpdate(float tpf) {
                super.controlUpdate(tpf);
                lightVFX.setColor(explosionColor.mult(5).mult(1 - time / maxTime));
            }

            @Override
            public void onTrigger() {
                emitter.stop();
                spatial.removeFromParent();
                getRootNode().removeLight(lightVFX);
                System.out.println("Explosion removed: " + model);

//	            System.out.println("debug Scene LightList:--------------------");
//	            for (Light light : getRootNode().getLocalLightList()) {
//	            	System.out.println(light);
//	            }
            }
        });

        emitter.play();

        return model;
    }

    /**
     * Preload the assets used in this prefab.
     *
     * @param assetManager for loading assets (not null)
     */
    @Override
    public void load(AssetManager assetManager) {
        assetManager.loadModel(assetName);
    }
}
