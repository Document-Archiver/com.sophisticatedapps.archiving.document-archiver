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
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import javafx.collections.ObservableMap;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ApplicationControllerTest extends BaseTest {

    private Stage stage;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) throws IOException {

        this.stage = aStage;
    }

    @AfterEach
    public void cleanUpEach() {

        stage = null;
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowDecideWhatToOpenDialog_selection_files() {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        DialogProvider tmpMockedDialogProvider = Mockito.mock(DialogProvider.class);
        when(tmpMockedDialogProvider.provideDecideWhatToOpenDialog(anyBoolean())).thenReturn(tmpMockedWelcomeDialog);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestMultipleFilesSelection(any(Stage.class)))
                .thenReturn(ALL_DOCUMENTS_LIST);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                tmpMockedDialogProvider, null);
        tmpApplicationController.showDecideWhatToOpenDialog(tmpMockedStage, true);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDecideWhatToOpenDialog(anyBoolean());
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        List<File> tmpSortedAllDocumentsList = new ArrayList<>(ALL_DOCUMENTS_LIST);
        tmpSortedAllDocumentsList.sort(Comparator.naturalOrder());
        assertEquals(tmpSortedAllDocumentsList, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(tmpSortedAllDocumentsList.get(0),
                tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowDecideWhatToOpenDialog_selection_files_and_cancel() {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        DialogProvider tmpMockedDialogProvider = Mockito.mock(DialogProvider.class);
        when(tmpMockedDialogProvider.provideDecideWhatToOpenDialog(anyBoolean())).thenReturn(tmpMockedWelcomeDialog);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestMultipleFilesSelection(any(Stage.class))).thenReturn(null);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                tmpMockedDialogProvider, null);
        tmpApplicationController.showDecideWhatToOpenDialog(tmpMockedStage, true);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDecideWhatToOpenDialog(anyBoolean());
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowDecideWhatToOpenDialog_selection_directory() {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        DialogProvider tmpMockedDialogProvider = Mockito.mock(DialogProvider.class);
        when(tmpMockedDialogProvider.provideDecideWhatToOpenDialog(anyBoolean())).thenReturn(tmpMockedWelcomeDialog);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestDirectorySelection(any(Stage.class))).thenReturn(TEST_SOURCE_FOLDER2);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                tmpMockedDialogProvider, null);
        tmpApplicationController.showDecideWhatToOpenDialog(tmpMockedStage, true);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDecideWhatToOpenDialog(anyBoolean());
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        List<File> tmpExpectedDocumentsList = List.of(TEST_JPG_FILE2, TEST_TEXT_FILE2);
        assertEquals(tmpExpectedDocumentsList, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(TEST_JPG_FILE2, tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }


    @SuppressWarnings("unchecked")
    @Test
    void testShowDecideWhatToOpenDialog_selection_empty_directory() {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        DialogProvider tmpMockedDialogProvider = Mockito.mock(DialogProvider.class);
        when(tmpMockedDialogProvider.provideDecideWhatToOpenDialog(anyBoolean())).thenReturn(tmpMockedWelcomeDialog);
        Alert tmpMockedEmptyDirectoryAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.provideDirectoryDoesNotContainFilesAlert())
                .thenReturn(tmpMockedEmptyDirectoryAlert);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices
                .requestDirectorySelection(any(Stage.class))).thenReturn(TEST_EMPTY_SOURCE_FOLDER);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                tmpMockedDialogProvider, null);
        tmpApplicationController.showDecideWhatToOpenDialog(tmpMockedStage, true);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDecideWhatToOpenDialog(anyBoolean());
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDirectoryDoesNotContainFilesAlert();
        verify(tmpMockedEmptyDirectoryAlert, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowDecideWhatToOpenDialog_selection_directory_and_cancel() {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        DialogProvider tmpMockedDialogProvider = Mockito.mock(DialogProvider.class);
        when(tmpMockedDialogProvider.provideDecideWhatToOpenDialog(anyBoolean())).thenReturn(tmpMockedWelcomeDialog);

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestDirectorySelection(any(Stage.class))).thenReturn(null);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                tmpMockedDialogProvider, null);
        tmpApplicationController.showDecideWhatToOpenDialog(tmpMockedStage, true);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDecideWhatToOpenDialog(anyBoolean());
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @Test
    void testRequestDirectorySelection() {

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestDirectorySelection(any(Stage.class))).thenReturn(TEST_SOURCE_FOLDER);

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                null, null);

        File tmpResult = tmpApplicationController.requestDirectorySelection(stage);

        verify(tmpMockedApplicationServices, Mockito.times(1)).requestDirectorySelection(any(Stage.class));
        assertEquals(TEST_SOURCE_FOLDER, tmpResult);
    }

    @Test
    void testRequestMultipleFilesSelection() {

        ApplicationServices tmpMockedApplicationServices = Mockito.mock(ApplicationServices.class);
        when(tmpMockedApplicationServices.requestMultipleFilesSelection(any(Stage.class)))
                .thenReturn(ALL_DOCUMENTS_LIST);

        ApplicationController tmpApplicationController = new ApplicationController(tmpMockedApplicationServices,
                null, null);

        List<File> tmpResult = tmpApplicationController.requestMultipleFilesSelection(stage);

        verify(tmpMockedApplicationServices, Mockito.times(1)).requestMultipleFilesSelection(any(Stage.class));
        assertEquals(ALL_DOCUMENTS_LIST, tmpResult);
    }

}
