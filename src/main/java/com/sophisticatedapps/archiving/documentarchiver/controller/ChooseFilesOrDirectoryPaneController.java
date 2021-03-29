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

import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
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

    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    private AlertProvider alertProvider;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public ChooseFilesOrDirectoryPaneController() {

        this(new AlertProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom AlertProvider.
     *
     * @param   anAlertProvider AlertProvider to use.
     */
    public ChooseFilesOrDirectoryPaneController(AlertProvider anAlertProvider) {

        this(new FileChooser(), new DirectoryChooser(), anAlertProvider);
    }

    /**
     * Alternative constructor which allows to pass custom File- and DirectoryChooser and AlertProvider.
     *
     * @param   aFileChooser        FileChooser to use.
     * @param   aDirectoryChooser   DirectoryChooser to use.
     * @param   anAlertProvider     AlertProvider to use.
     */
    public ChooseFilesOrDirectoryPaneController(FileChooser aFileChooser, DirectoryChooser aDirectoryChooser,
                                                AlertProvider anAlertProvider) {

        this.fileChooser = aFileChooser;
        this.directoryChooser = aDirectoryChooser;
        this.alertProvider = anAlertProvider;
    }

    @FXML
    protected void handleChooseFilesButtonAction() {

        List<File> tmpFilesList = fileChooser.showOpenMultipleDialog(stage);

        if ((tmpFilesList != null) && (!tmpFilesList.isEmpty())) {

            // We have to wrap the result in a new List, since the result is not modifiable.
            setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
        }
        else {

            setNewAllDocumentsAndCurrentDocument(null, null);
        }
    }

    @FXML
    protected void handleChooseDirectoryButtonAction() {

        File tmpDirectory = directoryChooser.showDialog(stage);

        if (tmpDirectory != null) {

            List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(tmpDirectory.listFiles(File::isFile)));

            if (!tmpFilesList.isEmpty()) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
            }
            else {

                setNewAllDocumentsAndCurrentDocument(null, null);
                alertProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
            }
        }
        else {

            setNewAllDocumentsAndCurrentDocument(null, null);
        }
    }

    protected static class AlertProvider {

        public Alert provideDirectoryDoesNotContainFilesAlert() {

            return (new Alert(Alert.AlertType.WARNING,
                    LanguageUtil.i18n("choose-files-or-directory-pane-controller.alert-provider.directory-does-not-contain-files-alert"),
                    ButtonType.CLOSE));
        }
    }

}
