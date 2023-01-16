package mygame.devtool;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.RotationOrder;
import com.jme3.bullet.animation.CenterHeuristic;
import com.jme3.bullet.animation.DacConfiguration;
import com.jme3.bullet.animation.DynamicAnimControl;
import com.jme3.bullet.animation.LinkConfig;
import com.jme3.bullet.animation.MassHeuristic;
import com.jme3.bullet.animation.RagUtils;
import com.jme3.bullet.animation.RangeOfMotion;
import com.jme3.bullet.animation.ShapeHeuristic;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3utilities.Heart;
import jme3utilities.MyString;
import mygame.AnimDefs;

/**
 * A headless SimpleApplication to import certain C-G models used in the Archer
 * Game Template.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class ImportCgms extends SimpleApplication {
    // *************************************************************************
    // constants and loggers

    /**
     * message logger for this class
     */
    final private static Logger logger
            = Logger.getLogger(ImportCgms.class.getName());
    /**
     * filesystem path to the destination asset root (the directory/folder for
     * output)
     */
    final private static String assetDirPath = "../game/src/main/resources";
    /**
     * local copy of {@link com.jme3.math.Vector3f#UNIT_XYZ}
     */
    final private static Vector3f scaleIdentity = new Vector3f(1f, 1f, 1f);
    // *************************************************************************
    // new methods exposed

    /**
     * Main entry point for the ImportCgms application.
     *
     * @param arguments array of command-line arguments (not null)
     */
    public static void main(String[] arguments) {
        // Mute the chatty loggers found in some imported packages.
        Heart.setLoggingLevels(Level.WARNING);

        // Instantiate the application.
        ImportCgms application = new ImportCgms();

        // Log the working directory.
        String userDir = System.getProperty("user.dir");
        logger.log(Level.INFO, "working directory is {0}",
                MyString.quote(userDir));

        // Import the C-G models.
        application.start(JmeContext.Type.Headless);
    }
    // *************************************************************************
    // SimpleApplication methods

    /**
     * Convert GLTF format computer-graphics models to J3O format for faster
     * loading.
     */
    @Override
    public void simpleInitApp() {
        // Convert the arrow model to J3O format.
        Spatial arrow = assetManager.loadModel("Models/Arrow/arrow.glb");
        writeToJ3O(arrow, "Models/Arrow/arrow.j3o");

        // Convert the bow model to J3O format.
        Spatial bow = assetManager.loadModel("Models/Bow/bow.gltf");
        writeToJ3O(bow, "Models/Bow/bow.j3o");

        // Scale the Drake model and add a DynamicAnimControl.
        Spatial drake
                = assetManager.loadModel("Models/Drake/Drake-no-ragdoll.j3o");
        drake.setLocalScale(1.1f);
        AbstractControl sControl = RagUtils.findSControl(drake);
        Spatial controlledSpatial = sControl.getSpatial();
        DynamicAnimControl ragdoll = new DynamicAnimControl();
        configureDrakeRagdoll(ragdoll);
        controlledSpatial.addControl(ragdoll);
        writeToJ3O(drake, AnimDefs.Monster.ASSET_PATH);

        // Convert the level_rough model to J3O format.
        Spatial levelRough
                = assetManager.loadModel("Scenes/level_rough.gltf");
        writeToJ3O(levelRough, "Scenes/level_rough.j3o");

        stop();
    }
    // *************************************************************************
    // private methods

    /**
     * Configure a DynamicAnimControl for the Drake model.
     *
     * @param ragdoll the control to configure (not null, modified)
     */
    private void configureDrakeRagdoll(DynamicAnimControl ragdoll) {
        ragdoll.setIgnoredHops(2);

        float density = 1f;
        LinkConfig fourSphere = new LinkConfig(density, MassHeuristic.Density,
                ShapeHeuristic.FourSphere, scaleIdentity,
                CenterHeuristic.Mean, RotationOrder.XZY);
        LinkConfig twoSphere = new LinkConfig(density, MassHeuristic.Density,
                ShapeHeuristic.TwoSphere, scaleIdentity,
                CenterHeuristic.Mean, RotationOrder.XZY);
        LinkConfig vertexHull = new LinkConfig(density, MassHeuristic.Density,
                ShapeHeuristic.VertexHull, scaleIdentity,
                CenterHeuristic.Mean, RotationOrder.XZY);

        // trunk, neck, and head
        ragdoll.setConfig(DacConfiguration.torsoName, fourSphere);
        ragdoll.link("mixamorig:Spine", vertexHull,
                new RangeOfMotion(0.2f, -1f, 0.1f, -0.1f, 0.1f, -0.1f));
        ragdoll.link("mixamorig:Spine1", vertexHull,
                new RangeOfMotion(0.2f, 0.3f, 0.3f));
        ragdoll.link("mixamorig:Spine2", vertexHull,
                new RangeOfMotion(0.4f, 0.6f, 0.5f));

        ragdoll.link("mixamorig:Neck", vertexHull,
                new RangeOfMotion(0.6f, -0.3f, 0.6f, -0.6f, 0.4f, -0.4f));
        ragdoll.link("mixamorig:Head", fourSphere,
                new RangeOfMotion(0.6f, -0.3f, 0.6f, -0.6f, 0.7f, -0.7f));

        // left arm
        ragdoll.link("mixamorig:LeftShoulder", vertexHull,
                new RangeOfMotion(0.4f, -0.2f, 0f, 0f, 0.6f, -0.3f));
        ragdoll.link("mixamorig:LeftArm", vertexHull,
                new RangeOfMotion(0.2f, -1.5f, 0.5f, -0.5f, 1f, -1.6f));
        ragdoll.link("mixamorig:LeftForeArm", twoSphere,
                new RangeOfMotion(0f, -2f, 1f, -1f, 0f, 0f));
        ragdoll.link("mixamorig:LeftHand", fourSphere,
                new RangeOfMotion(0.9f, 0f, 0.3f));

        // right arm
        ragdoll.link("mixamorig:RightShoulder", vertexHull,
                new RangeOfMotion(0.4f, -0.2f, 0f, 0f, 0.6f, -0.3f));
        ragdoll.link("mixamorig:RightArm", vertexHull,
                new RangeOfMotion(0.2f, -1.5f, 0.5f, -0.5f, 1.6f, -1f));
        ragdoll.link("mixamorig:RightForeArm", twoSphere,
                new RangeOfMotion(0f, -2f, 1f, -1f, 0f, 0f));
        ragdoll.link("mixamorig:RightHand", fourSphere,
                new RangeOfMotion(0.9f, 0f, 0.3f));

        // left leg
        ragdoll.link("mixamorig:LeftUpLeg", twoSphere,
                new RangeOfMotion(0.2f, -1.1f, 0.4f, -0.4f, 0.2f, -0.4f));
        ragdoll.link("mixamorig:LeftLeg", fourSphere,
                new RangeOfMotion(2f, 0f, 0.1f, -0.1f, 0f, 0f));
        ragdoll.link("mixamorig:LeftFoot", vertexHull,
                new RangeOfMotion(0.6f, -0.4f, 0.4f, -0.4f, 0.4f, -0.4f));

        // right leg
        ragdoll.link("mixamorig:RightUpLeg", twoSphere,
                new RangeOfMotion(0.2f, -1.1f, 0.4f, -0.4f, 0.4f, -0.2f));
        ragdoll.link("mixamorig:RightLeg", fourSphere,
                new RangeOfMotion(2f, 0f, 0.1f, -0.1f, 0f, 0f));
        ragdoll.link("mixamorig:RightFoot", vertexHull,
                new RangeOfMotion(0.6f, -0.4f, 0.4f, -0.4f, 0.4f, -0.4f));
    }

    /**
     * Write the specified model to the specified J3O file.
     *
     * @param subtree
     * @param writeAssetPath
     */
    private void writeToJ3O(Spatial subtree, String writeAssetPath) {
        String writeFilePath
                = String.format("%s/%s", assetDirPath, writeAssetPath);
        Heart.writeJ3O(writeFilePath, subtree);
    }
}
