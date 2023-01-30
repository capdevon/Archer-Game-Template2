package mygame.devtool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Armature;
import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.plugin.export.material.J3MExporter;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;

/**
 *
 * @author capdevon
 */
public class TestAnimCombiner extends SimpleApplication {

    private final String resources = "src/main/resources/";
    private final String inputDir = "Models/Erika";
    private final String characterModel = "Erika"; // Main Chacarter (T-pose)

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        TestAnimCombiner app = new TestAnimCombiner();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {

        File dir = new File(resources + inputDir);
        String[] pathNames = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return !name.contains(characterModel) && name.endsWith(".gltf");
            }
        });

        Spatial myModel = assetManager.loadModel(inputDir + "/" + characterModel + ".gltf");
        AnimComposer targetComposer = AnimUtils.getAnimComposer(myModel);
        Armature targetArmature = AnimUtils.getSkinningControl(myModel).getArmature();

        for (String fileName : pathNames) {

            Spatial mocap = assetManager.loadModel(inputDir + "/" + fileName);
            AnimComposer sourceComposer = AnimUtils.getAnimComposer(mocap);

            for (AnimClip sourceClip : sourceComposer.getAnimClips()) {
                // Set the clip name equal to the file name.
                String clipName = fileName.substring(0, fileName.lastIndexOf('.'));
                // It assumes that the Armature of the source file and the target file have the same joints.
                AnimClip animCopy = AnimUtils.retargetClip(clipName, sourceClip, targetArmature);
                targetComposer.addAnimClip(animCopy);
            }
        }

        String dirName = resources + inputDir;
        myModel.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                Material mat = geom.getMaterial();
                File file = new File(dirName, mat.getName() + ".j3m");
                writeJ3m(mat, file); //TODO: Link the new j3m file to the geometry.
            }
        });

        File fout = new File(dirName, characterModel + ".j3o");
        writeJ3o(myModel, fout);
        
        stop();
    }

    /**
     * Save spatial to j3o file.
     *
     * @param sp
     * @param file
     */
    private void writeJ3o(Spatial sp, File file) {
        try {
            System.out.println("Converting j3o: " + file.getAbsolutePath());
            BinaryExporter.getInstance().save(sp, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save Material to j3m file.
     *
     * @param material
     * @param file
     */
    private void writeJ3m(Material material, File file) {
        try {
            System.out.println("Writing material:" + file.getAbsolutePath());
            J3MExporter j3mExporter = new J3MExporter();
            j3mExporter.save(material, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
