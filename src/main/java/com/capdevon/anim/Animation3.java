package com.capdevon.anim;

import java.util.Objects;

import com.jme3.anim.AnimComposer;

/**
 *
 * @author capdevon
 */
public class Animation3 {

    public String name;
    public String layer = AnimComposer.DEFAULT_LAYER;
    public boolean loop = true;
    public float speed = 1.0f;

    public Animation3(String name, boolean loop) {
        this.name = name;
        this.loop = loop;
    }

    public Animation3(String name, String layer, boolean loop) {
        this.name = name;
        this.layer = layer;
        this.loop = loop;
    }

    public Animation3(String name, boolean loop, float speed) {
        this.name = name;
        this.loop = loop;
        this.speed = speed;
    }

    public Animation3(String name, String layer, boolean loop, float speed) {
        this.name = name;
        this.layer = layer;
        this.loop = loop;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public String getLayer() {
        return layer;
    }

    public boolean isLooping() {
        return loop;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "Animation3[name=" + name
                + ", layer=" + layer
                + ", loop=" + loop
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
