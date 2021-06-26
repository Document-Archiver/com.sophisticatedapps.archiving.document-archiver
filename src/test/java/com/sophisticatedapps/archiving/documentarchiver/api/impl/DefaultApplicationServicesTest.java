package com.sophisticatedapps.archiving.documentarchiver.api.impl;

import com.restart4j.ApplicationRestart;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
class DefaultApplicationServicesTest {

    private Stage primaryStage;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        primaryStage = aStage;
    }

    @AfterEach
    public void cleanUpEach() {

        primaryStage = null;
    }

    @Test
    void requestDirectorySelection() {

        DirectoryChooser tmpMockedDirectoryChooser = Mockito.mock(DirectoryChooser.class);

        (new DefaultApplicationServices(tmpMockedDirectoryChooser, null, null)).requestDirectorySelection(primaryStage);

        verify(tmpMockedDirectoryChooser, Mockito.times(1)).showDialog(any(Stage.class));
    }

    @Test
    void requestMultipleFilesSelection() {

        FileChooser tmpMockedFileChooser = Mockito.mock(FileChooser.class);

        (new DefaultApplicationServices(null, tmpMockedFileChooser, null)).requestMultipleFilesSelection(primaryStage);

        verify(tmpMockedFileChooser, Mockito.times(1)).showOpenMultipleDialog(primaryStage);
    }

    @Test
    void restartApp() {

        ApplicationRestart tmpMockedApplicationRestart = Mockito.mock(ApplicationRestart.class);

        (new DefaultApplicationServices(null, null, tmpMockedApplicationRestart)).restartApp();

        verify(tmpMockedApplicationRestart, Mockito.times(1)).restartApp();
    }

}
