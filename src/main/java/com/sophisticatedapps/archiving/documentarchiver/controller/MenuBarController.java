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
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MenuBarController extends BaseController {

    @FXML
    private void handleAboutMenuItemAction(ActionEvent anActionEvent) {

        Alert tmpAlert = new Alert(Alert.AlertType.NONE, "Copyright 2021 by Stephan Sann", ButtonType.CLOSE);
        tmpAlert.showAndWait();
    }

    @FXML
    private void handlePreferencesMenuItemAction(ActionEvent anActionEvent) {

        try {

            // Load preferences Pane
            FXMLLoader tmpPreferencesPaneLoader = new FXMLLoader();
            tmpPreferencesPaneLoader.setLocation(App.class.getResource("view/PreferencesPane.fxml"));
            VBox tmpPreferencesPane = tmpPreferencesPaneLoader.load();
            PreferencesPaneController tmpPreferencesPaneController = tmpPreferencesPaneLoader.getController();
            tmpPreferencesPaneController.rampUp(stage);

            // Create Dialog
            Dialog<Pair<String, String>> tmpDialog = new Dialog<>();
            tmpDialog.setTitle("Preferences");
            tmpDialog.setHeaderText("Change your archiving folder and/or your quick description words.");

            ButtonType tmpOkButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            tmpDialog.getDialogPane().getButtonTypes().addAll(tmpOkButtonType, ButtonType.CANCEL);

            tmpDialog.setResultConverter(aDialogButton -> {
                if (aDialogButton == tmpOkButtonType) {
                    return (new Pair<>(tmpPreferencesPaneController.getArchivingFolder(),
                            tmpPreferencesPaneController.getQuickDescriptionWords()));
                }
                return null;
            });

            tmpDialog.getDialogPane().setContent(tmpPreferencesPane);

            Optional<Pair<String, String>> tmpResult = tmpDialog.showAndWait();

            tmpResult.ifPresent(anResultPair -> {
                Properties tmpProperties = new Properties();
                tmpProperties.setProperty("archiving.path", anResultPair.getKey());
                tmpProperties.setProperty("quick.description.words", anResultPair.getValue());
                try {
                    FileUtil.writeProperties(GlobalConstants.PROPERTIES_FILE, tmpProperties);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Preferences have been saved and will be active with the next start of DocumentArchiver.",
                            ButtonType.CLOSE);
                    alert.showAndWait();
                }
                catch (IOException e) {
                    throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
                }
            });
        }
        catch (IOException e) {

            throw (new RuntimeException("Couldn't load preferences Pane: " + e.getMessage()));
        }
    }

    @FXML
    private void handleQuitMenuItemAction(ActionEvent anActionEvent) {

        Platform.exit();
    }

    @FXML
    private void handleOpenFilesOrDirectoryMenuItemAction(ActionEvent anActionEvent) {

        setNewAllDocumentsAndCurrentDocument(null, null);
    }

    @FXML
    private void handleHelpMenuItemAction(ActionEvent anActionEvent) {

        HostServices tmpHostServices =
                (HostServices)stage.getProperties().get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY);
        tmpHostServices.showDocument(GlobalConstants.WIKI_URL);
    }

}
