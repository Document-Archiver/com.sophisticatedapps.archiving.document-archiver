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

public class PreferencesPane extends VBox {

    private Label archivingFolderLabel;
    private TextField archivingFolderTextField;
    private Label quickDescriptionWordsLabel;
    private TextArea quickDescriptionWordsTextArea;

    public PreferencesPane(Label anArchivingFolderLabel, TextField anArchivingFolderTextField,
                           Label aQuickDescriptionWordsLabel, TextArea aQuickDescriptionWordsTextArea) {

        this.archivingFolderLabel = anArchivingFolderLabel;
        this.archivingFolderTextField = anArchivingFolderTextField;
        this.quickDescriptionWordsLabel = aQuickDescriptionWordsLabel;
        this.quickDescriptionWordsTextArea = aQuickDescriptionWordsTextArea;

        this.getChildren().addAll(archivingFolderLabel, archivingFolderTextField, quickDescriptionWordsLabel,
                quickDescriptionWordsTextArea);
    }

    public Label getArchivingFolderLabel() {
        return archivingFolderLabel;
    }

    public TextField getArchivingFolderTextField() {
        return archivingFolderTextField;
    }

    public Label getQuickDescriptionWordsLabel() {
        return quickDescriptionWordsLabel;
    }

    public TextArea getQuickDescriptionWordsTextArea() {
        return quickDescriptionWordsTextArea;
    }

}
