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

package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.App;
import com.sophisticatedapps.archiving.documentarchiver.controller.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class FXMLUtil {

    /**
     * Private constructor.
     */
    private FXMLUtil() {
    }

    /**
     * Loads a Region object and ramps up its controller.
     *
     * @param   aFxmlResource   FXML-resource to load.
     * @param   aStage          Stage to set to the controller.
     * @return  A Region object.
     */
    public static Region loadAndRampUpRegion(String aFxmlResource, Stage aStage) {

        try {

            FXMLLoader tmpRegionLoader = new FXMLLoader(App.class.getResource(aFxmlResource));

            Region tmpRegion = tmpRegionLoader.load();
            BaseController tmpBaseController = tmpRegionLoader.getController();

            if (tmpBaseController != null) {

                tmpBaseController.rampUp(aStage);
            }

            return tmpRegion;
        }
        catch (IOException e) {

            throw (new RuntimeException("Couldn't load region '" + aFxmlResource + "': " + e.getMessage()));
        }
    }

}
