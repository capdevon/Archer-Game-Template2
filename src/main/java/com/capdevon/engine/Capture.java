package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.system.AppSettings;
import java.io.File;

/**
 * 
 * @author capdevon
 */
public class Capture {

	/**
	 * @param app
	 * @param quality [0.5f]
	 * @param dirName
	 */
	public static void captureVideo(Application app, float quality, String dirName) {
		AppSettings settings = app.getContext().getSettings();
		long fileId = (System.currentTimeMillis() / 1000);
		String fileName = settings.getTitle() + "-" + fileId + ".avi";
		File file = new File(dirName, fileName);

		VideoRecorderAppState recorder = new VideoRecorderAppState(file, quality, settings.getFrameRate());
		app.getStateManager().attach(recorder);

		System.out.println("initVideoRecorder=" + file.getAbsolutePath());
	}

}