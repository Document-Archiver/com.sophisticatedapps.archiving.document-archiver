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
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.File;

public class DocumentsPaneBuilder {

    private Label documentsLabel;
    private ListView<File> documentsListView;

    public DocumentsPaneBuilder documentsLabel(Label aLabel) {

        this.documentsLabel = aLabel;
        return this;
    }

    public DocumentsPaneBuilder documentsListView(ListView<File> aListView) {

        this.documentsListView = aListView;
        return this;
    }

    public VBox build() {

        if (documentsLabel == null) {
            throw (new RuntimeException("documentsLabel not set."));
        }
        if (documentsListView == null) {
            throw (new RuntimeException("documentsListView not set."));
        }

        VBox tmpInfoPane = new VBox();
        tmpInfoPane.getChildren().addAll(documentsLabel, documentsListView);

        return tmpInfoPane;
    }

}
