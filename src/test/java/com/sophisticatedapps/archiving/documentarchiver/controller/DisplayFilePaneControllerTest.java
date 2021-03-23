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
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(ApplicationExtension.class)
class DisplayFilePaneControllerTest extends BaseTest {

    private Pane displayFilePane;
    private DisplayFilePaneController displayFilePaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/DisplayFilePane.fxml"));
        displayFilePane = loader.load();
        displayFilePaneController = loader.getController();
        displayFilePaneController.rampUp(aStage);
    }

    @AfterEach
    public void cleanUpEach() {

        displayFilePaneController.rampDown();

        displayFilePane = null;
        displayFilePaneController = null;
    }

    @Test
    void testHandleCurrentDocumentChangedToTextFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_TEXT_FILE);

        // Wait until sub Panes are set.
        ObservableList<Node> tmpDisplayPaneChildren = displayFilePane.getChildren();
        await().atMost(10, TimeUnit.SECONDS)
                .until(tmpDisplayPaneChildren::isEmpty, Predicate.isEqual(Boolean.FALSE));

        // Now there should be a TextArea on our display file Pane.
        TextArea tmpTextArea = (TextArea)tmpDisplayPaneChildren.get(0);
        assertEquals("Simple text for testing.", tmpTextArea.getText());
    }

    @Test
    void testHandleCurrentDocumentChangedToPngFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_PNG_FILE);

        // Wait until sub Panes are set.
        ObservableList<Node> tmpDisplayPaneChildren = displayFilePane.getChildren();
        await().atMost(10, TimeUnit.SECONDS)
                .until(tmpDisplayPaneChildren::isEmpty, Predicate.isEqual(Boolean.FALSE));

        // Now there should be a ScrollView on our display file Pane.
        ScrollPane tmpWrapperPane = (ScrollPane)tmpDisplayPaneChildren.get(0);
        assertSame(ImageView.class, tmpWrapperPane.getContent().getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToMp3File() {

        displayFilePaneController.setNewCurrentDocument(TEST_MP3_FILE);

        // Wait until sub Panes are set.
        ObservableList<Node> tmpDisplayPaneChildren = displayFilePane.getChildren();
        await().atMost(10, TimeUnit.SECONDS)
                .until(tmpDisplayPaneChildren::isEmpty, Predicate.isEqual(Boolean.FALSE));

        // Now there should be a ScrollView on our display file Pane.
        StackPane tmpPane = (StackPane)tmpDisplayPaneChildren.get(0);
        Button tmpPlayStopButton = (Button)tmpPane.getChildren().get(0);
        tmpPlayStopButton.fire();
        assertSame("Stop", tmpPlayStopButton.getText());
        tmpPlayStopButton.fire();
        //assertSame("Play", tmpPlayStopButton.getText());
    }

}
