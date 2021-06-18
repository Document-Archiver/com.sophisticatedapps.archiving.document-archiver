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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.RootPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class RootPaneControllerTest extends BaseTest {

    private BorderPane rootPane;
    private RootPaneController rootPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<RootPaneController,BorderPane> tmpRootPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/RootPane.fxml", aStage);
        rootPane = tmpRootPaneControllerRegionPair.getRegion();
        rootPaneController = tmpRootPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        rootPaneController.rampDown();

        rootPane = null;
        rootPaneController = null;
    }

    @Test
    void testSetWidths() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        rootPaneController.setNewCurrentDocument(TEST_TEXT_FILE);

        WaitForAsyncUtils.waitForFxEvents();

        rootPaneController.stage.setWidth(1016);
        MethodUtils.invokeMethod(rootPaneController, true, "setWidths");

        assertEquals(200, ((Pane)rootPane.getLeft()).getPrefWidth());
        assertEquals(500, ((Pane)rootPane.getCenter()).getPrefWidth());
        assertEquals(300, ((Pane)rootPane.getRight()).getPrefWidth());
    }

    @Test
    void testSetHeights() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        rootPaneController.setNewCurrentDocument(TEST_TEXT_FILE);

        WaitForAsyncUtils.waitForFxEvents();

        rootPaneController.stage.setHeight(555);
        MethodUtils.invokeMethod(rootPaneController, true, "setHeights");

        assertEquals(505, ((Pane)rootPane.getLeft()).getPrefHeight());
        assertEquals(505, ((Pane)rootPane.getCenter()).getPrefHeight());
        assertEquals(505, ((Pane)rootPane.getRight()).getPrefHeight());
    }

    @Test
    void testHandleCurrentDocumentChanged() {

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", TEST_TEXT_FILE2);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(("Archiving: " + TEST_TEXT_FILE2.getPath() + " (Tenant: Default Tenant)"), rootPaneController.stage.getTitle());
        assertEquals(VBox.class, rootPane.getLeft().getClass());
        assertEquals(StackPane.class, rootPane.getCenter().getClass());
        assertEquals(VBox.class, rootPane.getRight().getClass());
    }

    @Test
    void testHandleCurrentDocumentChanged_to_null() {

        Platform.runLater(() -> {

            try {

                MethodUtils.invokeMethod(rootPaneController, true, "handleCurrentDocumentChanged", (File)null);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                fail(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Choose file(s)", rootPaneController.stage.getTitle());
        assertNull(rootPane.getLeft());
        assertEquals(StackPane.class, rootPane.getCenter().getClass());
        assertNull(rootPane.getRight());
    }

}
