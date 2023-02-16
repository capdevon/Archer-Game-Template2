package mygame.ai;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimComposer;
import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.PhysicsLink;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.font.BitmapText;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import mygame.AnimDefs.Monster;

/**
 * Manage the behavior of a monster using a finite-state machine.
 *
 * @author capdevon
 */
public class AIControl extends AdapterControl implements ActionAnimEventListener {

    private static final Logger logger = Logger.getLogger(AIControl.class.getName());

    private enum AIState {
        IDLE, CHASE, ATTACK, HIT, WAIT, AWARE, DYING, RAGDOLL, SINKING
    }
    /**
     * acceleration when the monster sinks into the ground
     */
    private static final Vector3f sinkingGravity = new Vector3f(0f, -0.5f, 0f);
    /**
     * display the monster's status in the viewport
     */
    final private BitmapText hud;
    /**
     * physics controls
     */
    private BetterCharacterControl bcc;
    private DynamicAnimControl dac;

    private Animator animator;
    private boolean isAnimDone;

    private boolean invincible;
    private boolean isDead;
    private float maxHealth = 100f;
    private float health = maxHealth;
    private AIState currentState;
    private float stateTimer = 0;

    /**
     * Instantiate with the specified parameters.
     *
     * @param label to display the monster's status in the viewport
     */
    public AIControl(BitmapText label) {
        this.hud = label;
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = getComponent(BetterCharacterControl.class);
            this.dac = getComponentInChildren(DynamicAnimControl.class);
            this.animator = getComponent(Animator.class);

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

            case DYING:
                if (timeout(2f)) {
                    changeState(AIState.RAGDOLL);
                }
                break;

            case RAGDOLL:
                if (timeout(12f)) {
                    changeState(AIState.SINKING);
                }
                break;

            case SINKING:
                if (timeout(2f)) {
                    dac.setEnabled(false);
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
            case DYING:
                animator.setAnimation(Monster.Dying);
                bcc.setEnabled(false);
                bcc.getSpatial().removeControl(bcc);
                break;
            case HIT:
                animator.setAnimation(Monster.ReactionHit);
                break;
            case IDLE:
                animator.setAnimation(Monster.OrcIdle);
                break;

            case RAGDOLL:
                dac.setRagdollMode();
                break;

            case SINKING:
                PhysicsLink link = dac.getTorsoLink();
                PhysicsRigidBody body = link.getRigidBody();
                sinkJoinedBodies(body);
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
            changeState(AIState.DYING);
        }
    }

    /**
     * Sink the specified body into the ground, along with any responsive bodies
     * joined to it.
     *
     * @param body (not null)
     */
    private static void sinkJoinedBodies(PhysicsRigidBody body) {
        body.setContactResponse(false);
        body.setGravity(sinkingGravity);

        for (PhysicsJoint joint : body.listJoints()) {
            PhysicsBody joinedBody = joint.findOtherBody(body);
            if (joinedBody instanceof PhysicsRigidBody) {
                PhysicsRigidBody otherRigidBody = (PhysicsRigidBody) joinedBody;
                if (otherRigidBody.isContactResponse()) {
                    sinkJoinedBodies(otherRigidBody);
                }
            }
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
