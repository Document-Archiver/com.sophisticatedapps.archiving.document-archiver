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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class MediaTypeAudioVideoPaneControllerTest {

    //private Pane mediaTypeAudioVideoPane;
    private MediaTypeAudioVideoPaneController mediaTypeAudioVideoPaneController;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param aStage - Will be injected by the test runner.
     */
    @Start
    public void start(Stage aStage) {

        aStage.getProperties().put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, null);
        aStage.getProperties().put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);

        FXMLUtil.ControllerRegionPair<MediaTypeAudioVideoPaneController, Pane>
                tmpMediaTypeAudioVideoPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/MediaTypeAudioVideoPane.fxml", aStage);
        //mediaTypeAudioVideoPane = tmpMediaTypeAudioVideoPaneControllerRegionPair.getRegion();
        mediaTypeAudioVideoPaneController = tmpMediaTypeAudioVideoPaneControllerRegionPair.getController();
    }

    @AfterEach
    public void cleanUpEach() {

        mediaTypeAudioVideoPaneController.rampDown();

        //mediaTypeAudioVideoPane = null;
        mediaTypeAudioVideoPaneController = null;
    }

    @Test
    void setupMediaView() {

        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        @SuppressWarnings("unchecked")
        ReadOnlyObjectProperty<MediaException> tmpMockedErrorProperty =
                Mockito.mock(ReadOnlyObjectProperty.class);
        when(tmpMockedMediaPlayer.errorProperty()).thenReturn(tmpMockedErrorProperty);

        mediaTypeAudioVideoPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        assertSame(tmpMockedMediaPlayer,
                ((MediaView)mediaTypeAudioVideoPaneController.getMediaVisualization()).getMediaPlayer());
    }

    @Test
    void getMediaPlayer() {

        MediaPlayer tmpMockedMediaPlayer = Mockito.mock(MediaPlayer.class);
        @SuppressWarnings("unchecked")
        ReadOnlyObjectProperty<MediaException> tmpMockedErrorProperty =
                Mockito.mock(ReadOnlyObjectProperty.class);
        when(tmpMockedMediaPlayer.errorProperty()).thenReturn(tmpMockedErrorProperty);

        mediaTypeAudioVideoPaneController.setupMediaView(tmpMockedMediaPlayer, 250);

        assertSame(tmpMockedMediaPlayer, mediaTypeAudioVideoPaneController.getMediaPlayer());
    }

}
