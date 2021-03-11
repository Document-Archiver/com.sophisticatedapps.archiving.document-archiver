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
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(ApplicationExtension.class)
class DisplayFilePaneControllerTest {

    private Stage stage;
    private Pane displayFilePane;
    private DisplayFilePaneController displayFilePaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    //@Start
    public void xstart(Stage aStage) throws IOException {

        stage = aStage;

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/DisplayFilePane.fxml"));
        displayFilePane = loader.load();
        displayFilePaneController = loader.getController();
        displayFilePaneController.rampUp(aStage);

        aStage.setScene(new Scene(displayFilePane));
        aStage.show();
        aStage.toFront();
    }

    //@Test
    void xtestHandleCurrentDocumentChangedToTextFile() throws InterruptedException {

        File tmpTextFile = new File(getClass().getClassLoader().getResource("test.txt").getFile());

        stage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, tmpTextFile);

        // Wait until sub Panes are set.
        ObservableList tmpDisplayPaneChildren = displayFilePane.getChildren();
        await().atMost(10, TimeUnit.SECONDS)
                .until(tmpDisplayPaneChildren::isEmpty, Predicate.isEqual(Boolean.FALSE));

        // Now there should be a WebView on our display file Pane.
        Pane tmpWrapperPane = (Pane)tmpDisplayPaneChildren.get(0);
        assertSame(Text.class, tmpWrapperPane.getChildren().get(0).getClass());

        // Cleanup
        //displayFilePaneController.rampDown();
    }

}
