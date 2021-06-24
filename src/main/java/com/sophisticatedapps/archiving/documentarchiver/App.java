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

import com.restart4j.ApplicationRestart;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationServices;
import com.sophisticatedapps.archiving.documentarchiver.api.DialogProvider;
import com.sophisticatedapps.archiving.documentarchiver.util.*;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class App extends Application {

    private final ApplicationServices applicationServices;
    private final DialogProvider dialogProvider;

    private Stage primaryStage;

    public App() {

        this(null, null);
    }

    public App(ApplicationServices anApplicationServices) {

        this(anApplicationServices, null);
    }

    public App(DialogProvider aDialogProvider) {

        this(null, aDialogProvider);
    }

    public App(ApplicationServices anApplicationServices, DialogProvider aDialogProvider) {

        this.applicationServices =
                (Objects.isNull(anApplicationServices) ? (new DefaultApplicationServices()) : anApplicationServices);
        this.dialogProvider = (Objects.isNull(aDialogProvider) ? (new DefaultDialogProvider()) : aDialogProvider);
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

        primaryStage = aPrimaryStage;
        Thread.setDefaultUncaughtExceptionHandler(this::showError);

        // Set dimensions
        Rectangle2D tmpBounds = Screen.getPrimary().getVisualBounds();
        aPrimaryStage.setX(tmpBounds.getMinX());
        aPrimaryStage.setY(tmpBounds.getMinY());
        aPrimaryStage.setWidth(tmpBounds.getWidth());
        aPrimaryStage.setHeight(tmpBounds.getHeight());

        // Create root pane
        BorderPane tmpRootPane = (BorderPane)FXMLUtil.loadAndRampUpRegion("view/RootPane.fxml",
                (new DefaultApplicationContext(applicationServices, dialogProvider, this.getHostServices(),
                        aPrimaryStage))).getRegion();

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
        placeIcons();

        // Show
        Scene tmpScene = new Scene(tmpRootPane);
        ThemeUtil.applyCurrentTheme(tmpScene);
        aPrimaryStage.setScene(tmpScene);
        aPrimaryStage.show();

        // If we didn't receive a file to use via command line parameter, we will show a welcome dialog.
        if (Objects.isNull(tmpFirstParameter)) {

            scheduleWelcomeDialog();
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

    private void placeIcons() {

        // Set stage icon
        primaryStage.getIcons().add(GlobalConstants.APP_ICON);

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

    private void scheduleWelcomeDialog() {

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
            if (Objects.isNull(primaryStage.getProperties().get(GlobalConstants.CURRENT_DOCUMENT_PROPERTY_KEY))) {

                Platform.runLater(this::showWelcomeDialog);
            }
        })).start();
    }

    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    private void showWelcomeDialog() {

        Optional<ButtonType> tmpResult = dialogProvider.provideWelcomeDialog().showAndWait();

        // ButtonData.NO means open a directory, YES means open (multiple) file(s).
        if (ButtonBar.ButtonData.NO == tmpResult.get().getButtonData()) { // NOSONAR

            File tmpDirectory = applicationServices.requestDirectorySelection(primaryStage);

            if (!Objects.isNull(tmpDirectory)) {

                List<File> tmpWrapperList = new ArrayList<>();
                DirectoryUtil.readDirectoryRecursive(
                        tmpDirectory, tmpWrapperList, DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);

                if (!tmpWrapperList.isEmpty()) {

                    tmpWrapperList.sort(Comparator.naturalOrder());
                    setFilesListToStageProperties(tmpWrapperList, primaryStage);
                }
                else {

                    dialogProvider.provideDirectoryDoesNotContainFilesAlert().showAndWait();
                }
            }
        }
        else {

            List<File> tmpFilesList = applicationServices.requestMultipleFilesSelection(primaryStage);

            if (!CollectionUtil.isNullOrEmpty(tmpFilesList)) {

                // We have to wrap the result in a new List, since the given List may not be modifiable.
                List<File> tmpWrapperList = new ArrayList<>(tmpFilesList);
                tmpWrapperList.sort(Comparator.naturalOrder());
                setFilesListToStageProperties(tmpWrapperList, primaryStage);
            }
        }
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

            dialogProvider.provideExceptionAlert(tmpMsg).showAndWait();
        }
        else {

            System.err.println(tmpMsg); // NOSONAR
        }
    }

    protected static class DefaultDialogProvider implements DialogProvider {

        @Override
        public Dialog<ButtonType> provideWelcomeDialog() {

            ImageView tmpImageView = new ImageView(GlobalConstants.APP_ICON);
            tmpImageView.setFitWidth(80);
            tmpImageView.setFitHeight(80);

            ButtonType tmpOpenFilesButtonType = new ButtonType(
                    LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-files-button.text"),
                    ButtonBar.ButtonData.YES);
            ButtonType tmpOpenDirectoryButtonType = new ButtonType(
                    LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.open-directory-button.text"),
                    ButtonBar.ButtonData.NO);

            Dialog<ButtonType> tmpDialog = new Dialog<>();
            tmpDialog.setGraphic(tmpImageView);
            tmpDialog.setTitle(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.title"));
            tmpDialog.setHeaderText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.header-text"));
            tmpDialog.setContentText(LanguageUtil.i18n("base-controller.dialog-provider.welcome-dialog.content-text"));
            tmpDialog.getDialogPane().getButtonTypes().addAll(tmpOpenFilesButtonType, tmpOpenDirectoryButtonType);

            return tmpDialog;
        }

        @Override
        public Alert provideDirectoryDoesNotContainFilesAlert() {

            return (new Alert(Alert.AlertType.WARNING,
                    LanguageUtil.i18n("menu-bar-controller.dialog-provider.directory-does-not-contain-files-alert"),
                    ButtonType.CLOSE));
        }

        @Override
        public Alert provideExceptionAlert(String aMsg) {

            return (new Alert(Alert.AlertType.ERROR, aMsg, ButtonType.CLOSE));
        }
    }

    protected static class DefaultApplicationContext implements ApplicationContext {

        private final ApplicationServices applicationServices;
        private final DialogProvider dialogProvider;
        private final HostServices hostServices;
        private final Stage primaryStage;

        public DefaultApplicationContext(ApplicationServices anApplicationServices, DialogProvider aDialogProvider,
                                         HostServices aHostServices, Stage aPrimaryStage) {

            applicationServices = anApplicationServices;
            dialogProvider = aDialogProvider;
            hostServices = aHostServices;
            primaryStage = aPrimaryStage;
        }

        @Override
        public ApplicationServices getApplicationServices() {

            return applicationServices;
        }

        @Override
        public DialogProvider getDialogProvider() {

            return dialogProvider;
        }

        @Override
        public HostServices getHostServices() {

            return hostServices;
        }

        @Override
        public Stage getPrimaryStage() {

            return primaryStage;
        }
    }

    protected static class DefaultApplicationServices implements ApplicationServices {

        private static final DirectoryChooser DIRECTORY_CHOOSER = new DirectoryChooser();
        private static final FileChooser FILE_CHOOSER = new FileChooser();

        @Override
        public File requestDirectorySelection(Stage aStage) {

            return DIRECTORY_CHOOSER.showDialog(aStage);
        }

        @Override
        public List<File> requestMultipleFilesSelection(Stage aStage) {

            return FILE_CHOOSER.showOpenMultipleDialog(aStage);
        }

        @Override
        public void restartApp() {

            ApplicationRestart.builder().build().restartApp();
        }
    }

}
