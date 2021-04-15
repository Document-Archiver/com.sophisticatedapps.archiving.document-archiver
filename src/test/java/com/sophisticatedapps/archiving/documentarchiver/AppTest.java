/*
 * Copyright 2021 by Stephan Sann (https://github.com/stephansann)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sophisticatedapps.archiving.documentarchiver;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.App"
 */
@ExtendWith(ApplicationExtension.class)
class AppTest extends BaseTest {

    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {

        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {

        System.setErr(standardErr);
    }

    @Test
    void testMain_no_arguments() throws IllegalAccessException {

        // Start clean
        FieldUtils.writeStaticField(App.class, "filesFromArgs", null,true);

        try {

            App.main(new String[]{});
        }
        catch (IllegalStateException e) {

            // "java.lang.IllegalStateException: Application launch must not be called more than once" is okay.
            assertEquals("Application launch must not be called more than once", e.getMessage());
        }

        @SuppressWarnings("unchecked")
        List<File> tmpFilesFromArgs =
                (List<File>)FieldUtils.readStaticField(App.class,"filesFromArgs",true);
        assertNull(tmpFilesFromArgs);
    }

    /**
     * Test if parameter gets checked correctly.
     */
    @Test
    void testMain_non_existing_file_argument() {

        App.main(new String[]{ "/foo/bar" });
        assertEquals("File does not exist: /foo/bar", outputStreamCaptor.toString().trim());
    }

    /**
     * Test if parameter gets checked correctly.
     */
    @Test
    void testMain_invalid_file_argument() {

        Throwable tmpException =
                assertThrows(RuntimeException.class, () -> App.main(new String[]{ NUL_CHARACTER_STRING }));
        assertEquals("Could not create File object for '\u0000': Invalid file path", tmpException.getMessage());
    }

    @Test
    void testMain_file_argument() throws IllegalAccessException {

        // Start clean
        FieldUtils.writeStaticField(App.class, "filesFromArgs", null,true);

        try {

            App.main(new String[]{ TEST_TEXT_FILE.getPath() });
        }
        catch (IllegalStateException e) {

            // "java.lang.IllegalStateException: Application launch must not be called more than once" is okay.
            assertEquals("Application launch must not be called more than once", e.getMessage());
        }

        @SuppressWarnings("unchecked")
        List<File> tmpFilesFromArgs =
                (List<File>)FieldUtils.readStaticField(App.class,"filesFromArgs",true);
        assertEquals(TEST_TEXT_FILE, tmpFilesFromArgs.get(0));
    }

    @Test
    void testMain_directory_argument() throws IllegalAccessException {

        // Start clean
        FieldUtils.writeStaticField(App.class, "filesFromArgs", null,true);

        try {

            App.main(new String[]{ TEST_SOURCE_FOLDER.getPath() });
        }
        catch (IllegalStateException e) {

            // "java.lang.IllegalStateException: Application launch must not be called more than once" is okay.
            assertEquals("Application launch must not be called more than once", e.getMessage());
        }

        Set<File> tmpExpectedFilesSet = new HashSet<>(ALL_DOCUMENTS_LIST);
        @SuppressWarnings("unchecked")
        Set<File> tmpFilesFromArgsSet = new HashSet<>(
                (List<File>)FieldUtils.readStaticField(App.class,"filesFromArgs",true));
        assertEquals(tmpExpectedFilesSet, tmpFilesFromArgsSet);
    }

    @Test
    void testStart() throws IllegalAccessException {

        // Mock the stage
        Stage tmpMockedStage = Mockito.mock(Stage.class);
        ObservableMap<Object, Object> tmpPropertiesMap = FXCollections.observableMap(new HashMap<>());
        ObservableList<Image> tmpIconsList = FXCollections.observableList(new ArrayList<>());
        doReturn(tmpPropertiesMap).when(tmpMockedStage).getProperties();
        doReturn(tmpIconsList).when(tmpMockedStage).getIcons();

        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theX", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setX(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theY", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setY(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theWidth", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setWidth(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theHeight", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setHeight(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theScene", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setScene(any(Scene.class));

        ReadOnlyDoubleProperty tmpMockedWidthProperty = Mockito.mock(ReadOnlyDoublePropertyBase.class);
        ReadOnlyDoubleProperty tmpMockedHeightProperty = Mockito.mock(ReadOnlyDoublePropertyBase.class);
        doReturn(tmpMockedWidthProperty).when(tmpMockedStage).widthProperty();
        doReturn(tmpMockedHeightProperty).when(tmpMockedStage).heightProperty();

        // Start the App
        List<File> tmpFilesFromArgs = Collections.singletonList(TEST_TEXT_FILE);
        FieldUtils.writeStaticField(App.class, "filesFromArgs", tmpFilesFromArgs, true);
        Platform.runLater(() -> (new App()).start(tmpMockedStage));

        WaitForAsyncUtils.waitForFxEvents();

        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        assertEquals(tmpBounds.getMinX(), tmpPropertiesMap.get("theX"));
        assertEquals(tmpBounds.getMinY(), tmpPropertiesMap.get("theY"));
        assertEquals(tmpBounds.getWidth(), tmpPropertiesMap.get("theWidth"));
        assertEquals(tmpBounds.getHeight(), tmpPropertiesMap.get("theHeight"));

        assertEquals(Scene.class, tmpPropertiesMap.get("theScene").getClass());
        verify(tmpMockedStage, Mockito.times(1)).show();

        assertEquals(tmpFilesFromArgs, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(TEST_TEXT_FILE, tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));

        assertEquals(HostServices.class, tmpPropertiesMap.get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY).getClass());
        assertEquals(Image.class, tmpIconsList.get(0).getClass());
        //assertTrue(Taskbar.getTaskbar().getIconImage() instanceof java.awt.Image);
    }

}
