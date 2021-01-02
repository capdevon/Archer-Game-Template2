package mygame.weapon;

import com.capdevon.control.TimerControl;
import com.jme3.app.Application;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ExplosionPrefab extends PrefabComponent {

    private String assetName;
    private ColorRGBA explosionColor;
    private float lifeTimeVFX = 5f;

    public ExplosionPrefab(Application app, String effectName, ColorRGBA effectColor, float effectDuration) {
        super(app);
        this.assetName = effectName;
        this.explosionColor = effectColor;
        this.lifeTimeVFX = effectDuration;
    }

    @Override
    public Spatial loadModel() {
        return app.getAssetManager().loadModel(assetName);
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {

        Node model = (Node) loadModel();
        model.setName("Explosion-" + nextSeqId());
        model.setLocalTranslation(position);
        parent.attachChild(model);

        PointLight lightVFX = new PointLight();
        lightVFX.setPosition(position.add(0, 1f, 0));
        lightVFX.setRadius(13);
        lightVFX.setColor(explosionColor.mult(5));
        app.getRootNode().addLight(lightVFX);

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
                app.getRootNode().removeLight(lightVFX);
                System.out.println("Explosion removed: " + model);

//	            System.out.println("debug Scene LightList:--------------------");
//	            for (Light light : app.getRootNode().getLocalLightList()) {
//	            	System.out.println(light);
//	            }
            }
        });

        emitter.play();

        return model;
    }
}
