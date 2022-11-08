package mygame.player;

import com.capdevon.anim.Animator;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.scene.Node;
import jme3utilities.Loadable;

/**
 * Encapsulate information about the player's character/avatar.
 *
 * @author sgold@sonic.net
 */
public class PlayerModel implements Loadable {

    final static String ASSET_PATH = "Models/Archer/Erika.j3o";

    /**
     * Instantiate a 3-D model for the player's character/avatar.
     *
     * @param assetManager for loading assets (not null)
     * @return a new Node
     */
    public Node instantiate(AssetManager assetManager) {
        Node result = (Node) assetManager.loadModel(ASSET_PATH);
        result.setName("Player");

        // add Physics & Animation Control
        result.addControl(new Animator());
        result.addControl(new BetterCharacterControl(.4f, 1.8f, 80f));
        
        return result;
    }

    /**
     * Preload the assets used in this model.
     *
     * @param assetManager for loading assets (not null)
     */
    @Override
    public void load(AssetManager assetManager) {
        // Create a node for the character model
        assetManager.loadModel(ASSET_PATH);
    }
}
