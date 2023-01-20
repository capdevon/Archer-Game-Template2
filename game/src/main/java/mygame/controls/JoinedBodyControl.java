package mygame.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.infos.RigidBodyMotionState;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.clone.Cloner;
import java.io.IOException;
import java.util.logging.Logger;
import jme3utilities.Validate;

/**
 * A custom PhysicsControl for a dynamic rigid body that can be joined to other
 * bodies.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class JoinedBodyControl extends AbstractPhysicsControl {
    // *************************************************************************
    // constants and loggers

    /**
     * message logger for this class
     */
    final public static Logger logger3
            = Logger.getLogger(JoinedBodyControl.class.getName());
    // *************************************************************************
    // fields

    /**
     * main rigid body
     */
    final protected PhysicsRigidBody centerBody;
    /**
     * temporary math objects
     */
    final private Quaternion tmpUpdateOrientation = new Quaternion();
    final private Vector3f tmpUpdateLocation = new Vector3f();
    // *************************************************************************
    // constructors

    /**
     * Instantiate an enabled Control.
     *
     * @param bodyShape the desired shape for the rigid body (not null, alias
     * created)
     * @param mass the desired mass for the rigid body (gt;0)
     */
    public JoinedBodyControl(CollisionShape bodyShape, float mass) {
        Validate.nonNull(bodyShape, "shape");
        Validate.positive(mass, "mass");

        this.centerBody = new PhysicsRigidBody(bodyShape, mass);
        centerBody.setUserObject(this);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Access the rigid body managed by this Control.
     *
     * @return the pre-existing instance (not null)
     */
    public PhysicsRigidBody getBody() {
        return centerBody;
    }
    // *************************************************************************
    // AbstractPhysicsControl methods

    /**
     * Add the managed physics objects to the space.
     */
    @Override
    protected void addPhysics() {
        PhysicsSpace space = getPhysicsSpace();
        space.addCollisionObject(centerBody);
        for (PhysicsJoint joint : centerBody.listJoints()) {
            space.addJoint(joint);
        }
    }

    /**
     * Callback from {@link com.jme3.util.clone.Cloner} to convert this
     * shallow-cloned Control into a deep-cloned one, using the specified Cloner
     * and original to resolve copied fields.
     *
     * @param cloner the Cloner that's cloning this Control (not null, modified)
     * @param original the instance from which this Control was shallow-cloned
     * (not null, unaffected)
     */
    @Override
    public void cloneFields(Cloner cloner, Object original) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Create spatial-dependent data. Invoked when this Control is added to a
     * Spatial.
     *
     * @param spatial the controlled spatial (not null, alias created)
     */
    @Override
    protected void createSpatialData(Spatial spatial) {
        // do nothing
    }

    /**
     * De-serialize this Control from the specified importer, for example when
     * loading from a J3O file.
     *
     * @param importer (not null)
     * @throws IOException from the importer
     */
    @Override
    public void read(JmeImporter importer) throws IOException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Remove all managed physics objects from the PhysicsSpace.
     */
    @Override
    protected void removePhysics() {
        PhysicsSpace space = getPhysicsSpace();
        PhysicsJoint[] joints = centerBody.listJoints();
        for (PhysicsJoint joint : joints) {
            joint.destroy();
            space.removeJoint(joint);
        }
        space.removeCollisionObject(centerBody);
    }

    /**
     * Destroy spatial-dependent data. Invoked when this Control is removed from
     * its Spatial.
     *
     * @param spatial the Spatial to which this Control was added (unused)
     */
    @Override
    protected void removeSpatialData(Spatial spatial) {
        // do nothing
    }

    /**
     * Translate the body to the specified location.
     *
     * @param location the desired location (in physics-space coordinates, not
     * null, finite, unaffected)
     */
    @Override
    public void setPhysicsLocation(Vector3f location) {
        Validate.finite(location, "location");
        centerBody.setPhysicsLocation(location);
    }

    /**
     * Rotate the body to the specified orientation.
     *
     * @param orientation the desired orientation (in physics-space coordinates,
     * not null, not zero, unaffected)
     */
    @Override
    protected void setPhysicsRotation(Quaternion orientation) {
        Validate.nonZero(orientation, "orientation");
        centerBody.setPhysicsRotation(orientation);
    }

    /**
     * Update this Control. Invoked once per frame during the logical-state
     * update, provided the Control is added to a scene. Do not invoke directly
     * from user code.
     *
     * @param tpf the time interval between frames (in seconds, &ge;0)
     */
    @Override
    public void update(float tpf) {
        if (!isEnabled()) {
            return;
        }

        RigidBodyMotionState motionState = centerBody.getMotionState();
        motionState.getLocation(tmpUpdateLocation);
        motionState.getOrientation(tmpUpdateOrientation);
        applyPhysicsTransform(tmpUpdateLocation, tmpUpdateOrientation);
    }

    /**
     * Serialize this Control to the specified exporter, for example when saving
     * to a J3O file.
     *
     * @param exporter (not null)
     * @throws IOException from the exporter
     */
    @Override
    public void write(JmeExporter exporter) throws IOException {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
