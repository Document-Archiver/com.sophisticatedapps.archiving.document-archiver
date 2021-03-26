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

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class DisplayFilePaneController extends BaseController {

    private static final Map<FileTypeEnum, Class<? extends DisplayFileNodeAssembler>>
            NODE_ASSEMBLER_BY_FILETYPE;

    static {

        Map<FileTypeEnum, Class<? extends DisplayFileNodeAssembler>> tmpAssemblerMap =
                new EnumMap<>(FileTypeEnum.class);
        tmpAssemblerMap.put(FileTypeEnum.PDF, DisplayPDFNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.TXT, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.JPG, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.PNG, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.GIF, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.HEIC, DisplayUnsupportedFiletypeNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.XML, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.DOC, DisplayUnsupportedFiletypeNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MP3, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.M4A, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.WAV, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MP4, DisplayVideoNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MOV, DisplayUnsupportedFiletypeNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.UNSUPPORTED, DisplayUnsupportedFiletypeNodeAssembler.class);

        NODE_ASSEMBLER_BY_FILETYPE = Collections.unmodifiableMap(tmpAssemblerMap);
    }

    @FXML
    private Pane displayFilePane;

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Add listener
        addCurrentDocumentChangedListener(aChange -> handleCurrentDocumentChanged((File)aChange.getValueAdded()));
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (aNewCurrentDocument != null) {

            try {

                Class<? extends DisplayFileNodeAssembler> tmpFileNodeAssemblerClass =
                        NODE_ASSEMBLER_BY_FILETYPE.get(FileUtil.getFiletype(aNewCurrentDocument));
                Node tmpFileDisplayNode = tmpFileNodeAssemblerClass.getDeclaredConstructor().newInstance()
                        .assemble(aNewCurrentDocument, stage, displayFilePane.getPrefWidth(),
                                (displayFilePane.getPrefHeight() - 50));

                Platform.runLater(() -> displayFilePane.getChildren().setAll(tmpFileDisplayNode));
            }
            catch (Exception e) {

                throw (new RuntimeException("Could not assemble display file pane: ".concat(e.getMessage())));
            }
        }
        else {

            displayFilePane.getChildren().clear();
        }
    }

    private interface DisplayFileNodeAssembler {

        Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight);
    }

    protected static class DisplayUnsupportedFiletypeNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try {
                Desktop.getDesktop().open(aFile);
            }
            catch (Exception e) {
                throw (new RuntimeException("Desktop app could not be opened: ".concat(e.getMessage())));
            }

            StackPane tmpStackPane = new StackPane();
            tmpStackPane.getChildren().add(new Label("Unsupported Filetype. Opened external viewer."));

            return tmpStackPane;
        }
    }

    protected static class DisplayPDFNodeAssembler implements DisplayFileNodeAssembler {

        private static final PDFDisplayer PDF_VIEWER = new PDFDisplayer();

        static {

            PDF_VIEWER.setSecondaryToolbarToggleVisibility(false);
        }

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try {

                PDF_VIEWER.loadPDF(aFile);
                WebView tmpWebView = (WebView)PDF_VIEWER.toNode();
                tmpWebView.setPrefWidth(aPrefWidth);
                tmpWebView.setPrefHeight(aPrefHeight);
                return (new Pane(tmpWebView));
            }
            catch (Exception e) {

                throw (new RuntimeException("PDF could not be loaded."));
            }
        }
    }

    protected static class DisplayImageNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(aFile))) {

                // Creating the image object
                javafx.scene.image.Image tmpImage = new Image(tmpInputStream);

                // Creating the image view
                ImageView tmpImageView = new ImageView();

                // Setting image to the image view
                tmpImageView.setImage(tmpImage);

                //Setting the image view parameters
                tmpImageView.setX(10);
                tmpImageView.setY(10);
                tmpImageView.setFitWidth(aPrefWidth);
                tmpImageView.setPreserveRatio(true);

                return (new ScrollPane(tmpImageView));
            }
            catch (Exception e) {

                throw (new RuntimeException("Image could not be loaded."));
            }
        }
    }

    protected static class DisplayAudioNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try {

                FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MediaTypeAudioPane.fxml"));
                VBox tmpMediaTypeAudioPane = tmpLoader.load();
                MediaTypeAudioPaneController tmpMediaTypeAudioPaneController = tmpLoader.getController();
                tmpMediaTypeAudioPaneController.rampUp(aStage);

                setupMediaView(tmpMediaTypeAudioPaneController, tmpMediaTypeAudioPane, aFile, aPrefWidth, aPrefHeight);

                return tmpMediaTypeAudioPane;
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not load MediaTypeAudioPane: " + e.getMessage()));
            }
        }

        protected void setupMediaView(MediaTypeAudioPaneController aController, VBox aMediaTypePane, File aFile,
                double aPrefWidth, double aPrefHeight) {

            aMediaTypePane.setPrefWidth(aPrefWidth);
            aMediaTypePane.setPrefHeight(aPrefHeight);
            aMediaTypePane.setAlignment(Pos.CENTER);

            try {

                MediaPlayer tmpMediaPlayer = new MediaPlayer(new Media(Paths.get(aFile.getPath()).toUri().toString()));
                aController.setupMediaView(tmpMediaPlayer, aPrefWidth);
            }
            catch (MediaException e) {

                // Media not supported.
                aMediaTypePane.getChildren().add(new Label("Sorry - media not supported."));
            }
        }
    }

    protected static class DisplayVideoNodeAssembler extends DisplayAudioNodeAssembler {

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try {

                FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MediaTypeAudioVideoPane.fxml"));
                VBox tmpMediaTypeAudioVideoPane = tmpLoader.load();
                MediaTypeAudioVideoPaneController tmpMediaTypeAudioVideoPaneController = tmpLoader.getController();
                tmpMediaTypeAudioVideoPaneController.rampUp(aStage);

                setupMediaView(tmpMediaTypeAudioVideoPaneController, tmpMediaTypeAudioVideoPane, aFile, aPrefWidth,
                        aPrefHeight);

                return tmpMediaTypeAudioVideoPane;
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not load MediaTypeAudioVideoPane: " + e.getMessage()));
            }
        }
    }

    protected static class DisplayTextNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(aFile))) {

                // Reading in the text
                ByteArrayOutputStream tmpByteArrayOutputStream = new ByteArrayOutputStream();
                byte[] tmpByteSize = new byte[1024];
                int tmpLength;

                while ((tmpLength = tmpInputStream.read(tmpByteSize)) != -1) {

                    tmpByteArrayOutputStream.write(tmpByteSize, 0, tmpLength);
                }

                // Creating a TextArea for the text
                TextArea tmpTextAreaView = new TextArea();
                tmpTextAreaView.setPrefWidth(aPrefWidth);
                tmpTextAreaView.setPrefHeight(aPrefHeight);

                // Setting text to the text view
                tmpTextAreaView.setText(tmpByteArrayOutputStream.toString(Charset.defaultCharset().toString()));

                return tmpTextAreaView;
            }
            catch (Exception e) {

                throw (new RuntimeException("Text could not be loaded."));
            }
        }
    }

}
