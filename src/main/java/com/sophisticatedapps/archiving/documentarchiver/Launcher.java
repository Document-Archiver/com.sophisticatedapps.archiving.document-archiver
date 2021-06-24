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

import com.install4j.api.launcher.StartupNotification;
import javafx.stage.Stage;
import org.awaitility.Awaitility;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Launcher {

    private static Stage primaryStage;

    public static void main(String[] args) {

        StartupNotification.registerStartupListener(aPath -> {

            try {

                // Wait in case stage is not ready yet.
                Awaitility.await().atMost(5, TimeUnit.SECONDS).until(Launcher::isStageReady);

                App.setFilesListToStageProperties(App.externalPathStringToFilesList(aPath), primaryStage);
            }
            catch (IOException e) {

                throw (new RuntimeException(("Could not open file: " + aPath), e));
            }
        });

        (new LaunchApp()).fireUp(args);
    }

    private static boolean isStageReady() {

        return ((!Objects.isNull(primaryStage)) && primaryStage.isShowing());
    }

    public static class LaunchApp extends App {

        public void fireUp(String[] args) {

            launch(args);
        }

        @Override
        public void start(Stage aPrimaryStage) {

            Launcher.primaryStage = aPrimaryStage; // NOSONAR

            super.start(aPrimaryStage);
        }
    }

}
