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

import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    private static DialogProvider dialogProvider = new DialogProvider();

    /**
     * Main method.
     *
     * @param   args    Command line arguments.
     */
    public static void main(String[] args) {

        launch(args);
    }

    /**
     * Application start method.
     *
     * @param   aPrimaryStage   The primary stage.
     */
    @Override
    public void start(Stage aPrimaryStage) {

        Thread.setDefaultUncaughtExceptionHandler(App::showError);

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

        // Check if we received a file to use via command line parameter
        String tmpFirstParameter = getFirstParameter();

        if (!Objects.isNull(tmpFirstParameter)) {

            // Set files from args to stage properties (will trigger the panes)
            try {

                setFilesListToStageProperties(externalPathStringToFilesList(tmpFirstParameter), tmpStageProperties);
            }
            catch (IOException e) {

                showError(Thread.currentThread(), e);
            }
        }

        // Place icons
        placeIcons(aPrimaryStage);

        // Show
        Scene tmpScene = new Scene(tmpRootPane);
        ThemeUtil.applyCurrentTheme(tmpScene);
        aPrimaryStage.setScene(tmpScene);
        aPrimaryStage.show();
    }

    private static void placeIcons(Stage aStage) {

        // Set stage icon
        aStage.getIcons().add(GlobalConstants.APP_ICON);

        // Set taskbar icon (may not be supported on all systems (e.g. Linux))
        try {

            // AWT Image
            final URL imageResource =
                    Thread.currentThread().getContextClassLoader().getResource("binder-icon.png");
            final java.awt.Image tmpAwtImage = Toolkit.getDefaultToolkit().getImage(imageResource);

            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(tmpAwtImage);
        }
        catch (UnsupportedOperationException | UnsatisfiedLinkError e) {

            // never mind.
        }
    }

    private String getFirstParameter() {

        List<String> tmpRawParameterList = getParameters().getRaw();

        if (!tmpRawParameterList.isEmpty()) {

            String tmpFirstParameter = tmpRawParameterList.get(0);

            if (!StringUtil.isNullOrEmpty(tmpFirstParameter)) {

                return tmpFirstParameter;
            }
        }

        return null;
    }

    private static List<File> externalPathStringToFilesList(String anExternalPathString) throws IOException {

        File tmpFile = FileUtil.argToFile(anExternalPathString);

        if(tmpFile.exists()) {

            List<File> tmpReturn = new ArrayList<>();

            if (tmpFile.isDirectory()) {

                DirectoryUtil.readDirectoryRecursive(tmpFile, tmpReturn, DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);
                tmpReturn.sort(Comparator.naturalOrder());
            }
            else {

                tmpReturn.add(tmpFile);
            }

            return tmpReturn;
        }
        else {

            throw (new IOException("File does not exist: ".concat(anExternalPathString)));
        }
    }

    private void setFilesListToStageProperties(List<File> aFilesList, ObservableMap<Object, Object> aStageProperties) {

        if (!CollectionUtil.isNullOrEmpty(aFilesList)) {

            aStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aFilesList);
            aStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aFilesList.get(0));
        }
    }

    protected static void showError(Thread aThread, Throwable aThrowable) {

        Throwable tmpCause = aThrowable.getCause();
        String tmpMsg = (aThrowable.getMessage() + " (" +
                ((tmpCause != null) ? tmpCause.getMessage() : "No additional information") + ")");

        if (Platform.isFxApplicationThread()) {

            dialogProvider.provideExceptionAlert(tmpMsg).showAndWait();
        }
        else {

            System.err.println(tmpMsg); // NOSONAR
        }
    }

    protected static class DialogProvider {

        public Alert provideExceptionAlert(String aMsg) {

            return (new Alert(Alert.AlertType.ERROR, aMsg, ButtonType.CLOSE));
        }
    }

}
