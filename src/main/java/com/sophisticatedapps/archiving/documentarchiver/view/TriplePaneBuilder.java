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

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TriplePaneBuilder {

    private VBox documentsPane;
    private Pane fileDisplayPane;
    private VBox infoPane;

    public TriplePaneBuilder documentsPane(VBox aPane) {

        this.documentsPane = aPane;
        return this;
    }

    public TriplePaneBuilder fileDisplayPane(Pane aPane) {

        this.fileDisplayPane = aPane;
        return this;
    }

    public TriplePaneBuilder infoPane(VBox aPane) {

        this.infoPane = aPane;
        return this;
    }

    public TriplePane build() {

        if (documentsPane == null) {

            throw (new RuntimeException("documentsPane not set."));
        }
        if (fileDisplayPane == null) {

            throw (new RuntimeException("fileDisplayPane not set."));
        }
        if (infoPane == null) {

            throw (new RuntimeException("infoPane not set."));
        }

        return (new TriplePane(documentsPane, fileDisplayPane, infoPane));
    }

}
