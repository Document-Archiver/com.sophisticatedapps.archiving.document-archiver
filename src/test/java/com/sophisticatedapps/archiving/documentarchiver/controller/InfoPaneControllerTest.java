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
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
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
import java.nio.file.Paths;
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
                FXMLUtil.loadAndRampUpRegion("view/InfoPane.fxml", aStage);
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

        assertEquals(781, tmpExistingTagsListView.getPrefHeight());
        assertEquals(781, tmpSelectedTagsListView.getPrefHeight());
    }

    @Test
    void testHandleCurrentDocumentChanged() throws IOException {

        infoPaneController.setNewAllDocumentsAndCurrentDocument(ALL_DOCUMENTS_LIST, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // We will need the test file's date and time
        BasicFileAttributes tmpFileAttributes = Files
                .readAttributes(Paths.get(TEST_TEXT_FILE2.getPath()), BasicFileAttributes.class);
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
        infoPaneController.handleDatePickerValueChanged(false);

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
    void testHandleSubmitButtonAction() throws IOException, IllegalAccessException {

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
        InfoPaneController.AlertProvider tmpMockedAlertProvider = Mockito.mock(InfoPaneController.AlertProvider.class);
        when(tmpMockedAlertProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);
        FieldUtils.writeField(infoPaneController, "alertProvider", tmpMockedAlertProvider, true);

        // "Click" submit
        Platform.runLater(() -> infoPaneController.handleSubmitButtonAction());

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
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
    }

    @Test
    void testHandleSubmitButtonAction_with_close_app() throws IOException, IllegalAccessException {

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
        InfoPaneController.AlertProvider tmpMockedAlertProvider = Mockito.mock(InfoPaneController.AlertProvider.class);
        when(tmpMockedAlertProvider.provideAllDoneAlert()).thenReturn(tmpMockedAllDoneAlert);
        FieldUtils.writeField(infoPaneController, "alertProvider", tmpMockedAlertProvider, true);

        // "Click" submit
        Platform.runLater(() -> infoPaneController.handleSubmitButtonAction());

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
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
    }

    @Test
    void testHandleSubmitButtonAction_file_exists_in_archive() throws IllegalAccessException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);
        File tmpNewCurrentDocument = new File(tempDir, "test.txt");
        List<File> tmpNewAllDocuments = new ArrayList<>();
        tmpNewAllDocuments.add(tmpNewCurrentDocument);

        Alert tmpMockedAlert = Mockito.mock(Alert.class);
        InfoPaneController.AlertProvider tmpMockedDialogProvider = Mockito.mock(InfoPaneController.AlertProvider.class);
        when(tmpMockedDialogProvider.provideArchiveFileNotSuccessfulAlert(any(Exception.class)))
                .thenReturn(tmpMockedAlert);
        FieldUtils.writeField(infoPaneController, "alertProvider", tmpMockedDialogProvider, true);

        infoPaneController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, tmpNewCurrentDocument);

        WaitForAsyncUtils.waitForFxEvents();

        DatePicker tmpDatePicker = (DatePicker)infoPane.lookup("#datePicker");
        @SuppressWarnings("unchecked")
        ListView<String> tmpSelectedTagsListView = (ListView<String>)infoPane.lookup("#selectedTagsListView");

        tmpDatePicker.setValue(LocalDate.from(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.parse("01.07.2021")));
        tmpSelectedTagsListView.getItems().addAll("sna", "fu");

        WaitForAsyncUtils.waitForFxEvents();

        // "Click" submit
        Button tmpSubmitButton = (Button)infoPane.lookup("#submitButton");
        tmpSubmitButton.getOnAction().handle(null);

        // Document should still be current document and alert should have been triggered.
        assertSame(tmpNewCurrentDocument, infoPaneController.getCurrentDocument());
        verify(tmpMockedAlert, Mockito.times(1)).showAndWait();

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
    }

    @Test
    void testDatePickerStringConverterToString() {

        InfoPaneController.DatePickerStringConverter tmpDatePickerStringConverter =
                new InfoPaneController.DatePickerStringConverter();

        LocalDate tmpLocalDate = LocalDate.now();

        assertEquals(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.format(tmpLocalDate),
                tmpDatePickerStringConverter.toString(tmpLocalDate));
        assertEquals(StringUtil.EMPTY_STRING, tmpDatePickerStringConverter.toString(null));
    }

    @Test
    void testDatePickerStringConverterFromString() {

        InfoPaneController.DatePickerStringConverter tmpDatePickerStringConverter =
                new InfoPaneController.DatePickerStringConverter();

        LocalDate tmpLocalDate = LocalDate.now();

        assertEquals(tmpLocalDate, tmpDatePickerStringConverter
                .fromString(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.format(tmpLocalDate)));
        assertNull(tmpDatePickerStringConverter.fromString(null));
        assertNull(tmpDatePickerStringConverter.fromString(StringUtil.EMPTY_STRING));
    }

    @Test
    void testGenericFileTimeAgentDetermineFileTime() throws IOException {

        // We will need the test file's date and time
        BasicFileAttributes tmpFileAttributes = Files
                .readAttributes(Paths.get(TEST_TEXT_FILE.getPath()), BasicFileAttributes.class);
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

        assertEquals(23, tmpFileDateTime.getDayOfMonth());
        assertEquals(Month.JANUARY, tmpFileDateTime.getMonth());
        assertEquals(2021, tmpFileDateTime.getYear());
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
    void testAlertProvider_provideArchiveFileNotSuccessfulAlert() {

        InfoPaneController.AlertProvider tmpAlertProvider = new InfoPaneController.AlertProvider();
        Exception tmpException = new Exception("This is a test");
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpAlertProvider.provideArchiveFileNotSuccessfulAlert(tmpException)));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("This is a test", tmpAlert.getContentText());
    }

    @Test
    void testAlertProvider_provideAllDoneAlert() {

        InfoPaneController.AlertProvider tmpAlertProvider = new InfoPaneController.AlertProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpAlertProvider.provideAllDoneAlert()));

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
