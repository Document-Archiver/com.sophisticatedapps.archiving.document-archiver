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
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/InfoPane.fxml"));
        infoPane = loader.load();
        infoPaneController = loader.getController();
        infoPaneController.rampUp(aStage);
    }

    @AfterEach
    public void cleanUpEach() {

        infoPaneController.rampDown();

        infoPane = null;
        infoPaneController = null;
    }

    @Test
    void testHandleCurrentDocumentChanged() throws IOException {

        infoPaneController.setNewAllDocumentsAndCurrentDocument(DOCUMENTS_LIST, TEST_TEXT_FILE2);

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

        infoPaneController.setNewAllDocumentsAndCurrentDocument(DOCUMENTS_LIST, TEST_TEXT_FILE2);

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
        MockTextField tmpMockedTagsTextField = new MockTextField();
        FieldUtils.writeField(infoPaneController, "tagsTextField", tmpMockedTagsTextField, true);

        // We have to prefill the List of existing tags
        infoPaneController.setNewExistingTagsToListView(Arrays.asList("Kenya", "Tanzania"));

        @SuppressWarnings("unchecked")
        ListView<String> tmpExistingTagsListView = (ListView<String>)infoPane.lookup("#existingTagsListView");
        tmpExistingTagsListView.getSelectionModel().select(0);
        tmpExistingTagsListView.getOnKeyPressed().handle(MOCK_KEY_EVENT_WITH_CODE_UP);

        // Verify focus was requested by (mocked) tags TextField
        assertEquals(1, tmpMockedTagsTextField.getTimesFocusRequested());
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
    void testHandleSubmitButtonAction() throws IOException {

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

        // "Click" submit
        Button tmpSubmitButton = (Button)infoPane.lookup("#submitButton");
        tmpSubmitButton.getOnAction().handle(null);

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

        // Cleanup
        assertTrue(tmpTargetFile.delete());
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
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

    private static class MockTextField extends TextField {

        private int timesFocusRequested = 0;

        @Override
        public void requestFocus() {

            timesFocusRequested++;
        }

        public int getTimesFocusRequested() {

            return timesFocusRequested;
        }
    }

}
