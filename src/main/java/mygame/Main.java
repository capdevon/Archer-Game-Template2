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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 *
 */
public class Main extends BaseGameApplication {
    
    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
    	
        Main app = new Main();
        
        AppSettings settings = new AppSettings(true);
        settings.setUseJoysticks(true);
        settings.setResolution(800, 600);
        settings.setFrequency(60);
        settings.setFrameRate(30);
        settings.setSamples(4);
        settings.setBitsPerPixel(32);
        
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
    public void setupScene() {
        Spatial scene = PhysicsTestHelper.getMainScene(assetManager);
        rootNode.attachChild(scene);
        
//        Node targets  = PhysicsTestHelper.createUnshadedBox(assetManager, 20);
//        rootNode.attachChild(targets);
        
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio_nature = getAudioEnv("Sound/Environment/Nature.ogg", true, false, 4);
    }
    
}

