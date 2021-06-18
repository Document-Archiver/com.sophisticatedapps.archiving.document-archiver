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

package com.sophisticatedapps.archiving.documentarchiver;

import com.sophisticatedapps.archiving.documentarchiver.api.FileSystemServices;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.App"
 */
@ExtendWith(ApplicationExtension.class)
class AppTest extends BaseTest {

    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {

        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {

        System.setErr(standardErr);
    }

    @Test
    void testStart_no_arguments() {

        HashMap<Object,Object> tmpPropertiesMap = doStart(null, false);

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @Test
    void testStart_empty_argument() {

        HashMap<Object,Object> tmpPropertiesMap = doStart("", false);

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    /**
     * Test if parameter gets checked correctly.
     */
    @Test
    void testStart_non_existing_file_argument() {

        HashMap<Object,Object> tmpPropertiesMap = doStart("/foo/bar", true);

        String tmpErrorMsg = (String)tmpPropertiesMap.get("theErrorMsg");
        assertNotNull(tmpErrorMsg);
        assertEquals("File does not exist: /foo/bar (No additional information)", tmpErrorMsg);

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    /**
     * Test if parameter gets checked correctly.
     */
    @Test
    void testStart_invalid_file_argument() {

        HashMap<Object,Object> tmpPropertiesMap = doStart(NUL_CHARACTER_STRING, true);

        String tmpErrorMsg = (String)tmpPropertiesMap.get("theErrorMsg");
        assertNotNull(tmpErrorMsg);
        assertEquals("Could not create File object for '\u0000': Invalid file path (No additional information)",
                tmpErrorMsg);

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @Test
    void testStart_file_argument() {

        HashMap<Object,Object> tmpPropertiesMap = doStart(TEST_TEXT_FILE.getPath(), false);

        List<File> tmpFilesFromArgs = Collections.singletonList(TEST_TEXT_FILE);
        assertEquals(tmpFilesFromArgs, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(TEST_TEXT_FILE, tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testStart_directory_argument() {

        HashMap<Object,Object> tmpPropertiesMap = doStart(TEST_SOURCE_FOLDER.getPath(), false);

        assertTrue(((List<File>)tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY))
                .containsAll(ALL_DOCUMENTS_LIST));
        assertEquals(TEST_JPG_FILE2, tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testStart_check_properties() {

        HashMap<Object,Object> tmpPropertiesMap = doStart("", false);

        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        assertEquals(tmpBounds.getMinX(), tmpPropertiesMap.get("theX"));
        assertEquals(tmpBounds.getMinY(), tmpPropertiesMap.get("theY"));
        assertEquals(tmpBounds.getWidth(), tmpPropertiesMap.get("theWidth"));
        assertEquals(tmpBounds.getHeight(), tmpPropertiesMap.get("theHeight"));

        assertEquals(HostServices.class, tmpPropertiesMap.get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY).getClass());
        assertEquals(1, ((ObservableList<Image>)tmpPropertiesMap.get("theIconsList")).size());

        assertEquals(Scene.class, tmpPropertiesMap.get("theScene").getClass());
        verify(((Stage)tmpPropertiesMap.get("theStage")), Mockito.times(1)).show();

        //assertTrue(Taskbar.getTaskbar().getIconImage() instanceof java.awt.Image);
    }

    @SuppressWarnings("unchecked")
    private HashMap<Object,Object> doStart(String aParameter, boolean aShouldShowError) {

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();

        // Mock the stage
        Stage tmpMockedStage = Mockito.mock(Stage.class);

        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);

        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        ObservableList<Image> tmpIconsList = FXCollections.observableList(new ArrayList<>());
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        doReturn(tmpIconsList).when(tmpMockedStage).getIcons();

        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theX", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setX(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theY", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setY(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theWidth", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setWidth(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theHeight", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setHeight(anyDouble());
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theScene", anInvocationOnMock.getArgument(0)); return null;
        }).when(tmpMockedStage).setScene(any(Scene.class));

        ReadOnlyDoubleProperty tmpMockedWidthProperty = Mockito.mock(ReadOnlyDoublePropertyBase.class);
        ReadOnlyDoubleProperty tmpMockedHeightProperty = Mockito.mock(ReadOnlyDoublePropertyBase.class);
        doReturn(tmpMockedWidthProperty).when(tmpMockedStage).widthProperty();
        doReturn(tmpMockedHeightProperty).when(tmpMockedStage).heightProperty();

        Alert tmpMockedAlert = Mockito.mock(Alert.class);
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put("theErrorMsg", anInvocationOnMock.getArgument(0));
            return tmpMockedAlert;
        }).when(tmpMockedDialogProvider).provideExceptionAlert(anyString());

        // Start the App
        Platform.runLater(() -> {

            Application.Parameters tmpMockedParameters = Mockito.mock(Application.Parameters.class);
            when(tmpMockedParameters.getRaw()).thenReturn(Collections.singletonList(aParameter));
            App tmpMockedApp = Mockito.spy(new App(tmpMockedDialogProvider));
            when(tmpMockedApp.getParameters()).thenReturn(tmpMockedParameters);
            HostServices tmpMockedHostServices = Mockito.mock(HostServices.class);
            when(tmpMockedApp.getHostServices()).thenReturn(tmpMockedHostServices);

            doCallRealMethod().when(tmpMockedApp).start(any(Stage.class));
            tmpMockedApp.start(tmpMockedStage);
        });
        WaitForAsyncUtils.waitForFxEvents();

        if (aShouldShowError) {
            verify(tmpMockedAlert, Mockito.times(1)).showAndWait();
        }
        else {
            verify(tmpMockedAlert, Mockito.times(0)).showAndWait();
        }

        tmpPropertiesMap.put("theStage", tmpMockedStage);
        tmpPropertiesMap.put("theIconsList", tmpIconsList);

        return tmpPropertiesMap;
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowWelcomeDialog_selection_files() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedWelcomeDialog);

        FileSystemServices tmpMockedFileSystemServices = Mockito.mock(FileSystemServices.class);
        when(tmpMockedFileSystemServices.requestMultipleFilesSelection(any(Stage.class)))
                .thenReturn(ALL_DOCUMENTS_LIST);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        App tmpApp = new App(tmpMockedFileSystemServices, tmpMockedDialogProvider);

        MethodUtils.invokeMethod(tmpApp, true, "showWelcomeDialog", tmpMockedStage);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        List<File> tmpSortedAllDocumentsList = new ArrayList<>(ALL_DOCUMENTS_LIST);
        tmpSortedAllDocumentsList.sort(Comparator.naturalOrder());
        assertEquals(tmpSortedAllDocumentsList, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(tmpSortedAllDocumentsList.get(0),
                tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowWelcomeDialog_selection_files_and_cancel() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.YES));
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedWelcomeDialog);

        FileSystemServices tmpMockedFileSystemServices = Mockito.mock(FileSystemServices.class);
        when(tmpMockedFileSystemServices.requestMultipleFilesSelection(any(Stage.class))).thenReturn(null);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        App tmpApp = new App(tmpMockedFileSystemServices, tmpMockedDialogProvider);

        MethodUtils.invokeMethod(tmpApp, true, "showWelcomeDialog", tmpMockedStage);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowWelcomeDialog_selection_directory() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedWelcomeDialog);

        FileSystemServices tmpMockedFileSystemServices = Mockito.mock(FileSystemServices.class);
        when(tmpMockedFileSystemServices.requestDirectorySelection(any(Stage.class))).thenReturn(TEST_SOURCE_FOLDER2);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        App tmpApp = new App(tmpMockedFileSystemServices, tmpMockedDialogProvider);

        MethodUtils.invokeMethod(tmpApp, true, "showWelcomeDialog", tmpMockedStage);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        List<File> tmpExpectedDocumentsList = List.of(TEST_JPG_FILE2, TEST_TEXT_FILE2);
        assertEquals(tmpExpectedDocumentsList, tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertEquals(TEST_JPG_FILE2, tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowWelcomeDialog_selection_empty_directory() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedWelcomeDialog);
        Alert tmpMockedEmptyDirectoryAlert = Mockito.mock(Alert.class);
        when(tmpMockedDialogProvider.provideDirectoryDoesNotContainFilesAlert())
                .thenReturn(tmpMockedEmptyDirectoryAlert);

        FileSystemServices tmpMockedFileSystemServices = Mockito.mock(FileSystemServices.class);
        when(tmpMockedFileSystemServices
                .requestDirectorySelection(any(Stage.class))).thenReturn(TEST_EMPTY_SOURCE_FOLDER);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        App tmpApp = new App(tmpMockedFileSystemServices, tmpMockedDialogProvider);

        MethodUtils.invokeMethod(tmpApp, true, "showWelcomeDialog", tmpMockedStage);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideDirectoryDoesNotContainFilesAlert();
        verify(tmpMockedEmptyDirectoryAlert, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testShowWelcomeDialog_selection_directory_and_cancel() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Dialog<ButtonType> tmpMockedWelcomeDialog = Mockito.mock(Dialog.class);
        when(tmpMockedWelcomeDialog.showAndWait()).thenReturn(Optional.of(ButtonType.NO));
        App.DialogProvider tmpMockedDialogProvider = Mockito.mock(App.DialogProvider.class);
        when(tmpMockedDialogProvider.provideWelcomeDialog()).thenReturn(tmpMockedWelcomeDialog);

        FileSystemServices tmpMockedFileSystemServices = Mockito.mock(FileSystemServices.class);
        when(tmpMockedFileSystemServices.requestDirectorySelection(any(Stage.class))).thenReturn(null);

        HashMap<Object,Object> tmpPropertiesMap = new HashMap<>();
        ObservableMap<Object,Object> tmpMockedPropertiesMap = Mockito.mock(ObservableMap.class);
        doAnswer(anInvocationOnMock -> {
            tmpPropertiesMap.put(anInvocationOnMock.getArgument(0), anInvocationOnMock.getArgument(1)); return null;
        }).when(tmpMockedPropertiesMap).put(any(), any());

        Stage tmpMockedStage = Mockito.mock(Stage.class);
        doReturn(tmpMockedPropertiesMap).when(tmpMockedStage).getProperties();
        App tmpApp = new App(tmpMockedFileSystemServices, tmpMockedDialogProvider);

        MethodUtils.invokeMethod(tmpApp, true, "showWelcomeDialog", tmpMockedStage);
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDialogProvider, Mockito.times(1)).provideWelcomeDialog();
        verify(tmpMockedWelcomeDialog, Mockito.times(1)).showAndWait();

        assertNull(tmpPropertiesMap.get(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY));
        assertNull(tmpPropertiesMap.get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY));
    }

    @Test
    void testSetFilesListToStageProperties_empty_files_list() {

        assertDoesNotThrow(() -> App.setFilesListToStageProperties(null, null));
    }

    @Test
    void testShowError_exception_without_cause() {

        IOException tmpException = new IOException("File not good.");
        (new App()).showError(Thread.currentThread(), tmpException);
        assertEquals("File not good. (No additional information)", outputStreamCaptor.toString().trim());
    }

    @Test
    void testShowError_exception_with_cause() {

        IOException tmpException = new IOException("File not good.", (new IOException("Path invalid.")));
        (new App()).showError(Thread.currentThread(), tmpException);
        assertEquals("File not good. (Path invalid.)", outputStreamCaptor.toString().trim());
    }

    @Test
    void testDialogProvider_provideWelcomeDialog() {

        App.DialogProvider tmpDialogProvider = new App.DialogProvider();
        final List<Dialog<ButtonType>> tmpDialogList = new ArrayList<>();

        Platform.runLater(() -> tmpDialogList.add(tmpDialogProvider.provideWelcomeDialog()));

        WaitForAsyncUtils.waitForFxEvents();

        Dialog<ButtonType> tmpDialog = tmpDialogList.get(0);
        assertNotNull(tmpDialog);
        assertSame(GlobalConstants.APP_ICON, ((ImageView)tmpDialog.getGraphic()).getImage());
        assertEquals("Welcome to Document Archiver", tmpDialog.getTitle());
        assertEquals("Thanks for using Document Archiver!", tmpDialog.getHeaderText());
        assertTrue(tmpDialog.getContentText().startsWith("Next you will have to choose what you want to archive."));
    }


    @Test
    void testDialogProvider_provideDirectoryDoesNotContainFilesAlert() {

        App.DialogProvider tmpDialogProvider = new App.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideDirectoryDoesNotContainFilesAlert()));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("The chosen directory doesn't contain files.", tmpAlert.getContentText());
    }

    @Test
    void testDialogProvider_provideExceptionAlert() {

        App.DialogProvider tmpDialogProvider = new App.DialogProvider();
        final List<Alert> tmpAlertList = new ArrayList<>();

        Platform.runLater(() -> tmpAlertList.add(tmpDialogProvider.provideExceptionAlert("This is a test")));

        WaitForAsyncUtils.waitForFxEvents();

        Alert tmpAlert = tmpAlertList.get(0);
        assertNotNull(tmpAlert);
        assertEquals("This is a test", tmpAlert.getContentText());
    }

}
