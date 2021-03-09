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
import com.sophisticatedapps.archiving.documentarchiver.model.Tags;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoPaneController extends BaseController {

    private static final Pattern FIND_DESCRIPTION_IN_FILENAME_PATTERN =
            Pattern.compile(".*/(.*?)\\.[A-Za-z0-9]+?");

    private static final Map<FileTypeEnum, Class<? extends FileTimeAgent>> TIME_AGENTS_BY_FILETYPE;

    static {

        TIME_AGENTS_BY_FILETYPE = Map.of(FileTypeEnum.PDF, GenericFileTimeAgent.class,
                FileTypeEnum.TXT, GenericFileTimeAgent.class,
                FileTypeEnum.JPG, JPGFileTimeAgent.class,
                FileTypeEnum.PNG, GenericFileTimeAgent.class,
                FileTypeEnum.GIF, GenericFileTimeAgent.class,
                FileTypeEnum.XML, GenericFileTimeAgent.class,
                FileTypeEnum.UNSUPPORTED, GenericFileTimeAgent.class);
    }

    @FXML
    private DatePicker datePicker;

    @FXML
    private CheckBox utilizeTimeInformationCheckBox;

    @FXML
    private TextField timeInformationTextField;

    @FXML
    private ComboBox<String> quickDescriptionWordsComboBox;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField tagsTextField;

    @FXML
    private ListView<String> existingTagsListView;

    @FXML
    private ListView<String> selectedTagsListView;

    @FXML
    private CheckBox takeOverDescriptionAndTagsCheckBox;

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Set converter
        datePicker.setConverter(new DatePickerStringConverter());

        // Set values
        quickDescriptionWordsComboBox.getItems().addAll(
                GlobalConstants.APP_PROPERTIES.getProperty("quick.description.words").split(","));

        // Add listener
        addCurrentDocumentChangedListener(aChange ->
                handleCurrentDocumentChanged((File)aChange.getValueAdded()));
        datePicker.focusedProperty().addListener((aChangedValue, anOldValue, aNewValue) ->
                handleDatePickerValueChanged(aNewValue));
        utilizeTimeInformationCheckBox.selectedProperty().addListener((anObservable, anOldValue, aNewValue) ->
                handleUtilizeTimeInformationCheckBoxValueChanged(aNewValue));
        quickDescriptionWordsComboBox.valueProperty().addListener((aChangedValue, anOldValue, aNewValue) ->
                handleQuickDescriptionWordsComboBoxValueChanged(aNewValue));
        tagsTextField.textProperty().addListener((anObservable, anOldValue, aNewValue) ->
                handleTagsTextFieldTextChanged(aNewValue));
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (aNewCurrentDocument != null) {

            FileTypeEnum tmpFileType = FileUtil.getFiletype(aNewCurrentDocument);
            LocalDateTime tmpFileDateTime;

            try {
                tmpFileDateTime = TIME_AGENTS_BY_FILETYPE.get(tmpFileType)
                        .getDeclaredConstructor().newInstance().determineFileTime(aNewCurrentDocument);
            }
            catch(Exception e) {
                tmpFileDateTime = LocalDateTime.now();
            }

            datePicker.setValue(tmpFileDateTime.toLocalDate());
            utilizeTimeInformationCheckBox.setSelected(tmpFileType.isUtilizeTimeInformationDefault());
            timeInformationTextField.setText(GlobalConstants.ONLY_TIME_DATE_TIME_FORMATTER.format(tmpFileDateTime));
            timeInformationTextField.setDisable(!tmpFileType.isUtilizeTimeInformationDefault());

            // (Re-)set description TextField and selected tags ListView?
            if (!takeOverDescriptionAndTagsCheckBox.isSelected()) {

                Matcher tmpMatcher = FIND_DESCRIPTION_IN_FILENAME_PATTERN.matcher(aNewCurrentDocument.getPath());
                if (tmpMatcher.find()) {
                    descriptionTextField.setText(StringUtil.retrieveDescriptionSafeString(tmpMatcher.group(1)));
                }

                selectedTagsListView.getItems().clear();
            }

            ObservableList<String> tmpOriginalExistingTagsList = FXCollections.observableList(
                    new ArrayList<>(Tags.getExistingTags(GlobalConstants.ARCHIVING_FOLDER, tmpFileType)));
            FilteredList<String> tmpFilteredExistingTagsList = new FilteredList<>(tmpOriginalExistingTagsList);
            existingTagsListView.setItems(tmpFilteredExistingTagsList);
        }
    }

    /**
     * Method to call when the value of the DatePicker was changed.
     * Not possible to set via FXML, has to be called via 'datePicker.focusedProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    private void handleDatePickerValueChanged(boolean aNewValue) {

        // Without this, manual set dates are not taken over
        if (!aNewValue) {

            LocalDate tmpCurrentValue = datePicker.getValue();
            LocalDate tmpValueOfText = datePicker.getConverter().fromString(datePicker.getEditor().getText());

            if(!tmpCurrentValue.equals(tmpValueOfText)) {

                datePicker.setValue(tmpValueOfText);
            }
        }
    }

    /**
     * Method to call when the value of the utilize time information CheckBox was changed.
     * Not possible to set via FXML, has to be called via
     * 'utilizeTimeInformationCheckBox.selectedProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    private void handleUtilizeTimeInformationCheckBoxValueChanged(boolean aNewValue) {

        timeInformationTextField.setDisable(!aNewValue);
    }

    /**
     * Method to call when the value of the quick description words ComboBox was changed.
     * Not possible to set via FXML, has to be called via 'quickDescriptionWordsComboBox.valueProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    private void handleQuickDescriptionWordsComboBoxValueChanged(String aNewValue) {

        descriptionTextField.setText(aNewValue + " " + descriptionTextField.getText());
    }

    /**
     * Method to call when the text of the tag TextField was changed.
     * Not possible to set via FXML, has to be called via 'tagsTextField.textProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    private void handleTagsTextFieldTextChanged(String aNewValue) {

        FilteredList<String> tmpFilteredList = (FilteredList<String>)existingTagsListView.getItems();

        if (aNewValue.isEmpty()) {

            tmpFilteredList.setPredicate(null);
        }
        else {

            final String tmpNewValueString = aNewValue.toUpperCase();
            tmpFilteredList.setPredicate(s -> s.toUpperCase().contains(tmpNewValueString));
        }
    }

    @FXML
    private void handleTagsTextFieldKeyPressed(KeyEvent aKeyEvent) {

        if (aKeyEvent.getCode().equals(KeyCode.ENTER)) {

            String tmpNewTag = StringUtil.retrieveTagNameSafeString(tagsTextField.getText());
            addToListIfNotContainedYet(selectedTagsListView.getItems(), tmpNewTag);
            tagsTextField.setText("");
        }
        else if (aKeyEvent.getCode().equals(KeyCode.DOWN)) {

            if (!existingTagsListView.getItems().isEmpty()) {

                existingTagsListView.requestFocus();
                existingTagsListView.getFocusModel().focus(0);
                existingTagsListView.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void handleExistingTagsListViewClicked(MouseEvent anActionEvent) {

        String tmpSelectedItem = existingTagsListView.getSelectionModel().getSelectedItem();
        addToListIfNotContainedYet(selectedTagsListView.getItems(), tmpSelectedItem);
        tagsTextField.setText("");
        tagsTextField.requestFocus();
    }

    @FXML
    private void handleExistingTagsListViewKeyPressed(KeyEvent aKeyEvent) {

        if (aKeyEvent.getCode().equals(KeyCode.UP)) {

            if (existingTagsListView.getSelectionModel().getSelectedIndex() == 0) {

                tagsTextField.requestFocus();
            }
        }
        else if (aKeyEvent.getCode().equals(KeyCode.ENTER)) {

            // Same as if clicked.
            handleExistingTagsListViewClicked(null);
        }
    }

    @FXML
    private void handleSelectedTagsListViewClicked(MouseEvent anActionEvent) {

        String tmpSelectedItem = selectedTagsListView.getSelectionModel().getSelectedItem();
        selectedTagsListView.getItems().remove(tmpSelectedItem);
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent anActionEvent) {

        File tmpCurrentDocument = getCurrentDocument();

        DefinedFileProperties tmpDfp = new DefinedFileProperties(datePicker.getValue(),
                utilizeTimeInformationCheckBox.isSelected(), timeInformationTextField.getText(),
                descriptionTextField.getText().trim(), selectedTagsListView.getItems(),
                FileUtil.getFiletype(tmpCurrentDocument));

        try {

            FileUtil.moveFileToArchive(tmpCurrentDocument, tmpDfp);

            List<File> tmpAllDocuments = getAllDocuments();
            tmpAllDocuments.remove(tmpCurrentDocument);

            // Trigger listeners by first setting the all documents List to null.
            setNewAllDocuments(null);

            setNewAllDocumentsAndCurrentFile(tmpAllDocuments,
                    (tmpAllDocuments.isEmpty() ? null : tmpAllDocuments.get(0)));
        }
        catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    private void addToListIfNotContainedYet(List<String> aList, String aString) {

        if (!aList.contains(aString)) {

            aList.add(aString);
        }
    }

    /**
     * StringConverter for DatePickers.
     */
    private static class DatePickerStringConverter extends StringConverter<LocalDate> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("dd.MM.yyyy");

        @Override
        public String toString(LocalDate aLocalDate) {

            if(aLocalDate==null) {

                return "";
            }

            return DATE_TIME_FORMATTER.format(aLocalDate);
        }

        @Override
        public LocalDate fromString(String aDateString) {

            if((aDateString == null) || (aDateString.trim().isEmpty())) {

                return null;
            }

            return LocalDate.parse(aDateString, DATE_TIME_FORMATTER);
        }
    }

    private interface FileTimeAgent {

        LocalDateTime determineFileTime(File aFile) throws DateTimeException;
    }

    protected static class GenericFileTimeAgent implements FileTimeAgent {

        @Override
        public LocalDateTime determineFileTime(File aFile) throws DateTimeException {

            try {

                BasicFileAttributes tmpFileAttributes = Files
                        .readAttributes(Paths.get(aFile.getPath()), BasicFileAttributes.class);

                return LocalDateTime.ofInstant(Instant.ofEpochMilli(
                        tmpFileAttributes.creationTime().toMillis()), ZoneId.systemDefault());
            }
            catch (DateTimeException e) {

                throw e;
            }
            catch (Exception e) {

                throw (new DateTimeException(e.getMessage()));
            }
        }
    }

    protected static class JPGFileTimeAgent implements FileTimeAgent {

        private static final DateTimeFormatter EXIF_DATA_DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

        @Override
        public LocalDateTime determineFileTime(File aFile) throws DateTimeException {

            try {

                final JpegImageMetadata tmpMetadata = (JpegImageMetadata) Imaging.getMetadata(aFile);
                final TiffField tmpTiffField =
                        tmpMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);

                return LocalDateTime.parse(
                        String.valueOf(tmpTiffField.getValue()), EXIF_DATA_DATE_TIME_FORMATTER);
            }
            catch (Exception e) {

                return (new GenericFileTimeAgent().determineFileTime(aFile));
            }
        }
    }

}
