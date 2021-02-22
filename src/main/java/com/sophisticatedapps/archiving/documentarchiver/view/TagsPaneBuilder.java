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

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class TagsPaneBuilder {

    private ListView<String> existingTagsListView;
    private Node middlePane;
    private ListView<String> selectedTagsListView;

    public TagsPaneBuilder existingTagsListView(ListView<String> aListView) {

        this.existingTagsListView = aListView;
        return this;
    }

    public TagsPaneBuilder middlePane(Node aPane) {

        this.middlePane = aPane;
        return this;
    }

    public TagsPaneBuilder selectedTagsListView(ListView<String> aListView) {

        this.selectedTagsListView = aListView;
        return this;
    }

    public Pane build() {

        if (existingTagsListView == null) {

            throw (new RuntimeException("existingTagsListView not set."));
        }
        if (middlePane == null) {

            throw (new RuntimeException("middlePane not set."));
        }
        if (selectedTagsListView == null) {

            throw (new RuntimeException("selectedTagsListView not set."));
        }

        HBox tmpRootPane = new HBox();
        tmpRootPane.getChildren().add(existingTagsListView);
        tmpRootPane.getChildren().add(middlePane);
        tmpRootPane.getChildren().add(selectedTagsListView);

        return tmpRootPane;
    }

}
