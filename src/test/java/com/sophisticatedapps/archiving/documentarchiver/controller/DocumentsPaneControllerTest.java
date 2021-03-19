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

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.DocumentsPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class DocumentsPaneControllerTest extends BaseTest {

    private Pane documentsPane;
    private DocumentsPaneController documentsPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/DocumentsPane.fxml"));
        documentsPane = loader.load();
        documentsPaneController = loader.getController();
        documentsPaneController.rampUp(aStage);
    }

    @AfterEach
    public void cleanUpEach() {

        documentsPaneController.rampDown();

        documentsPane = null;
        documentsPaneController = null;
    }

    @Test
    void testHandleAllDocumentsChanged() {

        documentsPaneController.setNewAllDocuments(DOCUMENTS_LIST);

        WaitForAsyncUtils.waitForFxEvents();

        // Get documents ListView
        @SuppressWarnings("unchecked")
        ListView<File> tmpDocumentsListView = (ListView<File>)documentsPane.lookup("#documentsListView");
        assertNotNull(tmpDocumentsListView);

        List<File> tmpDocumentsList = tmpDocumentsListView.getItems();
        assertTrue(tmpDocumentsList.contains(TEST_TEXT_FILE));
        assertTrue(tmpDocumentsList.contains(TEST_TEXT_FILE2));
        assertTrue(tmpDocumentsList.contains(TEST_PDF_FILE));
    }

    @Test
    void testHandleCurrentDocumentChanged() {

        documentsPaneController.setNewAllDocumentsAndCurrentDocument(DOCUMENTS_LIST, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // Get documents ListView
        @SuppressWarnings("unchecked")
        ListView<File> tmpDocumentsListView = (ListView<File>)documentsPane.lookup("#documentsListView");
        assertNotNull(tmpDocumentsListView);

        assertSame(TEST_TEXT_FILE2, tmpDocumentsListView.getFocusModel().getFocusedItem());
        assertSame(TEST_TEXT_FILE2, tmpDocumentsListView.getSelectionModel().getSelectedItem());
    }

    @Test
    void testHandleDocumentsListViewClicked() {

        documentsPaneController.setNewAllDocumentsAndCurrentDocument(DOCUMENTS_LIST, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        // Get documents ListView
        @SuppressWarnings("unchecked")
        ListView<File> tmpDocumentsListView = (ListView<File>)documentsPane.lookup("#documentsListView");
        assertNotNull(tmpDocumentsListView);

        // Set selection to PDF file
        tmpDocumentsListView.getSelectionModel().select(2);

        // "Click"
        documentsPaneController.handleDocumentsListViewClicked();

        WaitForAsyncUtils.waitForFxEvents();

        // Now the PDF file should be the current document.
        assertSame(TEST_PDF_FILE, documentsPaneController.getCurrentDocument());
    }

}
