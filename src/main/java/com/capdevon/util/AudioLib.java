/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.util;

import com.capdevon.engine.AudioClip;

/**
 *
 */
public interface AudioLib {

    AudioClip ENV_NATURE        = new AudioClip("Sound/Environment/Nature.ogg", 4f, true, false);
    AudioClip BOW_PULL          = new AudioClip("Sounds/Archer/bow-pull.wav", 1f);
    AudioClip ARROW_HIT         = new AudioClip("Sounds/Archer/arrow-impact-2.wav", 1f);
    AudioClip GRASS_FOOTSTEPS   = new AudioClip("Sounds/Footsteps/Grass-Running-3.wav", .4f, true);

}
