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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class DocumentsPaneTest extends BaseTest {

    private Pane documentsPane;
    private Stage stage;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        this.stage = aStage;

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        documentsPane = (VBox)FXMLUtil.loadAndRampUpRegion("view/DocumentsPane.fxml", stage).getRegion();

        aStage.setScene(new Scene(documentsPane));
        aStage.show();
        aStage.toFront();
    }

    @AfterEach
    public void cleanUpEach() {

        documentsPane = null;
        stage = null;
    }

    /**
     * Check the documents ListView.
     */
    @Test
    void testDocumentsListView() {

        Platform.runLater(() -> {

            stage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, ALL_DOCUMENTS_LIST);
            stage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, TEST_JPG_FILE);
        });

        WaitForAsyncUtils.waitForFxEvents();

        @SuppressWarnings("unchecked")
        ListView<File> tmpDocumentsListView = (ListView<File>)documentsPane.lookup("#documentsListView");

        assertTrue(tmpDocumentsListView.getItems().containsAll(ALL_DOCUMENTS_LIST));
        assertSame(TEST_JPG_FILE, tmpDocumentsListView.getFocusModel().getFocusedItem());
        assertSame(TEST_JPG_FILE, tmpDocumentsListView.getSelectionModel().getSelectedItem());
    }

}
