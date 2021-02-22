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

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TriplePane extends HBox {

    private final VBox documentsPane;
    private final Pane fileDisplayPane;
    private final VBox infoPane;

    public TriplePane(VBox aDocumentsPane, Pane aFileDisplayPane, VBox anInfoPane) {

        this.documentsPane = aDocumentsPane;
        this.fileDisplayPane = aFileDisplayPane;
        this.infoPane = anInfoPane;

        this.getChildren().addAll(aDocumentsPane, aFileDisplayPane, anInfoPane);
    }

    public VBox getDocumentsPane() {
        return documentsPane;
    }

    public Pane getFileDisplayPane() {
        return fileDisplayPane;
    }

    public VBox getInfoPane() {
        return infoPane;
    }

}
