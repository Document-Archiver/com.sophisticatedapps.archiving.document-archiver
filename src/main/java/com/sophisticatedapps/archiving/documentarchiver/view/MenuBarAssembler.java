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
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

            Alert alert = new Alert(Alert.AlertType.NONE, "TODO", ButtonType.CLOSE);
            alert.showAndWait();
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
