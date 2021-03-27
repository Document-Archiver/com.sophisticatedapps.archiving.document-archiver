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

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.PreferencesPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class PreferencesPaneControllerTest {

    private Pane preferencesPane;
    private PreferencesPaneController preferencesPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<PreferencesPaneController,Pane> tmpPreferencesPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/PreferencesPane.fxml", aStage);
        preferencesPane = tmpPreferencesPaneControllerRegionPair.getRegion();
        preferencesPaneController = tmpPreferencesPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        preferencesPaneController.rampDown();

        preferencesPane = null;
        preferencesPaneController = null;
    }

    @Test
    void getArchivingFolder() {

        TextField tmpArchivingFolderTextField = (TextField)preferencesPane.lookup("#archivingFolderTextField");
        tmpArchivingFolderTextField.setText("/foobar");

        assertEquals("/foobar", preferencesPaneController.getArchivingFolder());
    }

    @Test
    void getQuickDescriptionWords() {

        TextArea tmpQuickDescriptionWordsTextArea =
                (TextArea)preferencesPane.lookup("#quickDescriptionWordsTextArea");
        tmpQuickDescriptionWordsTextArea.setText("foo, bar, snafu");

        assertEquals("foo,bar,snafu", preferencesPaneController.getQuickDescriptionWords());
    }

}
