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

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.controller.BaseController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class FXMLUtil {

    /**
     * Private constructor.
     */
    private FXMLUtil() {
    }

    /**
     * Loads a Region object and ramps up its controller.
     *
     * @param   aFxmlResource           FXML-resource to load.
     * @param   anApplicationContext    An ApplicationContext instance.
     * @return  A ControllerRegionPair object.
     */
    public static <C extends BaseController,R> ControllerRegionPair<C,R>
            loadAndRampUpRegion(String aFxmlResource, ApplicationContext anApplicationContext) {

        return loadAndRampUpRegion(aFxmlResource, anApplicationContext, GlobalConstants.DEFAULT_RESOURCE_LOAD_CONTEXT);
    }

    /**
     * Loads a Region object and ramps up its controller.
     *
     * @param   aFxmlResource           FXML-resource to load.
     * @param   anApplicationContext    An ApplicationContext instance.
     * @param   aResourceLoadContext    A ResourceLoadContext to find (language-)resources.
     * @return  A ControllerRegionPair object.
     */
    public static <C extends BaseController,R> ControllerRegionPair<C,R> loadAndRampUpRegion(
            String aFxmlResource, ApplicationContext anApplicationContext, ResourceLoadContext aResourceLoadContext) {

        try {

            FXMLLoader tmpRegionLoader =
                    new FXMLLoader(aResourceLoadContext.getSearchBase().getResource(aFxmlResource));
            tmpRegionLoader.setResources(
                    LanguageUtil.getResourceBundleForCurrentLanguage(aResourceLoadContext));

            R tmpRegion = tmpRegionLoader.load();
            C tmpBaseController = tmpRegionLoader.getController();

            if (!Objects.isNull(tmpBaseController)) {

                tmpBaseController.rampUp(anApplicationContext);
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
