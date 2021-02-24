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

package com.sophisticatedapps.archiving.documentarchiver;

import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import com.sophisticatedapps.archiving.documentarchiver.view.TriplePane;
import com.sophisticatedapps.archiving.documentarchiver.view.TriplePaneAssembler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {

    private static final TriplePaneAssembler.TriplePanePrefWidthRatios TRIPLE_PANE_PREF_WIDTH_RATIOS =
            new TriplePaneAssembler.TriplePanePrefWidthRatios(2, 5, 3);
    private static final Insets SUB_PANES_INSETS = new Insets(10);
    private static final double ELEMENT_SPACING = 5;

    private static final TriplePaneAssembler.DocumentSelectedCallback DOCUMENT_SELECTED_CALLBACK =
            assembleDocumentSelectedCallback();
    private static final TriplePaneAssembler.MoveDocumentCallBack MOVE_DOCUMENT_CALL_BACK =
            assembleMoveDocumentCallBack();

    private static final Pattern PARAMETER_PATH_PATTERN =
            Pattern.compile(".*?/+(.*)");

    private static File currentFile;
    private static List<File> allFiles;

    public static void main(String[] args) {

        if (args.length > 0) {

            String tmpOrgFilePath;

            Matcher tmpMatcher = PARAMETER_PATH_PATTERN.matcher(args[0]);
            if (tmpMatcher.find()) {
                String tmpFoundPath = tmpMatcher.group(1);
                try {
                    if (args[0].startsWith("file:")) {
                        tmpFoundPath = URLDecoder.decode(tmpFoundPath, Charset.defaultCharset().toString());
                    }
                }
                catch (UnsupportedEncodingException e) {
                    throw (new RuntimeException("Could not decode path: ".concat(e.getMessage())));
                }
                tmpOrgFilePath = "/".concat(tmpFoundPath);
            }
            else {
                throw (new RuntimeException("Invalid argument given: ".concat(args[0])));
            }

            File tmpFile = new File(tmpOrgFilePath);
            if(!tmpFile.exists()) {
                throw (new RuntimeException("File does not exist: ".concat(tmpOrgFilePath)));
            }

            if (tmpFile.isDirectory()) {
                // We have to wrap the result in a new List, since the result is not modifiable.
                allFiles = new ArrayList<>(
                        Arrays.asList(Objects.requireNonNull(tmpFile.listFiles(File::isFile))));
                if (!allFiles.isEmpty()) {
                    currentFile = allFiles.get(0);
                }
            }
            else {
                currentFile = tmpFile;
                allFiles = new ArrayList<>();
                allFiles.add(currentFile);
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage aPrimaryStage) {

        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        aPrimaryStage.setX(tmpBounds.getMinX());
        aPrimaryStage.setY(tmpBounds.getMinY());
        aPrimaryStage.setWidth(tmpBounds.getWidth());
        aPrimaryStage.setHeight(tmpBounds.getHeight());

        if (currentFile != null) {

            assembleAndSetSceneForExistingFile(aPrimaryStage, allFiles, currentFile);
        }
        else {

            assembleAndSetSceneForNonExistingFile(aPrimaryStage, "Choose file(s) or directory.");
        }

        placeIcons(aPrimaryStage);
        aPrimaryStage.show();
    }

    private static void assembleAndSetSceneForExistingFile(Stage aStage, List<File> aFilesList, File aFile) {

        assembleAndSetSceneForExistingFile(aStage, aFilesList, aFile, null);
    }

    private static void assembleAndSetSceneForExistingFile(Stage aStage, List<File> aFilesList, File aFile,
                                                           DefinedFileProperties aDfp) {

        TriplePane tmpTriplePane = (new TriplePaneAssembler())
                .allFiles(aFilesList)
                .currentFile(aFile)
                .fileType(getFiletype(aFile))
                .definedFileProperties(aDfp)
                .prefWidthRatios(TRIPLE_PANE_PREF_WIDTH_RATIOS)
                .subPanesPadding(SUB_PANES_INSETS)
                .elementSpacing(ELEMENT_SPACING)
                .documentSelectedCallback(DOCUMENT_SELECTED_CALLBACK)
                .moveDocumentCallBack(MOVE_DOCUMENT_CALL_BACK)
                .assemble(aStage);

        aStage.setTitle("Archiving: ".concat(aFile.getPath()));
        placeNewRootPane(aStage, tmpTriplePane);
    }

    private static void assembleAndSetSceneForNonExistingFile(Stage aStage, String aMessage) {

        Button tmpChooseFilesButton = new Button("Choose file(s)");
        tmpChooseFilesButton.setOnAction(actionEvent -> {

            FileChooser tmpFileChooser = new FileChooser();
            List<File> tmpFilesList = tmpFileChooser.showOpenMultipleDialog(aStage);

            if ((tmpFilesList != null) && (!tmpFilesList.isEmpty())) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                assembleAndSetSceneForExistingFile(aStage, (new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
            }
            else {

                assembleAndSetSceneForNonExistingFile(aStage, "No file selected.");
            }
        });

        Button tmpChooseDirectoryButton = new Button("Choose directory");
        tmpChooseDirectoryButton.setOnAction(actionEvent -> {

            DirectoryChooser tmpDirectoryChooser = new DirectoryChooser();
            File tmpDirectory = tmpDirectoryChooser.showDialog(aStage);

            if (tmpDirectory != null) {

                List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(tmpDirectory.listFiles(File::isFile)));

                if (!tmpFilesList.isEmpty()) {

                    // We have to wrap the result in a new List, since the result is not modifiable.
                    assembleAndSetSceneForExistingFile(aStage, (new ArrayList<>(tmpFilesList)), tmpFilesList.get(0));
                }
                else {

                    assembleAndSetSceneForNonExistingFile(aStage,
                            "Empty directory: ".concat(tmpDirectory.getPath()));
                }
            }
            else {

                assembleAndSetSceneForNonExistingFile(aStage, "No directory selected.");
            }
        });

        VBox tmpChooseFilesOrDirectoryPane = new VBox(10);
        tmpChooseFilesOrDirectoryPane.setAlignment(Pos.CENTER);
        tmpChooseFilesOrDirectoryPane.getChildren().addAll(
                new Label(aMessage), tmpChooseFilesButton, tmpChooseDirectoryButton);

        aStage.setTitle("Choose file(s) or directory");
        placeNewRootPane(aStage, tmpChooseFilesOrDirectoryPane);
    }

    private static TriplePaneAssembler.DocumentSelectedCallback assembleDocumentSelectedCallback() {

        return App::assembleAndSetSceneForExistingFile;
    }

    private static TriplePaneAssembler.MoveDocumentCallBack assembleMoveDocumentCallBack() {

        return (aPrimaryStage, anAllFilesList, aCurrentFile, aDfp, aTakeOverDescriptionAndTags) -> {

            try {

                FileUtil.moveFileToArchive(aCurrentFile, aDfp);
                anAllFilesList.remove(aCurrentFile);

                if (anAllFilesList.isEmpty()) {

                    assembleAndSetSceneForNonExistingFile(aPrimaryStage, "All done :-) Start over?");
                }
                else {

                    assembleAndSetSceneForExistingFile(aPrimaryStage, anAllFilesList, anAllFilesList.get(0),
                            (aTakeOverDescriptionAndTags ? aDfp : null));
                }
            }
            catch (Exception e) {

                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
                alert.showAndWait();
            }
        };
    }

    private static void placeNewRootPane(Stage aStage, Pane aPane) {

        Scene tmpScene = aStage.getScene();

        if (tmpScene != null) {

            tmpScene.setRoot(aPane);
        }
        else {

            aStage.setScene(new Scene(aPane));
        }
     }

    private static void placeIcons(Stage aStage) {

        // JavaFX Image
        Image tmpIconImage = new Image(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("binder-icon.png")));

        // Set stage icon
        aStage.getIcons().add(tmpIconImage);

        // AWT Image
        final URL imageResource =
                Thread.currentThread().getContextClassLoader().getResource("binder-icon.png");
        final java.awt.Image tmpAwtImage = Toolkit.getDefaultToolkit().getImage(imageResource);

        // Set taskbar icon (may not be supported on all systems (e.g. Linux))
        try {

            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(tmpAwtImage);
        }
        catch (UnsupportedOperationException e) {

            // never mind.
        }
    }

    private static FileTypeEnum getFiletype(File aFile) {

        String tmpFileExtension = "";

        // Get file Name first
        String tmpFileName = aFile.getName();
        final int tmpListIndexOfDot = tmpFileName.lastIndexOf(".");

        // If fileName do not contain "." or starts with "." then it is not a valid file
        if (tmpListIndexOfDot >= 1) {

            tmpFileExtension = tmpFileName.substring(tmpListIndexOfDot + 1);
        }

        return FileTypeEnum.byFileExtension(tmpFileExtension.toLowerCase(), true);
    }

}
