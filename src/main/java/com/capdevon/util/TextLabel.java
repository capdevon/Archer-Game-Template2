package com.capdevon.util;

import com.jme3.font.BitmapText;
import com.jme3.scene.Node;

public class TextLabel {

	public Node guiNode;
	public BitmapText bitmapText;

	public TextLabel(Node guiNode, BitmapText bitmapText) {
		super();
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
