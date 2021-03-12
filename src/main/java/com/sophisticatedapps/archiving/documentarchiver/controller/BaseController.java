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
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseController {

    private final List<MapChangeListener<Object, Object>> stagePropertiesListenersList = new ArrayList<>();

    protected Stage stage;

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

}
