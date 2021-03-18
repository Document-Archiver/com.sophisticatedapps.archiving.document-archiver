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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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
    public void cleanUpEach(){

        infoPaneController.rampDown();
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

    // TODO - test remaining methods

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
        tmpTargetFile.delete();
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
    }

}
