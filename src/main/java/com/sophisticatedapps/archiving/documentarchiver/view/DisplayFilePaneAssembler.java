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

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class DisplayFilePaneAssembler {

    private static final Map<FileTypeEnum, Class<? extends DisplayFileNodeAssembler>> NODE_ASSEMBLER_BY_FILETYPE;

    private File currentFile;
    private FileTypeEnum fileType;

    static {

        NODE_ASSEMBLER_BY_FILETYPE = Map.of(FileTypeEnum.PDF, DisplayPDFNodeAssembler.class,
                FileTypeEnum.TXT, DisplayTextNodeAssembler.class,
                FileTypeEnum.JPG, DisplayImageNodeAssembler.class,
                FileTypeEnum.PNG, DisplayImageNodeAssembler.class,
                FileTypeEnum.GIF, DisplayImageNodeAssembler.class,
                FileTypeEnum.UNSUPPORTED, DisplayUnsupportedFiletypeNodeAssembler.class);
    }

    public DisplayFilePaneAssembler currentFile(File aFile) {

        this.currentFile = aFile;
        return this;
    }

    public DisplayFilePaneAssembler fileType(FileTypeEnum aFileType) {

        this.fileType = aFileType;
        return this;
    }

    public Pane assemble() {

        if (currentFile == null) {

            throw (new RuntimeException("currentFile not set."));
        }
        if (fileType == null) {

            throw (new RuntimeException("fileType not set."));
        }

        try {

            Class<? extends DisplayFileNodeAssembler> tmpFileNodeAssemblerClass =
                    NODE_ASSEMBLER_BY_FILETYPE.get(fileType);
            Node tmpFileDisplayNode =
                    tmpFileNodeAssemblerClass.getDeclaredConstructor().newInstance().assemble(currentFile);

            StackPane tmpFileDisplayNodeWrapperPane = new StackPane();
            tmpFileDisplayNodeWrapperPane.setAlignment(Pos.BASELINE_LEFT);
            tmpFileDisplayNodeWrapperPane.setBackground(new Background(new BackgroundFill(Color.WHITE,
                    CornerRadii.EMPTY , new Insets(5))));
            tmpFileDisplayNodeWrapperPane.getChildren().add(tmpFileDisplayNode);

            return (tmpFileDisplayNodeWrapperPane);
        }
        catch (Exception e) {

            throw (new RuntimeException("Could not assemble display file pane: ".concat(e.getMessage())));
        }
    }

    private interface DisplayFileNodeAssembler {

        Node assemble(File aFile);
    }

    protected static class DisplayUnsupportedFiletypeNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Node assemble(File aFile) {

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

        @Override
        public Node assemble(File aFile) {

            PDFDisplayer tmpPDFViewer = new PDFDisplayer();

            try {

                tmpPDFViewer.loadPDF(aFile);
                return tmpPDFViewer.toNode();
            } catch (Exception e) {

                throw (new RuntimeException("PDF could not be loaded."));
            }
        }
    }

    protected static class DisplayImageNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Node assemble(File aFile) {

            try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(aFile))) {

                // Creating the image object
                Image tmpImage = new Image(tmpInputStream);

                // Creating the image view
                ImageView tmpImageView = new ImageView();

                // Setting image to the image view
                tmpImageView.setImage(tmpImage);

                //Setting the image view parameters
                tmpImageView.setX(10);
                tmpImageView.setY(10);
                tmpImageView.setFitWidth(575);
                tmpImageView.setPreserveRatio(true);

                return tmpImageView;
            }
            catch (Exception e) {

                throw (new RuntimeException("Image could not be loaded."));
            }
        }
    }

    protected static class DisplayTextNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Node assemble(File aFile) {

            try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(aFile))) {

                // Reading in the text
                ByteArrayOutputStream tmpByteArrayOutputStream = new ByteArrayOutputStream();
                byte[] tmpByteSize = new byte[1024];
                int tmpLength;

                while ((tmpLength = tmpInputStream.read(tmpByteSize)) != -1) {

                    tmpByteArrayOutputStream.write(tmpByteSize, 0, tmpLength);
                }

                // Creating the text view
                Text tmpTextView = new Text();

                // Setting text to the text view
                tmpTextView.setText(tmpByteArrayOutputStream.toString(Charset.defaultCharset().toString()));

                //Setting the image view parameters
                tmpTextView.setX(10);
                tmpTextView.setY(10);

                return tmpTextView;
            }
            catch (Exception e) {

                throw (new RuntimeException("Text could not be loaded."));
            }
        }
    }

}
