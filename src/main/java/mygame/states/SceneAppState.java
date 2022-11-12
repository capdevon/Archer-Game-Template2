package mygame.states;

import org.shaderblowex.filter.MipmapBloomFilter;

import com.capdevon.engine.SimpleAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;

/**
 *
 * @author capdevon
 */
public class SceneAppState extends SimpleAppState {

    private DirectionalLight sun;
    private FilterPostProcessor fpp;

    private boolean generateLightProbe = false;

    @Override
    public void simpleInit() {
        setupSkyBox();
        setupScene();
        setupLights();
        setupFilters();
    }

    private void setupSkyBox() {
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupScene() {
        Spatial scene = assetManager.loadModel("Scenes/level_rough.glb");
        scene.setName("MainScene");
        scene.move(0, -5, 0);
        rootNode.attachChild(scene);
        
        scene.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                System.out.println("--Add RigidBodyControl: " + geom);
                CollisionShape shape = CollisionShapeFactory.createMeshShape(geom);
                RigidBodyControl rgb = new RigidBodyControl(shape, PhysicsBody.massForStatic);
                geom.addControl(rgb);
                getPhysicsSpace().add(rgb);
            }
        });
        
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", AudioData.DataType.Stream);
        audio.setLooping(true);
        audio.setPositional(false);
        audio.setVolume(2);
        rootNode.attachChild(audio);
        audio.play();
    }

    private void setupLights() {
        sun = new DirectionalLight();
        sun.setName("SunLight");
        sun.setDirection(new Vector3f(-4.9236743f, -1.27054665f, 5.896916f));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        if (generateLightProbe) {
            EnvironmentCamera envCam = new EnvironmentCamera(); // Make an env camera
            stateManager.attach(envCam);
            envCam.initialize(stateManager, app); // Manually initialize so we can add a probe before the next update happens
            LightProbe probe = LightProbeFactory.makeProbe(envCam, rootNode);
            probe.getArea().setRadius(100); // Set the probe's radius in world units
            rootNode.addLight(probe);
            
        } else {
            // add a PBR probe.
            Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
            LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
            lightProbe.getArea().setRadius(100);
            rootNode.addLight(lightProbe);
        }
    }

    private void setupFilters() {
        // Shadows
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
//        dlsf.setLight(sun);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        LightScatteringFilter lsf = new LightScatteringFilter(sun.getDirection().mult(-300));
        lsf.setLightDensity(0.5f);

//        BloomFilter bloom = new BloomFilter();
//        bloom.setExposurePower(55);
//        bloom.setBloomIntensity(1.0f);
                
        MipmapBloomFilter bloom = new MipmapBloomFilter(MipmapBloomFilter.Quality.High, MipmapBloomFilter.GlowMode.Scene);
        bloom.setExposurePower(0.7f);
        bloom.setBloomIntensity(0.4f, 0.55f);

        SSAOFilter ssao = new SSAOFilter(5f, 10f, 0.8f, 0.70f);
        ssao.setApproximateNormals(true);

        FXAAFilter fxaa = new FXAAFilter();

        fpp = new FilterPostProcessor(assetManager);
//        int numSamples = settings.getSamples();
//        if (numSamples > 0) {
//            fpp.setNumSamples(numSamples);
//        }
//
//        fpp.addFilter(dlsf);
        fpp.addFilter(ssao);
        fpp.addFilter(bloom);
        fpp.addFilter(lsf);
        fpp.addFilter(fxaa);
        viewPort.addProcessor(fpp);
    }

}
