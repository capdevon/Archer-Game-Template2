/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

public class AudioClip {

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

    @Override
    public String toString() {
        return "AudioClip [file=" + file
                + ", volume=" + volume
                + ", looping=" + looping
                + ", positional=" + positional
                + "]";
    }

}
