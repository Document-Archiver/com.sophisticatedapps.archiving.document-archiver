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

import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileSystemViewPaneController extends BaseController {

    @FXML
    private Pane fileSystemViewPane;

    @FXML
    private TableView<ZipEntry> fileSystemTableView;

    @FXML
    private TableColumn<ZipEntry, String> nameColumn;

    @FXML
    private TableColumn<ZipEntry, String> sizeColumn;

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Cell value factories
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(aZipEntryStringCellDataFeatures -> {
            ZipEntry tmpZipEntry = aZipEntryStringCellDataFeatures.getValue();
            return (new ReadOnlyStringWrapper(
                    tmpZipEntry.isDirectory() ? "DIR" : String.valueOf(tmpZipEntry.getSize()))); // NOSONAR
        });

        // Row factory
        fileSystemTableView.setRowFactory(aTableView -> {

            TableRow<ZipEntry> tmpTableRow = new TableRow<>();

            tmpTableRow.setOnMouseClicked(anEvent -> {

                if (anEvent.getClickCount() == 2 && (! tmpTableRow.isEmpty()) ) {

                    openZipEntryInExternalViewer(tmpTableRow.getItem());
                }
            });

            return tmpTableRow;
        });

        // Listeners
        fileSystemViewPane.widthProperty().addListener((anObservable, anOldValue, aNewValue) -> setWidths());
        fileSystemViewPane.heightProperty().addListener((anObservable, anOldValue, aNewValue) ->
                fileSystemTableView.setPrefHeight(aNewValue.doubleValue()));

    }

    private void setWidths() {

        double tmpFileSystemViewPanePrefWidth = fileSystemViewPane.getPrefWidth();
        fileSystemTableView.setPrefWidth(tmpFileSystemViewPanePrefWidth);
        nameColumn.setPrefWidth(tmpFileSystemViewPanePrefWidth * 0.8);
        sizeColumn.setPrefWidth((tmpFileSystemViewPanePrefWidth * 0.2) - 20);
    }

    public void setZipFile(ZipFile aZipFile) {

        Enumeration<? extends ZipEntry> tmpZipEntries = aZipFile.entries(); // NOSONAR
        ObservableList<ZipEntry> tmpTableViewItems = fileSystemTableView.getItems();

        while (tmpZipEntries.hasMoreElements()) {

            tmpTableViewItems.add(tmpZipEntries.nextElement());
        }
    }

    private void openZipEntryInExternalViewer(ZipEntry aZipEntry) {

        try (FileSystem tmpZipFileSystem = FileSystems.newFileSystem(getCurrentDocument().toPath(), null)) {

            // Check if ZipEntry is faulty (file names can contain path traversal attacks (eg: ../../../etc/password))
            File tmpFileToExtract = new File(aZipEntry.getName());
            String tmpCanonicalDestinationPath = tmpFileToExtract.getCanonicalPath();

            if (!tmpCanonicalDestinationPath.startsWith((new File("")).getCanonicalPath())) {

                throw (new IOException("Entry is outside of the target directory"));
            }

            Path tmpPathToExtract = tmpZipFileSystem.getPath(tmpFileToExtract.getPath());
            File tmpTempFile = new File(System.getProperty("java.io.tmpdir"),
                    (UUID.randomUUID().toString() + '.' + FileUtil.getFileExtension(tmpPathToExtract)));
            Files.copy(tmpPathToExtract, tmpTempFile.toPath());

            openExternalViewer(tmpTempFile);
        }
        catch (IOException e) {

            throw (new RuntimeException("Could not get ZIP file contents: " + e.getMessage()));
        }
    }

}
