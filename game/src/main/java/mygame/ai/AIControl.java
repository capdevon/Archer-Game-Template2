package mygame.ai;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimComposer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;

import mygame.AnimDefs.Monster;

/**
 * A state machine to manage the behavior of a monster.
 *
 * @author capdevon
 */
public class AIControl extends AdapterControl implements ActionAnimEventListener {

    private static final Logger logger = Logger.getLogger(AIControl.class.getName());

    public enum AIState {
        IDLE, CHASE, ATTACK, HIT, DEAD, WAIT, AWARE
    }

    public Spatial player;
    public BitmapText hud;

    private BetterCharacterControl bcc;
    private Animator animator;
    private boolean isAnimDone;

    private boolean invincible;
    private boolean isDead;
    private float maxHealth = 100f;
    private float health = maxHealth;
    private AIState currentState;
    private float stateTimer = 0;
    /**
     * unique ID for debugging, assigned serially
     */
    final private int id;
    /**
     * the next ID to be assigned
     */
    private static int nextId = 0;

    /**
     * Instantiate a state machine and assign it an ID.
     */
    public AIControl() {
        this.id = nextId;
        ++nextId;
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = getComponent(BetterCharacterControl.class);
            this.animator = getComponentInChildren(Animator.class);

            animator.createDefaultActions();
            animator.addListener(this);
            changeState(AIState.IDLE);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        stateTimer += tpf;

        switch (currentState) {
            case IDLE:
                break;
            case WAIT:
                break;
            case AWARE:
                break;
            case CHASE:
                break;
            case ATTACK:
                break;
            case HIT:
                if (isAnimDone) {
                    changeState(AIState.AWARE);
                }
                break;
            case DEAD:
                if (isAnimDone) {
                    spatial.removeFromParent();
                }
                break;
            default:
                break;
        }
    }

    private void updateAnimations() {
        switch (currentState) {
            case ATTACK:
                animator.setAnimation(Monster.Attack2);
                break;
            case AWARE:
                animator.setAnimation(Monster.Scream);
                break;
            case CHASE:
                animator.setAnimation(Monster.Running);
                break;
            case DEAD:
                animator.setAnimation(Monster.Dying);
                break;
            case HIT:
                animator.setAnimation(Monster.ReactionHit);
                break;
            case IDLE:
                animator.setAnimation(Monster.OrcIdle);
                break;
            case WAIT:
                break;
            default:
                break;
        }
    }

    private void changeState(AIState newState) {
        logger.log(Level.INFO, "{0}   {1} -> {2}", new Object[]{
            spatial.getName(), currentState, newState
        });

        stateTimer = 0;
        currentState = newState;
        updateAnimations();
        updateHudText();
    }

    private boolean timeout(float timeToWait) {
        return stateTimer > timeToWait;
    }

    public void takeDamage(float damage) {
        if (invincible) {
            return;
        }

        float healthBefore = health;
        health -= damage;
        health = FastMath.clamp(health, 0f, maxHealth);

        float trueDamageAmount = healthBefore - health;
        if (trueDamageAmount > 0f) {
            changeState(AIState.HIT);
        }

        handleDeath();
    }

    public void kill() {
        health = 0f;
        changeState(AIState.HIT);

        handleDeath();
    }

    private void handleDeath() {
        if (isDead) {
            return;
        }

        if (health <= 0f) {
            isDead = true;
            bcc.setEnabled(false);
            changeState(AIState.DEAD);
        }
    }

    private void updateHudText() {
        String status = String.format("%s - %s%n %.2f ",
                spatial.getName(), currentState, health);
        hud.setText(status);
    }

    @Override
    public void onAnimCycleDone(AnimComposer animComposer, String animName, boolean loop) {
        isAnimDone = true;
    }

    @Override
    public void onAnimChange(AnimComposer animComposer, String animName) {
        isAnimDone = false;
    }

}
