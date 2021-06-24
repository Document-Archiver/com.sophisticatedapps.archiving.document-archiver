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

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.controller.MenuBarController;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
class MenuBarTest extends BaseTest {

    private Stage stage;
    private MenuBar menuBar;
    private TestMenuBarController menuBarController;

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

        FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MenuBar.fxml"));
        tmpLoader.setResources(LanguageUtil.getResourceBundleForCurrentLanguage());
        tmpLoader.setControllerFactory(aParam -> Mockito.spy(new TestMenuBarController()));
        menuBar = tmpLoader.load();
        menuBarController = tmpLoader.getController();
        menuBarController.rampUp(getApplicationContext(aStage));

        aStage.setScene(new Scene(menuBar));
        aStage.setHeight(250);
        aStage.show();
        aStage.toFront();
    }

    @AfterEach
    public void cleanUpEach() {

        menuBarController.rampDown();

        menuBar = null;
        menuBarController = null;

        Platform.runLater(() -> {

            stage.hide();
            stage = null;
        });
    }

    /**
     * Test the language selection.
     */
    @Test
    void testLanguageSelection() {

        final Map<String, Object> tmpFiredMenuItemMap = new HashMap<>();

        doAnswer(anInvocationOnMock -> {
            tmpFiredMenuItemMap.put("menuItem", ((ActionEvent)anInvocationOnMock.getArgument(0)).getSource());
            return null;
        }).when(menuBarController).handleChangeLanguageMenuItemAction(any(ActionEvent.class));

        Menu tmpLanguageMenu = menuBar.getMenus().get(5);
        assertEquals("languageMenu", tmpLanguageMenu.getId());
        RadioMenuItem tmpEnglishLanguageMenuItem = (RadioMenuItem)tmpLanguageMenu.getItems().get(0);
        assertEquals("englishLanguageMenuItem", tmpEnglishLanguageMenuItem.getId());
        assertTrue(tmpEnglishLanguageMenuItem.isSelected());
        RadioMenuItem tmpGermanLanguageMenuItem = (RadioMenuItem)tmpLanguageMenu.getItems().get(1);
        assertEquals("germanLanguageMenuItem", tmpGermanLanguageMenuItem.getId());
        assertFalse(tmpGermanLanguageMenuItem.isSelected());
        RadioMenuItem tmpSpanishLanguageMenuItem = (RadioMenuItem)tmpLanguageMenu.getItems().get(2);
        assertEquals("spanishLanguageMenuItem", tmpSpanishLanguageMenuItem.getId());
        assertFalse(tmpSpanishLanguageMenuItem.isSelected());

        Platform.runLater(tmpEnglishLanguageMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        verify(menuBarController, Mockito.times(1)).handleChangeLanguageMenuItemAction(any(ActionEvent.class));
        assertEquals("englishLanguageMenuItem", ((MenuItem)tmpFiredMenuItemMap.get("menuItem")).getId());

        Platform.runLater(tmpGermanLanguageMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        verify(menuBarController, Mockito.times(2)).handleChangeLanguageMenuItemAction(any(ActionEvent.class));
        assertEquals("germanLanguageMenuItem", ((MenuItem)tmpFiredMenuItemMap.get("menuItem")).getId());

        Platform.runLater(tmpSpanishLanguageMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        verify(menuBarController, Mockito.times(3)).handleChangeLanguageMenuItemAction(any(ActionEvent.class));
        assertEquals("spanishLanguageMenuItem", ((MenuItem)tmpFiredMenuItemMap.get("menuItem")).getId());
    }

    /**
     * Extended MenuBarController for testing.
     */
    private static class TestMenuBarController extends MenuBarController {

        @Override
        protected void handleChangeLanguageMenuItemAction(ActionEvent anActionEvent) {
            super.handleChangeLanguageMenuItemAction(anActionEvent);
        }
    }

}
