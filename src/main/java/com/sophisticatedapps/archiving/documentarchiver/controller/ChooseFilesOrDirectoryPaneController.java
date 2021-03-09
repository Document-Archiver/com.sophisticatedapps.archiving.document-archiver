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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChooseFilesOrDirectoryPaneController extends BaseController {

    @FXML
    private void handleChooseFilesButtonAction(ActionEvent anActionEvent) {

        FileChooser tmpFileChooser = new FileChooser();
        List<File> tmpFilesList = tmpFileChooser.showOpenMultipleDialog(stage);

        if ((tmpFilesList != null) && (!tmpFilesList.isEmpty())) {

            // We have to wrap the result in a new List, since the result is not modifiable.
            setNewAllDocumentsAndCurrentFile((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
        }
    }

    @FXML
    private void handleChooseDirectoryButtonAction(ActionEvent anActionEvent) {

        DirectoryChooser tmpDirectoryChooser = new DirectoryChooser();
        File tmpDirectory = tmpDirectoryChooser.showDialog(stage);

        if (tmpDirectory != null) {

            List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(tmpDirectory.listFiles(File::isFile)));

            if (!tmpFilesList.isEmpty()) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                setNewAllDocumentsAndCurrentFile((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
            }
            else {

                Alert tmpAlert = new Alert(Alert.AlertType.WARNING,
                        "The chosen directory doesn't contain files.", ButtonType.CLOSE);
                tmpAlert.showAndWait();
            }
        }
    }

}
