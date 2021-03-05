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

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PreferencesPaneBuilder {

    private Label archivingFolderLabel;
    private TextField archivingFolderTextField;
    private Label quickDescriptionWordsLabel;
    private TextArea quickDescriptionWordsTextArea;

    public PreferencesPaneBuilder archivingFolderLabel(Label aLabel) {

        this.archivingFolderLabel = aLabel;
        return this;
    }

    public PreferencesPaneBuilder archivingFolderTextField(TextField aTextField) {

        this.archivingFolderTextField = aTextField;
        return this;
    }

    public PreferencesPaneBuilder quickDescriptionWordsLabel(Label aLabel) {

        this.quickDescriptionWordsLabel = aLabel;
        return this;
    }

    public PreferencesPaneBuilder quickDescriptionWordsTextArea(TextArea aTextArea) {

        this.quickDescriptionWordsTextArea = aTextArea;
        return this;
    }

    public PreferencesPane build() {

        if (archivingFolderLabel == null) {
            throw (new RuntimeException("archivingFolderLabel not set."));
        }
        if (archivingFolderTextField == null) {
            throw (new RuntimeException("archivingFolderTextField not set."));
        }
        if (quickDescriptionWordsLabel == null) {
            throw (new RuntimeException("quickDescriptionWordsLabel not set."));
        }
        if (quickDescriptionWordsTextArea == null) {
            throw (new RuntimeException("quickDescriptionWordsTextArea not set."));
        }

        return (new PreferencesPane(archivingFolderLabel, archivingFolderTextField,
                quickDescriptionWordsLabel, quickDescriptionWordsTextArea));
    }

}
