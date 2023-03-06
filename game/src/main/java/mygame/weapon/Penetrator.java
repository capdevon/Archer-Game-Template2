package mygame.weapon;

import com.capdevon.engine.FRotator;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.RotationOrder;
import com.jme3.bullet.animation.DacLinks;
import com.jme3.bullet.animation.PhysicsLink;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.JoinedBodyControl;
import com.jme3.bullet.joints.New6Dof;
import com.jme3.bullet.joints.motors.MotorParam;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.logging.Logger;
import jme3utilities.Validate;
import jme3utilities.math.MyVector3f;
import mygame.Main;
import mygame.ai.AIControl;

/**
 * A custom PhysicsControl for a dynamic rigid body with a tip that penetrates
 * obstructions.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class Penetrator
        extends JoinedBodyControl
        implements PhysicsTickListener {
    // *************************************************************************
    // constants and loggers

    /**
     * number of axes in a vector
     */
    final public static int numAxes = 3;
    /**
     * message logger for this class
     */
    final public static Logger logger2
            = Logger.getLogger(Penetrator.class.getName());
    // *************************************************************************
    // fields

    /**
     * swept shape for the tip
     */
    final private ConvexShape tipShape;
    /**
     * penetration depth as a fraction of the penetrator's length
     */
    private float penetrationFraction = 0.35f;
    /**
     * location of the tip (in scaled shape coordinates)
     */
    final private Vector3f tipLocalOffset;
    /**
     * temporary math objects
     */
    final private Quaternion tmpEndOrientation = new Quaternion();
    final private Quaternion tmpStartOrientation = new Quaternion();
    final private Quaternion tmpTurn = new Quaternion();

    final private Transform tmpTipEnd = new Transform();
    final private Transform tmpTipStart = new Transform();

    final private Vector3f tmpAngularComponent = new Vector3f();
    final private Vector3f tmpAngularVelocity = new Vector3f();
    final private Vector3f tmpEndOffset = new Vector3f();
    final private Vector3f tmpLinearVelocity = new Vector3f();
    final private Vector3f tmpRotationAxis = new Vector3f();
    final private Vector3f tmpCenterStartLocation = new Vector3f();
    final private Vector3f tmpStartOffset = new Vector3f();
    final private Vector3f tmpTipMotion = new Vector3f();
    // *************************************************************************
    // constructors

    /**
     * Instantiate an enabled Control.
     *
     * @param bodyShape the desired shape for the rigid body (not null, alias
     * created)
     * @param mass the desired mass for the rigid body (gt;0)
     * @param tipLocalOffset offset of the tip (in local coordinates, not null,
     * not zero)
     * @param penetrationFraction (&ge;0, &lt;1)
     */
    public Penetrator(CollisionShape bodyShape, float mass,
            Vector3f tipLocalOffset, float penetrationFraction) {
        super(bodyShape, mass);
        Validate.nonZero(tipLocalOffset, "tip local offset");
        Validate.fraction(penetrationFraction, "penetration fraction");

        getRigidBody().setCollisionGroup(Main.AMMO_GROUP);

        this.tipLocalOffset = tipLocalOffset.clone();
        this.penetrationFraction = penetrationFraction;

        float margin = CollisionShape.getDefaultMargin();
        this.tipShape = new SphereCollisionShape(margin);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Launch with the specified tip location and velocity.
     *
     * @param tipLocation the desired location of the tip (in physics-space
     * coordinates, not null)
     * @param velocity the desired launch velocity (in physics-space
     * coordinates, not null, not zero)
     */
    void launch(Vector3f tipLocation, Vector3f velocity) {
        Validate.nonNull(tipLocation, "tip location");
        Validate.nonZero(velocity, "velocity");

        PhysicsRigidBody centerBody = getRigidBody();
        centerBody.setLinearVelocity(velocity);

        Vector3f directionOfMotion = velocity.normalize();
        Quaternion orientation = FRotator.lookRotation(directionOfMotion);
        centerBody.setPhysicsRotation(orientation);

        Vector3f tipWorldOffset = orientation.mult(tipLocalOffset, null);
        Vector3f centerLocation = tipLocation.subtract(tipWorldOffset);
        centerBody.setPhysicsLocation(centerLocation);
    }
    // *************************************************************************
    // JointedBodyControl methods

    /**
     * Add the managed physics objects to the space.
     */
    @Override
    protected void addPhysics() {
        super.addPhysics();
        getPhysicsSpace().addTickListener(this);
    }

    /**
     * Remove all managed physics objects from the PhysicsSpace.
     */
    @Override
    protected void removePhysics() {
        getPhysicsSpace().removeTickListener(this);
        super.removePhysics();
    }
    // *************************************************************************
    // PhysicsTickListener methods

    /**
     * Callback from Bullet, invoked just before the physics is stepped. A good
     * time to clear/apply forces and reposition kinematic objects.
     *
     * @param space the space that's about to be stepped (not null)
     * @param timeStep the time per simulation step (in seconds, &ge;0)
     */
    @Override
    public void prePhysicsTick(PhysicsSpace space, float timeStep) {
        if (getRigidBody().countJoints() == 0) {
            tipCheck(timeStep);
        }
    }

    /**
     * Callback from Bullet, invoked just after the physics has been stepped. A
     * good time to re-activate deactivated objects.
     *
     * @param space the space that was just stepped (not null)
     * @param timeStep the time per simulation step (in seconds, &ge;0)
     */
    @Override
    public void physicsTick(PhysicsSpace space, float timeStep) {
        // do nothing
    }
    // *************************************************************************
    // private methods

    /**
     * Use a sweep test to detect and respond to tip contacts.
     *
     * @param timeStep the time per physics step (in seconds, &ge;0)
     */
    private void tipCheck(float timeStep) {
        PhysicsRigidBody centerBody = getRigidBody();
        centerBody.getPhysicsLocation(tmpCenterStartLocation);
        centerBody.getPhysicsRotation(tmpStartOrientation);
        centerBody.getLinearVelocity(tmpLinearVelocity);
        centerBody.getAngularVelocity(tmpAngularVelocity);

        // Calculate the starting offset of the tip in physics space.
        tmpStartOrientation.mult(tipLocalOffset, tmpStartOffset);

        // Estimate the velocity of the tip in physics space.
        tmpLinearVelocity.mult(timeStep, tmpTipMotion); // linear component only
        float rate = tmpAngularVelocity.length();
        if (rate > 0f) {
            // Predict the body's orientation at the end of the step.
            tmpRotationAxis.set(tmpAngularVelocity);
            tmpRotationAxis.divideLocal(rate);
            float angle = timeStep * rate;
            tmpTurn.fromAngleNormalAxis(angle, tmpRotationAxis);
            tmpTurn.mult(tmpStartOrientation, tmpEndOrientation);

            // Predict the tip's offset at the end of the step.
            tmpEndOrientation.mult(tipLocalOffset, tmpEndOffset);

            tmpEndOffset.subtract(tmpStartOffset, tmpAngularComponent);
            tmpTipMotion.addLocal(tmpAngularComponent);
        }

        float tipMotionDistance = tmpTipMotion.length();
        float margin = centerBody.getCollisionShape().getMargin();
        if (tipMotionDistance > margin) {
            int penGroup = centerBody.getCollisionGroup();
            int penWith = centerBody.getCollideWithGroups();

            // Perform a sweep test to identify potential tip contacts.
            Vector3f tipStartLocation = tmpTipStart.getTranslation(); // alias
            tmpCenterStartLocation.add(tmpStartOffset, tipStartLocation);
            tipStartLocation.add(tmpTipMotion, tmpTipEnd.getTranslation());
            PhysicsSpace space = getPhysicsSpace();
            List<PhysicsSweepTestResult> sweepTest
                    = space.sweepTest(tipShape, tmpTipStart, tmpTipEnd);

            // Select a rigid-body contact that's not another penetrator.
            PhysicsRigidBody nearestBody = null;
            float nearestFraction = 9f;
            for (PhysicsSweepTestResult hit : sweepTest) {
                PhysicsCollisionObject hitPco = hit.getCollisionObject();
                if (!(hitPco instanceof PhysicsRigidBody)) {
                    continue; // not a rigid body - ignore!
                }
                if (hitPco.getUserObject() instanceof Penetrator) {
                    continue; // another penetrator - ignore!
                }
                int hitGroup = hitPco.getCollisionGroup();
                int hitWith = hitPco.getCollideWithGroups();
                if ((hitGroup & penWith | hitWith & penGroup) == 0x0) {
                    continue; // The collision groups don't collide.
                }

                // Compare with the nearest contact so far.
                float hitFraction = hit.getHitFraction();
                assert hitFraction <= 1f : hitFraction;
                if (hitFraction < nearestFraction) {
                    nearestBody = (PhysicsRigidBody) hitPco;
                    nearestFraction = hitFraction;
                }
            }

            if (nearestBody != null) {
                penetrate(nearestBody, nearestFraction,
                        tipStartLocation, tmpTipEnd.getTranslation());
            }
        }
    }

    /**
     * Initiate penetration.
     *
     * @param hit the body that was hit (not null)
     * @param hitFraction the hit fraction of the sweep-test result (&ge;0,
     * &le;1)
     * @param tipStartLocation the physics-space location of the tip at the
     * start of the sweep (not null)
     * @param tipEndLocation the physics-space location of the tip at the end of
     * the sweep (not null)
     */
    private void penetrate(PhysicsRigidBody hit, float hitFraction,
            Vector3f tipStartLocation, Vector3f tipEndLocation) {
        // Determine which PhysicsLink was hit, if any.
        PhysicsLink hitLink = null;
        Object userObject = hit.getUserObject();
        if (userObject instanceof PhysicsLink) {
            hitLink = (PhysicsLink) userObject;
        }
        /*
         * Transfer enough momentum so that the body will barely reach
         * the point of impact on the next physics tick.
         */
        PhysicsRigidBody centerBody = getRigidBody();
        Vector3f velocity = centerBody.getLinearVelocity();
        float factor = (1f - hitFraction) * centerBody.getMass();
        Vector3f impulse = velocity.mult(factor);
        hit.applyCentralImpulse(impulse);
        impulse.negateLocal();
        centerBody.applyCentralImpulse(impulse);
        /*
         * Create a constraint, its pivot located at the point of impact
         * and its axes aligned with those of the penetrator.
         */
        Matrix3f hitMatrix = hit.getPhysicsRotationMatrix(null);
        hitMatrix.invertLocal();

        Vector3f hitOffset;
        if (hitLink == null) {
            hitOffset = hit.getPhysicsLocation(null);
            hitOffset.negateLocal();
            Vector3f pivotLocation = MyVector3f.lerp(
                    hitFraction, tipStartLocation, tipEndLocation, null);
            hitOffset.addLocal(pivotLocation);
            hitMatrix.mult(hitOffset, hitOffset);
        } else {
            // Zero the offset in the PhysicsLink, to avoid grazing shots.
            hitOffset = Vector3f.ZERO;
        }

        Matrix3f bodyMatrix = centerBody.getPhysicsRotationMatrix(null);
        hitMatrix.multLocal(bodyMatrix);
        Vector3f bodyOffset
                = tipLocalOffset.mult(1f - 2f * penetrationFraction);
        New6Dof fixedConstraint = new New6Dof(
                centerBody, hit, bodyOffset, hitOffset, Matrix3f.IDENTITY,
                hitMatrix, RotationOrder.XYZ);
        for (int axisIndex = 0; axisIndex < numAxes; ++axisIndex) {
            int dofIndex = axisIndex + 3;
            fixedConstraint.set(MotorParam.UpperLimit, dofIndex, 0f);
            fixedConstraint.set(MotorParam.LowerLimit, dofIndex, 0f);
        }
        getPhysicsSpace().addJoint(fixedConstraint);

        // Copy the ignore list and the collision-group settings.
        PhysicsCollisionObject[] pcos = hit.listIgnoredPcos();
        centerBody.setIgnoreList(pcos);
        centerBody.addToIgnoreList(hit);
        int hitGroup = hit.getCollisionGroup();
        centerBody.setCollisionGroup(hitGroup);
        int hitWithGroups = hit.getCollideWithGroups();
        centerBody.setCollideWithGroups(hitWithGroups);

        // If an enemy ragdoll was hit, apply damage to it.
        if (hitLink != null) {
            DacLinks ragdoll = hitLink.getControl();
            Spatial animationSpatial = ragdoll.getSpatial();
            Node gameObject = animationSpatial.getParent();
            AIControl ai = gameObject.getControl(AIControl.class);
            ai.takeDamage(25f); // TODO some hits do more damage
        }
    }
}
