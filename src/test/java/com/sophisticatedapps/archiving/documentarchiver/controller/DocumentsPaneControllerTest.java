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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

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

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/DocumentsPane.fxml"));
        documentsPane = loader.load();
        documentsPaneController = loader.getController();
        documentsPaneController.rampUp(aStage);

        aStage.setScene(new Scene(documentsPane));
        aStage.show();
        aStage.toFront();
    }

    @Test
    void testHandleAllDocumentsChanged() {

        documentsPaneController.setNewAllDocuments(DOCUMENTS_LIST);
    }

}
