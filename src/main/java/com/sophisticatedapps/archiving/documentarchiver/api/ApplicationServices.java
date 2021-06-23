package com.sophisticatedapps.archiving.documentarchiver.api;

import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public interface ApplicationServices {

    /**
     * Request the user to select a directory.
     *
     * @return  File object of the selected directory.
     */
    File requestDirectorySelection(Stage aStage);

    /**
     * Request the user to select one or multiple Files.
     *
     * @return  List of File objects representing the selected files.
     */
    List<File> requestMultipleFilesSelection(Stage aStage);

    /**
     * Restart the Application.
     */
    void restartApp();

}
