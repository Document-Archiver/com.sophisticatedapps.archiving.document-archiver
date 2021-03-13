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

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class DocumentsPaneController extends BaseController {

    @FXML
    private ListView<File> documentsListView;

    @Override
    @SuppressWarnings("unchecked")
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Set cell factory for documents ListView.
        documentsListView.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(File aFile, boolean anEmpty) {

                super.updateItem(aFile, anEmpty);

                if (anEmpty || (aFile == null)) {

                    setText(null);
                }
                else {

                    setText(aFile.getName());
                }
            }
        });

        // Add listeners
        addAllDocumentsChangedListener(aChange ->
                handleAllDocumentsChanged((List<File>)aChange.getValueAdded()));
        addCurrentDocumentChangedListener(aChange ->
                handleCurrentDocumentChanged((File)aChange.getValueAdded()));
    }

    /**
     * Method to call when the List of all documents has changed.
     *
     * @param   aNewAllDocumentsList The new all documents List.
     */
    private void handleAllDocumentsChanged(List<File> aNewAllDocumentsList) {

        if (aNewAllDocumentsList != null) {

            documentsListView.getItems().setAll(aNewAllDocumentsList);
        }
        else {

            documentsListView.getItems().clear();
        }
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (aNewCurrentDocument != null) {

            final ObservableList<File> tmpItems = documentsListView.getItems();

            final int tmpCurrentDocumentIndex = tmpItems.indexOf(aNewCurrentDocument);
            documentsListView.getFocusModel().focus(tmpCurrentDocumentIndex);
            documentsListView.getSelectionModel().select(tmpCurrentDocumentIndex);
        }
    }

    @FXML
    protected void handleDocumentsListViewClicked() {

        setNewCurrentDocument(documentsListView.getSelectionModel().getSelectedItem());
    }

}
