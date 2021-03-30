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

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MenuBarController extends BaseController {

    private static final Map<String,Locale> LOCALES_BY_MENU_ITEM_MAP = Map.of(
            "englishLanguageMenuItem", Locale.ENGLISH, "germanLanguageMenuItem", Locale.GERMAN);
    private DialogProvider dialogProvider;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public MenuBarController() {

        this(new DialogProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom AlertProvider.
     * @param   anDialogProvider Custom AlertProvider
     */
    public MenuBarController(DialogProvider anDialogProvider) {

        this.dialogProvider = anDialogProvider;
    }

    @FXML
    protected void handleAboutMenuItemAction() {

        this.dialogProvider.provideAboutDialog().showAndWait();
    }

    @FXML
    protected void handlePreferencesMenuItemAction() {

        // Load preferences Pane
        FXMLUtil.ControllerRegionPair<PreferencesPaneController, Pane> tmpPreferencesPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/PreferencesPane.fxml", stage);
        Pane tmpPreferencesPane = tmpPreferencesPaneControllerRegionPair.getRegion();
        PreferencesPaneController tmpPreferencesPaneController =
                tmpPreferencesPaneControllerRegionPair.getController();

        // Create and show Dialog
        Optional<ButtonType> tmpResult = dialogProvider.providePreferencesDialog(tmpPreferencesPane).showAndWait();

        // Deal with result
        tmpResult.ifPresent(aButtonType -> {

            if (ButtonType.OK == aButtonType) {

                Pair<String, String> tmpArchivingPathPropertiesPair = new Pair<>(
                        PropertiesUtil.KEY_ARCHIVING_PATH, tmpPreferencesPaneController.getArchivingFolder());
                Pair<String, String> tmpQuickDescriptionWordsPropertiesPair = new Pair<>(
                        PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS,
                        tmpPreferencesPaneController.getQuickDescriptionWords());

                try {

                    PropertiesUtil.updateApplicationProperties(tmpArchivingPathPropertiesPair,
                            tmpQuickDescriptionWordsPropertiesPair);
                    dialogProvider.providePreferencesChangedAlert().showAndWait();
                }
                catch (IOException e) {

                    throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
                }
            }
        });

        tmpPreferencesPaneController.rampDown();
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
            dialogProvider.providePreferencesChangedAlert(tmpNewLocale).showAndWait();
        }
    }

    protected static class DialogProvider {

        public Dialog<ButtonType> provideAboutDialog() {

            return (new Alert(Alert.AlertType.NONE,
                    LanguageUtil.i18n("menu-bar-controller.alert-provider.about-alert"),
                    ButtonType.CLOSE));
        }

        public Dialog<ButtonType> providePreferencesDialog(Pane aPreferencesPane) {

            Dialog<ButtonType> tmpDialog = new Dialog<>();
            tmpDialog.setTitle(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.title"));
            tmpDialog.setHeaderText(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.header-text"));
            tmpDialog.getDialogPane().setContent(aPreferencesPane);
            tmpDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            return tmpDialog;
        }

        public Alert providePreferencesChangedAlert() {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.alert-provider.preferences-changed-alert"),
                    ButtonType.CLOSE));
        }

        public Alert providePreferencesChangedAlert(Locale aLanguageLocale) {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.alert-provider.preferences-changed-alert",
                            aLanguageLocale),
                    ButtonType.CLOSE));
        }
    }

}
