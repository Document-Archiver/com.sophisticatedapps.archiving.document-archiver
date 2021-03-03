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

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MenuBarBuilder {

    private Menu documentArchiverMenu;
    private Menu fileMenu;
    private Menu helpMenu;

    public MenuBarBuilder documentArchiverMenu(Menu aMenu) {

        this.documentArchiverMenu = aMenu;
        return this;
    }

    public MenuBarBuilder fileMenu(Menu aMenu) {

        this.fileMenu = aMenu;
        return this;
    }

    public MenuBarBuilder helpMenu(Menu aMenu) {

        this.helpMenu = aMenu;
        return this;
    }

    public MenuBar build() {

        if (documentArchiverMenu == null) {

            throw (new RuntimeException("documentArchiverMenu not set."));
        }
        if (fileMenu == null) {

            throw (new RuntimeException("fileMenu not set."));
        }
        if (helpMenu == null) {

            throw (new RuntimeException("helpMenu not set."));
        }

        return (new MenuBar(documentArchiverMenu, fileMenu, helpMenu));
    }

}
