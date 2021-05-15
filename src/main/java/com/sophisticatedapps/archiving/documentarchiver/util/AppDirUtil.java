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

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AppDirUtil {

    private static File userDataDir;

    static {

        AppDirs tmpAppDirs = AppDirsFactory.getInstance();
        userDataDir = new File(tmpAppDirs.getUserDataDir("DocumentArchiver", null, "SophisticatedApps"));

        if(!userDataDir.exists()) {

            try {

                Files.createDirectories(userDataDir.toPath());
            }
            catch (IOException e) {

                throw (new RuntimeException("Could not create user data dir: " + e.getMessage()));
            }
        }
    }

    /**
     * Private constructor.
     */
    private AppDirUtil() {
    }

    /**
     * Get the user data dir for the application.
     *
     * @return  User data dir of the application (for example "/Users/johndoe/Library/Application Support/myapp/1.2.3")
     */
    public static File getUserDataDir() {

        return userDataDir;
    }

}
