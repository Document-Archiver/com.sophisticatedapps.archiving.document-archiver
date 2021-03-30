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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<MenuBarController,MenuBar> tmpMenuBarControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/MenuBar.fxml", aStage);
        menuBarController = tmpMenuBarControllerRegionPair.getController();
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
        MenuBarController.DialogProvider tmpMockedDialogProvider = Mockito.mock(MenuBarController.DialogProvider.class);
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

        MenuBarController.DialogProvider tmpMockedDialogProvider = Mockito.mock(MenuBarController.DialogProvider.class);

        Alert tmpMockedPreferencesChangedAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.providePreferencesChangedAlert()).thenReturn(tmpMockedPreferencesChangedAlert);

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
    void handleQuitMenuItemAction() {

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        menuBarController.stage = tmpMockedStage;

        menuBarController.handleQuitMenuItemAction();

        verify(tmpMockedStage, Mockito.times(1)).hide();
    }

    @Test
    void handleOpenFilesOrDirectoryMenuItemAction() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);
        menuBarController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        menuBarController.handleOpenFilesOrDirectoryMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(menuBarController.getAllDocuments());
        assertNull(menuBarController.getCurrentDocument());
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
        MenuBarController.DialogProvider tmpMockedDialogProvider = Mockito.mock(MenuBarController.DialogProvider.class);
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

    @Test
    void testDialogProvider_provideAboutDialog() {

        MenuBarController.DialogProvider tmpDialogProvider = new MenuBarController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideAboutDialog()));

        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpAlert = tmpDialogList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Copyright"));
    }

    @Test
    void testDialogProvider_providePreferencesDialog() {

        MenuBarController.DialogProvider tmpDialogProvider = new MenuBarController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        PreferencesPaneController tmpMockedController = Mockito.mock(PreferencesPaneController.class);
        Pane tmpMockedPane = new Pane();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.providePreferencesDialog(tmpMockedPane)));

        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertSame(tmpMockedPane, tmpDialog.getDialogPane().getContent());
    }

    @Test
    void testDialogProvider_providePreferencesChangedAlert() {

        MenuBarController.DialogProvider tmpDialogProvider = new MenuBarController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.providePreferencesChangedAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Preferences have been saved"));
    }

    @Test
    void testDialogProvider_providePreferencesChangedAlert_with_Locale() {

        MenuBarController.DialogProvider tmpDialogProvider = new MenuBarController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.providePreferencesChangedAlert(Locale.GERMAN)));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Einstellungen wurden gespeichert"));
    }

}
