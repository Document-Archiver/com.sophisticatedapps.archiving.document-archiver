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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class InfoPaneBuilder {

    private Label selectDateLabel;
    private DatePicker datePicker;
    private CheckBox utilizeTimeInformationCheckBox;
    private TextField timeInformationTextField;
    private Label defineDescriptionLabel;
    private ComboBox<String> quickDescriptionWordsComboBox;
    private TextField descriptionTextField;
    private Label selectTagsLabel;
    private TextField tagsTextField;
    private Node tagsPane;
    private CheckBox takeOverDescriptionAndTagsCheckBox;
    private Button submitButton;

    public InfoPaneBuilder selectDateLabel(Label aLabel) {

        this.selectDateLabel = aLabel;
        return this;
    }

    public InfoPaneBuilder datePicker(DatePicker aDatePicker) {

        this.datePicker = aDatePicker;
        return this;
    }

    public InfoPaneBuilder utilizeTimeInformationCheckBox(CheckBox aCheckBox) {

        this.utilizeTimeInformationCheckBox = aCheckBox;
        return this;
    }

    public InfoPaneBuilder timeInformationTextField(TextField aTimeInformationTextField) {

        this.timeInformationTextField = aTimeInformationTextField;
        return this;
    }

    public InfoPaneBuilder defineDescriptionLabel(Label aLabel) {

        this.defineDescriptionLabel = aLabel;
        return this;
    }

    public InfoPaneBuilder quickDescriptionWordsComboBox(ComboBox<String> aComboBox) {

        this.quickDescriptionWordsComboBox = aComboBox;
        return this;
    }

    public InfoPaneBuilder descriptionTextField(TextField aTextField) {

        this.descriptionTextField = aTextField;
        return this;
    }

    public InfoPaneBuilder selectTagsLabel(Label aLabel) {

        this.selectTagsLabel = aLabel;
        return this;
    }

    public InfoPaneBuilder tagsTextField(TextField aTextField) {

        this.tagsTextField = aTextField;
        return this;
    }

    public InfoPaneBuilder tagsPane(Node aTagsPane) {

        this.tagsPane = aTagsPane;
        return this;
    }

    public InfoPaneBuilder takeOverDescriptionAndTagsCheckBox(CheckBox aCheckBox) {

        this.takeOverDescriptionAndTagsCheckBox = aCheckBox;
        return this;
    }

    public InfoPaneBuilder submitButton(Button aButton) {

        this.submitButton = aButton;
        return this;
    }

    public VBox build() {

        if (selectDateLabel == null) {
            throw (new RuntimeException("selectDateLabel not set."));
        }
        if (datePicker == null) {
            throw (new RuntimeException("datePicker not set."));
        }
        if (utilizeTimeInformationCheckBox == null) {
            throw (new RuntimeException("utilizeTimeInformationCheckBox not set."));
        }
        if (timeInformationTextField == null) {
            throw (new RuntimeException("timeInformationTextField not set."));
        }
        if (defineDescriptionLabel == null) {
            throw (new RuntimeException("defineDescriptionLabel not set."));
        }
        if (quickDescriptionWordsComboBox == null) {
            throw (new RuntimeException("quickDescriptionWordsComboBox not set."));
        }
        if (descriptionTextField == null) {
            throw (new RuntimeException("descriptionTextField not set."));
        }
        if (selectTagsLabel == null) {
            throw (new RuntimeException("selectTagsLabel not set."));
        }
        if (tagsTextField == null) {
            throw (new RuntimeException("tagsTextField not set."));
        }
        if (tagsPane == null) {
            throw (new RuntimeException("tagsPane not set."));
        }
        if (takeOverDescriptionAndTagsCheckBox == null) {
            throw (new RuntimeException("takeOverDescriptionAndTagsCheckBox not set."));
        }
        if (submitButton == null) {
            throw (new RuntimeException("submitButton not set."));
        }

        VBox tmpInfoPane = new VBox();
        ObservableList<Node> tmpChildren = tmpInfoPane.getChildren();
        tmpChildren.add(selectDateLabel);
        tmpChildren.add(datePicker);
        tmpChildren.add(utilizeTimeInformationCheckBox);
        tmpChildren.add(timeInformationTextField);
        tmpChildren.add(defineDescriptionLabel);
        tmpChildren.add(quickDescriptionWordsComboBox);
        tmpChildren.add(descriptionTextField);
        tmpChildren.add(selectTagsLabel);
        tmpChildren.add(tagsTextField);
        tmpChildren.add(tagsPane);
        tmpChildren.add(takeOverDescriptionAndTagsCheckBox);
        tmpChildren.add(submitButton);

        return tmpInfoPane;
    }

}
