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
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.MenuBarController".
 */
@ExtendWith(ApplicationExtension.class)
class MenuBarControllerTest extends BaseTest {

    //private MenuBar menuBar;
    private MenuBarController menuBarController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<MenuBarController,MenuBar> tmpMenuBarControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/MenuBar.fxml", aStage);
        menuBarController = tmpMenuBarControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        menuBarController.rampDown();

        //menuBar = null;
        menuBarController = null;
    }

    @Test
    void handleAboutMenuItemAction() throws IllegalAccessException {

        Alert tmpMockedAboutAlert = Mockito.mock(Alert.class);

        MenuBarController.AlertProvider tmpMockedAlertProvider = new MenuBarController.AlertProvider() {
            @Override
            public Alert provideAboutAlert() {
                return tmpMockedAboutAlert;
            }
        };

        FieldUtils.writeField(menuBarController, "alertProvider", tmpMockedAlertProvider, true);

        menuBarController.handleAboutMenuItemAction();

        verify(tmpMockedAboutAlert, Mockito.times(1)).showAndWait();
    }

    //@Test
    void handlePreferencesMenuItemAction() {
    }

    @Test
    void handleQuitMenuItemAction() {

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        menuBarController.stage = tmpMockedStage;

        menuBarController.handleQuitMenuItemAction();

        verify(tmpMockedStage, Mockito.times(1)).hide();
    }

    @Test
    void handleOpenFilesOrDirectoryMenuItemAction() {

        List<File> tmpNewAllDocuments = Collections.singletonList(TEST_TEXT_FILE2);
        menuBarController.setNewAllDocumentsAndCurrentDocument(tmpNewAllDocuments, TEST_TEXT_FILE2);

        WaitForAsyncUtils.waitForFxEvents();

        menuBarController.handleOpenFilesOrDirectoryMenuItemAction();

        WaitForAsyncUtils.waitForFxEvents();

        assertNull(menuBarController.getAllDocuments());
        assertNull(menuBarController.getCurrentDocument());
    }

    @Test
    void handleHelpMenuItemAction() {

        HostServices tmpMockedHostServices = Mockito.mock(HostServices.class);
        menuBarController.stage.getProperties().put(GlobalConstants.HOST_SERVICES_PROPERTY_KEY, tmpMockedHostServices);

        menuBarController.handleHelpMenuItemAction();

        verify(tmpMockedHostServices, Mockito.times(1)).showDocument(any(String.class));
    }

    @Test
    void testAlertProvider() {

        MenuBarController.AlertProvider tmpAlertProvider = new MenuBarController.AlertProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpAlertProvider.provideAboutAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertTrue(tmpAlert.getContentText().startsWith("Copyright"));
    }

}
