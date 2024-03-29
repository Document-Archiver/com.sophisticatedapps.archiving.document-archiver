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
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class BaseControllerTest extends BaseTest {

    private BaseController baseController;

    @Mock
    MapChangeListener<Object, Object> mapChangeListener;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, ALL_DOCUMENTS_LIST);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, TEST_TEXT_FILE);

        baseController = new BaseController(){};
        baseController.rampUp(getApplicationContext(aStage));
    }

    @Test
    void testRampDown() {

        // Ramp down
        baseController.rampDown();

        // Stage should be set to NULL now
        assertNull(baseController.stage);
    }

    @Test
    void getCurrentDocument() {

        final File tmpCurrentDocument = baseController.getCurrentDocument();
        assertSame(TEST_TEXT_FILE, tmpCurrentDocument);
    }

    @Test
    void getAllDocuments() {

        final List<File> tmpAllDocuments = baseController.getAllDocuments();
        assertSame(ALL_DOCUMENTS_LIST, tmpAllDocuments);
    }

    @Test
    void addListenerForProperty() {

        // Add listener
        baseController.addListenerForProperty(mapChangeListener, GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);

        // Change current document value
        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // Verify listener was called
        verify(mapChangeListener, Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void addAllDocumentsChangedListener() {

        // Add listener
        baseController.addAllDocumentsChangedListener(mapChangeListener);

        // Change all documents value
        baseController.setNewAllDocuments(Collections.singletonList(TEST_TEXT_FILE2));

        WaitForAsyncUtils.waitForFxEvents();

        // Verify listener was called
        verify(mapChangeListener,
                Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void addCurrentDocumentChangedListener() {

        // Add listener
        baseController.addCurrentDocumentChangedListener(mapChangeListener);

        // Change current document value
        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // Verify listener was called
        verify(mapChangeListener,
                Mockito.times(1)).onChanged(any(MapChangeListener.Change.class));
    }

    @Test
    void setNewAllDocumentsAndCurrentDocument() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);

        baseController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        assertSame(TEST_TEXT_FILE2, baseController.getCurrentDocument());
        assertSame(tmpNewAllDocuments, baseController.getAllDocuments());
    }

    @Test
    void setNewAllDocuments() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);

        baseController.setNewAllDocuments(tmpNewAllDocuments);

        WaitForAsyncUtils.waitForFxEvents();

        assertSame(tmpNewAllDocuments, baseController.getAllDocuments());
    }

    @Test
    void setNewCurrentDocument() {

        baseController.setNewCurrentDocument(TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        assertSame(TEST_TEXT_FILE2, baseController.getCurrentDocument());
    }

    @Test
    void testAssembleSubStage() {

        baseController.stage.setX(50);
        baseController.stage.setY(30);
        baseController.stage.setWidth(240);
        baseController.stage.setHeight(100);

        final List<Stage> tmpStageList = new ArrayList<>();
        Platform.runLater(() -> tmpStageList.add(baseController.assembleSubStage(0.8)));
        WaitForAsyncUtils.waitForFxEvents();

        Stage tmpStage = tmpStageList.get(0);
        assertNotNull(tmpStage);
        assertEquals(74, tmpStage.getX());
        assertEquals(40, tmpStage.getY());
        assertEquals(192, tmpStage.getWidth());
        assertEquals(80, tmpStage.getHeight());
    }

    @Test
    void testDialogProvider_provideAboutDialog() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideAboutDialog()));
        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpAlert = tmpDialogList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Copyright"));
    }

    @Test
    void testDialogProvider_provideUpdateCheckDialog() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> {
            try {
                tmpDialogList.add(tmpDialogProvider.provideUpdateCheckDialog());
            }
            catch (IOException e) {
                fail(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        String tmpCurrentVersion = LanguageUtil.i18n("global.application.version");
        assertEquals(("You are using to most current version of Document Archiver (" + tmpCurrentVersion + ")."),
                tmpDialog.getContentText());
    }

    @Test
    void testDialogProvider_providePreferencesDialog() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Pane tmpMockedPane = new Pane();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.providePreferencesDialog(tmpMockedPane)));

        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertSame(tmpMockedPane, tmpDialog.getDialogPane().getContent());
    }

    @Test
    void testDialogProvider_providePreferencesChangedAlert() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.providePreferencesChangedAlert(Locale.GERMAN)));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Einstellungen wurden gespeichert"));
    }

    @Test
    void testDialogProvider_provideManageTenantsDialog() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Pane tmpMockedPane = new Pane();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideManageTenantsDialog(tmpMockedPane)));
        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertSame(tmpMockedPane, tmpDialog.getDialogPane().getContent());
    }

    @Test
    void testDialogProvider_provideConfirmTenantDeletionAlert() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideConfirmTenantDeletionAlert("Boo")));
        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("Are you sure you want to delete tenant \"Boo\"? All documents archived within this tenant will be deleted irrevocable!", tmpAlert.getContentText());
    }

    @Test
    void testDialogProvider_provideDirectoryDoesNotContainFilesAlert() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideDirectoryDoesNotContainFilesAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("The chosen directory doesn't contain files.", tmpAlert.getContentText());
    }

    @Test
    void testDialogProvider_providePluginNotAvailableAlert() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.providePluginNotAvailableAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("The required plugin is not installed yet or outdated. Download and install the current version?",
                tmpAlert.getContentText());
    }

    @Test
    void testDialogProvider_provideSystemInformationDialog() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideSystemInformationDialog(250, 100)));
        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertEquals(TextArea.class, tmpDialog.getDialogPane().getContent().getClass());
    }

    @Test
    void testDialogProvider_provideExceptionAlert() {

        BaseController.DialogProvider tmpDialogProvider = new BaseController.DialogProvider();
        Exception tmpException = new Exception("This is a test");
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideExceptionAlert(tmpException)));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("This is a test", tmpAlert.getContentText());
    }

}
