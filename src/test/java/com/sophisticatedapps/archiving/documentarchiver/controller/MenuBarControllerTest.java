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
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.ArchiveBrowsingService;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.MenuBarController".
 */
@ExtendWith(ApplicationExtension.class)
class MenuBarControllerTest extends BaseTest {

    @TempDir
    File tempDir;

    //private MenuBar menuBar;
    private MenuBarController menuBarController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException, IllegalAccessException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestDirectorySelection(any(Stage.class))).thenReturn(TEST_SOURCE_FOLDER);
        when(tmpMockedApplicationServices.requestMultipleFilesSelection(any(Stage.class)))
                .thenReturn(ALL_DOCUMENTS_LIST);

        // Set the test archiving folder as core archiving folder, so the reading of tenants delivers something.
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", TEST_ARCHIVING_FOLDER, true);

        FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MenuBar.fxml"));
        tmpLoader.setResources(LanguageUtil.getResourceBundleForCurrentLanguage());
        //menuBar =
        tmpLoader.load();
        menuBarController = tmpLoader.getController();
        menuBarController.rampUp(getApp(aStage, tmpMockedApplicationServices));
    }

    @AfterEach
    public void cleanUpEach() throws IllegalAccessException {

        menuBarController.rampDown();

        //menuBar = null;
        menuBarController = null;

        // Set back core archiving folder
        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void handleAboutMenuItemAction() throws IllegalAccessException {

        Alert tmpMockedAboutAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideAboutDialog()).thenReturn(tmpMockedAboutAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        menuBarController.handleAboutMenuItemAction();

        verify(tmpMockedAboutAlert, Mockito.times(1)).showAndWait();
    }

    @Test
    void handleCheckForUpdatesMenuItemAction() throws IllegalAccessException, IOException {

        HostServices tmpMockedHostServices = Mockito.mock(HostServices.class);
        menuBarController.stage.getProperties().put(GlobalConstants.HOST_SERVICES_PROPERTY_KEY, tmpMockedHostServices);

        Alert tmpMockedUpdateCheckDialog = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideUpdateCheckDialog()).thenReturn(tmpMockedUpdateCheckDialog);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        // First run with cancel button
        when(tmpMockedUpdateCheckDialog.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
        menuBarController.handleCheckForUpdatesMenuItemAction();
        verify(tmpMockedUpdateCheckDialog, Mockito.times(1)).showAndWait();
        verify(tmpMockedHostServices, Mockito.times(0)).showDocument(any(String.class));

        // Second run with "open download site" button
        when(tmpMockedUpdateCheckDialog.showAndWait()).thenReturn(
                Optional.of(new ButtonType("Foo", ButtonBar.ButtonData.LEFT)));
        menuBarController.handleCheckForUpdatesMenuItemAction();
        verify(tmpMockedUpdateCheckDialog, Mockito.times(2)).showAndWait();
        verify(tmpMockedHostServices, Mockito.times(1)).showDocument(any(String.class));
    }

    @Test
    void handlePreferencesMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        when(tmpMockedDialogProvider.providePreferencesChangedAlert(any(Locale.class)))
                .thenReturn(tmpMockedPreferencesChangedAlert);

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedDialog = Mockito.mock(Dialog.class);
        when(tmpMockedDialog.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        doAnswer(anInvocationOnMock -> {
            Pane tmpPreferencesPane = anInvocationOnMock.getArgument(0);
            ((TextField)tmpPreferencesPane.getChildren().get(1)).setText("/foo/bar/snafu");
            ((TextArea)tmpPreferencesPane.getChildren().get(3)).setText("ha ,hi, ho");
            return tmpMockedDialog;
        }).when(tmpMockedDialogProvider).providePreferencesDialog(any(Pane.class));

        FieldUtils.writeField(menuBarController,"dialogProvider", tmpMockedDialogProvider, true);

        // Call method
        menuBarController.handlePreferencesMenuItemAction();

        // Read the newly written properties
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("/foo/bar/snafu", tmpReadProperties.getProperty(PropertiesUtil.KEY_ARCHIVING_PATH));
        assertEquals("ha,hi,ho", tmpReadProperties.getProperty(PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS));

        // Run again - this time with closing the App
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        Platform.runLater(() -> menuBarController.handlePreferencesMenuItemAction());
        WaitForAsyncUtils.waitForFxEvents();

        verify(menuBarController.app.getApplicationServices(), Mockito.times(1)).restartApp();

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void handlePreferencesMenuItemAction_with_exception() throws IllegalAccessException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, NUL_CHARACTER_STRING);
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert(any(Locale.class)))
                .thenReturn(tmpMockedPreferencesChangedAlert);

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedDialog = Mockito.mock(Dialog.class);
        when(tmpMockedDialog.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        doAnswer(anInvocationOnMock -> {
            Pane tmpPreferencesPane = anInvocationOnMock.getArgument(0);
            ((TextField)tmpPreferencesPane.getChildren().get(1)).setText("/foo/bar/snafu");
            ((TextArea)tmpPreferencesPane.getChildren().get(3)).setText("ha ,hi, ho");
            return tmpMockedDialog;
        }).when(tmpMockedDialogProvider).providePreferencesDialog(any(Pane.class));

        FieldUtils.writeField(menuBarController,"dialogProvider", tmpMockedDialogProvider, true);

        // Call method
        Throwable tmpException =
                assertThrows(RuntimeException.class, () -> menuBarController.handlePreferencesMenuItemAction());
        assertEquals("Could not write properties: Invalid file path", tmpException.getMessage());

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void testHandleChangeTenantMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert(any(Locale.class)))
                .thenReturn(tmpMockedPreferencesChangedAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        MenuItem tmpMockedMenuItem = Mockito.mock(MenuItem.class);
        ActionEvent tmpMockedActionEvent = Mockito.mock(ActionEvent.class);
        when(tmpMockedActionEvent.getSource()).thenReturn(tmpMockedMenuItem);

        // Select with current tenant
        when(tmpMockedMenuItem.getId()).thenReturn(PropertiesUtil.ACTIVE_TENANT.getName().concat("TenantMenuItem"));
        menuBarController.handleChangeTenantMenuItemAction(tmpMockedActionEvent);
        verify(tmpMockedPreferencesChangedAlert, Mockito.times(0)).showAndWait();

        // Select with not current tenant
        when(tmpMockedMenuItem.getId()).thenReturn("SnaFuTenantMenuItem");
        menuBarController.handleChangeTenantMenuItemAction(tmpMockedActionEvent);
        verify(tmpMockedPreferencesChangedAlert, Mockito.times(1)).showAndWait();

        // Read properties in
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("SnaFu", tmpReadProperties.getProperty(PropertiesUtil.KEY_ACTIVE_TENANT));

        // Run again - this time with closing the App
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        Platform.runLater(() -> menuBarController.handleChangeTenantMenuItemAction(tmpMockedActionEvent));
        WaitForAsyncUtils.waitForFxEvents();

        verify(menuBarController.app.getApplicationServices(), Mockito.times(1)).restartApp();

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void handleManageTenantsMenuItemAction() throws IllegalAccessException {

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedDialog = Mockito.mock(Dialog.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideManageTenantsDialog(any(Pane.class))).thenReturn(tmpMockedDialog);
        FieldUtils.writeField(menuBarController,"dialogProvider", tmpMockedDialogProvider, true);

        menuBarController.handleManageTenantsMenuItemAction();

        verify(tmpMockedDialog, Mockito.times(1)).showAndWait();
    }

    @Test
    void handleQuitMenuItemAction() {

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        menuBarController.stage = tmpMockedStage;

        menuBarController.handleQuitMenuItemAction();

        verify(tmpMockedStage, Mockito.times(1)).hide();
    }

    @Test
    void handleOpenFilesMenuItemAction() {

        menuBarController.handleOpenFilesMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        // Not same, since List will be wrapped into a new List.
        assertTrue(menuBarController.getAllDocuments().containsAll(ALL_DOCUMENTS_LIST));
        assertSame(TEST_JPG_FILE2, menuBarController.getCurrentDocument());
    }

    @Test
    void handleOpenDirectoryMenuItemAction() {

        menuBarController.handleOpenDirectoryMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        List<File> tmpChosenDocuments = menuBarController.getAllDocuments();
        assertEquals(ALL_DOCUMENTS_LIST.size(), tmpChosenDocuments.size());
        assertTrue(tmpChosenDocuments.contains(TEST_TEXT_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_TEXT_FILE2));
        assertTrue(tmpChosenDocuments.contains(TEST_PDF_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_JPG_FILE));
        assertTrue(tmpChosenDocuments.contains(TEST_JPG_FILE2));
        assertTrue(tmpChosenDocuments.contains(TEST_MP3_FILE));
    }

    @Test
    void testHandleChangeThemeMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        Platform.runLater(() -> {

            // We have to set a Scene to the stage, otherwise the style cannot be set.
            menuBarController.stage.setScene(new Scene(new Pane()));

            MenuItem tmpMockedMenuItem = Mockito.mock(MenuItem.class);
            when(tmpMockedMenuItem.getId()).thenReturn("darkThemeMenuItem");
            ActionEvent tmpMockedActionEvent = Mockito.mock(ActionEvent.class);
            when(tmpMockedActionEvent.getSource()).thenReturn(tmpMockedMenuItem);

            menuBarController.handleChangeThemeMenuItemAction(tmpMockedActionEvent);
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(ThemeUtil.ThemeEnum.DARK.getPathToCss(), menuBarController.stage.getScene().getStylesheets().get(0));
        // Properties updated?
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");
        assertEquals("DARK", tmpReadProperties.getProperty(PropertiesUtil.KEY_APPEARANCE_THEME));

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void handleToggleFullScreenMenuItemAction() {

        final List<Boolean> tmpFullScreenInformationList = new ArrayList<>();
        tmpFullScreenInformationList.add(Boolean.FALSE);

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doAnswer(anInvocationOnMock -> {
            tmpFullScreenInformationList.set(0, anInvocationOnMock.getArgument(0));
            return null;
        }).when(tmpMockedStage).setFullScreen(anyBoolean());
        doAnswer(anInvocationOnMock -> tmpFullScreenInformationList.get(0)).when(tmpMockedStage).isFullScreen();
        menuBarController.stage = tmpMockedStage;

        Platform.runLater(() -> menuBarController.handleToggleFullScreenMenuItemAction());
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(tmpFullScreenInformationList.get(0));

        Platform.runLater(() -> menuBarController.handleToggleFullScreenMenuItemAction());
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(tmpFullScreenInformationList.get(0));
    }

    //@Test
    void xhandleArchiveBrowserMenuItemAction() throws IllegalAccessException {

        // First round with download denied
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        Alert tmpMockedPluginNotAvailableAlert = Mockito.mock(Alert.class);
        when(tmpMockedPluginNotAvailableAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        when(tmpMockedDialogProvider.providePluginNotAvailableAlert()).thenReturn(tmpMockedPluginNotAvailableAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        Platform.runLater(() -> menuBarController.handleArchiveBrowserMenuItemAction());
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedPluginNotAvailableAlert, Mockito.times(1)).showAndWait();

        // Second round with download
        when(tmpMockedPluginNotAvailableAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        Platform.runLater(() -> menuBarController.handleArchiveBrowserMenuItemAction());
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedPluginNotAvailableAlert, Mockito.times(2)).showAndWait();

        // Now the ArchiveBrowser plugin should be available.
        assertTrue(PluginUtil.isPluginAvailable(ArchiveBrowsingService.class));
        assertTrue(PluginUtil.isArchiveBrowsingPluginUpToDate());

        // Test to fire up the plugin
        Stage tmpMockedStage = Mockito.mock(Stage.class);
        @SuppressWarnings("unchecked")
        ObservableMap<Object, Object> tmpMockedStageProperties = Mockito.mock(ObservableMap.class);
        when(tmpMockedStage.getProperties()).thenReturn(tmpMockedStageProperties);
        ReadOnlyDoubleProperty tmpMockedReadOnlyDoubleProperty = Mockito.mock(ReadOnlyDoubleProperty.class);
        when(tmpMockedStage.widthProperty()).thenReturn(tmpMockedReadOnlyDoubleProperty);
        when(tmpMockedStage.heightProperty()).thenReturn(tmpMockedReadOnlyDoubleProperty);
        PluginUtil.fireArchiveBrowsingPlugin(tmpMockedStage);
        verify(tmpMockedStage, Mockito.times(1)).setScene(any(Scene.class));
    }

    @Test
    void handleHelpMenuItemAction() {

        HostServices tmpMockedHostServices = Mockito.mock(HostServices.class);
        menuBarController.stage.getProperties().put(GlobalConstants.HOST_SERVICES_PROPERTY_KEY, tmpMockedHostServices);

        menuBarController.handleHelpMenuItemAction();

        verify(tmpMockedHostServices, Mockito.times(1)).showDocument(any(String.class));
    }

    @Test
    void handleSystemInformationMenuItemAction() throws IllegalAccessException {

        @SuppressWarnings("unchecked")
        Dialog<ButtonType> tmpMockedSystemInformationDialog = Mockito.mock(Dialog.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideSystemInformationDialog(anyDouble(), anyDouble()))
                .thenReturn(tmpMockedSystemInformationDialog);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        menuBarController.handleSystemInformationMenuItemAction();
        verify(tmpMockedSystemInformationDialog, Mockito.times(1)).showAndWait();
    }

    @Test
    void testHandleChangeLanguageMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert(any(Locale.class)))
                .thenReturn(tmpMockedPreferencesChangedAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        MenuItem tmpMockedMenuItem = Mockito.mock(MenuItem.class);
        ActionEvent tmpMockedActionEvent = Mockito.mock(ActionEvent.class);
        when(tmpMockedActionEvent.getSource()).thenReturn(tmpMockedMenuItem);

        // Select with current language
        when(tmpMockedMenuItem.getId()).thenReturn("englishLanguageMenuItem");
        menuBarController.handleChangeLanguageMenuItemAction(tmpMockedActionEvent);
        verify(tmpMockedPreferencesChangedAlert, Mockito.times(0)).showAndWait();

        // Select with not current language
        when(tmpMockedMenuItem.getId()).thenReturn("germanLanguageMenuItem");
        menuBarController.handleChangeLanguageMenuItemAction(tmpMockedActionEvent);
        verify(tmpMockedPreferencesChangedAlert, Mockito.times(1)).showAndWait();

        // Read properties in
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("de", tmpReadProperties.getProperty(PropertiesUtil.KEY_LANGUAGE_LOCALE));

        // Run again - this time with closing the App
        when(tmpMockedPreferencesChangedAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        Platform.runLater(() -> menuBarController.handleChangeLanguageMenuItemAction(tmpMockedActionEvent));
        WaitForAsyncUtils.waitForFxEvents();

        verify(menuBarController.app.getApplicationServices(), Mockito.times(1)).restartApp();

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

}
