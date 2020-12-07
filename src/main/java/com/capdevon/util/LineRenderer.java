package com.capdevon.util;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Curve;

public class LineRenderer {

	private int nbSubSegments = 1;
	private Spline spline;
	private Geometry geom;
	private Material material;
	
	public LineRenderer(AssetManager assetManager, String shapeName) {
		this(assetManager, 1, ColorRGBA.Green, shapeName);
	}
	
	public LineRenderer(AssetManager assetManager, float lineWidth, ColorRGBA color, String shapeName) {
		spline = new Spline();
		spline.addControlPoint(new Vector3f(0, -1000f, 0));
		geom = new Geometry(shapeName, new Curve(spline, nbSubSegments));
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color.clone());
        material.getAdditionalRenderState().setLineWidth(lineWidth);
        geom.setMaterial(material);
	}
	
	public void clearPoints() {
		spline.clearControlPoints();
	}
	
	public void addPoint(Vector3f point) {
		spline.addControlPoint(point);
	}
	
	public void setPoints(List<Vector3f> points) {
		spline.clearControlPoints();
		for (Vector3f point : points) {
			spline.addControlPoint(point);
		}
	}
	
	public void updateGeometry() {
		geom.setMesh(new Curve(spline, 1));
		geom.updateModelBound();
	}
	
	public boolean isEnabled() {
        boolean isEnabled = (geom.getCullHint() == CullHint.Never || geom.getCullHint() == CullHint.Dynamic);
        return isEnabled;
    }

    public void setEnabled(boolean enable) {
    	geom.setCullHint(enable ? CullHint.Never : CullHint.Always);
    }
    
    public void setColor(ColorRGBA color) {
    	material.setColor("Color", color.clone());
    }
    
    public void setLineWidth(float lineWidth) {
    	material.getAdditionalRenderState().setLineWidth(lineWidth);
    }
    
    public void setParent(Node parent) {
    	parent.attachChild(geom);
    }
	
}
