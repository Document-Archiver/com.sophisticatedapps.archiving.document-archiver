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

import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PreferencesPaneController extends BaseController {

    @FXML
    private TextField archivingFolderTextField;

    @FXML
    private TextArea quickDescriptionWordsTextArea;

    @Override
    public void rampUp(ApplicationContext anApplicationContext) {

        super.rampUp(anApplicationContext);

        archivingFolderTextField.setText(DirectoryUtil.getCoreArchivingFolder().getPath());
        quickDescriptionWordsTextArea.setText(PropertiesUtil.QUICK_DESCRIPTION_WORDS);
    }

    public String getArchivingFolder() {

        return archivingFolderTextField.getText();
    }

    public String getQuickDescriptionWords() {

        return StringUtil.cleanQuickDescriptionWordsString(quickDescriptionWordsTextArea.getText());
    }

}
