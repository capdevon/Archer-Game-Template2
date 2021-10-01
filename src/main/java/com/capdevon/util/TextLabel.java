package com.capdevon.util;

import com.jme3.font.BitmapText;
import com.jme3.scene.Node;

public class TextLabel {

    public final Node guiNode;
    public final BitmapText bitmapText;

    public TextLabel(Node guiNode, BitmapText bitmapText) {
        this.guiNode = guiNode;
        this.bitmapText = bitmapText;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            guiNode.attachChild(bitmapText);
        } else {
            guiNode.detachChild(bitmapText);
        }
    }

}
