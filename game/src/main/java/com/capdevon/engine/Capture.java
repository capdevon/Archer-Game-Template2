package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.system.AppSettings;
import java.awt.DisplayMode;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author capdevon
 */
public class Capture {

    private static final Logger logger = Logger.getLogger(Capture.class.getName());

    private Capture() {
        // private constructor.
    }

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

        DisplayMode mode = DsUtils.displayMode();
        int frameRate = mode.getRefreshRate();
        if (frameRate < 0) {
            throw new IllegalArgumentException("FrameRate must not be negative: " + frameRate);
        }

        VideoRecorderAppState recorder = new VideoRecorderAppState(file, quality, frameRate);
        app.getStateManager().attach(recorder);

        System.out.println("Start VideoRecorder=" + file.getAbsolutePath());
    }

    /**
     * If a VideoRecorderAppState is attached, detach it.
     *
     * @param stateManager the application's AppState manager (not null)
     */
    public static void cleanup(AppStateManager stateManager) {
        VideoRecorderAppState appState = stateManager.getState(VideoRecorderAppState.class);
        if (appState != null) {
            File file = appState.getFile();
            stateManager.detach(appState);
            // TODO - use a Logger instead of System.out
            System.out.println("Stop VideoRecorder=" + file.getAbsolutePath());
        }
    }

    /**
     * Take a screenshot.
     *
     * @param app the running application (not null)
     * @param tpf passed to {@code onAction()}
     */
    public static void takeScreenshot(Application app, float tpf) {
        AppStateManager stateManager = app.getStateManager();
        ScreenshotAppState appState = stateManager.getState(ScreenshotAppState.class);
        appState.onAction("ScreenShot", true, tpf);
        logger.log(Level.WARNING, "Took a screenshot.");
    }

    /**
     * Toggle video recording on/off.
     *
     * @param app the running application (not null)
     * @param qualityLevel the desired video quality (&ge;0, &le;1)
     */
    public static void toggleVideo(Application app, float qualityLevel) {
        AppStateManager stateManager = app.getStateManager();
        VideoRecorderAppState appState = stateManager.getState(VideoRecorderAppState.class);
        if (appState == null) {
            String dirName = System.getProperty("user.dir");
            captureVideo(app, qualityLevel, dirName);
        } else {
            cleanup(stateManager);
        }
    }
}
