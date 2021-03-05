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

package com.sophisticatedapps.archiving.documentarchiver.view;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.model.Tags;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

public class InfoPaneAssembler {

    private static final Pattern FIND_DESCRIPTION_IN_FILENAME_PATTERN =
            Pattern.compile(".*/(.*?)\\.[A-Za-z0-9]+?");

    private static final Map<FileTypeEnum, Class<? extends FileTimeAgent>> TIME_AGENTS_BY_FILETYPE;

    private List<File> allFiles;
    private File currentFile;
    private FileTypeEnum fileType;
    private DefinedFileProperties definedFileProperties;

    private MoveDocumentCallBack moveDocumentCallBack;

    static {

        TIME_AGENTS_BY_FILETYPE = Map.of(FileTypeEnum.PDF, GenericFileTimeAgent.class,
                FileTypeEnum.TXT, GenericFileTimeAgent.class,
                FileTypeEnum.JPG, JPGFileTimeAgent.class,
                FileTypeEnum.PNG, GenericFileTimeAgent.class,
                FileTypeEnum.GIF, GenericFileTimeAgent.class,
                FileTypeEnum.XML, GenericFileTimeAgent.class,
                FileTypeEnum.UNSUPPORTED, GenericFileTimeAgent.class);
    }

    public InfoPaneAssembler allFiles(List<File> anAllFilesList) {

        this.allFiles = anAllFilesList;
        return this;
    }

    public InfoPaneAssembler currentFile(File aFile) {

        this.currentFile = aFile;
        return this;
    }

    public InfoPaneAssembler fileType(FileTypeEnum aFileType) {

        this.fileType = aFileType;
        return this;
    }

    public InfoPaneAssembler definedFileProperties(DefinedFileProperties aDefinedFileProperties) {

        this.definedFileProperties = aDefinedFileProperties;
        return this;
    }

    public InfoPaneAssembler moveDocumentCallBack(MoveDocumentCallBack aMoveDocumentCallBack) {

        this.moveDocumentCallBack = aMoveDocumentCallBack;
        return this;
    }

    public VBox assemble() {

        if (allFiles == null) {

            throw (new RuntimeException("allFiles not set."));
        }
        if (currentFile == null) {

            throw (new RuntimeException("currentFile not set."));
        }
        if (fileType == null) {

            throw (new RuntimeException("fileType not set."));
        }
        if (moveDocumentCallBack == null) {

            throw (new RuntimeException("moveDocumentCallBack not set."));
        }

        LocalDateTime tmpFileDateTime;
        try {
            tmpFileDateTime = TIME_AGENTS_BY_FILETYPE.get(fileType)
                    .getDeclaredConstructor().newInstance().determineFileTime(currentFile);
        }
        catch(Exception e) {
            tmpFileDateTime = LocalDateTime.now();
        }

        DatePicker tmpDatePicker = assembleDatePicker(tmpFileDateTime);
        CheckBox tmpUtilizeTimeInformationCheckBox = assembleUtilizeTimeInformationCheckBox();
        TextField tmpTimeInformationTextField = assembleTimeInformationTextField(tmpFileDateTime);
        ComboBox<String> tmpQuickDescriptionWordsComboBox = assembleQuickDescriptionWordsComboBox();
        TextField tmpDescriptionTextField = assembleDescriptionTextField();
        TextField tmpTagsTextField = new TextField();
        ListView<String> tmpExistingTagsListView = assembleExistingTagsListView();
        ListView<String> tmpSelectedTagsListView = assembleSelectedTagsListView();
        CheckBox tmpTakeOverDescriptionAndTagsCheckBox = assembleTakeOverDescriptionAndTagsCheckBox();
        Button tmpSubmitButton = new Button("Move file");

        addEventListeners(tmpDatePicker, tmpUtilizeTimeInformationCheckBox, tmpTimeInformationTextField,
                tmpQuickDescriptionWordsComboBox, tmpDescriptionTextField, tmpTagsTextField,
                tmpExistingTagsListView, tmpSelectedTagsListView, tmpTakeOverDescriptionAndTagsCheckBox,
                tmpSubmitButton);

        Pane tmpTagsPane = (new TagsPaneBuilder())
                .existingTagsListView(tmpExistingTagsListView)
                .middlePane(new Label("->"))
                .selectedTagsListView(tmpSelectedTagsListView)
                .build();

        return (new InfoPaneBuilder())
                .selectDateLabel(new Label("Please select date:"))
                .datePicker(tmpDatePicker)
                .utilizeTimeInformationCheckBox(tmpUtilizeTimeInformationCheckBox)
                .timeInformationTextField(tmpTimeInformationTextField)
                .defineDescriptionLabel(new Label("Please define Description:"))
                .quickDescriptionWordsComboBox(tmpQuickDescriptionWordsComboBox)
                .descriptionTextField(tmpDescriptionTextField)
                .selectTagsLabel(new Label("Please select tags:"))
                .tagsTextField(tmpTagsTextField)
                .tagsPane(tmpTagsPane)
                .takeOverDescriptionAndTagsCheckBox(tmpTakeOverDescriptionAndTagsCheckBox)
                .submitButton(tmpSubmitButton)
                .build();
    }

    private DatePicker assembleDatePicker(LocalDateTime aLocalDateTime) {

        DatePicker tmpDatePicker = new DatePicker();
        tmpDatePicker.setConverter(new DatePickerStringConverter());

        tmpDatePicker.setValue(aLocalDateTime.toLocalDate());

        return tmpDatePicker;
    }

    private CheckBox assembleUtilizeTimeInformationCheckBox() {

        CheckBox tmpCheckBox = new CheckBox("Utilize time information");

        tmpCheckBox.setSelected(fileType.isUtilizeTimeInformationDefault());

        return tmpCheckBox;
    }

    private TextField assembleTimeInformationTextField(LocalDateTime aLocalDateTime) {

        TextField tmpTextField =
                new TextField(GlobalConstants.ONLY_TIME_DATE_TIME_FORMATTER.format(aLocalDateTime));

        tmpTextField.setDisable(!fileType.isUtilizeTimeInformationDefault());

        return tmpTextField;
    }

    private ComboBox<String> assembleQuickDescriptionWordsComboBox() {

        ComboBox<String> tmpDescriptionWordsComboBox = new ComboBox<>();
        tmpDescriptionWordsComboBox.setPromptText("Quick description words:");
        tmpDescriptionWordsComboBox.getItems().addAll(
                GlobalConstants.APP_PROPERTIES.getProperty("quick.description.words").split(","));

        return tmpDescriptionWordsComboBox;
    }

    private TextField assembleDescriptionTextField() {

        TextField tmpDescriptionTextField = new TextField();

        if (definedFileProperties != null) {

            tmpDescriptionTextField.setText(definedFileProperties.getDescription());
        }
        else {

            Matcher tmpMatcher = FIND_DESCRIPTION_IN_FILENAME_PATTERN.matcher(currentFile.getPath());

            if (tmpMatcher.find()) {

                tmpDescriptionTextField.setText(StringUtil.retrieveDescriptionSafeString(tmpMatcher.group(1)));
            }
        }

        return tmpDescriptionTextField;
    }

    private ListView<String> assembleExistingTagsListView() {

        ObservableList<String> tmpOriginalList = FXCollections.observableList(
                new ArrayList<>(Tags.getExistingTags(GlobalConstants.ARCHIVING_FOLDER, fileType)));

        FilteredList<String> tmpFilteredList = new FilteredList<>(tmpOriginalList);

        return (new ListView<>(tmpFilteredList));
    }

    private ListView<String> assembleSelectedTagsListView() {

        ListView<String> tmpListView = new ListView<>();

        if (definedFileProperties != null) {

            tmpListView.getItems().addAll(definedFileProperties.getTags());
        }

        return tmpListView;
    }

    private CheckBox assembleTakeOverDescriptionAndTagsCheckBox() {

        CheckBox tmpCheckBox = new CheckBox("Take over description and tags to next document");

        if (definedFileProperties != null) {

            tmpCheckBox.setSelected(true);
        }

        return tmpCheckBox;
    }

    private void addEventListeners(DatePicker aDatePicker, CheckBox anUtilizeTimeInformationCheckBox, TextField
            aTimeInformationTextField, ComboBox<String> aQuickDescriptionWordsComboBox, TextField aDescriptionTextField,
            TextField aTagsTextField, ListView<String> anExistingTagsListView, ListView<String> aSelectedTagsListView,
            CheckBox aTakeOverDescriptionAndTagsCheckBox, Button aSubmitButton) {

        // DatePicker (without this manual set dates are not taken over)
        aDatePicker.focusedProperty().addListener((aChangedValue, anOldValue, aNewValue) -> {
            if (!aNewValue) {
                LocalDate tmpCurrentValue = aDatePicker.getValue();
                LocalDate tmpValueOfText =
                        aDatePicker.getConverter().fromString(aDatePicker.getEditor().getText());
                if(!tmpCurrentValue.equals(tmpValueOfText)) {
                    aDatePicker.setValue(tmpValueOfText);
                }
            }
        });

        // Utilize time information CheckBox
        anUtilizeTimeInformationCheckBox.selectedProperty().addListener(
                (anObservableValue, anOldValue, aNewValue) ->
                        aTimeInformationTextField.setDisable(!aNewValue));

        // Quick description words ComboBox
        aQuickDescriptionWordsComboBox.valueProperty().addListener((aChangedValue, anOldValue, aNewValue)
                -> aDescriptionTextField.setText(aNewValue + " " + aDescriptionTextField.getText()));

        // Tags TextField
        aTagsTextField.textProperty().addListener((anObservable, anOldValue, aNewValue) ->  {
            FilteredList<String> tmpFilteredList = (FilteredList<String>)anExistingTagsListView.getItems();
            if (aNewValue.isEmpty()) {
                tmpFilteredList.setPredicate(null);
            } else {
                final String tmpNewValueString = aNewValue.toUpperCase();
                tmpFilteredList.setPredicate(s -> s.toUpperCase().contains(tmpNewValueString));
            }
        });
        aTagsTextField.setOnKeyPressed(tmpKeyEvent -> {
            if (tmpKeyEvent.getCode().equals(KeyCode.ENTER)) {
                String tmpNewTag = StringUtil.retrieveTagNameSafeString(aTagsTextField.getText());
                addToListIfNotContainedYet(aSelectedTagsListView.getItems(), tmpNewTag);
                aTagsTextField.setText("");
            }
            else if (tmpKeyEvent.getCode().equals(KeyCode.DOWN)) {
                if (anExistingTagsListView.getItems().size() > 0) {
                    anExistingTagsListView.requestFocus();
                    anExistingTagsListView.getFocusModel().focus(0);
                    anExistingTagsListView.getSelectionModel().select(0);
                }
            }
        });

        // Existing tags ListView
        EventHandler<Event> tmpEventHandler = event -> {
            String tmpSelectedItem = anExistingTagsListView.getSelectionModel().getSelectedItem();
            addToListIfNotContainedYet(aSelectedTagsListView.getItems(), tmpSelectedItem);
            aTagsTextField.setText("");
            aTagsTextField.requestFocus();
        };
        anExistingTagsListView.setOnMouseClicked(tmpEventHandler);
        anExistingTagsListView.setOnKeyPressed(tmpKeyEvent -> {
            if (tmpKeyEvent.getCode().equals(KeyCode.UP)) {
                if (anExistingTagsListView.getSelectionModel().getSelectedIndex() == 0) {
                    aTagsTextField.requestFocus();
                }
            }
            else if (tmpKeyEvent.getCode().equals(KeyCode.ENTER)) {
                tmpEventHandler.handle(tmpKeyEvent);
            }
        });

        // Selected tags ListView
        aSelectedTagsListView.setOnMouseClicked(event -> {
            String tmpSelectedItem = aSelectedTagsListView.getSelectionModel().getSelectedItem();
            aSelectedTagsListView.getItems().remove(tmpSelectedItem);
        });

        // Submit button
        aSubmitButton.setOnAction(event -> {
            DefinedFileProperties tmpDfp = new DefinedFileProperties(aDatePicker.getValue(),
                    anUtilizeTimeInformationCheckBox.isSelected(), aTimeInformationTextField.getText(),
                    aDescriptionTextField.getText().trim(), aSelectedTagsListView.getItems(), fileType);
            moveDocumentCallBack.call(allFiles, currentFile, tmpDfp,
                    aTakeOverDescriptionAndTagsCheckBox.isSelected());
        });
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

    public interface MoveDocumentCallBack {

        void call(List<File> anAllFilesList, File aCurrentFile, DefinedFileProperties aDfp,
                  boolean aTakeOverDescriptionAndTags);
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
