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
import com.sophisticatedapps.archiving.documentarchiver.type.Tenant;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.controller.PreferencesPaneController".
 */
@ExtendWith(ApplicationExtension.class)
class ManageTenantsPaneControllerTest extends BaseTest {

    @TempDir
    File tempDir;

    private Pane manageTenantsPane;
    private ManageTenantsPaneController manageTenantsPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<ManageTenantsPaneController,Pane> tmpPreferencesPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/ManageTenantsPane.fxml", getApp(aStage));
        manageTenantsPane = tmpPreferencesPaneControllerRegionPair.getRegion();
        manageTenantsPaneController = tmpPreferencesPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        manageTenantsPaneController.rampDown();

        manageTenantsPane = null;
        manageTenantsPaneController = null;
    }

    @Test
    void handleCreateTenantButtonAction() throws IllegalAccessException {

        File tmpTestCoreArchivingFolder = new File(tempDir, "TestCoreArchivingFolder");
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", tmpTestCoreArchivingFolder, true);

        TextField tmpNewTenantNameTextField = (TextField)manageTenantsPane.lookup("#newTenantNameTextField");
        tmpNewTenantNameTextField.setText("So&New");
        @SuppressWarnings("unchecked")
        TableView<Tenant> tmpExistingTenantsTableView = (TableView<Tenant>)manageTenantsPane.lookup("#tenantsTableView");

        assertTrue(tmpExistingTenantsTableView.getItems().isEmpty());

        manageTenantsPaneController.handleCreateTenantButtonAction();
        assertTrue((new File(tmpTestCoreArchivingFolder, "@So-New")).exists());
        assertEquals(1, tmpExistingTenantsTableView.getItems().size());
        assertEquals(StringUtil.EMPTY_STRING, tmpNewTenantNameTextField.getText());

        // Try again with "effectively same" new name.
        tmpNewTenantNameTextField.setText("So!New");
        manageTenantsPaneController.handleCreateTenantButtonAction();
        assertEquals(1, tmpExistingTenantsTableView.getItems().size()); // Still the same
        assertEquals("So!New", tmpNewTenantNameTextField.getText());

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void handleCreateTenantButtonAction_with_Exception() throws IllegalAccessException {

        File tmpTestCoreArchivingFolder = new File(tempDir, NUL_CHARACTER_STRING);
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", tmpTestCoreArchivingFolder, true);

        TextField tmpNewTenantNameTextField = (TextField)manageTenantsPane.lookup("#newTenantNameTextField");
        tmpNewTenantNameTextField.setText("So&New");

        Alert tmpExceptionAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider.provideExceptionAlert(any(Exception.class))).thenReturn(tmpExceptionAlert);
        FieldUtils.writeField(manageTenantsPaneController, "dialogProvider", tmpMockedDialogProvider, true);

        manageTenantsPaneController.handleCreateTenantButtonAction();
        verify(tmpMockedDialogProvider, Mockito.times(1)).provideExceptionAlert(any(Exception.class));
        verify(tmpExceptionAlert, Mockito.times(1)).showAndWait();

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void deleteTenant() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        File tmpTestCoreArchivingFolder = new File(tempDir, "TestCoreArchivingFolder");
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", tmpTestCoreArchivingFolder, true);

        TextField tmpNewTenantNameTextField = (TextField)manageTenantsPane.lookup("#newTenantNameTextField");
        tmpNewTenantNameTextField.setText("So#New");
        @SuppressWarnings("unchecked")
        TableView<Tenant> tmpExistingTenantsTableView = (TableView<Tenant>)manageTenantsPane.lookup("#tenantsTableView");

        assertTrue(tmpExistingTenantsTableView.getItems().isEmpty());

        // First create a tenant
        File tmpCreatedTenantFolder = new File(tmpTestCoreArchivingFolder, "@So-New");
        manageTenantsPaneController.handleCreateTenantButtonAction();
        assertTrue(tmpCreatedTenantFolder.exists());
        assertEquals(1, tmpExistingTenantsTableView.getItems().size());

        // Delete it again
        Alert tmpMockedConfirmationAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider
                .provideConfirmTenantDeletionAlert(any(String.class))).thenReturn(tmpMockedConfirmationAlert);
        FieldUtils.writeField(manageTenantsPaneController, "dialogProvider", tmpMockedDialogProvider, true);
        Tenant tmpTenant = new Tenant("So-New");

        // First we give a NULL value
        MethodUtils.invokeMethod(manageTenantsPaneController, true, "deleteTenant", (Tenant)null);
        assertTrue(tmpCreatedTenantFolder.exists());
        assertEquals(1, tmpExistingTenantsTableView.getItems().size());

        // Then we do not confirm
        when(tmpMockedConfirmationAlert.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        MethodUtils.invokeMethod(manageTenantsPaneController, true, "deleteTenant", tmpTenant);
        assertTrue(tmpCreatedTenantFolder.exists());
        assertEquals(1, tmpExistingTenantsTableView.getItems().size());

        // Now we confirm
        when(tmpMockedConfirmationAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        MethodUtils.invokeMethod(manageTenantsPaneController, true, "deleteTenant", tmpTenant);
        assertFalse(tmpCreatedTenantFolder.exists());
        assertEquals(0, tmpExistingTenantsTableView.getItems().size());

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void deleteTenant_with_Exception() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        File tmpTestCoreArchivingFolder = new File(tempDir, NUL_CHARACTER_STRING);
        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", tmpTestCoreArchivingFolder, true);

        Alert tmpMockedConfirmationAlert = Mockito.mock(Alert.class);
        when(tmpMockedConfirmationAlert.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        Alert tmpExceptionAlert = Mockito.mock(Alert.class);
        BaseController.DialogProvider tmpMockedDialogProvider = Mockito.mock(BaseController.DialogProvider.class);
        when(tmpMockedDialogProvider
                .provideConfirmTenantDeletionAlert(any(String.class))).thenReturn(tmpMockedConfirmationAlert);
        when(tmpMockedDialogProvider.provideExceptionAlert(any(Exception.class))).thenReturn(tmpExceptionAlert);
        FieldUtils.writeField(manageTenantsPaneController, "dialogProvider", tmpMockedDialogProvider, true);

        MethodUtils.invokeMethod(manageTenantsPaneController, true, "deleteTenant", (new Tenant("FooBar")));
        verify(tmpMockedDialogProvider, Mockito.times(1)).provideExceptionAlert(any(Exception.class));
        verify(tmpExceptionAlert, Mockito.times(1)).showAndWait();

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @SuppressWarnings("unchecked")
    @Test
    void isTenantsChanged() throws IllegalAccessException {

        Tenant tmpTenant1 = new Tenant("One");
        Tenant tmpTenant2 = new Tenant("Two");
        Tenant tmpTenant3 = new Tenant("Three");

        List<Tenant> tmpFormerTenantList =
                (List<Tenant>)FieldUtils.readField(manageTenantsPaneController, "formerTenantList", true);
        tmpFormerTenantList.add(tmpTenant1);
        tmpFormerTenantList.add(tmpTenant2);
        assertTrue(manageTenantsPaneController.isTenantsChanged());

        TableView<Tenant> tmpExistingTenantsTableView = (TableView<Tenant>)manageTenantsPane.lookup("#tenantsTableView");
        tmpExistingTenantsTableView.getItems().addAll(tmpTenant1, tmpTenant2);
        assertFalse(manageTenantsPaneController.isTenantsChanged());

        tmpFormerTenantList.add(tmpTenant3);
        tmpExistingTenantsTableView.getItems().add(tmpTenant2);
        assertTrue(manageTenantsPaneController.isTenantsChanged());

        tmpFormerTenantList.remove(tmpTenant3);
        tmpFormerTenantList.add(tmpTenant2);
        tmpExistingTenantsTableView.getItems().remove(tmpTenant2);
        tmpExistingTenantsTableView.getItems().add(tmpTenant3);
        assertTrue(manageTenantsPaneController.isTenantsChanged());

        tmpFormerTenantList.remove(tmpTenant1);
        assertTrue(manageTenantsPaneController.isTenantsChanged());
    }

}
