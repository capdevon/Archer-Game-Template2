/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.util;

import com.capdevon.physx.Physics;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.util.SkyFactory;

/**
 *
 */
public abstract class BaseGameApplication extends SimpleApplication {
    
    public BulletAppState physics;
    public FilterPostProcessor fpp;
    public Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);
    
    /** Initialize the physics simulation */
    public void initPhysics(boolean debug) {
        physics = new BulletAppState();
        physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(Physics.DEFAULT_GRAVITY);
        physics.setDebugEnabled(debug);
    }
    
    public void setupScene() {
        Spatial scene = PhysicsTestHelper.getTownScene(assetManager);
        rootNode.attachChild(scene);
    }
    
    public void setupSkyBox() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, 
                "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap));
    }
    
    /** An ambient light and a directional sun light */
    public void setupLights() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
    
    public void setupFilters() {
        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(0.5f);

        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(bloom);
        fpp.addFilter(lsf);
        viewPort.addProcessor(fpp);
    }
    
    public AudioNode getAudioEnv(String fileName, boolean loop, float vol) {
        return getAudioEnv(fileName, loop, false, vol);
    }
    
    public AudioNode getAudioEnv(String fileName, boolean loop, boolean positonal, float vol) {
        AudioNode audio = new AudioNode(assetManager, fileName, AudioData.DataType.Stream);
        audio.setLooping(loop); // activate continuous playing
        audio.setPositional(positonal);
        audio.setVolume(vol);
        rootNode.attachChild(audio);
        audio.play();
        return audio;
    }

    public AudioNode getAudioClip(String fileName, boolean loop, float vol) {
        return getAudioClip(fileName, loop, vol, 1);
    }

    public AudioNode getAudioClip(String fileName, boolean loop, float vol, float pitch) {
        AudioNode audio = new AudioNode(assetManager, fileName, AudioData.DataType.Buffer);
        audio.setPositional(false);
        audio.setLooping(loop);
        audio.setVolume(vol);
        audio.setPitch(pitch);
        return audio;
    }
    
    public Geometry getAxisUnitX() {
        return getArrow("AX", Vector3f.UNIT_X, ColorRGBA.Red);
    }

    public Geometry getAxisUnitY() {
        return getArrow("AY", Vector3f.UNIT_Y, ColorRGBA.Green);
    }

    public Geometry getAxisUnitZ() {
        return getArrow("AZ", Vector3f.UNIT_Z, ColorRGBA.Blue);
    }

    public Geometry getArrow(String name, Vector3f dir, ColorRGBA color) {
        Geometry g = new Geometry(name, new Arrow(dir));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }
    
}
