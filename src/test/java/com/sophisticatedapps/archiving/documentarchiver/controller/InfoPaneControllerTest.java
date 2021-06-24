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
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.DocumentsPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class InfoPaneControllerTest extends BaseTest {

    @TempDir
    File tempDir;

    private Pane infoPane;
    private InfoPaneController infoPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<InfoPaneController,Pane> tmpInfoPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/InfoPane.fxml", getApplicationContext(aStage));
        infoPane = tmpInfoPaneControllerRegionPair.getRegion();
        infoPaneController = tmpInfoPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        infoPaneController.rampDown();

        infoPane = null;
        infoPaneController = null;
    }

    @Test
    void testSetWidths() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        infoPane.setPrefWidth(222);
        MethodUtils.invokeMethod(infoPaneController, true, "setWidths");

        @SuppressWarnings("unchecked")
        ComboBox<String> tmpComboBox = (ComboBox<String>)
                FieldUtils.readField(infoPaneController, "quickDescriptionWordsComboBox", true);
        assertEquals(222, tmpComboBox.getPrefWidth());
    }

    @Test
    void testSetHeights() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        infoPane.setPrefHeight(1111);
        MethodUtils.invokeMethod(infoPaneController, true, "setHeights");

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)
                FieldUtils.readField(infoPaneController, "existingTagsListView", true);
        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)
                FieldUtils.readField(infoPaneController, "selectedTagsListView", true);

        assertEquals(761, tmpExistingTagsListView.getPrefHeight());
        assertEquals(761, tmpSelectedTagsListView.getPrefHeight());
    }

    @Test
    void testHandleCurrentDocumentChanged() throws IOException {

        infoPaneController.setNewAllDocumentsAndCurrentDocument(ALL_DOCUMENTS_LIST, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // We will need the test file's date and time
        BasicFileAttributes tmpFileAttributes =
                Files.readAttributes(TEST_TEXT_FILE2.toPath(), BasicFileAttributes.class);
        LocalDateTime tmpFileDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                tmpFileAttributes.creationTime().toMillis()), ZoneId.systemDefault());

        // Check DatePicker
        DatePicker tmpDatePicker = (DatePicker)infoPane.lookup("#datePicker");
        assertNotNull(tmpDatePicker);
        assertEquals(tmpFileDateTime.toLocalDate(), tmpDatePicker.getValue());

        // Check utilize time information CheckBox
        CheckBox tmpUticb = (CheckBox)infoPane.lookup("#utilizeTimeInformationCheckBox");
        assertNotNull(tmpUticb);
        assertFalse(tmpUticb.isSelected());

        // Check time information TextField
        TextField tmpTimeInformationTextField = (TextField)infoPane.lookup("#timeInformationTextField");
        assertNotNull(tmpTimeInformationTextField);
        assertEquals(GlobalConstants.ONLY_TIME_DATE_TIME_FORMATTER.format(tmpFileDateTime),
                tmpTimeInformationTextField.getText());

        // TODO - check remaining fields
    }

    @Test
    void testHandleDatePickerValueChanged() {

        infoPaneController.setNewAllDocumentsAndCurrentDocument(ALL_DOCUMENTS_LIST, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        DatePicker tmpDatePicker = (DatePicker)infoPane.lookup("#datePicker");
        assertNotNull(tmpDatePicker);

        tmpDatePicker.getEditor().setText("20.01.1975");
        infoPaneController.handleDatePickerFocusedPropertyValueChanged(false);

        // Get the value
        LocalDate tmpLocalDate = tmpDatePicker.getValue();
        assertEquals(20, tmpLocalDate.getDayOfMonth());
        assertEquals(Month.JANUARY, tmpLocalDate.getMonth());
        assertEquals(1975, tmpLocalDate.getYear());
    }

    @Test
    void testHandleUtilizeTimeInformationCheckBoxValueChanged() {

        TextField tmpTimeInformationTextField = (TextField)infoPane.lookup("#timeInformationTextField");
        assertNotNull(tmpTimeInformationTextField);

        infoPaneController.handleUtilizeTimeInformationCheckBoxValueChanged(true);
        assertFalse(tmpTimeInformationTextField.isDisabled());

        infoPaneController.handleUtilizeTimeInformationCheckBoxValueChanged(false);
        assertTrue(tmpTimeInformationTextField.isDisabled());
    }

    @Test
    void testHandleQuickDescriptionWordsComboBoxValueChanged() {

        TextField tmpDescriptionTextField = (TextField)infoPane.lookup("#descriptionTextField");
        assertNotNull(tmpDescriptionTextField);

        tmpDescriptionTextField.setText("bar");
        infoPaneController.handleQuickDescriptionWordsComboBoxValueChanged("foo");

        assertEquals("foo bar", tmpDescriptionTextField.getText());
    }

    @Test
    void testHandleTagsTextFieldTextChanged() {

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("iPhone", "iPad", "iMac"));

        // Now the existing tags ListView should have 3 "visible" tags.
        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        assertEquals(3, tmpExistingTagsListView.getItems().size());

        // Put some text into the tags TextField (which will trigger the Listener and will be used for filtering)
        TextField tmpTagsTextField = (TextField)infoPane.lookup("#tagsTextField");
        tmpTagsTextField.setText("iP");

        // Now the existing tags ListView should have 2 "visible" tags.
        assertEquals(2, tmpExistingTagsListView.getItems().size());

        // Clear the tags TextField, and there should be 3 "visible" tags again.
        tmpTagsTextField.setText("");
        assertEquals(3, tmpExistingTagsListView.getItems().size());
    }

    @Test
    void testHandleTagsTextFieldKeyPressed_enter() {

        TextField tmpTagsTextField = (TextField)infoPane.lookup("#tagsTextField");
        tmpTagsTextField.setText("CoolNewTag");
        tmpTagsTextField.getOnKeyPressed().handle(MOCK_KEY_EVENT_WITH_CODE_ENTER);

        WaitForAsyncUtils.waitForFxEvents();

        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");
        assertTrue(tmpSelectedTagsListView.getItems().contains("CoolNewTag"));
    }

    @Test
    void testHandleTagsTextFieldKeyPressed_down() {

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("foo", "bar"));

        // Trigger the key event.
        TextField tmpTagsTextField = (TextField)infoPane.lookup("#tagsTextField");
        tmpTagsTextField.getOnKeyPressed().handle(MOCK_KEY_EVENT_WITH_CODE_DOWN);

        WaitForAsyncUtils.waitForFxEvents();

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        assertEquals("foo", tmpExistingTagsListView.getFocusModel().getFocusedItem());
        assertEquals("foo", tmpExistingTagsListView.getSelectionModel().getSelectedItem());
    }

    @Test
    void testHandleExistingTagsListViewClicked() {

        // Put some text into the tags TextField (which has to be cleared)
        TextField tmpTagsTextField = (TextField)infoPane.lookup("#tagsTextField");
        tmpTagsTextField.setText("Some random text");

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("Java", "Swift", "PHP"));

        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");
        assertEquals(0, tmpSelectedTagsListView.getItems().size());

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        tmpExistingTagsListView.getSelectionModel().select(1);
        tmpExistingTagsListView.getOnMouseClicked().handle(null);

        assertEquals(1, tmpSelectedTagsListView.getItems().size());
        assertEquals("Swift", tmpSelectedTagsListView.getItems().get(0));
        assertEquals("", tmpTagsTextField.getText());
    }

    @Test
    void testHandleExistingTagsListViewClicked_click_on_empty_list_area() {

        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");
        assertEquals(0, tmpSelectedTagsListView.getItems().size());

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        tmpExistingTagsListView.getOnMouseClicked().handle(null);

        assertEquals(0, tmpSelectedTagsListView.getItems().size());
    }

    @Test
    void testHandleExistingTagsListViewKeyPressed_enter() {

        // Put some text into the tags TextField (which has to be cleared)
        TextField tmpTagsTextField = (TextField)infoPane.lookup("#tagsTextField");
        tmpTagsTextField.setText("Some random text");

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("iOS", "macOS", "tvOS"));

        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");
        assertEquals(0, tmpSelectedTagsListView.getItems().size());

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        tmpExistingTagsListView.getSelectionModel().select(1);
        tmpExistingTagsListView.getOnKeyPressed().handle(MOCK_KEY_EVENT_WITH_CODE_ENTER);

        assertEquals(1, tmpSelectedTagsListView.getItems().size());
        assertEquals("macOS", tmpSelectedTagsListView.getItems().get(0));
        assertEquals("", tmpTagsTextField.getText());
    }

    @Test
    void testHandleExistingTagsListViewKeyPressed_up() throws IllegalAccessException {

        // Exchange controller's tags TextField with a mocked one
        TextField tmpMockedTagsTextField = Mockito.mock(TextField.class);
        FieldUtils.writeField(infoPaneController, "tagsTextField", tmpMockedTagsTextField, true);

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("Kenya", "Tanzania"));

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        tmpExistingTagsListView.getSelectionModel().select(0);
        tmpExistingTagsListView.getOnKeyPressed().handle(MOCK_KEY_EVENT_WITH_CODE_UP);

        // Verify focus was requested by (mocked) tags TextField
        verify(tmpMockedTagsTextField, Mockito.times(1)).requestFocus();
    }

    @Test
    void testHandleSelectedTagsListViewClicked() {

        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");
        tmpSelectedTagsListView.getItems().setAll("Mouse", "Keyboard");
        assertEquals(2, tmpSelectedTagsListView.getItems().size());

        // Click.
        tmpSelectedTagsListView.getSelectionModel().select(0);
        tmpSelectedTagsListView.getOnMouseClicked().handle(null);

        assertEquals(1, tmpSelectedTagsListView.getItems().size());
        assertEquals("Keyboard", tmpSelectedTagsListView.getItems().get(0));
    }

    @Test
    void testHandleArchiveButtonAction() throws IOException, IllegalAccessException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);
        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        // Write some stuff to the new file
        try (FileWriter tmpFileWriter = new FileWriter(tmpNewCurrentDocument)) {
            tmpFileWriter.write("snafu");
        }

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);
        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");

        WaitForAsyncUtils.waitForFxEvents();

        tmpSelectedTagsListView.getItems().addAll("Java", "Swift");

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpMockedAllDoneAlert = Mockito.mock(Alert.class);
        when(tmpMockedAllDoneAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NEXT));
        InfoPaneController.DialogProvider tmpMockedDialogProvider =
                Mockito.mock(InfoPaneController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);
        FieldUtils.writeField(infoPaneController, "infoPaneDialogProvider", tmpMockedDialogProvider, true);

        // "Click" archive button
        Platform.runLater(() -> infoPaneController.handleArchiveButtonAction());

        WaitForAsyncUtils.waitForFxEvents();

        // File should be moved, so not be existing on the original path any more.
        assertFalse(tmpNewCurrentDocument.exists());

        // File should exist in (test) archiving folder
        LocalDate tmpFileDate = ((DatePicker)infoPane.lookup("#datePicker")).getValue();
        String tmpDateString = GlobalConstants.FILENAME_ONLY_DATE_DATE_TIME_FORMATTER.format(tmpFileDate);
        String tmpDescription = ((TextField)infoPane.lookup("#descriptionTextField")).getText();

        File tmpArchivingFolder =
                DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, tmpFileDate.getYear());
        File tmpTargetFile = new File(tmpArchivingFolder,
                (tmpDateString + "--" + tmpDescription + "__Java_Swift.txt"));

        assertTrue(tmpTargetFile.exists());

        // Current document and all documents list should be null/empty
        assertNull(infoPaneController.getCurrentDocument());
        assertTrue(infoPaneController.getAllDocuments().isEmpty());

        // Cleanup
        assertTrue(tmpTargetFile.delete());
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.CORE_ARCHIVING_FOLDER);
    }

    @Test
    void testHandleArchiveButtonAction_with_close_app() throws IOException, IllegalAccessException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);
        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        // Write some stuff to the new file
        try (FileWriter tmpFileWriter = new FileWriter(tmpNewCurrentDocument)) {
            tmpFileWriter.write("snafu");
        }

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);
        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");

        WaitForAsyncUtils.waitForFxEvents();

        tmpSelectedTagsListView.getItems().addAll("Java", "Swift");

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpMockedAllDoneAlert = Mockito.mock(Alert.class);
        when(tmpMockedAllDoneAlert.showAndWait()).thenReturn(Optional.of(ButtonType.FINISH));
        InfoPaneController.DialogProvider tmpMockedDialogProvider =
                Mockito.mock(InfoPaneController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);
        FieldUtils.writeField(infoPaneController, "infoPaneDialogProvider", tmpMockedDialogProvider, true);

        // "Click" archive button
        Platform.runLater(() -> infoPaneController.handleArchiveButtonAction());

        WaitForAsyncUtils.waitForFxEvents();

        // File should be moved, so not be existing on the original path any more.
        assertFalse(tmpNewCurrentDocument.exists());

        // File should exist in (test) archiving folder
        LocalDate tmpFileDate = ((DatePicker)infoPane.lookup("#datePicker")).getValue();
        String tmpDateString = GlobalConstants.FILENAME_ONLY_DATE_DATE_TIME_FORMATTER.format(tmpFileDate);
        String tmpDescription = ((TextField)infoPane.lookup("#descriptionTextField")).getText();

        File tmpArchivingFolder =
                DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, tmpFileDate.getYear());
        File tmpTargetFile = new File(tmpArchivingFolder,
                (tmpDateString + "--" + tmpDescription + "__Java_Swift.txt"));

        assertTrue(tmpTargetFile.exists());

        // Stage should be hidden.
        assertFalse(infoPaneController.stage.isShowing());

        // Cleanup
        assertTrue(tmpTargetFile.delete());
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.CORE_ARCHIVING_FOLDER);
    }

    @Test
    void testHandleArchiveButtonAction_file_exists_in_archive() throws IllegalAccessException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);
        File tmpNewCurrentDocument = new File(tempDir, "test.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        Alert tmpMockedAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideExceptionAlert(any(Exception.class)))
                .thenReturn(tmpMockedAlert);
        FieldUtils.writeField(infoPaneController, "dialogProvider", tmpMockedDialogProvider, true);

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);

        WaitForAsyncUtils.waitForFxEvents();

        DatePicker tmpDatePicker = (DatePicker)infoPane.lookup("#datePicker");
        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");

        tmpDatePicker.setValue(LocalDate.from(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.parse("01.07.2021")));
        tmpSelectedTagsListView.getItems().addAll("sna", "fu");

        WaitForAsyncUtils.waitForFxEvents();

        // "Click" archive button
        Button tmpArchiveButton = (Button)infoPane.lookup("#archiveButton");
        tmpArchiveButton.getOnAction().handle(null);

        // Document should still be current document and alert should have been triggered.
        assertSame(tmpNewCurrentDocument, infoPaneController.getCurrentDocument());
        verify(tmpMockedAlert, Mockito.times(1)).showAndWait();

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.CORE_ARCHIVING_FOLDER);
    }

    @Test
    void testHandleDeleteButtonAction_with_cancel() throws IOException, IllegalAccessException {

        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        // Write some stuff to the new file
        try (FileWriter tmpFileWriter = new FileWriter(tmpNewCurrentDocument)) {
            tmpFileWriter.write("text");
        }

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);
        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpMockedConfirmDeletionAlert = Mockito.mock(Alert.class);
        when(tmpMockedConfirmDeletionAlert.showAndWait()).thenReturn(Optional.of(ButtonType.CANCEL));
        InfoPaneController.DialogProvider tmpMockedDialogProvider =
                Mockito.mock(InfoPaneController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideConfirmDeletionAlert(tmpNewCurrentDocument))
                .thenReturn(tmpMockedConfirmDeletionAlert);
        FieldUtils.writeField(infoPaneController, "infoPaneDialogProvider", tmpMockedDialogProvider, true);

        // "Click" delete button
        Platform.runLater(() -> infoPaneController.handleDeleteButtonAction());
        WaitForAsyncUtils.waitForFxEvents();

        // File should not be removed.
        assertTrue(tmpNewCurrentDocument.exists());

        // Cleanup
        assertTrue(tmpNewCurrentDocument.delete());
    }

    @Test
    void testHandleDeleteButtonAction_with_move_to_trash() throws IOException, IllegalAccessException {

        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        // Write some stuff to the new file
        try (FileWriter tmpFileWriter = new FileWriter(tmpNewCurrentDocument)) {
            tmpFileWriter.write("text");
        }

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);
        WaitForAsyncUtils.waitForFxEvents();

        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        InfoPaneController.DialogProvider tmpMockedInfoPaneDialogProvider =
                Mockito.mock(InfoPaneController.DialogProvider.class);

        Alert tmpMockedConfirmDeletionAlert = Mockito.mock(Alert.class);
        when(tmpMockedConfirmDeletionAlert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        when(tmpMockedInfoPaneDialogProvider.provideConfirmDeletionAlert(tmpNewCurrentDocument))
                .thenReturn(tmpMockedConfirmDeletionAlert);

        Alert tmpMockedExceptionAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.provideExceptionAlert(any(Exception.class))).thenReturn(tmpMockedExceptionAlert);

        Alert tmpMockedAllDoneAlert = Mockito.mock(Alert.class);
        when(tmpMockedAllDoneAlert.showAndWait()).thenReturn(Optional.of(ButtonType.FINISH));
        when(tmpMockedInfoPaneDialogProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);

        FieldUtils.writeField(infoPaneController, "dialogProvider", tmpMockedDialogProvider, true);
        FieldUtils.writeField(infoPaneController, "infoPaneDialogProvider", tmpMockedInfoPaneDialogProvider, true);

        // "Click" delete button
        Platform.runLater(() -> infoPaneController.handleDeleteButtonAction());
        WaitForAsyncUtils.waitForFxEvents();

        // File should be found in trash. If trash not available, the Exception alert should have been triggered.
        File tmpTrashDirectory = getTrashDirectory();
        if (tmpTrashDirectory.exists()) {
            // File should be removed from its original place.
            assertFalse(tmpNewCurrentDocument.exists());
            // And be in the trash now.
            File tmpFileInTrash = new File(tmpTrashDirectory, tmpNewCurrentDocument.getName());
            assertTrue(tmpFileInTrash.exists());
            // Cleanup
            assertTrue(tmpFileInTrash.delete());
        }
        else {
            verify(tmpMockedExceptionAlert, Mockito.times(1)).showAndWait();
        }
    }

    private File getTrashDirectory() {
        File home = new File(System.getProperty("user.home"));
        File trash = new File(home, ".Trash");
        if (!trash.exists()) {
            trash = new File(home, "Trash");
            if (!trash.exists()) {
                File desktop = new File(home, "Desktop");
                if (desktop.exists()) {
                    trash = new File(desktop, ".Trash");
                    if (!trash.exists()) {
                        trash = new File(desktop, "Trash");
                        if (!trash.exists()) {
                            trash = new File(System.getProperty("fileutils.trash", "Trash"));
                        }
                    }
                }
            }
        }

        return trash;
    }

    @Test
    void testHandleDeleteButtonAction_with_delete() throws IOException, IllegalAccessException {

        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        // Write some stuff to the new file
        try (FileWriter tmpFileWriter = new FileWriter(tmpNewCurrentDocument)) {
            tmpFileWriter.write("text");
        }

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);
        WaitForAsyncUtils.waitForFxEvents();

        InfoPaneController.DialogProvider tmpMockedDialogProvider =
                Mockito.mock(InfoPaneController.DialogProvider.class);

        Alert tmpMockedConfirmDeletionAlert = Mockito.mock(Alert.class);
        when(tmpMockedConfirmDeletionAlert.showAndWait()).thenReturn(Optional.of(new ButtonType("other")));
        when(tmpMockedDialogProvider.provideConfirmDeletionAlert(tmpNewCurrentDocument))
                .thenReturn(tmpMockedConfirmDeletionAlert);

        Alert tmpMockedAllDoneAlert = Mockito.mock(Alert.class);
        when(tmpMockedAllDoneAlert.showAndWait()).thenReturn(Optional.of(ButtonType.FINISH));
        when(tmpMockedDialogProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);

        FieldUtils.writeField(infoPaneController, "infoPaneDialogProvider", tmpMockedDialogProvider, true);

        // "Click" delete button
        Platform.runLater(() -> infoPaneController.handleDeleteButtonAction());
        WaitForAsyncUtils.waitForFxEvents();

        // File should be removed.
        assertFalse(tmpNewCurrentDocument.exists());
    }

    @Test
    void testGenericFileTimeAgentDetermineFileTime() throws IOException {

        // We will need the test file's date and time
        BasicFileAttributes tmpFileAttributes =
                Files.readAttributes(TEST_TEXT_FILE.toPath(), BasicFileAttributes.class);
        LocalDateTime tmpExpectedFileDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                tmpFileAttributes.creationTime().toMillis()), ZoneId.systemDefault());

        InfoPaneController.GenericFileTimeAgent tmpGenericFileTimeAgent = new InfoPaneController.GenericFileTimeAgent();
        LocalDateTime tmpFileDateTime = tmpGenericFileTimeAgent.determineFileTime(TEST_TEXT_FILE);

        assertEquals(tmpExpectedFileDateTime, tmpFileDateTime);
    }

    @Test
    void testJPGFileTimeAgentDetermineFileTime() {

        InfoPaneController.JPGFileTimeAgent tmpJPGFileTimeAgent = new InfoPaneController.JPGFileTimeAgent();
        LocalDateTime tmpFileDateTime = tmpJPGFileTimeAgent.determineFileTime(TEST_JPG_FILE);

        assertEquals(17, tmpFileDateTime.getDayOfMonth());
        assertEquals(Month.AUGUST, tmpFileDateTime.getMonth());
        assertEquals(2020, tmpFileDateTime.getYear());
    }

    @Test
    void testJPGFileTimeAgentDetermineFileTime_no_exif_data() {

        InfoPaneController.JPGFileTimeAgent tmpJPGFileTimeAgent = new InfoPaneController.JPGFileTimeAgent();
        LocalDateTime tmpFileDateTime = tmpJPGFileTimeAgent.determineFileTime(TEST_JPG_FILE2);

        InfoPaneController.GenericFileTimeAgent tmpGenericFileTimeAgent = new InfoPaneController.GenericFileTimeAgent();
        LocalDateTime tmpExpectedFileDateTime = tmpGenericFileTimeAgent.determineFileTime(TEST_JPG_FILE2);

        assertEquals(tmpExpectedFileDateTime, tmpFileDateTime);
    }

    @Test
    void testDialogProvider_provideConfirmDeletionAlert() {

        File tmpNewCurrentDocument = new File(tempDir, "foobar.txt");

        InfoPaneController.DialogProvider tmpDialogProvider = new InfoPaneController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideConfirmDeletionAlert(tmpNewCurrentDocument)));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("Please confirm the deletion of file \"foobar.txt\".", tmpAlert.getContentText());

        ObservableList<ButtonType> tmpButtonTypes = tmpAlert.getButtonTypes();
        assertEquals("Move to trash", tmpButtonTypes.get(0).getText());
        assertEquals(ButtonBar.ButtonData.OK_DONE, tmpButtonTypes.get(0).getButtonData());
        assertEquals("Delete file", tmpButtonTypes.get(1).getText());
        assertEquals(ButtonBar.ButtonData.OTHER, tmpButtonTypes.get(1).getButtonData());
        assertEquals(ButtonType.CANCEL, tmpButtonTypes.get(2));
    }

    @Test
    void testDialogProvider_provideAllDoneAlert() {

        InfoPaneController.DialogProvider tmpDialogProvider = new InfoPaneController.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideAllDoneAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("All done :-)"));

        ObservableList<ButtonType> tmpButtonTypes = tmpAlert.getButtonTypes();
        assertEquals("Archive another document", tmpButtonTypes.get(0).getText());
        assertEquals(ButtonBar.ButtonData.NEXT_FORWARD, tmpButtonTypes.get(0).getButtonData());
        assertEquals("Close App", tmpButtonTypes.get(1).getText());
        assertEquals(ButtonBar.ButtonData.FINISH, tmpButtonTypes.get(1).getButtonData());
    }

}
