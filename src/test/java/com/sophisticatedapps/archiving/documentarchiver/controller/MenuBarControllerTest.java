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
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.ThemeUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
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
import static org.mockito.ArgumentMatchers.any;
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
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FileChooser tmpMockedFileChooser = Mockito.mock(FileChooser.class);
        when(tmpMockedFileChooser.showOpenMultipleDialog(any(Window.class))).thenReturn(ALL_DOCUMENTS_LIST);
        DirectoryChooser tmpMockedDirectoryChooser = Mockito.mock(DirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(TEST_RESOURCES_DIRECTORY);

        FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MenuBar.fxml"));
        tmpLoader.setResources(LanguageUtil.getResourceBundleForCurrentLanguage());
        tmpLoader.setControllerFactory(aParam -> new MenuBarController(
                tmpMockedFileChooser, tmpMockedDirectoryChooser, null));
        //menuBar =
        tmpLoader.load();
        menuBarController = tmpLoader.getController();
        menuBarController.rampUp(aStage);
    }

    @AfterEach
    public void cleanUpEach() {

        menuBarController.rampDown();

        //menuBar = null;
        menuBarController = null;
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
    void handlePreferencesMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert()).thenReturn(tmpMockedPreferencesChangedAlert);

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
        when(tmpMockedDialogProvider.providePreferencesChangedAlert()).thenReturn(tmpMockedPreferencesChangedAlert);

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
        assertSame(TEST_JPG_FILE, menuBarController.getCurrentDocument());
    }

    @Test
    void handleOpenFilesMenuItemAction_with_cancel() throws IllegalAccessException {

        FileChooser tmpMockedFileChooser = Mockito.mock(FileChooser.class);
        when(tmpMockedFileChooser.showOpenMultipleDialog(any(Window.class))).thenReturn(null);
        FieldUtils.writeField(menuBarController, "fileChooser", tmpMockedFileChooser, true);

        menuBarController.handleOpenFilesMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(menuBarController.getAllDocuments());
        assertNull(menuBarController.getCurrentDocument());
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
    void handleOpenDirectoryMenuItemAction_with_cancel() throws IllegalAccessException {

        DirectoryChooser tmpMockedDirectoryChooser = Mockito.mock(DirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(null);
        FieldUtils.writeField(menuBarController, "directoryChooser",
                tmpMockedDirectoryChooser, true);

        menuBarController.handleOpenDirectoryMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(menuBarController.getAllDocuments());
        assertNull(menuBarController.getCurrentDocument());
    }

    @Test
    void handleOpenDirectoryMenuItemAction__with_empty_folder() throws IllegalAccessException {

        DirectoryChooser tmpMockedDirectoryChooser = Mockito.mock(DirectoryChooser.class);
        when(tmpMockedDirectoryChooser.showDialog(any(Window.class))).thenReturn(TEST_ARCHIVING_FOLDER);
        FieldUtils.writeField(menuBarController, "directoryChooser",
                tmpMockedDirectoryChooser, true);

        Alert tmpDirectoryDoesNotContainFilesAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider =
                Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideDirectoryDoesNotContainFilesAlert())
                .thenReturn(tmpDirectoryDoesNotContainFilesAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        menuBarController.handleOpenDirectoryMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpDirectoryDoesNotContainFilesAlert, Mockito.times(1)).showAndWait();
        assertNull(menuBarController.getAllDocuments());
        assertNull(menuBarController.getCurrentDocument());
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
    void handleHelpMenuItemAction() {

        HostServices tmpMockedHostServices = Mockito.mock(HostServices.class);
        menuBarController.stage.getProperties().put(GlobalConstants.HOST_SERVICES_PROPERTY_KEY, tmpMockedHostServices);

        menuBarController.handleHelpMenuItemAction();

        verify(tmpMockedHostServices, Mockito.times(1)).showDocument(any(String.class));
    }

    @Test
    void testHandleChangeLanguageMenuItemAction() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        Alert tmpPreferencesChangedAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert(any(Locale.class)))
                .thenReturn(tmpPreferencesChangedAlert);
        FieldUtils.writeField(menuBarController, "dialogProvider", tmpMockedDialogProvider, true);

        MenuItem tmpMockedMenuItem = Mockito.mock(MenuItem.class);
        when(tmpMockedMenuItem.getId()).thenReturn("germanLanguageMenuItem");
        ActionEvent tmpMockedActionEvent = Mockito.mock(ActionEvent.class);
        when(tmpMockedActionEvent.getSource()).thenReturn(tmpMockedMenuItem);

        menuBarController.handleChangeLanguageMenuItemAction(tmpMockedActionEvent);

        // Alert triggered?
        verify(tmpPreferencesChangedAlert, Mockito.times(1)).showAndWait();

        // Read them in again
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("de", tmpReadProperties.getProperty(PropertiesUtil.KEY_LANGUAGE_LOCALE));

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

}
