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
import com.sophisticatedapps.archiving.documentarchiver.controller.InfoPaneController;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(ApplicationExtension.class)
class InfoPaneTest extends BaseTest {

    private Stage stage;
    private Pane infoPane;
    private TestInfoPaneController infoPaneController;

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

        FXMLLoader tmpRegionLoader = new FXMLLoader(App.class.getResource("view/InfoPane.fxml"));
        tmpRegionLoader.setResources(LanguageUtil.getResourceBundleForCurrentLanguage());
        tmpRegionLoader.setControllerFactory(aParam -> Mockito.spy(new TestInfoPaneController()));
        infoPane = tmpRegionLoader.load();
        infoPaneController = tmpRegionLoader.getController();
        infoPaneController.rampUp(getApplicationContext(aStage));

        aStage.setScene(new Scene(infoPane));
        aStage.show();
        aStage.toFront();
    }

    @AfterEach
    public void cleanUpEach() {

        Platform.runLater(() -> {
            // Do this to trigger the "unload" listener of the InfoPane.
            stage.getScene().setRoot(new Pane());
            stage.hide();
        });

        WaitForAsyncUtils.waitForFxEvents();

        infoPaneController.rampDown();

        infoPane = null;
        infoPaneController = null;

        stage = null;
    }

    /**
     * Check the DatePicker.
     *
     * @param   anFxRobot   FxRobot - injected by TestFX framework.
     */
    @Test
    void testDatePicker(FxRobot anFxRobot) {

        DatePicker tmpDatePicker = (DatePicker)infoPane.lookup("#datePicker");

        Platform.runLater(() -> {

            tmpDatePicker.requestFocus();
            tmpDatePicker.getEditor().setText("");
            anFxRobot.write("16.03.1993");

            // Make focus go away
            TextField tmpDescriptionTextField = (TextField)infoPane.lookup("#descriptionTextField");
            tmpDescriptionTextField.requestFocus();
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("1993-03-16", tmpDatePicker.getValue().toString());
        verify(infoPaneController, times(2)).handleDatePickerFocusedPropertyValueChanged(anyBoolean());
    }

    /**
     * Check the UtilizeTimeInformationCheckBox.
     */
    @Test
    void testUtilizeTimeInformationCheckBox() {

        CheckBox tmpUticb = (CheckBox)infoPane.lookup("#utilizeTimeInformationCheckBox");

        //Platform.runLater(() -> anFxRobot.clickOn(tmpUticb));
        tmpUticb.setSelected(true);

        WaitForAsyncUtils.waitForFxEvents();

        verify(infoPaneController, times(1))
                .handleUtilizeTimeInformationCheckBoxValueChanged(anyBoolean());
    }

    /**
     * Check the QuickDescriptionWordsComboBox.
     */
    @Test
    void testQuickDescriptionWordsComboBox() {

        @SuppressWarnings("unchecked")
        ComboBox<String> tmpComboBox = (ComboBox<String>)infoPane.lookup("#quickDescriptionWordsComboBox");

        Platform.runLater(() -> {

            // anFxRobot.clickOn(tmpComboBox); anFxRobot.type(KeyCode.DOWN); anFxRobot.type(KeyCode.ENTER);
            tmpComboBox.setValue("foo");
        });

        WaitForAsyncUtils.waitForFxEvents();

        verify(infoPaneController, times(1))
                .handleQuickDescriptionWordsComboBoxValueChanged(anyString());
    }

    /**
     * Extended InfoPaneController for testing.
     */
    private static class TestInfoPaneController extends InfoPaneController {

        @Override
        protected void handleDatePickerFocusedPropertyValueChanged(boolean aNewValue) {
            super.handleDatePickerFocusedPropertyValueChanged(aNewValue);
        }

        @Override
        protected void handleUtilizeTimeInformationCheckBoxValueChanged(boolean aNewValue) {
            super.handleUtilizeTimeInformationCheckBoxValueChanged(aNewValue);
        }

        @Override
        protected void handleQuickDescriptionWordsComboBoxValueChanged(String aNewValue) {
            super.handleQuickDescriptionWordsComboBoxValueChanged(aNewValue);
        }

    }

}
