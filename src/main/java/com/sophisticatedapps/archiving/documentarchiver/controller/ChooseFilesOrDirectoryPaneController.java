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
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChooseFilesOrDirectoryPaneController extends BaseController {

    protected DaFileChooser fileChooser = new DaFileChooser();
    protected DaDirectoryChooser directoryChooser = new DaDirectoryChooser();

    @FXML
    private void handleChooseFilesButtonAction(ActionEvent anActionEvent) {

        List<File> tmpFilesList = fileChooser.showOpenMultipleDialog(stage);

        if ((tmpFilesList != null) && (!tmpFilesList.isEmpty())) {

            // We have to wrap the result in a new List, since the result is not modifiable.
            setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
        }
    }

    @FXML
    private void handleChooseDirectoryButtonAction(ActionEvent anActionEvent) {

        File tmpDirectory = directoryChooser.showDialog(stage);

        if (tmpDirectory != null) {

            List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(tmpDirectory.listFiles(File::isFile)));

            if (!tmpFilesList.isEmpty()) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
            }
            else {

                Alert tmpAlert = new Alert(Alert.AlertType.WARNING,
                        "The chosen directory doesn't contain files.", ButtonType.CLOSE);
                tmpAlert.showAndWait();
            }
        }
    }

    public static class DaFileChooser {

        private final FileChooser fileChooser = new FileChooser();

        /**
         * Shows a new file open dialog in which multiple files can be selected.
         * The method doesn't return until the displayed open dialog is dismissed.
         * The return value specifies the files chosen by the user or {@code null}
         * if no selection has been made. If the owner window for the file dialog is
         * set, input to all windows in the dialog's owner chain is blocked while
         * the file dialog is being shown.
         * <p>
         * The returned list is unmodifiable and will throw
         * {@code UnsupportedOperationException} on each modification attempt.
         *
         * @param anOwnerWindow the owner window of the displayed file dialog
         * @return the selected files or {@code null} if no file has been selected
         */
        public List<File> showOpenMultipleDialog(final Window anOwnerWindow) {

            return fileChooser.showOpenMultipleDialog(anOwnerWindow);
        }
    }

    public static class DaDirectoryChooser {

        private final DirectoryChooser directoryChooser = new DirectoryChooser();

        /**
         * Shows a new directory selection dialog. The method doesn't return until
         * the displayed dialog is dismissed. The return value specifies the
         * directory chosen by the user or {@code null} if no selection has been
         * made. If the owner window for the directory selection dialog is set,
         * input to all windows in the dialog's owner chain is blocked while the
         * dialog is being shown.
         *
         * @param anOwnerWindow the owner window of the displayed dialog
         * @return the selected directory or {@code null} if no directory has been
         *      selected
         */
        public File showDialog(final Window anOwnerWindow) {

            return directoryChooser.showDialog(anOwnerWindow);
        }
    }

}
