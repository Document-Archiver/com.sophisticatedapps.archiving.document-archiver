package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DragAndDropPaneController extends BaseController {

    @FXML
    protected void handleDragOver(DragEvent aDragEvent) {

        if (aDragEvent.getDragboard().hasFiles()) {

            aDragEvent.acceptTransferModes(TransferMode.LINK);
        }
    }

    @FXML
    protected void handleDragDropped(DragEvent aDragEvent) {

        List<File> tmpFilesList = aDragEvent.getDragboard().getFiles();

        // There may be a mix of files and directories
        List<File> tmpWrapperList = new ArrayList<>();

        for (File tmpCurrentFile : tmpFilesList) {

            if (tmpCurrentFile.isDirectory()) {

                DirectoryUtil.readDirectoryRecursive(tmpCurrentFile, tmpWrapperList,
                        DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);
            }
            else if (tmpCurrentFile.isFile()) {

                tmpWrapperList.add(tmpCurrentFile);
            }
        }

        // Import all the files.
        applicationContext.getApplicationController().importFreshFilesList(stage, tmpWrapperList);
    }

}
