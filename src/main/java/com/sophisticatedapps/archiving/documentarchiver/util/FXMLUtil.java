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
     * @return  A ControllerRegionPair object.
     */
    public static <C extends BaseController,R> ControllerRegionPair<C,R> loadAndRampUpRegion(String aFxmlResource, Stage aStage) {

        try {

            FXMLLoader tmpRegionLoader = new FXMLLoader(App.class.getResource(aFxmlResource));

            R tmpRegion = tmpRegionLoader.load();
            C tmpBaseController = tmpRegionLoader.getController();

            if (tmpBaseController != null) {

                tmpBaseController.rampUp(aStage);
            }

            return (new ControllerRegionPair<>(tmpBaseController, tmpRegion));
        }
        catch (IOException | IllegalStateException e) {

            throw (new RuntimeException("Couldn't load region '" + aFxmlResource + "': " + e.getMessage()));
        }
    }

    public static class ControllerRegionPair<C,R> {

        private final C controller;
        private final R region;

        /**
         * Constructor.
         *
         * @param   aController Controller to set.
         * @param   aRegion     Region to set.
         */
        public ControllerRegionPair(C aController, R aRegion) {

            this.controller = aController;
            this.region = aRegion;
        }

        public C getController() {
            return controller;
        }

        public R getRegion() {
            return region;
        }
    }

}
