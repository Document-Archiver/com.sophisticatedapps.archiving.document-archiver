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

package com.sophisticatedapps.archiving.documentarchiver.view;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MenuBarAssembler {

    private OpenNewFileCallback openNewFileCallback;

    public MenuBarAssembler openNewFileCallback(OpenNewFileCallback anOpenNewFileCallback) {

        this.openNewFileCallback = anOpenNewFileCallback;
        return this;
    }

    public MenuBar assemble(Stage aStage) {

        if (openNewFileCallback == null) {

            throw (new RuntimeException("openNewFileCallback not set."));
        }

        Menu tmpDocumentArchiverMenu = new Menu("DocumentArchiver");
        tmpDocumentArchiverMenu.getItems().addAll(
                assembleAboutMenuItem(),
                (new SeparatorMenuItem()),
                assemblePreferencesMenuItem(),
                (new SeparatorMenuItem()),
                assembleQuitMenuItem());

        Menu tmpFileMenu = new Menu("File");
        tmpFileMenu.getItems().add(assembleOpenNewFileMenuItem(aStage));

        Menu tmpHelpMenu = new Menu("Help");
        tmpHelpMenu.getItems().add(assembleHelpMenuItem(aStage));

        return (new MenuBarBuilder())
                .documentArchiverMenu(tmpDocumentArchiverMenu)
                .fileMenu(tmpFileMenu)
                .helpMenu(tmpHelpMenu)
                .build();
    }

    private MenuItem assembleAboutMenuItem() {

        MenuItem tmpAboutMenuItem = new MenuItem("About DocumentArchiver");

        tmpAboutMenuItem.setOnAction(actionEvent -> {

            Alert alert = new Alert(Alert.AlertType.NONE, "Copyright 2021 by Stephan Sann", ButtonType.CLOSE);
            alert.showAndWait();
        });

        return tmpAboutMenuItem;
    }

    private MenuItem assemblePreferencesMenuItem() {

        MenuItem tmpAboutMenuItem = new MenuItem("Preferences...");

        tmpAboutMenuItem.setOnAction(actionEvent -> {

            PreferencesPane tmpPreferencesPane = (new PreferencesPaneAssembler().assemble());

            Dialog<Pair<String, String>> tmpDialog = new Dialog<>();
            tmpDialog.setTitle("Preferences");
            tmpDialog.setHeaderText("Change your archiving folder and/or your quick description words.");

            ButtonType tmpOkButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            tmpDialog.getDialogPane().getButtonTypes().addAll(tmpOkButtonType, ButtonType.CANCEL);

            tmpDialog.setResultConverter(aDialogButton -> {
                if (aDialogButton == tmpOkButtonType) {
                    return (new Pair<>(tmpPreferencesPane.getArchivingFolderTextField().getText(),
                            tmpPreferencesPane.getQuickDescriptionWordsTextArea().getText()));
                }
                return null;
            });

            tmpDialog.getDialogPane().setContent(tmpPreferencesPane);

            Optional<Pair<String, String>> tmpResult = tmpDialog.showAndWait();

            tmpResult.ifPresent(anResultPair -> {
                Properties tmpProperties = new Properties();
                tmpProperties.setProperty("archiving.path", anResultPair.getKey());
                tmpProperties.setProperty("quick.description.words",
                        StringUtil.cleanQuickDescriptionWordsString(anResultPair.getValue()));
                try {
                    FileUtil.writeProperties(GlobalConstants.PROPERTIES_FILE, tmpProperties);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Preferences have been saved and will be active with the next start of DocumentArchiver.",
                            ButtonType.CLOSE);
                    alert.showAndWait();
                }
                catch (IOException e) {
                    throw (new RuntimeException("Could not write properties: ".concat(String.valueOf(e.getMessage()))));
                }
            });
        });

        return tmpAboutMenuItem;
    }

    private MenuItem assembleQuitMenuItem() {

        MenuItem tmpQuitMenuItem = new MenuItem("Quit DocumentArchiver");

        tmpQuitMenuItem.setOnAction(actionEvent -> Platform.exit());

        return tmpQuitMenuItem;
    }

    private MenuItem assembleOpenNewFileMenuItem(Stage aStage) {

        MenuItem tmpAboutMenuItem = new MenuItem("Open File(s) Or Directory...");

        tmpAboutMenuItem.setOnAction(actionEvent -> this.openNewFileCallback.call(aStage));

        return tmpAboutMenuItem;
    }

    private MenuItem assembleHelpMenuItem(Stage aStage) {

        MenuItem tmpHelpMenuItem = new MenuItem("DocumentArchiver Help");

        tmpHelpMenuItem.setOnAction(actionEvent -> {

            HostServices tmpHostServices =
                    (HostServices)aStage.getProperties().get(GlobalConstants.HOST_SERVICES_PROPERTY_KEY);
            tmpHostServices.showDocument(GlobalConstants.WIKI_URL);
        });

        return tmpHelpMenuItem;
    }

    public interface OpenNewFileCallback {

        void call(Stage aStage);
    }

}
