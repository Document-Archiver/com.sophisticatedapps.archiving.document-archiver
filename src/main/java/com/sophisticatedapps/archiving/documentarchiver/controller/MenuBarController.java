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

import com.restart4j.ApplicationRestart;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuBarController extends BaseController {

    private static final BidiMap<String, ThemeUtil.ThemeEnum> THEMES_BY_MENU_ITEM_MAP = new DualHashBidiMap<>(Map.of(
            "lightThemeMenuItem", ThemeUtil.ThemeEnum.LIGHT,
            "darkThemeMenuItem", ThemeUtil.ThemeEnum.DARK,
            "autoThemeMenuItem", ThemeUtil.ThemeEnum.AUTO));

    private static final BidiMap<String, Locale> LOCALES_BY_MENU_ITEM_MAP = new DualHashBidiMap<>(Map.of(
            "englishLanguageMenuItem", Locale.ENGLISH,
            "germanLanguageMenuItem", Locale.GERMAN,
            "spanishLanguageMenuItem", Locale.forLanguageTag("es")));

    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    @FXML
    Menu viewMenu;

    @FXML
    Menu languageMenu;

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

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        selectCurrentThemeRadioMenuItem(THEMES_BY_MENU_ITEM_MAP.getKey(PropertiesUtil.APPEARANCE_THEME));
        selectCurrentLanguageRadioMenuItem(LOCALES_BY_MENU_ITEM_MAP.getKey(LanguageUtil.getCurrentLanguageLocale()));
    }

    private void selectCurrentThemeRadioMenuItem(String aLanguageRadioMenuItemId) {

        selectCurrentRadioMenuItem(viewMenu, aLanguageRadioMenuItemId);
    }

    private void selectCurrentLanguageRadioMenuItem(String aLanguageRadioMenuItemId) {

        selectCurrentRadioMenuItem(languageMenu, aLanguageRadioMenuItemId);
    }

    private void selectCurrentRadioMenuItem(Menu aMenu, String aRadioMenuItemId) {

        for (MenuItem tmpCurrentMenuItem : aMenu.getItems()) {

            ((RadioMenuItem)tmpCurrentMenuItem).setSelected(tmpCurrentMenuItem.getId().equals(aRadioMenuItemId));
        }
    }

    @FXML
    protected void handleAboutMenuItemAction() {

        this.dialogProvider.provideAboutDialog().showAndWait();
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
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
        if (ButtonType.OK == tmpResult.get()) { // NOSONAR

            Pair<String, String> tmpArchivingPathPropertiesPair = new Pair<>(
                    PropertiesUtil.KEY_ARCHIVING_PATH, tmpPreferencesPaneController.getArchivingFolder());
            Pair<String, String> tmpQuickDescriptionWordsPropertiesPair = new Pair<>(
                    PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS,
                    tmpPreferencesPaneController.getQuickDescriptionWords());

            try {

                PropertiesUtil.updateApplicationProperties(tmpArchivingPathPropertiesPair,
                        tmpQuickDescriptionWordsPropertiesPair);

                Optional<ButtonType> tmpRestartResult = dialogProvider.providePreferencesChangedAlert().showAndWait();

                // Should App be restarted?
                if (ButtonBar.ButtonData.YES == tmpRestartResult.get().getButtonData()) { // NOSONAR

                    ApplicationRestart.builder().build().restartApp();
                }
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
            }
        }

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
            List<File> tmpWrapperList = new ArrayList<>(tmpFilesList);
            tmpWrapperList.sort(Comparator.naturalOrder());
            setNewAllDocumentsAndCurrentDocument(tmpWrapperList, tmpWrapperList.get(0));
        }
    }

    @FXML
    protected void handleOpenDirectoryMenuItemAction() {

        File tmpDirectory = directoryChooser.showDialog(stage);

        if (!Objects.isNull(tmpDirectory)) {

            List<File> tmpWrapperList = new ArrayList<>();
            DirectoryUtil.readDirectoryRecursive(tmpDirectory, tmpWrapperList, (aFile -> (!aFile.isHidden())));

            if (!tmpWrapperList.isEmpty()) {

                tmpWrapperList.sort(Comparator.naturalOrder());
                setNewAllDocumentsAndCurrentDocument(tmpWrapperList, tmpWrapperList.get(0));
            }
            else {

                dialogProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
            }
        }
    }

    @FXML
    protected void handleChangeThemeMenuItemAction(ActionEvent anEvent) {

        ThemeUtil.ThemeEnum tmpNewTheme = THEMES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());
        ThemeUtil.setCurrentTheme(tmpNewTheme, stage.getScene());
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    protected void handleArchiveBrowserMenuItemAction() {

        if (!PluginUtil.isPluginAvailable("ArchiveBrowser")) {

            Optional<ButtonType> tmpShallDownloadResult = dialogProvider.providePluginNotAvailableAlert().showAndWait();

            /*
            // Should the plugin be downloaded?
            if (ButtonBar.ButtonData.YES == tmpShallDownloadResult.get().getButtonData()) { // NOSONAR

                InputStream in = new URL(FILE_URL).openStream();
                Files.copy(in, Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
            }
            */
        }
        else {

            Stage tmpPluginStage = assemblePluginStage();
            PluginUtil.fireArchiveBrowsingPlugin(tmpPluginStage);
            tmpPluginStage.show();
        }
    }

    @FXML
    protected void handleHelpMenuItemAction() {

        HostServices tmpHostServices =
                (HostServices)stage.getProperties().get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY);
        tmpHostServices.showDocument(GlobalConstants.WIKI_URL);
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    protected void handleChangeLanguageMenuItemAction(ActionEvent anEvent) {

        Locale tmpNewLocale = LOCALES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());

        if (!LanguageUtil.getCurrentLanguageLocale().equals(tmpNewLocale)) {

            LanguageUtil.setNewLanguage(tmpNewLocale);

            Optional<ButtonType> tmpRestartResult =
                    dialogProvider.providePreferencesChangedAlert(tmpNewLocale).showAndWait();

            // Should App be restarted?
            if (ButtonBar.ButtonData.YES == tmpRestartResult.get().getButtonData()) { // NOSONAR

                ApplicationRestart.builder().build().restartApp();
            }
        }
    }

}
