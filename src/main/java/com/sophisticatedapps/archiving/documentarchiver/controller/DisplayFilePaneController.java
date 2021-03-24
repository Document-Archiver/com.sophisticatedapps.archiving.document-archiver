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
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Map;

public class DisplayFilePaneController extends BaseController {

    private static final Map<FileTypeEnum, Class<? extends DisplayFilePaneController.DisplayFileNodeAssembler>>
            NODE_ASSEMBLER_BY_FILETYPE;

    static {

        NODE_ASSEMBLER_BY_FILETYPE = Map.of(FileTypeEnum.PDF, DisplayPDFNodeAssembler.class,
                FileTypeEnum.TXT, DisplayTextNodeAssembler.class,
                FileTypeEnum.JPG, DisplayImageNodeAssembler.class,
                FileTypeEnum.PNG, DisplayImageNodeAssembler.class,
                FileTypeEnum.GIF, DisplayImageNodeAssembler.class,
                FileTypeEnum.HEIC, DisplayUnsupportedFiletypeNodeAssembler.class,
                FileTypeEnum.XML, DisplayTextNodeAssembler.class,
                FileTypeEnum.DOC, DisplayUnsupportedFiletypeNodeAssembler.class,
                FileTypeEnum.MP3, DisplayAudioNodeAssembler.class,
                FileTypeEnum.UNSUPPORTED, DisplayUnsupportedFiletypeNodeAssembler.class);
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
                        .assemble(aNewCurrentDocument, displayFilePane.getPrefWidth(),
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

        Region assemble(File aFile, double aPrefWidth, double aPrefHeight);
    }

    protected static class DisplayUnsupportedFiletypeNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, double aPrefWidth, double aPrefHeight) {

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
        public Region assemble(File aFile, double aPrefWidth, double aPrefHeight) {

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
        public Region assemble(File aFile, double aPrefWidth, double aPrefHeight) {

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

        private static final String BUTTON_TEXT_PLAY = "Play";
        private static final String BUTTON_TEXT_STOP = "Stop";

        @Override
        public Region assemble(File aFile, double aPrefWidth, double aPrefHeight) {

            StackPane tmpStackPane = new StackPane();
            tmpStackPane.setPrefWidth(aPrefWidth);
            tmpStackPane.setPrefHeight(aPrefHeight);
            tmpStackPane.setAlignment(Pos.CENTER);

            try {

                tmpStackPane.setUserData(new MediaPlayer(new Media(Paths.get(aFile.getPath()).toUri().toString())));
                tmpStackPane.getChildren().add(assemblePlayStopButton(tmpStackPane));
            }
            catch (MediaException e) {

                // Audio not supported.
                tmpStackPane.getChildren().add(new Label("Sorry - audio not supported."));
            }

            return tmpStackPane;
        }

        protected Button assemblePlayStopButton(Pane aParentPane) {

            Button tmpPlayAudioButton = new Button(BUTTON_TEXT_PLAY);
            tmpPlayAudioButton.setOnAction(anEvent -> {

                MediaPlayer tmpMediaPlayer = (MediaPlayer)aParentPane.getUserData();

                if(tmpMediaPlayer.getStatus() != MediaPlayer.Status.PLAYING){

                    tmpMediaPlayer.play();
                    ((Button)anEvent.getSource()).setText(BUTTON_TEXT_STOP);
                }
                else {

                    tmpMediaPlayer.stop();
                    ((Button)anEvent.getSource()).setText(BUTTON_TEXT_PLAY);
                }
            });

            return tmpPlayAudioButton;
        }
    }

    protected static class DisplayTextNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(File aFile, double aPrefWidth, double aPrefHeight) {

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
