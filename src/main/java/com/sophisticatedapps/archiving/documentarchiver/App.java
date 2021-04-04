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

import com.sophisticatedapps.archiving.documentarchiver.util.CollectionUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    private static List<File> filesFromArgs;

    /**
     * Main method.
     *
     * @param   args    Command line arguments.
     */
    public static void main(String[] args) {

        if (args.length > 0) {

            File tmpFile = FileUtil.argToFile(args[0]);

            if(!tmpFile.exists()) {

                System.err.println("File does not exist: ".concat(args[0]));
                return;
            }

            if (tmpFile.isDirectory()) {

                // We have to wrap the result in a new List, since the result is not modifiable.
                filesFromArgs = new ArrayList<>(
                        Arrays.asList(Objects.requireNonNull(tmpFile.listFiles(File::isFile))));
            }
            else {

                filesFromArgs = new ArrayList<>();
                filesFromArgs.add(tmpFile);
            }
        }

        launch(args);
    }

    /**
     * Application start method.
     *
     * @param   aPrimaryStage   The primary stage.
     */
    @Override
    public void start(Stage aPrimaryStage) {

        // Set dimensions
        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        aPrimaryStage.setX(tmpBounds.getMinX());
        aPrimaryStage.setY(tmpBounds.getMinY());
        aPrimaryStage.setWidth(tmpBounds.getWidth());
        aPrimaryStage.setHeight(tmpBounds.getHeight());

        // Set stage properties
        ObservableMap<Object, Object> tmpStageProperties = aPrimaryStage.getProperties();
        tmpStageProperties.put(GlobalConstants.HOST_SERVICES_PROPERTY_KEY, this.getHostServices());

        // Create root pane
        BorderPane tmpRootPane =
                (BorderPane)FXMLUtil.loadAndRampUpRegion("view/RootPane.fxml", aPrimaryStage).getRegion();

        // Set files from args to stage properties
        if (!CollectionUtil.isNullOrEmpty(filesFromArgs)) {

            tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, filesFromArgs);
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, filesFromArgs.get(0));
        }
        else {

            // Have to set null to trigger listeners
            tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, null);
        }

        // Place icons
        placeIcons(aPrimaryStage);

        // Show
        aPrimaryStage.setScene(new Scene(tmpRootPane));
        aPrimaryStage.show();
    }

    private static void placeIcons(Stage aStage) {

        // JavaFX Image
        Image tmpIconImage = new Image(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("binder-icon.png")));

        // Set stage icon
        aStage.getIcons().add(tmpIconImage);

        // AWT Image
        final URL imageResource =
                Thread.currentThread().getContextClassLoader().getResource("binder-icon.png");
        final java.awt.Image tmpAwtImage = Toolkit.getDefaultToolkit().getImage(imageResource);

        // Set taskbar icon (may not be supported on all systems (e.g. Linux))
        try {

            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(tmpAwtImage);
        }
        catch (UnsupportedOperationException e) {

            // never mind.
        }
    }

}
