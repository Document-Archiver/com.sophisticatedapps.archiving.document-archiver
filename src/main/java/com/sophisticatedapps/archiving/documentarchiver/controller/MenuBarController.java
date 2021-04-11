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
import com.sophisticatedapps.archiving.documentarchiver.util.CollectionUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuBarController extends BaseController {

    private static final Map<String, String> THEMES_BY_MENU_ITEM_MAP = Map.of(
            "lightThemeMenuItem", GlobalConstants.LIGHT_THEME,
            "darkThemeMenuItem", GlobalConstants.DARK_THEME);

    private static final Map<String, Locale> LOCALES_BY_MENU_ITEM_MAP = Map.of(
            "englishLanguageMenuItem", Locale.ENGLISH,
            "germanLanguageMenuItem", Locale.GERMAN,
            "spanishLanguageMenuItem", Locale.forLanguageTag("es"));

    private DialogProvider dialogProvider;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public MenuBarController() {

        this(new DialogProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom DialogProvider.
     *
     * @param   aDialogProvider Custom DialogProvider
     */
    public MenuBarController(DialogProvider aDialogProvider) {

        this(new FileChooser(), new DirectoryChooser(), aDialogProvider);
    }

    /**
     * Alternative constructor which allows to pass custom File- and DirectoryChooser and DialogProvider.
     *
     * @param   aFileChooser        FileChooser to use.
     * @param   aDirectoryChooser   DirectoryChooser to use.
     * @param   aDialogProvider     DialogProvider to use.
     */
    public MenuBarController(FileChooser aFileChooser, DirectoryChooser aDirectoryChooser,
                             DialogProvider aDialogProvider) {

        this.fileChooser = aFileChooser;
        this.directoryChooser = aDirectoryChooser;
        this.dialogProvider = aDialogProvider;
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
    protected void handleOpenFilesMenuItemAction() {

        List<File> tmpFilesList = fileChooser.showOpenMultipleDialog(stage);

        if (!CollectionUtil.isNullOrEmpty(tmpFilesList)) {

            // We have to wrap the result in a new List, since the result is not modifiable.
            setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
        }
    }

    @FXML
    protected void handleOpenDirectoryMenuItemAction() {

        File tmpDirectory = directoryChooser.showDialog(stage);

        if (!Objects.isNull(tmpDirectory)) {

            List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(
                    tmpDirectory.listFiles(aFile -> (aFile.isFile() && (!aFile.isHidden())))));

            if (!tmpFilesList.isEmpty()) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                setNewAllDocumentsAndCurrentDocument((new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
            }
            else {

                dialogProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
            }
        }
    }

    @FXML
    protected void handleChangeThemeMenuItemAction(ActionEvent anEvent) {

        String tmpNewTheme = THEMES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());

        ObservableList<String> tmpSceneStylesheets = stage.getScene().getStylesheets();
        tmpSceneStylesheets.clear();
        tmpSceneStylesheets.add(tmpNewTheme);
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
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.about-dialog"),
                    ButtonType.CLOSE));
        }

        public Dialog<ButtonType> providePreferencesDialog(Pane aPreferencesPane) {

            Dialog<ButtonType> tmpDialog = new Dialog<>();
            tmpDialog.setTitle(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.title"));
            tmpDialog.setHeaderText(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.header-text"));

            DialogPane tmpDialogPane = tmpDialog.getDialogPane();
            tmpDialogPane.setContent(aPreferencesPane);
            tmpDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            tmpDialogPane.getStylesheets().add(GlobalConstants.DARK_THEME);

            return tmpDialog;
        }

        public Alert providePreferencesChangedAlert() {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.preferences-changed-alert"),
                    ButtonType.CLOSE));
        }

        public Alert providePreferencesChangedAlert(Locale aLanguageLocale) {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.preferences-changed-alert",
                            aLanguageLocale),
                    ButtonType.CLOSE));
        }

        public Alert provideDirectoryDoesNotContainFilesAlert() {

            return (new Alert(Alert.AlertType.WARNING,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.directory-does-not-contain-files-alert"),
                    ButtonType.CLOSE));
        }
    }

}
