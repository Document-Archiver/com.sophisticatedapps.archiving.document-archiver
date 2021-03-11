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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class ChooseFilesOrDirectoryPaneControllerTest {

    private static final File TEST_TEXT_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test.txt")).getFile()));
    private static final File TEST_PDF_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("test.pdf")).getFile()));
    private static final File RESOURCES_DIRECTORY = TEST_TEXT_FILE.getParentFile();
    private static final List<File> DOCUMENTS_LIST = Collections.singletonList(TEST_TEXT_FILE);

    private VBox chooseFilesOrDirectoryPane;
    private ChooseFilesOrDirectoryPaneController chooseFilesOrDirectoryPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        ChooseFilesOrDirectoryPaneController.DaFileChooser tmpMockedFileChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaFileChooser.class);
        when(tmpMockedFileChooser.showOpenMultipleDialog(any(Window.class))).thenReturn(DOCUMENTS_LIST);
        ChooseFilesOrDirectoryPaneController.DaDirectoryChooser tmpMockedDirectoryChooser =
                Mockito.mock(ChooseFilesOrDirectoryPaneController.DaDirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(RESOURCES_DIRECTORY);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/ChooseFilesOrDirectoryPane.fxml"));
        chooseFilesOrDirectoryPane = loader.load();
        chooseFilesOrDirectoryPaneController = loader.getController();
        chooseFilesOrDirectoryPaneController.rampUp(aStage);
        chooseFilesOrDirectoryPaneController.fileChooser = tmpMockedFileChooser;
        chooseFilesOrDirectoryPaneController.directoryChooser = tmpMockedDirectoryChooser;

        aStage.setScene(new Scene(chooseFilesOrDirectoryPane));
        aStage.show();
        aStage.toFront();
    }

    /**
     * @param aFxRobot - Will be injected by the test runner.
     */
    @Test
    void testHandleChooseFilesButtonAction(FxRobot aFxRobot) throws IOException, InterruptedException {

        Button tmpChooseFilesButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseFilesButton");
        Assertions.assertThat(tmpChooseFilesButton).hasText("Choose file(s)");
        aFxRobot.clickOn(tmpChooseFilesButton);

        // TODO - make this work within Maven tests (currently the Button listener doesn't get triggered there)
        // Not same, since List will be wrapped into a new List.
        //assertEquals(DOCUMENTS_LIST, chooseFilesOrDirectoryPaneController.getAllDocuments());
        //assertSame(DOCUMENTS_LIST.get(0), chooseFilesOrDirectoryPaneController.getCurrentDocument());
    }

    /**
     * @param aFxRobot - Will be injected by the test runner.
     */
    @Test
    void testHandleChooseDirectoryButtonAction(FxRobot aFxRobot) throws IOException {

        Button tmpChooseDirectoryButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseDirectoryButton");
        Assertions.assertThat(tmpChooseDirectoryButton).hasText("Choose directory");
        aFxRobot.clickOn(tmpChooseDirectoryButton);

        // TODO - make this work within Maven tests (currently the Button listener doesn't get triggered there)
        //List<File> tmpChosenDocuments = chooseFilesOrDirectoryPaneController.getAllDocuments();
        //assertEquals(3, tmpChosenDocuments.size());
        //assertTrue(tmpChosenDocuments.contains(TEST_TEXT_FILE));
        //assertTrue(tmpChosenDocuments.contains(TEST_PDF_FILE));
    }

}
