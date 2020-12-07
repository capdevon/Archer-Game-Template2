package com.capdevon.util;

import com.jme3.asset.AssetManager;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.water.SimpleWaterProcessor;

public class WaterFactory {

	private AssetManager assetManager;
	private ViewPort viewPort;

	public WaterFactory(AssetManager assetManager, ViewPort viewPort) {
		this.assetManager = assetManager;
		this.viewPort = viewPort;
	}

	public Geometry build(Node scene, float width, float height) {
		SimpleWaterProcessor wpss = new SimpleWaterProcessor(assetManager);
		viewPort.addProcessor(wpss);

		wpss.setPlane(new Plane(Vector3f.UNIT_Y, 0));
		wpss.setReflectionScene(scene);
		wpss.setReflectionClippingOffset(-0.1f);
		wpss.setDistortionScale(0.2f);
		wpss.setWaterDepth(0.4f);
		wpss.setWaveSpeed(0.06f);

		Geometry waterGeo = wpss.createWaterGeometry(width, height);
		waterGeo.setMaterial(wpss.getMaterial());
		waterGeo.setShadowMode(ShadowMode.Off);

		return waterGeo;
	}
}