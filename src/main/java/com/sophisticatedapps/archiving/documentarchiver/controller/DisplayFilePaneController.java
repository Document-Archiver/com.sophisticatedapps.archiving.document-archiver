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
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.ProcessesUtil;
import com.sun.jna.Platform; // NOSONAR
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.media.MediaView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.w3c.dom.Document;
import org.zwobble.mammoth.DocumentConverter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipFile;

public class DisplayFilePaneController extends BaseController {

    private static final Map<FileTypeEnum, Class<? extends DisplayFileNodeAssembler>>
            NODE_ASSEMBLER_BY_FILETYPE;

    private final List<ChangeListener<Number>> paneWidthPropertyListenersList = new ArrayList<>();
    private final List<ChangeListener<Number>> paneHeightPropertyListenersList = new ArrayList<>();

    @FXML
    private Pane displayFilePane;

    @FXML
    private Button openExternalViewerButton;

    static {

        Map<FileTypeEnum, Class<? extends DisplayFileNodeAssembler>> tmpAssemblerMap =
                new EnumMap<>(FileTypeEnum.class);
        tmpAssemblerMap.put(FileTypeEnum.PDF, DisplayPDFNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.TXT, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.JPG, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.PNG, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.GIF, DisplayImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.HEIC, DisplayHeicImageNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.XML, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.XHTML, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MD, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.JSON, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.YAML, DisplayTextNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.PAGES, DisplayUnsupportedFiletypeNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.DOC, DisplayDocNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.DOCX, DisplayDocxNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MP3, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.M4A, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.WAV, DisplayAudioNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MP4, DisplayVideoNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.MOV, DisplayUnsupportedFiletypeNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.ZIP, DisplayZipNodeAssembler.class);
        tmpAssemblerMap.put(FileTypeEnum.UNSUPPORTED, DisplayUnsupportedFiletypeNodeAssembler.class);

        NODE_ASSEMBLER_BY_FILETYPE = Collections.unmodifiableMap(tmpAssemblerMap);
    }

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Add listener
        final ChangeListener<Number> tmpPaneWidthPropertyListener =
                ((anObservable, anOldValue, aNewValue) -> setWidths());
        paneWidthPropertyListenersList.add(tmpPaneWidthPropertyListener);
        displayFilePane.widthProperty().addListener(tmpPaneWidthPropertyListener);

        final ChangeListener<Number> tmpPaneHeightPropertyListener =
                ((anObservable, anOldValue, aNewValue) -> setHeights());
        paneHeightPropertyListenersList.add(tmpPaneHeightPropertyListener);
        displayFilePane.heightProperty().addListener(tmpPaneHeightPropertyListener);

        addCurrentDocumentChangedListener(aChange -> handleCurrentDocumentChanged((File)aChange.getValueAdded()));
    }

    @Override
    public void rampDown() {

        super.rampDown();

        // Remove width and height listeners
        ReadOnlyDoubleProperty tmpPaneWidthProperty = displayFilePane.widthProperty();
        for (ChangeListener<Number> tmpCurrentListener : paneWidthPropertyListenersList) {
            tmpPaneWidthProperty.removeListener(tmpCurrentListener);
        }
        paneWidthPropertyListenersList.clear();

        ReadOnlyDoubleProperty tmpPaneHeightProperty = displayFilePane.heightProperty();
        for (ChangeListener<Number> tmpCurrentListener : paneHeightPropertyListenersList) {
            tmpPaneHeightProperty.removeListener(tmpCurrentListener);
        }
        paneHeightPropertyListenersList.clear();
    }

    private void setWidths() {

        ObservableList<Node> tmpDisplayFilePaneChildren = displayFilePane.getChildren();

        if (!tmpDisplayFilePaneChildren.isEmpty()) {

            Region tmpMediaRegion = (Region)tmpDisplayFilePaneChildren.get(0);
            tmpMediaRegion.setPrefWidth(displayFilePane.getPrefWidth());
        }
    }

    private void setHeights() {

        ObservableList<Node> tmpDisplayFilePaneChildren = displayFilePane.getChildren();

        if (!tmpDisplayFilePaneChildren.isEmpty()) {

            double tmpPrefHeight = displayFilePane.getPrefHeight();

            Region tmpMediaRegion = (Region)tmpDisplayFilePaneChildren.get(0);
            tmpMediaRegion.setPrefHeight(tmpPrefHeight);

            openExternalViewerButton.setTranslateY(tmpPrefHeight - 64);
        }
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (!Objects.isNull(aNewCurrentDocument)) {

            try {

                double tmpPrefHeight = displayFilePane.getPrefHeight();

                Class<? extends DisplayFileNodeAssembler> tmpFileNodeAssemblerClass =
                        NODE_ASSEMBLER_BY_FILETYPE.get(FileUtil.getFiletype(aNewCurrentDocument));
                Node tmpFileDisplayNode = tmpFileNodeAssemblerClass.getDeclaredConstructor().newInstance().assemble(
                        this, aNewCurrentDocument, stage, displayFilePane.getPrefWidth(), tmpPrefHeight);

                boolean tmpFileTypeInternallySupported =
                        (DisplayUnsupportedFiletypeNodeAssembler.class != tmpFileNodeAssemblerClass);
                openExternalViewerButton.setVisible(tmpFileTypeInternallySupported);

                if (tmpFileTypeInternallySupported) {

                    openExternalViewerButton.setText(
                            LanguageUtil.i18n("display-file-pane-controller.open-external-viewer-button.txt",
                                    FileUtil.getFileExtension(aNewCurrentDocument)));
                    openExternalViewerButton.setTranslateY(tmpPrefHeight - 64);
                }

                displayFilePane.getChildren().setAll(tmpFileDisplayNode, openExternalViewerButton);
            }
            catch (Exception e) {

                throw (new RuntimeException("Could not assemble display file pane: ".concat(e.getMessage())));
            }
        }
        else {

            displayFilePane.getChildren().clear();
        }
    }

    @FXML
    protected void handleOpenExternalViewerButtonAction() {

        openExternalViewer(getCurrentDocument());
    }

    private interface DisplayFileNodeAssembler {

        Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                        double aPrefWidth, double aPrefHeight);
    }

    protected static class DisplayUnsupportedFiletypeNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

            aDisplayFilePaneController.openExternalViewer(aFile);

            StackPane tmpStackPane = new StackPane();
            tmpStackPane.setPrefWidth(aPrefWidth);
            tmpStackPane.setPrefHeight(aPrefHeight);
            tmpStackPane.getChildren().add(new Label(LanguageUtil.i18n(
                    "display-file-pane-controller.display-unsupported-filetype-node-assembler.unsupported-filetype-label")));

            return tmpStackPane;
        }
    }

    protected static class DisplayPDFNodeAssembler implements DisplayFileNodeAssembler {

        private static final PDFDisplayer PDF_VIEWER = new PDFDisplayer();

        static {

            PDF_VIEWER.setSecondaryToolbarToggleVisibility(false);
        }

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

            try {

                PDF_VIEWER.loadPDF(aFile);
                WebView tmpWebView = (WebView)PDF_VIEWER.toNode();
                tmpWebView.setPrefWidth(aPrefWidth);
                tmpWebView.setPrefHeight(aPrefHeight);

                Pane tmpPane = new Pane(tmpWebView);
                tmpPane.widthProperty().addListener((anObservable, anOldValue, aNewValue) ->
                        tmpWebView.setPrefWidth(aNewValue.doubleValue()));
                tmpPane.heightProperty().addListener((anObservable, anOldValue, aNewValue) ->
                        tmpWebView.setPrefHeight(aNewValue.doubleValue()));

                return (tmpPane);
            }
            catch (Exception e) {

                throw (new RuntimeException("PDF could not be loaded."));
            }
        }
    }

    protected static class DisplayImageNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

            try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(aFile))) {

                // Creating the image object
                Image tmpImage = new Image(tmpInputStream);

                // Creating the image view
                ImageView tmpImageView = new ImageView();

                // Setting image to the image view
                tmpImageView.setImage(tmpImage);

                //Setting the image view parameters
                tmpImageView.setX(0);
                tmpImageView.setY(10);
                tmpImageView.setFitWidth(aPrefWidth);
                tmpImageView.setPreserveRatio(true);

                final Pane tmpPane = new Pane(tmpImageView);
                tmpPane.widthProperty().addListener((anObservable, anOldValue, aNewValue) ->
                        tmpImageView.setFitWidth(aNewValue.doubleValue()));

                return (tmpPane);
            }
            catch (Exception e) {

                throw (new RuntimeException("Image could not be loaded."));
            }
        }
    }

    protected static class DisplayHeicImageNodeAssembler extends DisplayImageNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController,
                               File aFile, Stage aStage, double aPrefWidth, double aPrefHeight) {

            // We only support HEIC on Macs
            if (Platform.isMac()) {

                try {

                    // Create JPG from HEIC
                    File tmpTempFile = ProcessesUtil.createTempJpg(aFile);

                    Region tmpReturn =
                            super.assemble(aDisplayFilePaneController, tmpTempFile, aStage, aPrefWidth, aPrefHeight);

                    Files.delete(tmpTempFile.toPath());

                    return tmpReturn;
                }
                catch (IOException e) {

                    return new StackPane(new Label("Problem while loading the HEIC file: " + e.getMessage()));
                }
            }
            else {

                return ((new DisplayUnsupportedFiletypeNodeAssembler())
                        .assemble(aDisplayFilePaneController, aFile, aStage, aPrefWidth, aPrefHeight));
            }
        }
    }

    protected static class DisplayAudioNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

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

            setWidthChangeListener(aController, aMediaTypePane);

            try {

                MediaPlayer tmpMediaPlayer = new MediaPlayer(new Media(aFile.toPath().toUri().toString()));
                aController.setupMediaView(tmpMediaPlayer, aPrefWidth);
            }
            catch (MediaException e) {

                // Media not supported.
                aMediaTypePane.getChildren().add(new Label(LanguageUtil.i18n(
                        "display-file-pane-controller.display-audio-node-assembler.media-not-supported-label")));
            }
        }

        protected void setWidthChangeListener(MediaTypeAudioPaneController aController, Pane aMediaTypePane) {

            aMediaTypePane.widthProperty().addListener((anObservable, anOldValue, aNewValue) ->
                ((ImageView)aController.getMediaVisualization()).setFitWidth(aNewValue.doubleValue() * 0.75));
        }
    }

    protected static class DisplayVideoNodeAssembler extends DisplayAudioNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

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

        @Override
        protected void setWidthChangeListener(MediaTypeAudioPaneController aController, Pane aMediaTypePane) {

            aMediaTypePane.widthProperty().addListener((anObservable, anOldValue, aNewValue) ->
                    ((MediaView)aController.getMediaVisualization()).setFitWidth(aNewValue.doubleValue()));
        }
    }

    protected static class DisplayTextNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

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
                tmpTextAreaView.setEditable(false);

                // Setting text to the text view
                tmpTextAreaView.setText(tmpByteArrayOutputStream.toString(Charset.defaultCharset().toString()));

                return tmpTextAreaView;
            }
            catch (Exception e) {

                throw (new RuntimeException("Text could not be loaded."));
            }
        }
    }

    protected abstract static class AbstractDisplayDocNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

            try {

                // Creating a WebView for the HTML
                WebView tmpWebView = new WebView();
                tmpWebView.setPrefWidth(aPrefWidth);
                tmpWebView.setPrefHeight(aPrefHeight);

                // Setting HTML to the WebView
                String tmpNote =
                        LanguageUtil.i18n("display-file-pane-controller.display-docx-node-assembler.preview-note");
                tmpWebView.getEngine().loadContent(tmpNote.concat(convertDocToHtml(aFile)));

                Pane tmpPane = new Pane(tmpWebView);
                tmpPane.widthProperty().addListener((anObservable, anOldValue, aNewValue) ->
                        tmpWebView.setPrefWidth(aNewValue.doubleValue()));
                tmpPane.heightProperty().addListener((anObservable, anOldValue, aNewValue) ->
                        tmpWebView.setPrefHeight(aNewValue.doubleValue()));

                return (tmpPane);
            }
            catch (IOException | TransformerException | ParserConfigurationException e) {

                throw (new RuntimeException("DOC(X) could not be loaded."));
            }
        }

        protected abstract String convertDocToHtml(File aFile)
                throws TransformerException, IOException, ParserConfigurationException;
    }

    protected static class DisplayDocxNodeAssembler extends AbstractDisplayDocNodeAssembler {

        private static final DocumentConverter DOCUMENT_CONVERTER = new DocumentConverter();

        @Override
        protected String convertDocToHtml(File aFile) throws IOException {

            return DOCUMENT_CONVERTER.convertToHtml(aFile).getValue();
        }
    }

    protected static class DisplayDocNodeAssembler extends AbstractDisplayDocNodeAssembler {

        @Override
        protected String convertDocToHtml(File aFile)
                throws TransformerException, IOException, ParserConfigurationException {

            HWPFDocumentCore tmpWordDocument = AbstractWordUtils.loadDoc(aFile);
            DocumentBuilderFactory tmpDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            tmpDocumentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tmpDocumentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            WordToHtmlConverter tmpWordToHtmlConverter = new WordToHtmlConverter(tmpDocumentBuilderFactory
                    .newDocumentBuilder().newDocument());
            tmpWordToHtmlConverter.processDocument(tmpWordDocument);
            Document tmpHtmlDocument = tmpWordToHtmlConverter.getDocument();

            ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
            DOMSource tmpDomSource = new DOMSource(tmpHtmlDocument);
            StreamResult tmpStreamResult = new StreamResult(tmpOut);

            TransformerFactory tmpTransformerFactory = TransformerFactory.newInstance();
            tmpTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tmpTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer tmpSerializer = tmpTransformerFactory.newTransformer();
            tmpSerializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tmpSerializer.setOutputProperty(OutputKeys.INDENT, "yes");
            tmpSerializer.setOutputProperty(OutputKeys.METHOD, "html");
            tmpSerializer.transform(tmpDomSource, tmpStreamResult);
            tmpOut.close();

            return tmpOut.toString();
        }
    }

    protected static class DisplayZipNodeAssembler implements DisplayFileNodeAssembler {

        @Override
        public Region assemble(DisplayFilePaneController aDisplayFilePaneController, File aFile, Stage aStage,
                               double aPrefWidth, double aPrefHeight) {

            FXMLUtil.ControllerRegionPair<FileSystemViewPaneController, Pane> tmpDisplayFilePaneControllerRegionPair =
                    FXMLUtil.loadAndRampUpRegion("view/FileSystemViewPane.fxml", aStage);
            Pane tmpFileSystemViewPane = tmpDisplayFilePaneControllerRegionPair.getRegion();
            FileSystemViewPaneController tmpFileSystemViewPaneController =
                    tmpDisplayFilePaneControllerRegionPair.getController();

            try (ZipFile tmpZipFile = new ZipFile(aFile)) {

                tmpFileSystemViewPaneController.setZipFile(tmpZipFile);

                tmpFileSystemViewPane.setPrefWidth(aPrefWidth);
                tmpFileSystemViewPane.setPrefHeight(aPrefHeight);

                return tmpFileSystemViewPane;
            }
            catch (IOException e) {

                throw (new RuntimeException("ZIP file could not be opened: ".concat(e.getMessage())));
            }
        }
    }

}
