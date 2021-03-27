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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class MediaTypeAudioPaneControllerTest {

    private Pane mediaTypeAudioPane;
    private MediaTypeAudioPaneController mediaTypeAudioPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<MediaTypeAudioPaneController,Pane> tmpMediaTypeAudioPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/MediaTypeAudioPane.fxml", aStage);
        mediaTypeAudioPane = tmpMediaTypeAudioPaneControllerRegionPair.getRegion();
        mediaTypeAudioPaneController = tmpMediaTypeAudioPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        mediaTypeAudioPaneController.rampDown();

        mediaTypeAudioPane = null;
        mediaTypeAudioPaneController = null;
    }

    @Test
    void setupMediaView() {

        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);

        mediaTypeAudioPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        assertSame(tmpMockedMediaPlayer, mediaTypeAudioPane.getUserData());
    }

    @Test
    void getMediaVisualization() {

        Node tmpVirtualizationNode = mediaTypeAudioPaneController.getMediaVisualization();

        assertNotNull(tmpVirtualizationNode);
        assertSame(ImageView.class, tmpVirtualizationNode.getClass());
    }

    @Test
    void getMediaPlayer() {

        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        mediaTypeAudioPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        assertSame(tmpMockedMediaPlayer, mediaTypeAudioPaneController.getMediaPlayer());
    }

    @Test
    void handlePlayButtonAction() {

        // Mock a MediaPlayer and set it to the controller
        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        when(tmpMockedMediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.STOPPED);
        mediaTypeAudioPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        // Get button and fire it.
        Button tmpPlayButton = (Button)mediaTypeAudioPane.lookup("#playButton");
        tmpPlayButton.fire();

        // MediaPlayer kicked?
        verify(tmpMockedMediaPlayer, Mockito.times(1)).play();
    }

    @Test
    void handlePauseButtonAction() {

        // Mock a MediaPlayer and set it to the controller
        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        when(tmpMockedMediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PLAYING);
        mediaTypeAudioPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        // Get button and fire it.
        mediaTypeAudioPaneController.handlePauseButtonAction();
        //Button tmpPauseButton = (Button)mediaTypeAudioPane.lookup("#pauseButton"); tmpPauseButton.fire();

        // MediaPlayer kicked?
        verify(tmpMockedMediaPlayer, Mockito.times(1)).pause();
    }

    @Test
    void handleStopButtonAction() {

        // Mock a MediaPlayer and set it to the controller
        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        when(tmpMockedMediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PLAYING);
        mediaTypeAudioPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        // Get button and fire it.
        mediaTypeAudioPaneController.handleStopButtonAction();
        //Button tmpStopButton = (Button)mediaTypeAudioPane.lookup("#stopButton"); tmpStopButton.fire();

        // MediaPlayer kicked?
        verify(tmpMockedMediaPlayer, Mockito.times(1)).stop();
    }

}
