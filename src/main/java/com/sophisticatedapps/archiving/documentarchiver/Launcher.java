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

package com.sophisticatedapps.archiving.documentarchiver;

import javafx.stage.Stage;

public class Launcher {

    public static void main(String[] args) {

        (new LaunchApp()).fireUp(args);
    }

    public static class LaunchApp extends App {

        private Stage primaryStage;

        public void fireUp(String[] args) {

            launch(args);
        }

        /**
         * Application start method.
         *
         * @param   aPrimaryStage   The primary stage.
         */
        @Override
        public void start(Stage aPrimaryStage) {

            primaryStage = aPrimaryStage;
            super.start(aPrimaryStage);
        }
    }

}
