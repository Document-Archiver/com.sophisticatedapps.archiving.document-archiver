package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.restart4j.ApplicationRestart;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class DefaultApplicationServices implements ApplicationServices {

    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;
    private final ApplicationRestart applicationRestart;

    /**
     * Initializes a new DefaultApplicationServices.
     */
    public DefaultApplicationServices() {

        this((new DirectoryChooser()), (new FileChooser()), ApplicationRestart.builder().build());
    }

    /**
     * Initializes a new DefaultApplicationServices with given DirectoryChooser, FileChooser and ApplicationRestart.
     *
     * @param   aDirectoryChooser       DirectoryChooser to use
     * @param   aFileChooser            FileChooser to use
     * @param   anApplicationRestart    ApplicationRestart to use.
     */
    public DefaultApplicationServices(
            DirectoryChooser aDirectoryChooser, FileChooser aFileChooser, ApplicationRestart anApplicationRestart) {

        this.directoryChooser = aDirectoryChooser;
        this.fileChooser = aFileChooser;
        this.applicationRestart = anApplicationRestart;
    }

    @Override
    public File requestDirectorySelection(Stage aStage) {

        return directoryChooser.showDialog(aStage);
    }

    @Override
    public List<File> requestMultipleFilesSelection(Stage aStage) {

        return fileChooser.showOpenMultipleDialog(aStage);
    }

    @Override
    public void restartApp() {

        applicationRestart.restartApp();
    }

}
