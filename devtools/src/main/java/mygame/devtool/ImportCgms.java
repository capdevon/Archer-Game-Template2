package mygame.devtool;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.animation.RagUtils;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3utilities.Heart;
import jme3utilities.MyString;
import mygame.AnimDefs;
import mygame.prefabs.DrakeControl;

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
    // *************************************************************************
    // new methods exposed

    /**
     * Main entry point for the ImportCgms application.
     *
     * @param arguments array of command-line arguments (not null)
     */
    public static void main(String[] arguments) {
        /*
         * Mute the chatty loggers found in some imported packages.
         */
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
        Spatial arrow = assetManager.loadModel("Models/Arrow/arrow.glb");
        writeToJ3O(arrow, "Models/Arrow/arrow.j3o");

        Spatial bow = assetManager.loadModel("Models/Bow/bow.gltf");
        writeToJ3O(bow, "Models/Bow/bow.j3o");

        // Scale the Drake model and add a DrakeControl.
        Spatial drake
                = assetManager.loadModel("Models/Drake/Drake-no-ragdoll.j3o");
        drake.setLocalScale(1.1f);
        AbstractControl sControl = RagUtils.findSControl(drake);
        Spatial controlledSpatial = sControl.getSpatial();
        DrakeControl ragdoll = new DrakeControl();
        controlledSpatial.addControl(ragdoll);
        writeToJ3O(drake, AnimDefs.Monster.ASSET_PATH);

        Spatial levelRough
                = assetManager.loadModel("Scenes/level_rough.gltf");
        writeToJ3O(levelRough, "Scenes/level_rough.j3o");

        stop();
    }
    // *************************************************************************
    // private methods

    /**
     * Write the specified model to a J3O file.
     */
    private void writeToJ3O(Spatial model, String writeAssetPath) {
        String writeFilePath
                = String.format("%s/%s", assetDirPath, writeAssetPath);
        Heart.writeJ3O(writeFilePath, model);
    }
}
