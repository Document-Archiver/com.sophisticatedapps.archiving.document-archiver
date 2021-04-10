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
import com.sophisticatedapps.archiving.documentarchiver.model.Archive;
import com.sophisticatedapps.archiving.documentarchiver.model.Tags;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InfoPaneController extends BaseController {

    private static final Map<FileTypeEnum, Class<? extends FileTimeAgent>> TIME_AGENTS_BY_FILETYPE;
    private static final KeyCodeCombination SHORTCUT_R_KEYCODE_COMBINATION = new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN);

    private final List<ChangeListener<Boolean>> datePickerFocusedPropertyListenersList = new ArrayList<>();
    private final List<ChangeListener<Boolean>> utilizeTimeInformationCheckBoxSelectedPropertyListenersList =
            new ArrayList<>();

    static {

        // Just the FileTypes which doesn't use "GenericFileTimeAgent.class" -> we are using 'getOrDefault' later.
        TIME_AGENTS_BY_FILETYPE = Map.of(FileTypeEnum.JPG, JPGFileTimeAgent.class);
    }

    @FXML
    private VBox infoPane;

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

    private AlertProvider alertProvider;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public InfoPaneController() {

        this(new AlertProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom AlertProvider.
     *
     * @param   anAlertProvider AlertProvider to use.
     */
    public InfoPaneController(AlertProvider anAlertProvider) {

        this.alertProvider = anAlertProvider;
    }

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Set converter, filter, etc.
        datePicker.setConverter(new DatePickerStringConverter());
        setNewExistingTagsToListView(Collections.emptyList());

        // Set values
        quickDescriptionWordsComboBox.getItems().addAll(PropertiesUtil.QUICK_DESCRIPTION_WORDS.split(","));

        // Add listener
        infoPane.widthProperty().addListener((anObservable, anOldValue, aNewValue) -> setWidths());
        infoPane.heightProperty().addListener((anObservable, anOldValue, aNewValue) -> setHeights());

        addCurrentDocumentChangedListener(aChange ->
                handleCurrentDocumentChanged((File)aChange.getValueAdded()));

        final ChangeListener<Boolean> tmpDatePickerFocusedPropertyListener =
                ((aChangedValue, anOldValue, aNewValue) -> handleDatePickerFocusedPropertyValueChanged(aNewValue));
        datePickerFocusedPropertyListenersList.add(tmpDatePickerFocusedPropertyListener);
        datePicker.focusedProperty().addListener(tmpDatePickerFocusedPropertyListener);

        final ChangeListener<Boolean> tmpUticbSelectedPropertyListener =
                ((anObservable, anOldValue, aNewValue) ->
                        handleUtilizeTimeInformationCheckBoxValueChanged(aNewValue));
        utilizeTimeInformationCheckBoxSelectedPropertyListenersList.add(tmpUticbSelectedPropertyListener);
        utilizeTimeInformationCheckBox.selectedProperty().addListener(tmpUticbSelectedPropertyListener);

        quickDescriptionWordsComboBox.valueProperty().addListener((aChangedValue, anOldValue, aNewValue) ->
                handleQuickDescriptionWordsComboBoxValueChanged(aNewValue));

        tagsTextField.textProperty().addListener((anObservable, anOldValue, aNewValue) ->
                handleTagsTextFieldTextChanged(aNewValue));

        // Listener for when the pane "gets" or "loses" a Scene (aNewScene) -> set/remove shortcuts.
        infoPane.sceneProperty().addListener((anObs, anOldScene, aNewScene) -> {
            if (!Objects.isNull(aNewScene)) {
                aNewScene.getAccelerators().put(SHORTCUT_R_KEYCODE_COMBINATION, this::handleSubmitButtonAction);
            }
            else {
                anOldScene.getAccelerators().remove(SHORTCUT_R_KEYCODE_COMBINATION);
            }
        });
    }

    @Override
    public void rampDown() {

        super.rampDown();

        // Remove all added Listeners
        ReadOnlyBooleanProperty tmpDatePickerFocusedProperty = datePicker.focusedProperty();
        for (ChangeListener<Boolean> tmpCurrentListener : datePickerFocusedPropertyListenersList) {
            tmpDatePickerFocusedProperty.removeListener(tmpCurrentListener);
        }
        datePickerFocusedPropertyListenersList.clear();

        BooleanProperty tmpUticbSelectedProperty = utilizeTimeInformationCheckBox.selectedProperty();
        for (ChangeListener<Boolean> tmpCurrentListener : utilizeTimeInformationCheckBoxSelectedPropertyListenersList) {
            tmpUticbSelectedProperty.removeListener(tmpCurrentListener);
        }
        utilizeTimeInformationCheckBoxSelectedPropertyListenersList.clear();
    }

    private void setWidths() {

        quickDescriptionWordsComboBox.setPrefWidth(infoPane.getPrefWidth());
    }

    private void setHeights() {

        double tmpTagsListViewsPrefHeight = (infoPane.getPrefHeight() - 350);
        existingTagsListView.setPrefHeight(tmpTagsListViewsPrefHeight);
        selectedTagsListView.setPrefHeight(tmpTagsListViewsPrefHeight);
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (!Objects.isNull(aNewCurrentDocument)) {

            FileTypeEnum tmpFileType = FileUtil.getFiletype(aNewCurrentDocument);
            LocalDateTime tmpFileDateTime;

            try {
                tmpFileDateTime = TIME_AGENTS_BY_FILETYPE.getOrDefault(tmpFileType, GenericFileTimeAgent.class)
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

                descriptionTextField.setText(FileUtil.getFileNameWithoutExtension(aNewCurrentDocument));
                selectedTagsListView.getItems().clear();
            }

            setNewExistingTagsToListView(new ArrayList<>(Tags.getExistingTags(tmpFileType)));
        }
    }

    /**
     * Method to call when the value of the DatePicker was changed.
     * Not possible to set via FXML, has to be called via 'datePicker.focusedProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    protected void handleDatePickerFocusedPropertyValueChanged(boolean aNewValue) {

        // Without this, manual set dates are not taken over
        if (!aNewValue) {

            LocalDate tmpCurrentValue = datePicker.getValue();
            LocalDate tmpValueOfText = datePicker.getConverter().fromString(datePicker.getEditor().getText());

            if(!String.valueOf(tmpCurrentValue).equals(String.valueOf(tmpValueOfText))) {

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
    protected void handleUtilizeTimeInformationCheckBoxValueChanged(boolean aNewValue) {

        timeInformationTextField.setDisable(!aNewValue);
    }

    /**
     * Method to call when the value of the quick description words ComboBox was changed.
     * Not possible to set via FXML, has to be called via 'quickDescriptionWordsComboBox.valueProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    protected void handleQuickDescriptionWordsComboBoxValueChanged(String aNewValue) {

        descriptionTextField.setText(aNewValue + " " + descriptionTextField.getText());
    }

    /**
     * Method to call when the text of the tag TextField was changed.
     * Not possible to set via FXML, has to be called via 'tagsTextField.textProperty().addListener'.
     *
     * @param   aNewValue   Value after change.
     */
    protected void handleTagsTextFieldTextChanged(String aNewValue) {

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
    protected void handleTagsTextFieldKeyPressed(KeyEvent aKeyEvent) {

        if (aKeyEvent.getCode().equals(KeyCode.ENTER)) {

            String tmpNewTag = StringUtil.retrieveTagNameSafeString(tagsTextField.getText());
            CollectionUtil.addToListIfNotContainedYet(selectedTagsListView.getItems(), tmpNewTag);
            tagsTextField.setText("");
        }
        else if (aKeyEvent.getCode().equals(KeyCode.DOWN) && (!existingTagsListView.getItems().isEmpty())) {

            existingTagsListView.requestFocus();
            existingTagsListView.getFocusModel().focus(0);
            existingTagsListView.getSelectionModel().select(0);
        }
    }

    @FXML
    protected void handleExistingTagsListViewClicked() {

        String tmpSelectedItem = existingTagsListView.getSelectionModel().getSelectedItem();
        CollectionUtil.addToListIfNotContainedYet(selectedTagsListView.getItems(), tmpSelectedItem);
        tagsTextField.setText("");
        tagsTextField.requestFocus();
    }

    @FXML
    protected void handleExistingTagsListViewKeyPressed(KeyEvent aKeyEvent) {

        if (aKeyEvent.getCode().equals(KeyCode.UP)) {

            if (existingTagsListView.getSelectionModel().getSelectedIndex() == 0) {

                tagsTextField.requestFocus();
            }
        }
        else if (aKeyEvent.getCode().equals(KeyCode.ENTER)) {

            // Same as if clicked.
            handleExistingTagsListViewClicked();
        }
    }

    @FXML
    protected void handleSelectedTagsListViewClicked() {

        String tmpSelectedItem = selectedTagsListView.getSelectionModel().getSelectedItem();
        selectedTagsListView.getItems().remove(tmpSelectedItem);
    }

    @FXML
    protected void handleSubmitButtonAction() {

        File tmpCurrentDocument = getCurrentDocument();

        DefinedFileProperties tmpDfp = new DefinedFileProperties(datePicker.getValue(),
                utilizeTimeInformationCheckBox.isSelected(), timeInformationTextField.getText(),
                descriptionTextField.getText().trim(), selectedTagsListView.getItems());

        try {

            Archive.moveFileToArchive(tmpCurrentDocument, tmpDfp);

            List<File> tmpAllDocuments = getAllDocuments();
            tmpAllDocuments.remove(tmpCurrentDocument);
            boolean tmpContinueWithNext = true;

            // All done? Show dialog asking the user to continue.
            if (tmpAllDocuments.isEmpty()) {

                Optional<ButtonType> tmpChoice = alertProvider.provideAllDoneAlert().showAndWait();
                if (tmpChoice.isPresent()) {
                    tmpContinueWithNext = (ButtonBar.ButtonData.NEXT_FORWARD == tmpChoice.get().getButtonData());
                }
            }

            if (tmpContinueWithNext) {

                // Trigger listeners by first setting the all documents List to null
                // (just removing a file from the list will not trigger listeners).
                setNewAllDocuments(null);

                setNewAllDocumentsAndCurrentDocument(tmpAllDocuments,
                        (tmpAllDocuments.isEmpty() ? null : tmpAllDocuments.get(0)));
            }
            else {

                stage.hide();
            }
        }
        catch (IOException e) {

            alertProvider.provideArchiveFileNotSuccessfulAlert(e).showAndWait();
        }
    }

    protected void setNewExistingTagsToListView(List<String> aNewExistingTagsList) {

        ObservableList<String> tmpObservableList = FXCollections.observableList(aNewExistingTagsList);
        existingTagsListView.setItems(new FilteredList<>(tmpObservableList));
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

    protected static class AlertProvider {

        private static final ButtonType CONTINUE_BUTTON_TYPE = new ButtonType(
                LanguageUtil.i18n("info-pane-controller.alert-provider.all-done-alert.continue-button.text"),
                ButtonBar.ButtonData.NEXT_FORWARD);
        private static final ButtonType CLOSE_APP_BUTTON_TYPE = new ButtonType(
                LanguageUtil.i18n("info-pane-controller.alert-provider.all-done-alert.close-app-button.text"),
                ButtonBar.ButtonData.FINISH);

        public Alert provideArchiveFileNotSuccessfulAlert(Exception anException) {

            return (new Alert(Alert.AlertType.ERROR, anException.getMessage(), ButtonType.CLOSE));
        }

        public Alert provideAllDoneAlert() {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("info-pane-controller.alert-provider.all-done-alert.content-text"),
                    CONTINUE_BUTTON_TYPE, CLOSE_APP_BUTTON_TYPE));
        }
    }

}
