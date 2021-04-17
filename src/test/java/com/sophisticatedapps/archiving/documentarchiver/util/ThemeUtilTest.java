package com.sophisticatedapps.archiving.documentarchiver.util;

import com.jthemedetecor.OsThemeDetector;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class ThemeUtilTest extends BaseTest {

    @TempDir
    File tempDir;

    @Test
    void getCurrentTheme() {

        assertEquals(ThemeUtil.ThemeEnum.AUTO, ThemeUtil.getCurrentTheme());
    }

    @Test
    void setCurrentTheme() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        ObservableList<String> tmpStylesheets = FXCollections.observableList(new ArrayList<>());
        Scene tmpMockedScene  = Mockito.mock(Scene.class);
        when(tmpMockedScene.getStylesheets()).thenReturn(tmpStylesheets);

        ThemeUtil.setCurrentTheme(ThemeUtil.ThemeEnum.LIGHT, tmpMockedScene);
        assertEquals(ThemeUtil.ThemeEnum.LIGHT.getPathToCss(), tmpStylesheets.get(0));

        // Properties updated?
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");
        assertEquals("LIGHT", tmpReadProperties.getProperty(PropertiesUtil.KEY_APPEARANCE_THEME));

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    void setCurrentTheme_with_auto() throws IllegalAccessException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        // Exchange OsThemeDetector
        OsThemeDetector tmpOriginalOsThemeDetector = (OsThemeDetector) FieldUtils.readStaticField(
                ThemeUtil.class, "osThemeDetector", true);
        OsThemeDetector tmpMockedOsThemeDetector = Mockito.mock(OsThemeDetector.class);
        FieldUtils.writeStaticField(ThemeUtil.class, "osThemeDetector", tmpMockedOsThemeDetector, true);

        List<Consumer<Boolean>> tmpMockRegisteredListenerList = new ArrayList<>();
        doAnswer(anInvocationOnMock -> {
            tmpMockRegisteredListenerList.add(anInvocationOnMock.getArgument(0));
            return null;
        }).when(tmpMockedOsThemeDetector).registerListener(any(Consumer.class));
        doAnswer(anInvocationOnMock -> {
            tmpMockRegisteredListenerList.remove(anInvocationOnMock.getArgument(0));
            return null;
        }).when(tmpMockedOsThemeDetector).removeListener(any(Consumer.class));

        ObservableList<String> tmpStylesheets = FXCollections.observableList(new ArrayList<>());
        Scene tmpMockedScene  = Mockito.mock(Scene.class);
        when(tmpMockedScene.getStylesheets()).thenReturn(tmpStylesheets);

        // Set auto-theme
        ThemeUtil.setCurrentTheme(ThemeUtil.ThemeEnum.AUTO, tmpMockedScene);
        assertEquals(ThemeUtil.ThemeEnum.AUTO, ThemeUtil.getCurrentTheme());
        // There should be a new Listener registered
        assertEquals(1, tmpMockRegisteredListenerList.size());

        // Trigger the Listener
        Consumer<Boolean> tmpListener = tmpMockRegisteredListenerList.get(0);
        tmpListener.accept(Boolean.TRUE);

        // Set non-auto-theme
        ThemeUtil.setCurrentTheme(ThemeUtil.ThemeEnum.DARK, tmpMockedScene);
        assertEquals(ThemeUtil.ThemeEnum.DARK, ThemeUtil.getCurrentTheme());
        // There should be no more Listener registered
        assertEquals(0, tmpMockRegisteredListenerList.size());

        // Write back original OsThemeDetector
        FieldUtils.writeStaticField(ThemeUtil.class, "osThemeDetector", tmpOriginalOsThemeDetector, true);

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void testThemeEnumGetPathToCss_with_auto() throws IllegalAccessException {

        OsThemeDetector tmpOriginalOsThemeDetector = (OsThemeDetector) FieldUtils.readStaticField(
                ThemeUtil.class, "osThemeDetector", true);
        OsThemeDetector tmpMockedOsThemeDetector = Mockito.mock(OsThemeDetector.class);
        FieldUtils.writeStaticField(ThemeUtil.class, "osThemeDetector", tmpMockedOsThemeDetector, true);

        when(tmpMockedOsThemeDetector.isDark()).thenReturn(Boolean.FALSE);
        assertEquals(ThemeUtil.ThemeEnum.LIGHT.getPathToCss(), ThemeUtil.ThemeEnum.AUTO.getPathToCss());

        when(tmpMockedOsThemeDetector.isDark()).thenReturn(Boolean.TRUE);
        assertEquals(ThemeUtil.ThemeEnum.DARK.getPathToCss(), ThemeUtil.ThemeEnum.AUTO.getPathToCss());

        // Write back original OsThemeDetector
        FieldUtils.writeStaticField(ThemeUtil.class, "osThemeDetector", tmpOriginalOsThemeDetector, true);
    }

    @Test
    void setCurrentTheme_with_exception() throws IllegalAccessException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, NUL_CHARACTER_STRING);
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        ObservableList<String> tmpStylesheets = FXCollections.observableList(new ArrayList<>());
        Scene tmpMockedScene  = Mockito.mock(Scene.class);
        when(tmpMockedScene.getStylesheets()).thenReturn(tmpStylesheets);

        RuntimeException tmpException = assertThrows(RuntimeException.class, () ->
                ThemeUtil.setCurrentTheme(ThemeUtil.ThemeEnum.LIGHT, tmpMockedScene));
        assertEquals("Could not update Properties: Invalid file path", tmpException.getMessage());

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

}
