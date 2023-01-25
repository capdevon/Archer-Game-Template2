package mygame.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public abstract class TimerControl extends AbstractControl {

    protected float time = 0;
    protected float maxTime = 0;

    public TimerControl(float maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    protected void controlUpdate(float tpf) {
        time += tpf;
        if (time > maxTime) {
            onTrigger();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // TODO Auto-generated method stub
    }

    public abstract void onTrigger();
}
