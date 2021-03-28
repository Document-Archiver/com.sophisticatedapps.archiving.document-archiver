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
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MenuBarController extends BaseController {

    private static final Map<String,Locale> LOCALES_BY_MENU_ITEM_MAP = Map.of(
            "englishLanguageMenuItem", Locale.ENGLISH, "germanLanguageMenuItem", Locale.GERMAN);
    private AlertProvider alertProvider;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public MenuBarController() {

        this(new AlertProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom AlertProvider.
     * @param   anAlertProvider Custom AlertProvider
     */
    public MenuBarController(AlertProvider anAlertProvider) {

        this.alertProvider = anAlertProvider;
    }

    @FXML
    protected void handleAboutMenuItemAction() {

        this.alertProvider.provideAboutAlert().showAndWait();
    }

    @FXML
    protected void handlePreferencesMenuItemAction() {

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
                Pair<String, String> tmpArchivingPathPropertiesPair =
                        new Pair<>(PropertiesUtil.KEY_ARCHIVING_PATH, anResultPair.getKey());
                Pair<String, String> tmpQuickDescriptionWordsPropertiesPair =
                        new Pair<>(PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS, anResultPair.getValue());
                try {
                    PropertiesUtil.updateApplicationProperties(tmpArchivingPathPropertiesPair,
                            tmpQuickDescriptionWordsPropertiesPair);
                    alertProvider.providePreferencesChangedAlert().showAndWait();
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
    protected void handleQuitMenuItemAction() {

        stage.hide();
    }

    @FXML
    protected void handleOpenFilesOrDirectoryMenuItemAction() {

        setNewAllDocumentsAndCurrentDocument(null, null);
    }

    @FXML
    protected void handleHelpMenuItemAction() {

        HostServices tmpHostServices =
                (HostServices)stage.getProperties().get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY);
        tmpHostServices.showDocument(GlobalConstants.WIKI_URL);
    }

    @FXML
    protected void handleChangeLanguageMenuItemAction(ActionEvent anEvent) {

        Locale tmpNewLocale = LOCALES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());

        if (!LanguageUtil.getCurrentLanguageLocale().equals(tmpNewLocale)) {

            LanguageUtil.setNewLanguage(tmpNewLocale);
            alertProvider.providePreferencesChangedAlert().showAndWait();
        }
    }

    protected static class AlertProvider {

        public Alert provideAboutAlert() {

            return (new Alert(Alert.AlertType.NONE,
                    "Copyright 2021 by Stephan Sann\n\nApplication icons made by Freepik (https://www.flaticon.com/free-icon/archives_170393)",
                    ButtonType.CLOSE));
        }

        public Alert providePreferencesChangedAlert() {

            return (new Alert(Alert.AlertType.INFORMATION,
                    "Preferences have been saved and will be active with the next start of DocumentArchiver.",
                    ButtonType.CLOSE));
        }
    }

}
