package mygame.weapon;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 * 
 * @author capdevon
 */
public class CrosshairData {

    // The BitmapText that will be used for this weapon's crosshair
    public BitmapText bitmapText;
    // The image that will be used for this weapon's crosshair
    public Picture picture;
    // The size of the crosshair image
    public int size = 1;
    // The color of the crosshair image
    public ColorRGBA color = ColorRGBA.White.clone();
    // parent node
    public Node guiNode;

    public CrosshairData(Node guiNode, Picture picture) {
        this.guiNode = guiNode;
        this.picture = picture;
    }

    public CrosshairData(Node guiNode, BitmapText bmp) {
        this.guiNode = guiNode;
        this.bitmapText = bmp;
    }

    public void setEnabled(boolean enabled) {
        int i = -1;
        if (picture != null)
            i = enabled ? guiNode.attachChild(picture) : guiNode.detachChild(picture);
        else if (bitmapText != null)
            i = enabled ? guiNode.attachChild(bitmapText) : guiNode.detachChild(bitmapText);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color.set(color);
        if (bitmapText != null) {
            bitmapText.setColor(color);
        }
    }

}
