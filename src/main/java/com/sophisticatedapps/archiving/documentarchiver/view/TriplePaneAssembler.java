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

import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class TriplePaneAssembler {

    private List<File> allFiles;
    private File currentFile;
    private FileTypeEnum fileType;
    private DefinedFileProperties definedFileProperties;

    private TriplePanePrefWidthRatios prefWidthRatios;
    private Insets subPanesPadding;
    private double elementSpacing;

    private DocumentSelectedCallback documentSelectedCallback;
    private MoveDocumentCallBack moveDocumentCallBack;

    public TriplePaneAssembler allFiles(List<File> anAllFilesList) {

        this.allFiles = anAllFilesList;
        return this;
    }

    public TriplePaneAssembler currentFile(File aFile) {

        this.currentFile = aFile;
        return this;
    }

    public TriplePaneAssembler fileType(FileTypeEnum aFileType) {

        this.fileType = aFileType;
        return this;
    }

    public TriplePaneAssembler definedFileProperties(DefinedFileProperties aDefinedFileProperties) {

        this.definedFileProperties = aDefinedFileProperties;
        return this;
    }

    public TriplePaneAssembler prefWidthRatios(TriplePanePrefWidthRatios aPrefWidthRatios) {

        this.prefWidthRatios = aPrefWidthRatios;
        return this;
    }

    public TriplePaneAssembler subPanesPadding(Insets aSubPanesPadding) {

        this.subPanesPadding = aSubPanesPadding;
        return this;
    }

    public TriplePaneAssembler elementSpacing(double anElementSpacing) {

        this.elementSpacing = anElementSpacing;
        return this;
    }

    public TriplePaneAssembler documentSelectedCallback(DocumentSelectedCallback aDocumentSelectedCallback) {

        this.documentSelectedCallback = aDocumentSelectedCallback;
        return this;
    }

    public TriplePaneAssembler moveDocumentCallBack(MoveDocumentCallBack aMoveDocumentCallBack) {

        this.moveDocumentCallBack = aMoveDocumentCallBack;
        return this;
    }

    public TriplePane assemble(Stage aStage) {

        if (allFiles == null) {

            throw (new RuntimeException("allFiles not set."));
        }
        if (currentFile == null) {

            throw (new RuntimeException("currentFile not set."));
        }
        if (fileType == null) {

            throw (new RuntimeException("fileType not set."));
        }
        if (moveDocumentCallBack == null) {

            throw (new RuntimeException("moveDocumentCallBack not set."));
        }

        InfoPaneAssembler.MoveDocumentCallBack tmpCallBack =
                (anAllFilesList, aCurrentFile, aDfp, aTakeOverDescriptionAndTags) ->
                moveDocumentCallBack.call(aStage, anAllFilesList, aCurrentFile, aDfp, aTakeOverDescriptionAndTags);

        VBox tmpDocumentsPane = assembleDocumentsPane(aStage);
        Pane tmpFileDisplayPane = (new DisplayFilePaneAssembler())
                .currentFile(currentFile)
                .fileType(fileType)
                .assemble();
        VBox tmpInfoPane = (new InfoPaneAssembler())
                .allFiles(allFiles)
                .currentFile(currentFile)
                .fileType(fileType)
                .definedFileProperties(definedFileProperties)
                .moveDocumentCallBack(tmpCallBack)
                .assemble();

        if (prefWidthRatios != null) {

            prefWidthRatios.applyPrefWidths(aStage.getWidth(), tmpDocumentsPane, tmpFileDisplayPane, tmpInfoPane);
        }

        if (subPanesPadding != null) {

            tmpDocumentsPane.setPadding(subPanesPadding);
            tmpFileDisplayPane.setPadding(subPanesPadding);
            tmpInfoPane.setPadding(subPanesPadding);
        }

        if (elementSpacing > 0) {

            tmpDocumentsPane.setSpacing(elementSpacing);
            tmpInfoPane.setSpacing(elementSpacing);
        }

        return (new TriplePaneBuilder())
                .documentsPane(tmpDocumentsPane)
                .fileDisplayPane(tmpFileDisplayPane)
                .infoPane(tmpInfoPane)
                .build();
    }

    private VBox assembleDocumentsPane(Stage aStage) {

        DocumentsPaneAssembler tmpDocumentsPaneAssembler = (new DocumentsPaneAssembler())
                .allFiles(allFiles)
                .currentFile(currentFile);

        if (this.documentSelectedCallback != null) {

            tmpDocumentsPaneAssembler.documentSelectedCallback((aFilesList, aFile) ->
                    documentSelectedCallback.call(aStage, aFilesList, aFile));
        }

        return tmpDocumentsPaneAssembler.assemble();
    }

    public static class TriplePanePrefWidthRatios {

        private final double firstPanePrefWidthRatio;
        private final double secondPanePrefWidthRatio;
        private final double thirdPanePrefWidthRatio;

        public TriplePanePrefWidthRatios(double aFirstPanePrefWidthRatio, double aSecondPanePrefWidthRatio,
                                         double aThirdPanePrefWidthRatio) {

            this.firstPanePrefWidthRatio = aFirstPanePrefWidthRatio;
            this.secondPanePrefWidthRatio = aSecondPanePrefWidthRatio;
            this.thirdPanePrefWidthRatio = aThirdPanePrefWidthRatio;
        }

        public void applyPrefWidths(double aCompleteWidth, Pane aFirstPane, Pane aSecondPane, Pane aThirdPane) {

            final double tmpRatioSum =
                    (firstPanePrefWidthRatio + secondPanePrefWidthRatio + thirdPanePrefWidthRatio);
            final double tmpWidthFraction = (aCompleteWidth / tmpRatioSum);

            aFirstPane.setPrefWidth(tmpWidthFraction * firstPanePrefWidthRatio);
            aSecondPane.setPrefWidth(tmpWidthFraction * secondPanePrefWidthRatio);
            aThirdPane.setPrefWidth(tmpWidthFraction * thirdPanePrefWidthRatio);
        }
    }

    public interface DocumentSelectedCallback {

        void call(Stage aStage, List<File> anAllFilesList, File aCurrentFile);
    }

    public interface MoveDocumentCallBack {

        void call(Stage aStage, List<File> anAllFilesList, File aCurrentFile,
                  DefinedFileProperties aDefinedFileProperties, boolean aTakeOverDescriptionAndTags);
    }

}
