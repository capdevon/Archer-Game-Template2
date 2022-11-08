package mygame.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import jme3utilities.Loadable;

/**
 *
 * @author capdevon
 */
public class AudioClip implements Loadable {

    public String file;
    public float volume = 1;
    public boolean looping = false;
    public boolean positional = false;

    public AudioClip(String file) {
        this.file = file;
    }

    public AudioClip(String file, float volume) {
        this.file = file;
        this.volume = volume;
    }

    public AudioClip(String file, float volume, boolean looping) {
        this.file = file;
        this.volume = volume;
        this.looping = looping;
    }

    public AudioClip(String file, float volume, boolean looping, boolean positional) {
        this.file = file;
        this.volume = volume;
        this.looping = looping;
        this.positional = positional;
    }

    /**
     * Preload the assets used in this clip.
     *
     * @param assetManager for loading assets (not null)
     */
    @Override
    public void load(AssetManager assetManager) {
        // Assume the clip will be buffered, not streamed. TODO
        boolean stream = false;
        boolean streamCache = true;
        AudioKey key = new AudioKey(file, stream, streamCache);
        assetManager.loadAudio(key);
    }

    @Override
    public String toString() {
        return "AudioClip [file=" + file
                + ", volume=" + volume
                + ", looping=" + looping
                + ", positional=" + positional
                + "]";
    }

}
