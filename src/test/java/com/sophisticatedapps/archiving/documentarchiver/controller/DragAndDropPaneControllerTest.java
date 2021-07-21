package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.api.impl.DefaultApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class DragAndDropPaneControllerTest extends BaseTest {

    //private Pane dragAndDropPane;
    private DragAndDropPaneController dragAndDropPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        ApplicationController tmpMockedApplicationController = Mockito.spy(new ApplicationController(null, null, null));

        FXMLLoader tmpRegionLoader = new FXMLLoader(App.class.getResource("view/DragAndDropPane.fxml"));
        tmpRegionLoader.setResources(LanguageUtil.getResourceBundleForCurrentLanguage());
        tmpRegionLoader.setControllerFactory(aParam -> Mockito.spy(new DragAndDropPaneController()));
        //dragAndDropPane =
        tmpRegionLoader.load();
        dragAndDropPaneController = tmpRegionLoader.getController();
        dragAndDropPaneController.rampUp(new DefaultApplicationContext(tmpMockedApplicationController, aStage));
    }

    @AfterEach
    public void cleanUpEach() {

        dragAndDropPaneController.rampDown();

        //dragAndDropPane = null;
        dragAndDropPaneController = null;
    }

    @Test
    void handleDragOver() {

        Dragboard tmpMockedDragboard = Mockito.mock(Dragboard.class);
        DragEvent tmpMockedDragEvent = Mockito.mock(DragEvent.class);
        when(tmpMockedDragEvent.getDragboard()).thenReturn(tmpMockedDragboard);

        // First run with "hasFiles" -> false
        when(tmpMockedDragboard.hasFiles()).thenReturn(false);
        dragAndDropPaneController.handleDragOver(tmpMockedDragEvent);
        verify(tmpMockedDragEvent, Mockito.times(0)).acceptTransferModes(any(TransferMode.class));

        // Second run with "hasFiles" -> true
        when(tmpMockedDragboard.hasFiles()).thenReturn(true);
        dragAndDropPaneController.handleDragOver(tmpMockedDragEvent);
        verify(tmpMockedDragEvent, Mockito.times(1)).acceptTransferModes(TransferMode.LINK);
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleDragDropped() {

        // Assemble a files list with single files and a directory
        List<File> tmpDroppedFilesList = new ArrayList<>();
        tmpDroppedFilesList.add(TEST_TEXT_FILE);
        tmpDroppedFilesList.add(TEST_SOURCE_FOLDER2);

        Dragboard tmpMockedDragboard = Mockito.mock(Dragboard.class);
        when(tmpMockedDragboard.getFiles()).thenReturn(tmpDroppedFilesList);
        DragEvent tmpMockedDragEvent = Mockito.mock(DragEvent.class);
        when(tmpMockedDragEvent.getDragboard()).thenReturn(tmpMockedDragboard);

        dragAndDropPaneController.handleDragDropped(tmpMockedDragEvent);
        verify(dragAndDropPaneController.applicationContext.getApplicationController(), Mockito.times(1))
                .importFreshFilesList(any(Stage.class), any(List.class));
        List<File> tmpAllDocumentsList = dragAndDropPaneController.getAllDocuments();
        assertEquals(3, tmpAllDocumentsList.size());
        assertTrue(tmpAllDocumentsList.containsAll(List.of(TEST_TEXT_FILE, TEST_TEXT_FILE2, TEST_JPG_FILE2)));
    }

}
