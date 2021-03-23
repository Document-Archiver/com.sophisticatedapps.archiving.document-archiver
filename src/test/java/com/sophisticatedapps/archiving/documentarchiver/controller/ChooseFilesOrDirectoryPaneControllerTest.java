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

package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class ChooseFilesOrDirectoryPaneControllerTest extends BaseTest {

    private VBox chooseFilesOrDirectoryPane;
    private ChooseFilesOrDirectoryPaneController chooseFilesOrDirectoryPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/ChooseFilesOrDirectoryPane.fxml"));
        chooseFilesOrDirectoryPane = loader.load();
        chooseFilesOrDirectoryPaneController = loader.getController();
        chooseFilesOrDirectoryPaneController.rampUp(aStage);
    }

    @AfterEach
    public void cleanUpEach() {

        chooseFilesOrDirectoryPaneController.rampDown();

        chooseFilesOrDirectoryPane = null;
        chooseFilesOrDirectoryPaneController = null;
    }

    /**
     * Test "handleChooseFilesButtonAction" with selection of multiple files.
     */
    @Test
    void testHandleChooseFilesButtonAction() throws IllegalAccessException {

        ChooseFilesOrDirectoryPaneController.DaFileChooser tmpMockedFileChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaFileChooser.class);
        when(tmpMockedFileChooser.showOpenMultipleDialog(any(Window.class))).thenReturn(ALL_DOCUMENTS_LIST);
        FieldUtils.writeField(chooseFilesOrDirectoryPaneController, "fileChooser", tmpMockedFileChooser, true);

        Button tmpChooseFilesButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseFilesButton");
        tmpChooseFilesButton.getOnAction().handle(null);

        WaitForAsyncUtils.waitForFxEvents();

        // Not same, since List will be wrapped into a new List.
        assertEquals(ALL_DOCUMENTS_LIST, chooseFilesOrDirectoryPaneController.getAllDocuments());
        assertSame(ALL_DOCUMENTS_LIST.get(0), chooseFilesOrDirectoryPaneController.getCurrentDocument());
    }

    /**
     * Test "handleChooseFilesButtonAction" with cancelation of the file chooser.
     */
    @Test
    void testHandleChooseFilesButtonAction_with_cancel() throws IllegalAccessException {

        ChooseFilesOrDirectoryPaneController.DaFileChooser tmpMockedFileChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaFileChooser.class);
        when(tmpMockedFileChooser.showOpenMultipleDialog(any(Window.class))).thenReturn(null);
        FieldUtils.writeField(chooseFilesOrDirectoryPaneController, "fileChooser", tmpMockedFileChooser, true);

        Button tmpChooseFilesButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseFilesButton");
        tmpChooseFilesButton.getOnAction().handle(null);

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(chooseFilesOrDirectoryPaneController.getAllDocuments());
        assertNull(chooseFilesOrDirectoryPaneController.getCurrentDocument());
    }

    /**
     * Test "handleChooseDirectoryButtonAction" with selection of a non-empty folder.
     */
    @Test
    void testHandleChooseDirectoryButtonAction() throws IllegalAccessException {

        ChooseFilesOrDirectoryPaneController.DaDirectoryChooser tmpMockedDirectoryChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaDirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(TEST_RESOURCES_DIRECTORY);
        FieldUtils.writeField(chooseFilesOrDirectoryPaneController, "directoryChooser",
                tmpMockedDirectoryChooser, true);

        Button tmpChooseDirectoryButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseDirectoryButton");
        tmpChooseDirectoryButton.getOnAction().handle(null);

        WaitForAsyncUtils.waitForFxEvents();

        List<File> tmpChosenDocuments = chooseFilesOrDirectoryPaneController.getAllDocuments();
        assertEquals(ALL_DOCUMENTS_LIST.size(), tmpChosenDocuments.size());
        assertTrue(tmpChosenDocuments.contains(TEST_TEXT_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_TEXT_FILE2));
        assertTrue(tmpChosenDocuments.contains(TEST_PDF_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_JPG_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_JPG_FILE2));
    }

    /**
     * Test "handleChooseDirectoryButtonAction" with cancelation of the directory chooser.
     */
    @Test
    void testHandleChooseDirectoryButtonAction_with_cancel() throws IllegalAccessException {

        ChooseFilesOrDirectoryPaneController.DaDirectoryChooser tmpMockedDirectoryChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaDirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(null);
        FieldUtils.writeField(chooseFilesOrDirectoryPaneController, "directoryChooser",
                tmpMockedDirectoryChooser, true);

        Button tmpChooseDirectoryButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseDirectoryButton");
        tmpChooseDirectoryButton.getOnAction().handle(null);

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(chooseFilesOrDirectoryPaneController.getAllDocuments());
        assertNull(chooseFilesOrDirectoryPaneController.getCurrentDocument());
    }

    /**
     * Test "handleChooseDirectoryButtonAction" with selection of an empty folder.
     */
    @Test
    void testHandleChooseDirectoryButtonAction_with_empty_folder() throws IllegalAccessException {

        ChooseFilesOrDirectoryPaneController.DaDirectoryChooser tmpMockedDirectoryChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaDirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(TEST_ARCHIVING_FOLDER);
        FieldUtils.writeField(chooseFilesOrDirectoryPaneController, "directoryChooser",
                tmpMockedDirectoryChooser, true);

        Button tmpChooseDirectoryButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseDirectoryButton");
        tmpChooseDirectoryButton.getOnAction().handle(null);

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(chooseFilesOrDirectoryPaneController.getAllDocuments());
        assertNull(chooseFilesOrDirectoryPaneController.getCurrentDocument());
    }

}
