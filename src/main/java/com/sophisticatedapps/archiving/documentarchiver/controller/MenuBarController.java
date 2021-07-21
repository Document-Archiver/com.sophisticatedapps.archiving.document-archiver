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
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.api.ArchiveBrowsingService;
import com.sophisticatedapps.archiving.documentarchiver.api.impl.DefaultApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

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

        this.dialogProvider = aDialogProvider;
    }

    @Override
    public void rampUp(ApplicationContext anApplicationContext) {

        super.rampUp(anApplicationContext);

        // Add tenants to tenants menu
        List<String> tmpTenantNamesList = TenantUtil.getAvailableTenantNames();
        List<RadioMenuItem> tmpRadioMenuItems = new ArrayList<>();

        for (String tmpCurrentTenantName : tmpTenantNamesList) {

            RadioMenuItem tmpRadioMenuItem = new RadioMenuItem(tmpCurrentTenantName);
            tmpRadioMenuItem.setId(tmpCurrentTenantName.concat("TenantMenuItem"));
            tmpRadioMenuItem.setToggleGroup(tenantToggleGroup);
            tmpRadioMenuItem.setOnAction(this::handleChangeTenantMenuItemAction);
            tmpRadioMenuItems.add(tmpRadioMenuItem);
        }

        tenantMenu.getItems().addAll(1, tmpRadioMenuItems);

        selectCurrentTenantRadioMenuItem(PropertiesUtil.ACTIVE_TENANT.getName());
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

            if (aRadioMenuItemId.equals(tmpCurrentMenuItem.getId())) {

                ((RadioMenuItem)tmpCurrentMenuItem).setSelected(true);
                return;
            }
        }
    }

    @FXML
    protected void handleAboutMenuItemAction() {

        this.dialogProvider.provideAboutDialog().showAndWait();
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    protected void handleCheckForUpdatesMenuItemAction() {

        try {

            Optional<ButtonType> tmpResult = this.dialogProvider.provideUpdateCheckDialog().showAndWait();

            // Open download site?
            if (ButtonBar.ButtonData.LEFT == tmpResult.get().getButtonData()) { // NOSONAR

                applicationContext.getApplicationController().showExternalResource(GlobalConstants.DOWNLOAD_SITE_URL);
            }
        }
        catch (IOException e) {

            this.dialogProvider.provideExceptionAlert(e).showAndWait();
        }
    }

    @FXML
    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    protected void handlePreferencesMenuItemAction() {

        // Load preferences Pane
        FXMLUtil.ControllerRegionPair<PreferencesPaneController, Pane> tmpPreferencesPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/PreferencesPane.fxml", applicationContext);
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

                //noinspection unchecked
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
        String tmpTenantName = tmpMenuItemId.substring(0, (tmpMenuItemId.length() - 14));

        if (!PropertiesUtil.ACTIVE_TENANT.getName().equals(tmpTenantName)) {

            try {

                PropertiesUtil.updateActiveTenant(tmpTenantName);

                askForRestartAfterPropertiesChanged();
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
            }
        }
    }

    @FXML
    protected void handleManageTenantsMenuItemAction() {

        // Load manage tenants Pane
        FXMLUtil.ControllerRegionPair<ManageTenantsPaneController, Pane> tmpManageTenantsPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/ManageTenantsPane.fxml", applicationContext);
        Pane tmpManageTenantsPane = tmpManageTenantsPaneControllerRegionPair.getRegion();
        ManageTenantsPaneController tmpManageTenantsPaneController =
                tmpManageTenantsPaneControllerRegionPair.getController();

        // Create and show Dialog
        dialogProvider.provideManageTenantsDialog(tmpManageTenantsPane).showAndWait();

        if (tmpManageTenantsPaneController.isTenantsChanged()) {

            askForRestartAfterPropertiesChanged();
        }

        tmpManageTenantsPaneController.rampDown();
    }

    @FXML
    protected void handleQuitMenuItemAction() {

        stage.hide();
    }

    @FXML
    protected void handleOpenFilesMenuItemAction() {

        ApplicationController tmpApplicationController = applicationContext.getApplicationController();
        tmpApplicationController.importFreshFilesList(stage,
                tmpApplicationController.requestMultipleFilesSelection(stage));
    }

    @FXML
    protected void handleOpenDirectoryMenuItemAction() {

        ApplicationController tmpApplicationController = applicationContext.getApplicationController();
        tmpApplicationController.importFreshDirectory(stage, tmpApplicationController.requestDirectorySelection(stage));
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
        ApplicationContext tmpApplicationContext =
                new DefaultApplicationContext(applicationContext.getApplicationController(), tmpPluginStage);
        PluginUtil.fireArchiveBrowsingPlugin(tmpApplicationContext);
        tmpPluginStage.show();
    }

    @FXML
    protected void handleHelpMenuItemAction() {

        applicationContext.getApplicationController().showExternalResource(GlobalConstants.WIKI_URL);
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

            applicationContext.getApplicationController().restartApp();
        }
    }

}
