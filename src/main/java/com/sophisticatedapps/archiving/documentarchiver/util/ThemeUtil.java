package com.sophisticatedapps.archiving.documentarchiver.util;

import com.jthemedetecor.OsThemeDetector;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ThemeUtil {

    private static final List<Consumer<Boolean>> OS_THEME_DETECTOR_LISTENERS = new ArrayList<>();

    private static OsThemeDetector osThemeDetector = OsThemeDetector.getDetector();
    private static ThemeEnum currentTheme = PropertiesUtil.APPEARANCE_THEME;

    /**
     * Private constructor.
     */
    private ThemeUtil() {
    }

    public static ThemeEnum getCurrentTheme() {

        return currentTheme;
    }

    public static void setCurrentTheme(ThemeEnum aTheme, Scene aScene) {

        currentTheme = aTheme;
        applyCurrentTheme(aScene);

        try {

            PropertiesUtil.updateApplicationAppearanceTheme(aTheme);
        }
        catch (IOException e) {

            throw (new RuntimeException("Could not update Properties: " + e.getMessage()));
        }
    }

    public static void applyCurrentTheme(Scene aScene) {

        applyTheme(currentTheme, aScene);
    }

    public static void applyTheme(ThemeEnum aTheme, Scene aScene) {

        if (ThemeEnum.AUTO == aTheme) {

            Consumer<Boolean> tmpOsThemeDetectorListener = (aBoolean -> applyCurrentTheme(aScene));
            osThemeDetector.registerListener(tmpOsThemeDetectorListener);
            OS_THEME_DETECTOR_LISTENERS.add(tmpOsThemeDetectorListener);
        }
        else if (!OS_THEME_DETECTOR_LISTENERS.isEmpty()) {

            for (Consumer<Boolean> tmpCurrentListener : OS_THEME_DETECTOR_LISTENERS) {

                osThemeDetector.removeListener(tmpCurrentListener);
            }

            OS_THEME_DETECTOR_LISTENERS.clear();
        }

        List<String> tmpSceneStylesheets = aScene.getStylesheets();
        tmpSceneStylesheets.clear();
        tmpSceneStylesheets.add(aTheme.getPathToCss());
    }

    public enum ThemeEnum {

        AUTO("auto"),
        DARK(Objects.requireNonNull(GlobalConstants.DEFAULT_RESOURCE_LOAD_CONTEXT.getSearchBase()
                .getResource("view/style.css")).toExternalForm()),
        LIGHT(Objects.requireNonNull(GlobalConstants.DEFAULT_RESOURCE_LOAD_CONTEXT.getSearchBase()
                .getResource("view/modena.css")).toExternalForm());

        private final String pathToCss;

        ThemeEnum(String aPathToCss) {

            this.pathToCss = aPathToCss;
        }

        public String getPathToCss() {

            if (AUTO == this) {

                if (osThemeDetector.isDark()) {

                    return DARK.pathToCss;
                }
                else {

                    return LIGHT.pathToCss;
                }
            }
            else {

                return pathToCss;
            }
        }
    }

}
