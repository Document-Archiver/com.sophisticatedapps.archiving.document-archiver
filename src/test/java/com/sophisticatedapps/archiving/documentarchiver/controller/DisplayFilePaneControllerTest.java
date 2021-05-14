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

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;
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
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class DisplayFilePaneControllerTest extends BaseTest {

    private Pane displayFilePane;
    private DisplayFilePaneController displayFilePaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    @SuppressWarnings("unused")
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<DisplayFilePaneController,Pane> tmpDisplayFilePaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/DisplayFilePane.fxml", aStage);
        displayFilePane = tmpDisplayFilePaneControllerRegionPair.getRegion();
        displayFilePaneController = tmpDisplayFilePaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        displayFilePaneController.rampDown();

        displayFilePane = null;
        displayFilePaneController = null;
    }

    @Test
    void testSetWidths() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        displayFilePaneController.setNewCurrentDocument(TEST_TEXT_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        displayFilePane.setPrefWidth(999);
        MethodUtils.invokeMethod(displayFilePaneController, true, "setWidths");

        TextArea tmpTextArea = (TextArea)displayFilePane.getChildren().get(0);
        assertEquals(999, tmpTextArea.getPrefWidth());
    }

    @Test
    void testSetHeights() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        displayFilePaneController.setNewCurrentDocument(TEST_TEXT_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        displayFilePane.setPrefHeight(555);
        MethodUtils.invokeMethod(displayFilePaneController, true, "setHeights");

        TextArea tmpTextArea = (TextArea)displayFilePane.getChildren().get(0);
        assertEquals(555, tmpTextArea.getPrefHeight());
    }

    @Test
    void testHandleOpenExternalViewerButtonAction() throws IllegalAccessException, IOException {

        // Set a mocked DesktopProvider to the DisplayFilePaneController
        Desktop tmpMockedDesktop = Mockito.mock(Desktop.class);
        BaseController.DesktopProvider tmpMockedDesktopProvider = Mockito.mock(BaseController.DesktopProvider.class);
        doReturn(tmpMockedDesktop).when(tmpMockedDesktopProvider).provideDesktop();
        FieldUtils.writeField(displayFilePaneController, "desktopProvider", tmpMockedDesktopProvider, true);

        displayFilePaneController.setNewCurrentDocument(TEST_TEXT_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        displayFilePaneController.handleOpenExternalViewerButtonAction();
        WaitForAsyncUtils.waitForFxEvents();

        verify(tmpMockedDesktop, Mockito.times(1)).open(any(File.class));
    }

    @Test
    void testHandleCurrentDocumentChangedToUnsupportedFile() throws IOException, IllegalAccessException {

        // Set a mocked DesktopProvider to the DisplayFilePaneController
        Desktop tmpMockedDesktop = Mockito.mock(Desktop.class);
        BaseController.DesktopProvider tmpMockedDesktopProvider = Mockito.mock(BaseController.DesktopProvider.class);
        doReturn(tmpMockedDesktop).when(tmpMockedDesktopProvider).provideDesktop();
        FieldUtils.writeField(displayFilePaneController, "desktopProvider", tmpMockedDesktopProvider, true);

        File tmpUnsupportedFile = new File("snafu.foobar");

        displayFilePaneController.setNewCurrentDocument(tmpUnsupportedFile);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a TextArea on our display file Pane.
        Pane tmpPane = (Pane)displayFilePane.getChildren().get(0);
        assertEquals(Label.class, tmpPane.getChildren().get(0).getClass());

        verify(tmpMockedDesktop, Mockito.times(1)).open(any(File.class));
    }

    @Test
    void testHandleCurrentDocumentChangedToPDFFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_PDF_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a TextArea on our display file Pane.
        Pane tmpPDFPane = (Pane)displayFilePane.getChildren().get(0);
        assertEquals(WebView.class, tmpPDFPane.getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToTextFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_TEXT_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a TextArea on our display file Pane.
        TextArea tmpTextArea = (TextArea)displayFilePane.getChildren().get(0);
        assertEquals("Simple text for testing.", tmpTextArea.getText());
    }

    @Test
    void testHandleCurrentDocumentChangedToSvgFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_SVG_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a Pane on our display file Pane containing a WebView.
        Pane tmpWrapperPane = (Pane)displayFilePane.getChildren().get(0);
        assertSame(WebView.class, tmpWrapperPane.getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToPngFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_PNG_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a Pane on our display file Pane.
        GesturePane tmpWrapperPane = (GesturePane)displayFilePane.getChildren().get(0);
        assertSame(ImageView.class, ((Pane)tmpWrapperPane.getContent()).getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToJpgFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_JPG_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a Pane on our display file Pane.
        GesturePane tmpWrapperPane = (GesturePane)displayFilePane.getChildren().get(0);
        assertSame(ImageView.class, ((Pane)tmpWrapperPane.getContent()).getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToMp3File() {

        displayFilePaneController.setNewCurrentDocument(TEST_MP3_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a "play" Button or a message on the assembled pane.
        VBox tmpMediaTypePane = (VBox)displayFilePane.getChildren().get(0);
        ObservableList<Node> tmpChildNodeList = tmpMediaTypePane.getChildren();
        Node tmpLastNode = tmpChildNodeList.get(tmpChildNodeList.size() - 1);

        if (tmpLastNode instanceof HBox) {

            assertEquals("Play", ((Button)((HBox)tmpLastNode).getChildren().get(0)).getText());
        }
        else {

            assertEquals("Sorry - media not supported.", ((Label)tmpLastNode).getText());
        }
    }

    @Test
    void testHandleCurrentDocumentChangedToDocxFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_DOCX_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a Pane on our display file Pane containing a WebView.
        Pane tmpWrapperPane = (Pane)displayFilePane.getChildren().get(0);
        assertSame(WebView.class, tmpWrapperPane.getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToDocFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_DOC_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a Pane on our display file Pane containing a WebView.
        Pane tmpWrapperPane = (Pane)displayFilePane.getChildren().get(0);
        assertSame(WebView.class, tmpWrapperPane.getChildren().get(0).getClass());
    }

    @Test
    void testHandleCurrentDocumentChangedToZipFile() {

        displayFilePaneController.setNewCurrentDocument(TEST_ZIP_FILE);
        WaitForAsyncUtils.waitForFxEvents();

        // Now there should be a TextArea on our display file Pane.
        Pane tmpZipPane = (Pane)displayFilePane.getChildren().get(0);
        assertEquals(TableView.class, tmpZipPane.getChildren().get(0).getClass());
    }

    @Test
    void testPaneRemovalCleanup() {

        DisplayFilePaneController.DisplayAudioNodeAssembler tmpDisplayAudioNodeAssembler =
                new DisplayFilePaneController.DisplayAudioNodeAssembler();

        // Mock a MediaPlayer and set it to a Pane
        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        when(tmpMockedMediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PLAYING);

        Region tmpPane = tmpDisplayAudioNodeAssembler.assemble(
                displayFilePaneController, TEST_MP3_FILE, null, 250, 250);
        tmpPane.setUserData(tmpMockedMediaPlayer);

        // Set and remove Pane to and from a parent Pane
        Pane tmpParentPane = new Pane(tmpPane);
        tmpParentPane.getChildren().remove(tmpPane);

        // "stop" should have been called.
        verify(tmpMockedMediaPlayer, Mockito.times(1)).stop();
    }

    @Test
    void testDisplayAudioNodeAssemblerSetupMediaView() throws IOException, URISyntaxException {

        FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MediaTypeAudioPane.fxml"));
        VBox tmpMediaTypeAudioPane = tmpLoader.load();
        MediaTypeAudioPaneController tmpMediaTypeAudioPaneController = tmpLoader.getController();
        tmpMediaTypeAudioPaneController.rampUp(displayFilePaneController.stage);

        DisplayFilePaneController.DisplayAudioNodeAssembler tmpDisplayAudioNodeAssembler =
                new DisplayFilePaneController.DisplayAudioNodeAssembler();

        tmpDisplayAudioNodeAssembler.setupMediaView(tmpMediaTypeAudioPaneController, tmpMediaTypeAudioPane,
                TEST_MP3_FILE, 250, 250);

        // Different outcome on CI build (no media support -> (MediaPlayer == null))
        MediaPlayer tmpMediaPlayer = tmpMediaTypeAudioPaneController.getMediaPlayer();

        if (tmpMediaPlayer == null) {

            assertEquals("Sorry - media not supported.", ((Label)tmpMediaTypeAudioPane.getChildren().get(2)).getText());
        }
        else {

            File tmpFileFromMediaPlayer = new File(new URI(tmpMediaPlayer.getMedia().getSource()));
            assertEquals(TEST_MP3_FILE, tmpFileFromMediaPlayer);
        }
    }

    @Test
    void testDisplayVideoNodeAssemblerSetupMediaView_with_exception() throws IOException {

        FXMLLoader tmpLoader = new FXMLLoader(App.class.getResource("view/MediaTypeAudioVideoPane.fxml"));
        VBox tmpMediaTypeAudioVideoPane = tmpLoader.load();
        MediaTypeAudioVideoPaneController tmpMediaTypeAudioVideoPaneController = tmpLoader.getController();
        tmpMediaTypeAudioVideoPaneController.rampUp(displayFilePaneController.stage);

        DisplayFilePaneController.DisplayVideoNodeAssembler tmpDisplayVideoNodeAssembler =
                new DisplayFilePaneController.DisplayVideoNodeAssembler();

        // We give an inappropriate file
        tmpDisplayVideoNodeAssembler.setupMediaView(tmpMediaTypeAudioVideoPaneController, tmpMediaTypeAudioVideoPane,
                TEST_TEXT_FILE, 250, 250);

        // Now there should be no media player set on the controller.
        assertNull(tmpMediaTypeAudioVideoPaneController.getMediaPlayer());

        // Pane should contain a warning message
        assertEquals("Sorry - media not supported.",
                ((Label)tmpMediaTypeAudioVideoPane.getChildren().get(2)).getText());
    }

    @Test
    void testDisplayZipNodeAssembler_with_exception() {

        DisplayFilePaneController.DisplayZipNodeAssembler tmpDisplayZipNodeAssembler =
                new DisplayFilePaneController.DisplayZipNodeAssembler();

        // Give an encrypted ZIP to the assembler
        RuntimeException tmpException = assertThrows(RuntimeException.class, () -> tmpDisplayZipNodeAssembler.assemble(
                displayFilePaneController, TEST_TEXT_FILE, displayFilePaneController.stage, 250, 250));
        assertEquals("ZIP file could not be opened: zip END header not found", tmpException.getMessage());
    }

}
