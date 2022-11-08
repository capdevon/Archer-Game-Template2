package mygame.ai;

import java.util.logging.Logger;

import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimComposer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;

import mygame.AnimDefs.Monster;

/**
 *
 * @author capdevon
 */
public class AIControl extends AdapterControl implements ActionAnimEventListener {

    private static final Logger logger = Logger.getLogger(AIControl.class.getName());

    public Spatial player;
    public BitmapText hud;

    private BetterCharacterControl bcc;
    private Animator animator;
    private boolean isDead;
    private boolean isAnimDone;
    private float maxHealth = 100f;
    private float health = maxHealth;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = getComponent(BetterCharacterControl.class);
            this.animator = getComponentInChildren(Animator.class);

            updateHealthbar();
            animator.createDefaultActions();
            animator.setAnimation(Monster.Idle);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        // do something...
    }

    public void takeDamage(float damage) {
        health -= damage;
        updateHealthbar();

        if (health <= 0) {
            // set death animation...
        } else {
            // set hit animation...
        }
    }

    private void updateHealthbar() {
        hud.setText(spatial.getName() + "\n" + health);
    }

    @Override
    public void onAnimCycleDone(AnimComposer animComposer, String animName, boolean loop) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimChange(AnimComposer animComposer, String animName) {
        // TODO Auto-generated method stub
    }

}
