/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.capdevon.audio.SoundManager;
import com.capdevon.engine.Capture;
import com.capdevon.engine.JMonkey3;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.Physics;
import com.capdevon.physx.PhysxDebugAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

import mygame.player.PlayerManager;

/**
 *
 */
public class Main extends SimpleApplication {

    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Archer-Game-Template-2");
        settings.setUseJoysticks(true);
        settings.setResolution(1280, 720);
        //settings.setSamples(4);
        //settings.setBitsPerPixel(32);
        //settings.setFrameRate(60);

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

        /** Initialize the physics simulation */
        BulletAppState physics = new BulletAppState();
        physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(Physics.DEFAULT_GRAVITY);
        physics.setDebugEnabled(false);

        stateManager.attach(new SceneAppState());
        stateManager.attach(new CubeAppState());
        stateManager.attach(new PhysxDebugAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(new PlayerManager());

        //String dirName = System.getProperty("user.dir") + "/video";
        //Capture.captureVideo(this, 0.5f, dirName);
    }

}
