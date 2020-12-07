/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SoundManager {
    
    private static boolean initialized; 
    private static AssetManager assetManager;
    private static Map<String, AudioNode> soundsMap = new HashMap<>();
    
    private SoundManager() {
        // singleton constructor
    }
    
    public static void init(AssetManager assetManager) {
        if (!initialized) {
            initialized = true;
            SoundManager.assetManager = assetManager;
        }
    }
    
    /**
     * @param sound
     * @return 
     */
    public static AudioNode getAudioEnv(AudioClip sound) {
        AudioNode audio = new AudioNode(assetManager, sound.file, AudioData.DataType.Stream);
        audio.setVolume(sound.volume);
        audio.setLooping(sound.looping);
        audio.setPositional(sound.positional);
        return audio;
    }
    
    /**
     * @param sound
     * @return 
     */
    public static AudioNode getAudioClip(AudioClip sound) {
        AudioNode audio = new AudioNode(assetManager, sound.file, AudioData.DataType.Buffer);
        audio.setVolume(sound.volume);
        audio.setLooping(sound.looping);
        audio.setPositional(sound.positional);
        return audio;
    }
    
    /**
     * Must be called to cash soundfx that wants to be loaded in the system.
     * @param name
     * @param sound
     */
    public static void registerAudioEnv(String name, AudioClip sound) {
        if (soundsMap.get(name) == null) {
            soundsMap.put(name, getAudioEnv(sound));
        }
    }
    
    /**
     * Must be called to cash soundfx that wants to be loaded in the system.
     * @param name
     * @param sound
     */
    public static void registerAudioClip(String name, AudioClip sound) {
        if (soundsMap.get(name) == null) {
            soundsMap.put(name, getAudioClip(sound));
        }
    }
    
    /**
     * Called when all sounds must be stopped.
     */
    public static void stopAll() {
        for (Map.Entry<String, AudioNode> entry : soundsMap.entrySet()) {
            String name = entry.getKey();
            AudioNode audioNode = entry.getValue();
            audioNode.stop();
        }
    }
    
    public static AudioNode getSound(String soundName) {
        return soundsMap.get(soundName);
    }
    
}
