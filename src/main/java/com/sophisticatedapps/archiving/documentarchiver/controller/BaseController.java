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
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.ThemeUtil;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BaseController {

    private final List<MapChangeListener<Object, Object>> stagePropertiesListenersList = new ArrayList<>();

    protected Stage stage;
    protected DialogProvider dialogProvider;

    /**
     * Default constructor.
     */
    protected BaseController() {

        this(new DialogProvider());
    }

    /**
     * Alternative constructor which allows to pass a custom DialogProvider.
     *
     * @param   aDialogProvider DialogProvider to use.
     */
    protected BaseController(DialogProvider aDialogProvider) {

        this.dialogProvider = aDialogProvider;
    }

    /**
     * Initialize the controller.
     *
     * @param   aStage  JavaFX stage.
     */
    public void rampUp(Stage aStage) {

        this.stage = aStage;
    }

    /**
     * Cleanup.
     */
    public void rampDown() {

        ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();

        // Remove all added Listeners
        for (MapChangeListener<Object, Object> tmpCurrentListener : stagePropertiesListenersList) {

            tmpStageProperties.removeListener(tmpCurrentListener);
        }

        // Clear our List of Listeners
        stagePropertiesListenersList.clear();

        // Release stage
        this.stage = null;
    }

    /**
     * Get the current document (from stage properties).
     *
     * @return  Current document as File object.
     */
    protected File getCurrentDocument() {

        return (File)stage.getProperties().get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);
    }

    /**
     * Get the current all-documents List (from stage properties).
     *
     * @return  Current all-documents List.
     */
    @SuppressWarnings("unchecked")
    protected List<File> getAllDocuments() {

        return (List<File>)stage.getProperties().get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY);
    }

    /**
     * Add a change listener for a certain property within the stage properties.
     *
     * @param   aMapChangeListener  Change listener to add.
     * @param   aProperty           Property to add the change listener for.
     */
    protected void addListenerForProperty(MapChangeListener<Object, Object> aMapChangeListener, String aProperty) {

        // Create a wrapper, which will only trigger the given Listener when the given property has changed.
        MapChangeListener<Object, Object> tmpListenerWrapper = (aChange -> {

            // Current document changed?
            if (aChange.getKey().equals(aProperty)) {

                aMapChangeListener.onChanged(aChange);
            }
        });

        // Add the wrapper listener to our List of added Listeners
        stagePropertiesListenersList.add(tmpListenerWrapper);

        // Add it to the stage properties.
        stage.getProperties().addListener(tmpListenerWrapper);
    }

    protected void addAllDocumentsChangedListener(MapChangeListener<Object, Object> aMapChangeListener) {

        addListenerForProperty(aMapChangeListener, GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY);
    }

    protected void addCurrentDocumentChangedListener(MapChangeListener<Object, Object> aMapChangeListener) {

        addListenerForProperty(aMapChangeListener, GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);
    }

    protected void setNewAllDocumentsAndCurrentDocument(List<File> aNewAllDocumentsList, File aNewCurrentDocument) {

        Platform.runLater(() -> {

            ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
            tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aNewAllDocumentsList);
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aNewCurrentDocument);
        });
    }

    protected void setNewAllDocuments(List<File> aNewAllDocumentsList) {

        Platform.runLater(() -> {

            ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
            tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aNewAllDocumentsList);
        });
    }

    protected void setNewCurrentDocument(File aNewCurrentDocument) {

        Platform.runLater(() -> {

            ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aNewCurrentDocument);
        });
    }

    protected static class DialogProvider {

        public Dialog<ButtonType> provideWelcomeDialog() {

            ImageView tmpImageView = new ImageView(GlobalConstants.APP_ICON);
            tmpImageView.setFitWidth(80);
            tmpImageView.setFitHeight(80);

            ButtonType tmpOpenFilesButtonType = new ButtonType(
                    LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-files-button.text"),
                    ButtonBar.ButtonData.YES);
            ButtonType tmpOpenDirectoryButtonType = new ButtonType(
                    LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-directory-button.text"),
                    ButtonBar.ButtonData.NO);

            Dialog<ButtonType> tmpDialog = new Dialog<>();
            tmpDialog.setGraphic(tmpImageView);
            tmpDialog.setTitle(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.title"));
            tmpDialog.setHeaderText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.header-text"));
            tmpDialog.setContentText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.content-text"));
            tmpDialog.getDialogPane().getButtonTypes().addAll(tmpOpenFilesButtonType, tmpOpenDirectoryButtonType);

            return tmpDialog;
        }

        public Dialog<ButtonType> provideAboutDialog() {

            return (new Alert(Alert.AlertType.NONE,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.about-dialog"),
                    ButtonType.CLOSE));
        }

        public Dialog<ButtonType> providePreferencesDialog(Pane aPreferencesPane) {

            Dialog<ButtonType> tmpDialog = new Dialog<>();
            tmpDialog.setTitle(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.title"));
            tmpDialog.setHeaderText(LanguageUtil.i18n("menu-bar-controller.preferences-dialog.header-text"));

            DialogPane tmpDialogPane = tmpDialog.getDialogPane();
            tmpDialogPane.setContent(aPreferencesPane);
            tmpDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            tmpDialogPane.getStylesheets().add(ThemeUtil.getCurrentTheme().getPathToCss());

            return tmpDialog;
        }

        public Alert providePreferencesChangedAlert() {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.preferences-changed-alert"),
                    ButtonType.CLOSE));
        }

        public Alert providePreferencesChangedAlert(Locale aLanguageLocale) {

            return (new Alert(Alert.AlertType.INFORMATION,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.preferences-changed-alert",
                            aLanguageLocale),
                    ButtonType.CLOSE));
        }

        public Alert provideDirectoryDoesNotContainFilesAlert() {

            return (new Alert(Alert.AlertType.WARNING,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.directory-does-not-contain-files-alert"),
                    ButtonType.CLOSE));
        }
    }

}
