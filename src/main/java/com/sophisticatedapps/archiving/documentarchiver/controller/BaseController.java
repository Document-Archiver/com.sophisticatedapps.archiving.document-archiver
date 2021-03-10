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
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class BaseController {

    protected Stage stage;

    public void rampUp(Stage aStage) {

        this.stage = aStage;
    }

    protected File getCurrentDocument() {

        return (File)stage.getProperties().get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);
    }

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

        ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();

        tmpStageProperties.addListener((MapChangeListener<Object, Object>) aChange -> {

            // Current document changed?
            if (aChange.getKey().equals(aProperty)) {

                aMapChangeListener.onChanged(aChange);
            }
        });
    }

    protected void addAllDocumentsChangedListener(MapChangeListener<Object, Object> aMapChangeListener) {

        addListenerForProperty(aMapChangeListener, GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY);
    }

    protected void addCurrentDocumentChangedListener(MapChangeListener<Object, Object> aMapChangeListener) {

        addListenerForProperty(aMapChangeListener, GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY);
    }

    protected void setNewAllDocumentsAndCurrentDocument(List<File> anAllDocumentsList, File aCurrentDocument) {

        ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
        tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, anAllDocumentsList);
        tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aCurrentDocument);
    }

    protected void setNewAllDocuments(List<File> anAllDocumentsList) {

        ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
        tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, anAllDocumentsList);
    }

    protected void setNewCurrentDocument(File aCurrentDocument) {

        ObservableMap<Object, Object> tmpStageProperties = stage.getProperties();
        tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aCurrentDocument);
    }

}
