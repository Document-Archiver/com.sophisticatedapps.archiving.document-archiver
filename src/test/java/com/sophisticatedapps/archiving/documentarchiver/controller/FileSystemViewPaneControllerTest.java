package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class FileSystemViewPaneControllerTest extends BaseTest {

    private Pane fileSystemViewPane;
    private FileSystemViewPaneController fileSystemViewPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<FileSystemViewPaneController, Pane> tmpDocumentsPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/FileSystemViewPane.fxml", getApplicationContext(aStage));
        fileSystemViewPane = tmpDocumentsPaneControllerRegionPair.getRegion();
        fileSystemViewPaneController = tmpDocumentsPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        fileSystemViewPaneController.rampDown();

        fileSystemViewPane = null;
        fileSystemViewPaneController = null;
    }

    @Test
    void testSetWidths() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        fileSystemViewPane.setPrefWidth(1000);

        MethodUtils.invokeMethod(fileSystemViewPaneController, true, "setWidths");

        @SuppressWarnings("unchecked")
        TableColumn<ZipEntry, String> tmpNameColumn = (TableColumn<ZipEntry, String>)
                FieldUtils.readField(fileSystemViewPaneController, "nameColumn", true);
        assertEquals(800, tmpNameColumn.getPrefWidth());

        @SuppressWarnings("unchecked")
        TableColumn<ZipEntry, String> tmpSizeColumn = (TableColumn<ZipEntry, String>)
                FieldUtils.readField(fileSystemViewPaneController, "sizeColumn", true);
        assertEquals(180, tmpSizeColumn.getPrefWidth());
    }

    @Test
    void testOpenZipEntryInExternalViewer() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        // Set a mocked DesktopProvider to the DisplayFilePaneController
        Desktop tmpMockedDesktop = Mockito.mock(Desktop.class);
        BaseController.DesktopProvider tmpMockedDesktopProvider = Mockito.mock(BaseController.DesktopProvider.class);
        doReturn(tmpMockedDesktop).when(tmpMockedDesktopProvider).provideDesktop();
        FieldUtils.writeField(fileSystemViewPaneController, "desktopProvider", tmpMockedDesktopProvider, true);

        fileSystemViewPaneController.setNewCurrentDocument(TEST_ZIP_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        try (ZipFile tmpZipFile = new ZipFile(TEST_ZIP_FILE)) {

            ZipEntry tmpZipEntry = tmpZipFile.getEntry("test.txt");

            MethodUtils.invokeMethod(fileSystemViewPaneController, true, "openZipEntryInExternalViewer",
                    tmpZipEntry);

        }
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDesktop, Mockito.times(1)).open(any(File.class));
    }

    @Test
    void testOpenZipEntryInExternalViewer_with_exception() {

        fileSystemViewPaneController.setNewCurrentDocument(TEST_ZIP_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        ZipEntry tmpMockedZipEntry = Mockito.mock(ZipEntry.class);
        when(tmpMockedZipEntry.getName()).thenReturn("../../foo");

        InvocationTargetException tmpException = assertThrows(InvocationTargetException.class, (() -> MethodUtils.invokeMethod(
                fileSystemViewPaneController, true, "openZipEntryInExternalViewer", tmpMockedZipEntry)));
        assertEquals("Could not get ZIP file contents: Entry is outside of the target directory",
                tmpException.getCause().getMessage());
    }

}
