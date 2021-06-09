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
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Launcher {

    public static void main(String[] args) {

        (new LaunchApp()).fireUp(args);
    }

    public static class LaunchApp extends App {

        private DaLogger logger = new DaLogger();

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

            StartupNotification.registerStartupListener(aPath -> {

                logger.log("StartupListenerCalled: " + LocalDateTime.now());

                try {

                    setFilesListToStageProperties(externalPathStringToFilesList(aPath), aPrimaryStage.getProperties());
                }
                catch (IOException e) {

                    showError(Thread.currentThread(), e);
                }
            });

            logger.log("Application start: " + LocalDateTime.now());

            super.start(aPrimaryStage);
        }
    }

    private static class DaLogger {

        private static final File LOGFILE_DIR = new File(AppDirsFactory.getInstance()
                .getUserLogDir("DocumentArchiver", null, "SophisticatedApps"));
        private static final Path LOGFILE_PATH = new File(LOGFILE_DIR, "event.log").toPath();

        static {

            try {

                Files.createDirectories(LOGFILE_DIR.toPath());
            }
            catch (IOException e) {

                throw (new RuntimeException(("Could not create log directory: " + e.getMessage()), e));
            }
        }

        private void log(String aLogEntry) {

            try {

                Files.write(LOGFILE_PATH, aLogEntry.concat("\n").getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            catch (IOException e) {

                throw (new RuntimeException(("Could not write log entry: " + e.getMessage()), e));
            }
        }
    }

}
