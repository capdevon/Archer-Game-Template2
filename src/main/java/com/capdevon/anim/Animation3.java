package com.capdevon.anim;

import com.jme3.animation.LoopMode;
import java.util.Objects;

/**
 * 
 * @author capdevon
 */
public class Animation3 {
    
    public String name;
    public LoopMode loopMode;
    public float blendTime = .15f;
    public float speed = 1;

    public Animation3(String name, LoopMode loopMode) {
        this.name = name;
        this.loopMode = loopMode;
    }
    
    public Animation3(String name, LoopMode loopMode, float blendTime) {
        this.name = name;
        this.loopMode = loopMode;
        this.blendTime = blendTime;
    }
    
    public Animation3(String name, LoopMode loopMode, float blendTime, float speed) {
        this.name = name;
        this.loopMode = loopMode;
        this.blendTime = blendTime;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public LoopMode getLoopMode() {
        return loopMode;
    }

    public float getBlendTime() {
        return blendTime;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Animation3[name=" + name 
                + ", loopMode=" + loopMode 
                + ", blendTime=" + blendTime 
                + ", speed=" + speed 
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Animation3) {
            Animation3 other = (Animation3) obj;
            return Objects.equals(this.name, other.name);
        }
        return false;
    }
    
}
