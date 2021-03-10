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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class ChooseFilesOrDirectoryPaneControllerTest {

    private VBox chooseFilesOrDirectoryPane;
    ChooseFilesOrDirectoryPaneController chooseFilesOrDirectoryPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/ChooseFilesOrDirectoryPane.fxml"));

        this.chooseFilesOrDirectoryPane = loader.load();
        this.chooseFilesOrDirectoryPaneController = loader.getController();
        this.chooseFilesOrDirectoryPaneController.rampUp(aStage);

        aStage.setScene(new Scene(chooseFilesOrDirectoryPane));
        aStage.show();
        aStage.toFront();
    }

    /**
     * @param aFxRobot - Will be injected by the test runner.
     */
    @Test
    void testHandleChooseFilesButtonAction(FxRobot aFxRobot) {

        Button tmpChooseFilesButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseFilesButton");
        Assertions.assertThat(tmpChooseFilesButton).hasText("Choose file(s)");
        aFxRobot.clickOn(tmpChooseFilesButton);

        // TODO - refactor the controller to get the FileChooser via dependency injection. Then:
        // FileChooser mockedChooser = Mockito.mock(FileChooser.class)
        // when(mockedChooser.showOpenDialog(any(Window.class)).thenReturn(someFile);
    }

    /**
     * @param aFxRobot - Will be injected by the test runner.
     */
    @Test
    void testHandleChooseDirectoryButtonAction(FxRobot aFxRobot) {

        Button tmpChooseDirectoryButton = (Button)chooseFilesOrDirectoryPane.lookup("#chooseDirectoryButton");
        Assertions.assertThat(tmpChooseDirectoryButton).hasText("Choose directory");
        aFxRobot.clickOn(tmpChooseDirectoryButton);
    }

}
