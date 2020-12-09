/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.capdevon.engine.JMonkey3;
import com.capdevon.engine.SoundManager;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.PhysxDebugAppState;
import com.capdevon.util.BaseGameApplication;
import com.capdevon.util.PhysicsTestHelper;
import com.jme3.app.FlyCamAppState;
import com.jme3.audio.AudioNode;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import org.shaderblowex.filter.MipMapBloom.MipmapBloomFilter;

/**
 *
 */
public class Main extends BaseGameApplication {
    private DirectionalLight sun;
    
    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
    	
        Main app = new Main();
        
        AppSettings settings = new AppSettings(true);
        settings.setUseJoysticks(true);
        settings.setResolution(1280, 720);
        settings.setFrequency(60);
        settings.setFrameRate(60);
        settings.setSamples(4);
        settings.setBitsPerPixel(32);
        settings.setGammaCorrection(true);
        
        app.setSettings(settings);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);
        
        JMonkey3.initEngine(this);
        SoundManager.init(assetManager);
        
        initPhysics(false);
        setupScene();
        setupSkyBox();
        setupLights();
        setupFilters();
        
        stateManager.attach(new CubeAppState());
        stateManager.attach(new PhysxDebugAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(new ParticleManager());
        stateManager.attach(new PlayerManager());
    }

    @Override
    public void setupLights() {
        sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        EnvironmentCamera envCam = new EnvironmentCamera(); //Make an env camera
        stateManager.attach(envCam);
        envCam.initialize(stateManager, this); //Manually initialize so we can add a probe before the next update happens
        LightProbe probe = LightProbeFactory.makeProbe(envCam, rootNode);
        probe.getArea().setRadius(100); //Set the probe's radius in world units
        rootNode.addLight(probe);
    }

    @Override
    public void setupFilters() {
        //Shadows
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(0.5f);

        MipmapBloomFilter bloom = new MipmapBloomFilter(MipmapBloomFilter.Quality.High, MipmapBloomFilter.GlowMode.Scene);
        bloom.setExposurePower(0.7f);
        bloom.setBloomIntensity(0.4f, 0.55f);

        SSAOFilter ssaoFilter = new SSAOFilter(5f, 10f, 0.8f, 0.70f);

        FXAAFilter fxaa = new FXAAFilter();

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(ssaoFilter);
        fpp.addFilter(lsf);
        fpp.addFilter(bloom);
        fpp.addFilter(fxaa);
        viewPort.addProcessor(fpp);
    }

    @Override
    public void setupScene() {
        Spatial scene = assetManager.loadModel("Scenes/level.gltf");
        scene.setName("MainScene");
        scene.move(0, -5, 0);
        scene.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        PhysicsTestHelper.addStaticMeshCollider(scene);
        rootNode.attachChild(scene);
        
//        Node targets  = PhysicsTestHelper.createUnshadedBox(assetManager, 20);
//        rootNode.attachChild(targets);
        
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio_nature = getAudioEnv("Sound/Environment/Nature.ogg", true, false, 4);
    }
    
}

