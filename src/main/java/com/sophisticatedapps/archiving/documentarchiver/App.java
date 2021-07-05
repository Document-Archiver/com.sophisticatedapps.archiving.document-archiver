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

import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.api.impl.DefaultApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.api.impl.DefaultApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.impl.DefaultDialogProvider;
import com.sophisticatedapps.archiving.documentarchiver.controller.ApplicationController;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    private final ApplicationController applicationController;

    public App() {

        this.applicationController = new ApplicationController((new DefaultApplicationServices()),
                (new DefaultDialogProvider()), getHostServices());
    }

    public App(ApplicationController anApplicationController) {

        this.applicationController = anApplicationController;
    }

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

        Thread.setDefaultUncaughtExceptionHandler(this::showError);
        ApplicationContext tmpApplicationContext = new DefaultApplicationContext(applicationController, aPrimaryStage);

        // Set dimensions
        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        aPrimaryStage.setX(tmpBounds.getMinX());
        aPrimaryStage.setY(tmpBounds.getMinY());
        aPrimaryStage.setWidth(tmpBounds.getWidth());
        aPrimaryStage.setHeight(tmpBounds.getHeight());

        // Create root pane
        BorderPane tmpRootPane = (BorderPane)
                FXMLUtil.loadAndRampUpRegion("view/RootPane.fxml", tmpApplicationContext).getRegion();

        // Check if we received a file to use via command line parameter
        String tmpFirstParameter = getFirstParameter();

        if (!Objects.isNull(tmpFirstParameter)) {

            // Set files from args to stage properties (will trigger the panes)
            try {

                setFilesListToStageProperties(externalPathStringToFilesList(tmpFirstParameter), aPrimaryStage);
            }
            catch (IOException e) {

                showError(Thread.currentThread(), e);
            }
        }

        // Place icons
        applicationController.placeIcons(aPrimaryStage);

        // Show
        Scene tmpScene = new Scene(tmpRootPane);
        ThemeUtil.applyCurrentTheme(tmpScene);
        aPrimaryStage.setScene(tmpScene);
        aPrimaryStage.show();

        // If we didn't receive a file to use via command line parameter, we will show a welcome dialog.
        if (Objects.isNull(tmpFirstParameter)) {

            scheduleWelcomeDialog(aPrimaryStage);
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

    private void scheduleWelcomeDialog(Stage aStage) {

        // We start a new Tread, since we do not want to put the FX-Thread to sleep.
        (new Thread(() -> {

            // Before we show the welcome dialog, we wait 1 sec (in case the StartupListener is called).
            try {

                Thread.sleep(1000);
            }
            catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            // Still no current document set? (StartupListener called?) If not show dialog.
            if (Objects.isNull(aStage.getProperties().get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY))) {

                Platform.runLater(() -> applicationController.showDecideWhatToOpenDialog(aStage, true));
            }
        })).start();
    }

    protected static List<File> externalPathStringToFilesList(String anExternalPathString) throws IOException {

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

    protected static void setFilesListToStageProperties(List<File> aFilesList, Stage aStage) {

        if (!CollectionUtil.isNullOrEmpty(aFilesList)) {

            runLaterOrNowIfOnFXThread(() -> {

                ObservableMap<Object,Object> tmpStageProperties = aStage.getProperties();
                tmpStageProperties.put(GlobalConstants.ALL_DOCUMENTS_PROPERTY_KEY, aFilesList);
                tmpStageProperties.put(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY, aFilesList.get(0));
            });
        }
    }

    protected static void runLaterOrNowIfOnFXThread(Runnable aRunnable) {

        if (Platform.isFxApplicationThread()) {

            aRunnable.run();
        }
        else {

            Platform.runLater(aRunnable);
        }
    }

    protected void showError(Thread aThread, Throwable aThrowable) {

        Throwable tmpCause = aThrowable.getCause();
        String tmpMsg = (aThrowable.getMessage() + " (" +
                ((tmpCause != null) ? tmpCause.getMessage() : "No additional information") + ")");

        if (Platform.isFxApplicationThread()) {

            applicationController.getDialogProvider().provideExceptionAlert(tmpMsg).showAndWait();
        }
        else {

            System.err.println(tmpMsg); // NOSONAR
        }
    }

}
