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
import com.sophisticatedapps.archiving.documentarchiver.controller.ManageTenantsPaneController;
import com.sophisticatedapps.archiving.documentarchiver.type.Tenant;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class ManageTenantsPaneTest extends BaseTest {

    private Stage stage;
    private Pane manageTenantsPane;
    private ManageTenantsPaneController manageTenantsPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IllegalAccessException {

        this.stage = aStage;

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        // Set the test archiving folder as core archiving folder, so the reading of tenants delivers something.
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", TEST_ARCHIVING_FOLDER, true);

        FXMLUtil.ControllerRegionPair<ManageTenantsPaneController,Pane> tmpPreferencesPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/ManageTenantsPane.fxml", getApplicationContext(aStage));
        manageTenantsPane = tmpPreferencesPaneControllerRegionPair.getRegion();
        manageTenantsPaneController = tmpPreferencesPaneControllerRegionPair.getController();

        aStage.setScene(new Scene(manageTenantsPane));
        aStage.show();
        aStage.toFront();
    }

    @AfterEach
    public void cleanUpEach() throws IllegalAccessException {

        manageTenantsPaneController.rampDown();

        manageTenantsPane = null;
        manageTenantsPaneController = null;

        Platform.runLater(() -> {

            stage.hide();
            stage = null;
        });

        // Set back core archiving folder
        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void testExistingTenantsTableView() {

        @SuppressWarnings("unchecked")
        TableView<Tenant> tmpTableView = (TableView<Tenant>)manageTenantsPane.lookup("#tenantsTableView");
        assertEquals(1, tmpTableView.getItems().size());
        assertEquals("MyTenant", tmpTableView.getItems().get(0).getName());
    }

}
