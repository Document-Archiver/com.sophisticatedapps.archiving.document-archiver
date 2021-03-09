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

import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class RootPaneController extends BaseController {

    private VBox documentsPane;
    private Pane displayFilePane;
    private VBox infoPane;
    private VBox chooseFilesOrDirectoryPane;

    @FXML
    private BorderPane rootPane;

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Load pane's sub-panes
        final MenuBar tmpMenuBar = (MenuBar) FXMLUtil.loadAndRampUpRegion("view/MenuBar.fxml", stage);
        documentsPane = (VBox)FXMLUtil.loadAndRampUpRegion("view/DocumentsPane.fxml", stage);
        displayFilePane = (Pane)FXMLUtil.loadAndRampUpRegion("view/DisplayFilePane.fxml", stage);
        infoPane = (VBox)FXMLUtil.loadAndRampUpRegion("view/InfoPane.fxml", stage);
        chooseFilesOrDirectoryPane = (VBox)FXMLUtil.loadAndRampUpRegion(
                "view/ChooseFilesOrDirectoryPane.fxml", stage);

        // Set dimensions
        final double tmpStageWidthTenth = (aStage.getWidth() / 10);
        documentsPane.setPrefWidth(tmpStageWidthTenth * 2);
        displayFilePane.setPrefWidth(tmpStageWidthTenth * 5);
        displayFilePane.setPrefHeight(aStage.getHeight());
        infoPane.setPrefWidth(tmpStageWidthTenth * 3);

        // Set MenuBar
        rootPane.setTop(tmpMenuBar);

        // Add change listeners
        addCurrentDocumentChangedListener(aChange -> handleCurrentDocumentChanged((File)aChange.getValueAdded()));
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (aNewCurrentDocument != null) {

            stage.setTitle("Archiving: ".concat(aNewCurrentDocument.getPath()));
            rootPane.setLeft(documentsPane);
            rootPane.setCenter(displayFilePane);
            rootPane.setRight(infoPane);
        }
        else {

            stage.setTitle("Choose file(s) or directory");
            rootPane.setLeft(null);
            rootPane.setCenter(chooseFilesOrDirectoryPane);
            rootPane.setRight(null);
        }
    }

}
