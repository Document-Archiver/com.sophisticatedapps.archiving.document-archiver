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

package com.sophisticatedapps.archiving.documentarchiver.view;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.controller.FileSystemViewPaneController;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class FileSystemViewPaneTest extends BaseTest {

    private Stage stage;
    private Pane fileSystemViewPane;
    private FileSystemViewPaneController fileSystemViewPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        this.stage = aStage;

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<FileSystemViewPaneController, Pane> tmpDocumentsPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/FileSystemViewPane.fxml", getApp(aStage));
        fileSystemViewPane = tmpDocumentsPaneControllerRegionPair.getRegion();
        fileSystemViewPaneController = tmpDocumentsPaneControllerRegionPair.getController();

        aStage.setScene(new Scene(fileSystemViewPane));
        aStage.show();
        aStage.toFront();
    }

    @AfterEach
    public void cleanUpEach() {

        fileSystemViewPaneController.rampDown();

        fileSystemViewPane = null;
        fileSystemViewPaneController = null;

        Platform.runLater(() -> {

            stage.hide();
            stage = null;
        });
    }

    @Test
    void testFileSystemTableView() throws IOException {

        try (ZipFile tmpZipFile = new ZipFile(TEST_ZIP_FILE)) {

            fileSystemViewPaneController.setZipFile(tmpZipFile);
        }

        @SuppressWarnings("unchecked")
        TableView<ZipEntry> tmpTableView = (TableView<ZipEntry>)fileSystemViewPane.lookup("#fileSystemTableView");
        assertEquals(3, tmpTableView.getItems().size());
    }

}
