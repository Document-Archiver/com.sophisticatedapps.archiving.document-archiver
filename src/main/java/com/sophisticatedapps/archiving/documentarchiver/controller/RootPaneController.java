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

import com.sophisticatedapps.archiving.documentarchiver.util.FXMLUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.LanguageUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RootPaneController extends BaseController {

    private final List<BaseController> paneControllerList = new ArrayList<>();
    private final List<ChangeListener<Number>> stageWidthPropertyListenersList = new ArrayList<>();
    private final List<ChangeListener<Number>> stageHeightPropertyListenersList = new ArrayList<>();

    private MenuBarController menuBarController;

    private Pane documentsPane;
    private Pane displayFilePane;
    private Pane infoPane;
    private Pane dragAndDropPane;

    @FXML
    private BorderPane rootPane;

    @Override
    public void rampUp(Stage aStage) {

        super.rampUp(aStage);

        // Load pane's sub-panes
        FXMLUtil.ControllerRegionPair<MenuBarController,MenuBar> tmpMenuBarControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/MenuBar.fxml", stage);
        FXMLUtil.ControllerRegionPair<DocumentsPaneController,Pane> tmpDocumentsPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/DocumentsPane.fxml", stage);
        FXMLUtil.ControllerRegionPair<DisplayFilePaneController,Pane> tmpDisplayFilePaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/DisplayFilePane.fxml", stage);
        FXMLUtil.ControllerRegionPair<InfoPaneController,Pane> tmpInfoPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/InfoPane.fxml", stage);
        FXMLUtil.ControllerRegionPair<InfoPaneController,Pane> tmpDragAndDropPaneControllerRegionPair =
                FXMLUtil.loadAndRampUpRegion("view/DragAndDropPane.fxml", stage);

        menuBarController = tmpMenuBarControllerRegionPair.getController();

        documentsPane = tmpDocumentsPaneControllerRegionPair.getRegion();
        displayFilePane = tmpDisplayFilePaneControllerRegionPair.getRegion();
        infoPane = tmpInfoPaneControllerRegionPair.getRegion();
        dragAndDropPane = tmpDragAndDropPaneControllerRegionPair.getRegion();

        // Remember the controller for later
        paneControllerList.add(menuBarController);
        paneControllerList.add(tmpDocumentsPaneControllerRegionPair.getController());
        paneControllerList.add(tmpDisplayFilePaneControllerRegionPair.getController());
        paneControllerList.add(tmpInfoPaneControllerRegionPair.getController());
        paneControllerList.add(tmpDragAndDropPaneControllerRegionPair.getController());

        // Set MenuBar
        rootPane.setTop(tmpMenuBarControllerRegionPair.getRegion());

        // Set dimensions
        setWidths();
        setHeights();

        // Add change listeners
        final ChangeListener<Number> tmpStageWidthPropertyListener =
                ((anObservable, anOldValue, aNewValue) -> setWidths());
        stageWidthPropertyListenersList.add(tmpStageWidthPropertyListener);
        aStage.widthProperty().addListener(tmpStageWidthPropertyListener);

        final ChangeListener<Number> tmpStageHeightPropertyListener =
                ((anObservable, anOldValue, aNewValue) -> setHeights());
        stageHeightPropertyListenersList.add(tmpStageHeightPropertyListener);
        aStage.heightProperty().addListener(tmpStageHeightPropertyListener);

        addCurrentDocumentChangedListener(aChange -> handleCurrentDocumentChanged((File)aChange.getValueAdded()));

        // Initialize panes like current document would have been set to NULL
        handleCurrentDocumentChanged(null);
    }

    @Override
    public void rampDown() {

        // Ramp down all Pane controllers
        for (BaseController tmpCurrentController : paneControllerList) {
            tmpCurrentController.rampDown();
        }

        // Remove width and height listeners
        ReadOnlyDoubleProperty tmpStageWidthProperty = stage.widthProperty();
        for (ChangeListener<Number> tmpCurrentListener : stageWidthPropertyListenersList) {
            tmpStageWidthProperty.removeListener(tmpCurrentListener);
        }
        stageWidthPropertyListenersList.clear();

        ReadOnlyDoubleProperty tmpStageHeightProperty = stage.heightProperty();
        for (ChangeListener<Number> tmpCurrentListener : stageHeightPropertyListenersList) {
            tmpStageHeightProperty.removeListener(tmpCurrentListener);
        }
        stageHeightPropertyListenersList.clear();

        // Do this after we did our own ramp down. Because stage will be set to null there.
        super.rampDown();
    }

    private void setWidths() {

        // Windows has a quirky bug, which reports the stage 16px wider than it is. Deduct 16px - doesn't hurt.
        final double tmpStageWidthTenth = ((stage.getWidth() - 16) / 10);
        documentsPane.setPrefWidth(tmpStageWidthTenth * 2);
        displayFilePane.setPrefWidth(tmpStageWidthTenth * 5);
        infoPane.setPrefWidth(tmpStageWidthTenth * 3);
    }

    private void setHeights() {

        final double aStageHeightMinus50 = (stage.getHeight() - 50);
        documentsPane.setPrefHeight(aStageHeightMinus50);
        displayFilePane.setPrefHeight(aStageHeightMinus50);
        infoPane.setPrefHeight(aStageHeightMinus50);
    }

    /**
     * Method to call when the current document changed.
     *
     * @param   aNewCurrentDocument The new current document.
     */
    private void handleCurrentDocumentChanged(File aNewCurrentDocument) {

        if (!Objects.isNull(aNewCurrentDocument)) {

            stage.setTitle(LanguageUtil.i18n("root-pane-controller.stage.title.archiving-now",
                    aNewCurrentDocument.getPath(), PropertiesUtil.ACTIVE_TENANT.getDisplayName()));

            rootPane.setLeft(documentsPane);
            rootPane.setCenter(displayFilePane);
            rootPane.setRight(infoPane);
        }
        else {

            stage.setTitle(LanguageUtil.i18n("root-pane-controller.stage.title.choose-files"));
            rootPane.setLeft(null);
            rootPane.setCenter(dragAndDropPane);
            rootPane.setRight(null);
        }
    }

    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    private void showWelcomeDialog() {

        Optional<ButtonType> tmpResult = dialogProvider.provideWelcomeDialog().showAndWait();

        // ButtonData.NO means open a directory, YES means open a file.
        if (ButtonBar.ButtonData.NO == tmpResult.get().getButtonData()) { // NOSONAR

            menuBarController.handleOpenDirectoryMenuItemAction();
        }
        else {

            menuBarController.handleOpenFilesMenuItemAction();
        }
    }

}
