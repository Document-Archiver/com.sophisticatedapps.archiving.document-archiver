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
import com.sophisticatedapps.archiving.documentarchiver.api.ArchiveBrowsingService;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
    ToggleGroup tenantToggleGroup;

    @FXML
    Menu tenantMenu;

    @FXML
    Menu appearanceMenu;

    @FXML
    Menu languageMenu;

    @FXML
    MenuItem toggleFullScreenMenuItem;

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

        // Add tenants to tenants menu
        List<String> tmpTenantsList = TenantUtil.getAvailableTenants();

        for (String tmpCurrentTenant : tmpTenantsList) {

            RadioMenuItem tmpRadioMenuItem = new RadioMenuItem(tmpCurrentTenant);
            tmpRadioMenuItem.setId(tmpCurrentTenant.concat("TenantMenuItem"));
            tmpRadioMenuItem.setToggleGroup(tenantToggleGroup);
            tmpRadioMenuItem.setOnAction(this::handleChangeTenantMenuItemAction);
            tenantMenu.getItems().add(tmpRadioMenuItem);
        }

        selectCurrentTenantRadioMenuItem(PropertiesUtil.ACTIVE_TENANT);
        selectCurrentThemeRadioMenuItem(THEMES_BY_MENU_ITEM_MAP.getKey(PropertiesUtil.APPEARANCE_THEME));
        selectCurrentLanguageRadioMenuItem(LOCALES_BY_MENU_ITEM_MAP.getKey(LanguageUtil.getCurrentLanguageLocale()));
    }

    private void selectCurrentTenantRadioMenuItem(String anActiveTenant) {

        selectCurrentRadioMenuItem(tenantMenu, anActiveTenant.concat("TenantMenuItem"));
    }

    private void selectCurrentThemeRadioMenuItem(String aThemeRadioMenuItemId) {

        selectCurrentRadioMenuItem(appearanceMenu, aThemeRadioMenuItemId);
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

                askForRestartAfterPropertiesChanged();
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
            }
        }

        tmpPreferencesPaneController.rampDown();
    }

    @FXML
    protected void handleChangeTenantMenuItemAction(ActionEvent anEvent) {

        String tmpMenuItemId = ((MenuItem)anEvent.getSource()).getId();
        // Tenant name is "FooBarTenantMenuItem" minus the last 14 chars ("TenantMenuItem") -> "FooBar"
        String tmpTenant = tmpMenuItemId.substring(0, (tmpMenuItemId.length() - 14));

        if (!PropertiesUtil.ACTIVE_TENANT.equals(tmpTenant)) {

            try {

                PropertiesUtil.updateActiveTenant(tmpTenant);

                askForRestartAfterPropertiesChanged();
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
            }
        }
    }

    @FXML
    protected void handleQuitMenuItemAction() {

        stage.hide();
    }

    @FXML
    protected void handleOpenFilesMenuItemAction() {

        importFreshFilesList(fileChooser.showOpenMultipleDialog(stage));
    }

    @FXML
    protected void handleOpenDirectoryMenuItemAction() {

        importFreshDirectory(directoryChooser.showDialog(stage));
    }

    @FXML
    protected void handleChangeThemeMenuItemAction(ActionEvent anEvent) {

        ThemeUtil.ThemeEnum tmpNewTheme = THEMES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());
        ThemeUtil.setCurrentTheme(tmpNewTheme, stage.getScene());
    }

    @FXML
    protected void handleToggleFullScreenMenuItemAction() {

        boolean tmpToggleTo = (!stage.isFullScreen());
        toggleFullScreenMenuItem.setText(tmpToggleTo ?
                LanguageUtil.i18n("menu-bar.view-menu.exit-full-screen-menu-item") :
                LanguageUtil.i18n("menu-bar.view-menu.enter-full-screen-menu-item"));
        stage.setFullScreen(tmpToggleTo);
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    protected void handleArchiveBrowserMenuItemAction() {

        if ((!PluginUtil.isPluginAvailable(ArchiveBrowsingService.class)) ||
                (!PluginUtil.isArchiveBrowsingPluginUpToDate())) {

            Optional<ButtonType> tmpShallDownloadResult = dialogProvider.providePluginNotAvailableAlert().showAndWait();

            // Should the plugin be downloaded?
            if (ButtonBar.ButtonData.YES == tmpShallDownloadResult.get().getButtonData()) { // NOSONAR

                try {

                    PluginUtil.addPluginFromURL(GlobalConstants.ARCHIVE_BROWSER_PLUGIN_URL, "ArchiveBrowser.jar");
                }
                catch (IOException e) {

                    dialogProvider.provideExceptionAlert(e).showAndWait();
                    return;
                }
            }
            else {

                return;
            }
        }

        Stage tmpPluginStage = assembleSubStage(0.9);
        PluginUtil.fireArchiveBrowsingPlugin(tmpPluginStage);
        tmpPluginStage.show();
    }

    @FXML
    protected void handleHelpMenuItemAction() {

        HostServices tmpHostServices =
                (HostServices)stage.getProperties().get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY);
        tmpHostServices.showDocument(GlobalConstants.WIKI_URL);
    }

    @FXML
    protected void handleSystemInformationMenuItemAction() {

        dialogProvider.provideSystemInformationDialog(
                (stage.getWidth() * 0.7), (stage.getHeight() * 0.7)).showAndWait();
    }

    @FXML
    protected void handleChangeLanguageMenuItemAction(ActionEvent anEvent) {

        Locale tmpNewLocale = LOCALES_BY_MENU_ITEM_MAP.get(((MenuItem)anEvent.getSource()).getId());

        if (!LanguageUtil.getCurrentLanguageLocale().equals(tmpNewLocale)) {

            LanguageUtil.setNewLanguage(tmpNewLocale);

            askForRestartAfterPropertiesChanged(tmpNewLocale);
        }
    }

    private void askForRestartAfterPropertiesChanged() {

        askForRestartAfterPropertiesChanged(LanguageUtil.getCurrentLanguageLocale());
    }

    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    private void askForRestartAfterPropertiesChanged(Locale aLocale) {

        Optional<ButtonType> tmpRestartResult = dialogProvider.providePreferencesChangedAlert(aLocale).showAndWait();

        // Should App be restarted?
        if (ButtonBar.ButtonData.YES == tmpRestartResult.get().getButtonData()) { // NOSONAR

            ApplicationRestart.builder().build().restartApp();
        }
    }

}
