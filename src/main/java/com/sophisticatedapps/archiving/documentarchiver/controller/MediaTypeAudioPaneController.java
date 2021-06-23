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
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class MediaTypeAudioPaneController extends BaseController {

    protected static final File AUDIO_FILE_ICON_FILE = (new File(Objects.requireNonNull(App.class
            .getClassLoader().getResource("audio-file-icon.png")).getFile()));

    @FXML
    private VBox mediaTypePane;

    @FXML
    private Node mediaVisualization;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @Override
    public void rampUp(App anApp) {

        super.rampUp(anApp);

        // Listener for when the pane is "closed" (aNewParent == null) -> stop MediaPlayer
        mediaTypePane.parentProperty().addListener((anObs, anOldParent, aNewParent) -> {

            if (Objects.isNull(aNewParent)) {

                MediaPlayer tmpMediaPlayer = getMediaPlayer();

                if((!Objects.isNull(tmpMediaPlayer)) && (tmpMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING)) {

                    tmpMediaPlayer.stop();
                }
            }
        });
    }

    public void setupMediaView(MediaPlayer aMediaPlayer, double aFixWidth) {

        mediaTypePane.setUserData(aMediaPlayer);

        ImageView tmpAudioImageView = (ImageView)getMediaVisualization();

        try (BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(AUDIO_FILE_ICON_FILE))) {

            // Creating the image object
            Image tmpImage = new Image(tmpInputStream);
            tmpAudioImageView.setImage(tmpImage);
        }
        catch (IOException e) {

            // Never mind - no pic.
        }

        tmpAudioImageView.setFitWidth(aFixWidth * 0.75);
    }

    protected Node getMediaVisualization() {

        return mediaVisualization;
    }

    protected MediaPlayer getMediaPlayer() {

        return (MediaPlayer)mediaTypePane.getUserData();
    }

    @FXML
    protected void handlePlayButtonAction() {

        MediaPlayer tmpMediaPlayer = getMediaPlayer();

        if((!Objects.isNull(tmpMediaPlayer)) && (tmpMediaPlayer.getStatus() != MediaPlayer.Status.PLAYING)) {

            tmpMediaPlayer.play();
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            stopButton.setDisable(false);
        }
    }

    @FXML
    protected void handlePauseButtonAction() {

        MediaPlayer tmpMediaPlayer = getMediaPlayer();

        if((!Objects.isNull(tmpMediaPlayer)) && (tmpMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING)) {

            tmpMediaPlayer.pause();
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(false);
        }
    }

    @FXML
    protected void handleStopButtonAction() {

        MediaPlayer tmpMediaPlayer = getMediaPlayer();

        if((!Objects.isNull(tmpMediaPlayer)) && (tmpMediaPlayer.getStatus() != MediaPlayer.Status.STOPPED)) {

            tmpMediaPlayer.stop();
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(true);
        }
    }

}
