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
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import com.sophisticatedapps.archiving.documentarchiver.util.CollectionUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.*;

public class ApplicationController {

    private final ApplicationServices applicationServices;
    private final DialogProvider dialogProvider;
    private final HostServices hostServices;

    public ApplicationController(ApplicationServices anApplicationServices, DialogProvider aDialogProvider,
                                     HostServices aHostServices) {

        applicationServices = anApplicationServices;
        dialogProvider = aDialogProvider;
        hostServices = aHostServices;
    }

    public DialogProvider getDialogProvider() {

        return dialogProvider;
    }

    public void placeIcons(Stage aStage) {

        // Set stage icon
        aStage.getIcons().add(GlobalConstants.APP_ICON);

        // Set taskbar icon (may not be supported on all systems (e.g. Linux))
        try {

            // AWT Image
            final URL imageResource =
                    Thread.currentThread().getContextClassLoader().getResource("binder-icon.png");
            final java.awt.Image tmpAwtImage = Toolkit.getDefaultToolkit().getImage(imageResource);

            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(tmpAwtImage);
        }
        catch (UnsupportedOperationException | UnsatisfiedLinkError e) {

            // never mind.
        }
    }

    /**
     * Open an external resource in an the System's default browser.
     *
     * @param   anURL   URL to open.
     */
    public void showExternalResource(String anURL) {

        hostServices.showDocument(anURL);
    }

    /**
     * Request the user to select a directory.
     *
     * @return  File object of the selected directory.
     */
    File requestDirectorySelection(Stage aStage) {

        return applicationServices.requestDirectorySelection(aStage);
    }

    /**
     * Request the user to select one or multiple Files.
     *
     * @return  List of File objects representing the selected files.
     */
    List<File> requestMultipleFilesSelection(Stage aStage) {

        return applicationServices.requestMultipleFilesSelection(aStage);
    }

    /**
     * Restart the Application.
     */
    void restartApp() {

        applicationServices.restartApp();
    }

    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    public void showDecideWhatToOpenDialog(Stage aStage, boolean aShowWelcomeMessage) {

        Optional<ButtonType> tmpResult =
                dialogProvider.provideDecideWhatToOpenDialog(aShowWelcomeMessage).showAndWait();

        // ButtonData.NO means open a directory, YES means open (multiple) file(s).
        if (ButtonBar.ButtonData.NO == tmpResult.get().getButtonData()) { // NOSONAR

            File tmpDirectory = applicationServices.requestDirectorySelection(aStage);

            if (!Objects.isNull(tmpDirectory)) {

                List<File> tmpWrapperList = new ArrayList<>();
                DirectoryUtil.readDirectoryRecursive(
                        tmpDirectory, tmpWrapperList, DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);

                if (!tmpWrapperList.isEmpty()) {

                    tmpWrapperList.sort(Comparator.naturalOrder());
                    setNewAllDocumentsAndCurrentDocument(aStage, tmpWrapperList, tmpWrapperList.get(0));
                }
                else {

                    dialogProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
                }
            }
        }
        else {

            List<File> tmpFilesList = applicationServices.requestMultipleFilesSelection(aStage);

            if (!CollectionUtil.isNullOrEmpty(tmpFilesList)) {

                // We have to wrap the result in a new List, since the given List may not be modifiable.
                List<File> tmpWrapperList = new ArrayList<>(tmpFilesList);
                tmpWrapperList.sort(Comparator.naturalOrder());
                setNewAllDocumentsAndCurrentDocument(aStage, tmpWrapperList, tmpWrapperList.get(0));
            }
        }
    }

    protected void runLaterOrNowIfOnFXThread(Runnable aRunnable) {

        if (Platform.isFxApplicationThread()) {

            aRunnable.run();
        }
        else {

            Platform.runLater(aRunnable);
        }
    }

    protected void setNewAllDocumentsAndCurrentDocument(Stage aStage, List<File> aNewAllDocumentsList,
                                                        File aNewCurrentDocument) {

        runLaterOrNowIfOnFXThread(() -> {

            ObservableMap<Object, Object> tmpStageProperties = aStage.getProperties();
            tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aNewAllDocumentsList);
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aNewCurrentDocument);
        });
    }

    protected void setNewAllDocuments(Stage aStage, List<File> aNewAllDocumentsList) {

        runLaterOrNowIfOnFXThread(() -> {

            ObservableMap<Object, Object> tmpStageProperties = aStage.getProperties();
            tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aNewAllDocumentsList);
        });
    }

    protected void setNewCurrentDocument(Stage aStage, File aNewCurrentDocument) {

        runLaterOrNowIfOnFXThread(() -> {

            ObservableMap<Object, Object> tmpStageProperties = aStage.getProperties();
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aNewCurrentDocument);
        });
    }

    /**
     * Get the current document (from stage properties).
     *
     * @param   aStage  Stage to get the current document for.
     * @return  Current document as File object.
     */
    protected File getCurrentDocument(Stage aStage) {

        return (File)aStage.getProperties().get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);
    }

    /**
     * Get the current all-documents List (from stage properties).
     *
     * @param   aStage  Stage to get the current document list for.
     * @return  Current all-documents List.
     */
    @SuppressWarnings("unchecked")
    protected List<File> getAllDocuments(Stage aStage) {

        return (List<File>)aStage.getProperties().get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY);
    }

    protected void importFreshDirectory(Stage aStage, File aDirectory) {

        if (!Objects.isNull(aDirectory)) {

            List<File> tmpWrapperList = new ArrayList<>();
            DirectoryUtil.readDirectoryRecursive(aDirectory, tmpWrapperList, DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);

            if (!tmpWrapperList.isEmpty()) {

                tmpWrapperList.sort(Comparator.naturalOrder());
                setNewAllDocumentsAndCurrentDocument(aStage, tmpWrapperList, tmpWrapperList.get(0));
            }
            else {

                dialogProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
            }
        }
    }

    protected void importFreshFilesList(Stage aStage, List<File> aFilesList) {

        if (!CollectionUtil.isNullOrEmpty(aFilesList)) {

            // We have to wrap the result in a new List, since the given List may not be modifiable.
            List<File> tmpWrapperList = new ArrayList<>(aFilesList);
            tmpWrapperList.sort(Comparator.naturalOrder());
            setNewAllDocumentsAndCurrentDocument(aStage, tmpWrapperList, tmpWrapperList.get(0));
        }
    }

}
