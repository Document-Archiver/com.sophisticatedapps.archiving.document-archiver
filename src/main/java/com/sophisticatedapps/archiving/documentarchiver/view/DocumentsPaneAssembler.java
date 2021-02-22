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

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class DocumentsPaneAssembler {

    private List<File> allFiles;
    private File currentFile;

    private DocumentSelectedCallback documentSelectedCallback;

    public DocumentsPaneAssembler allFiles(List<File> anAllFilesList) {

        this.allFiles = anAllFilesList;
        return this;
    }

    public DocumentsPaneAssembler currentFile(File aFile) {

        this.currentFile = aFile;
        return this;
    }

    public DocumentsPaneAssembler documentSelectedCallback(DocumentSelectedCallback aDocumentSelectedCallback) {

        this.documentSelectedCallback = aDocumentSelectedCallback;
        return this;
    }

    public VBox assemble() {

        if (allFiles == null) {

            throw (new RuntimeException("allFiles not set."));
        }
        if (currentFile == null) {

            throw (new RuntimeException("currentFile not set."));
        }

        final ListView<File> tmpDocumentsListView = assembleDocumentsView();

        return (new DocumentsPaneBuilder())
                .documentsLabel(new Label("Documents"))
                .documentsListView(tmpDocumentsListView)
                .build();
    }

    private ListView<File> assembleDocumentsView() {

        final ListView<File> tmpDocumentsListView = new ListView<>();

        tmpDocumentsListView.setCellFactory(param -> new ListCell<>() {
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

        if (this.documentSelectedCallback != null) {

            tmpDocumentsListView.setOnMouseClicked(mouseEvent -> documentSelectedCallback.call(
                    allFiles, tmpDocumentsListView.getSelectionModel().getSelectedItem()));
        }

        final ObservableList<File> tmpItems = tmpDocumentsListView.getItems();

        tmpItems.addAll(allFiles);

        final int tmpCurrentFileIndex = tmpItems.indexOf(currentFile);
        tmpDocumentsListView.getFocusModel().focus(tmpCurrentFileIndex);
        tmpDocumentsListView.getSelectionModel().select(tmpCurrentFileIndex);

        return tmpDocumentsListView;
    }

    public interface DocumentSelectedCallback {

        void call(List<File> anAllFilesList, File aCurrentFile);
    }

}
