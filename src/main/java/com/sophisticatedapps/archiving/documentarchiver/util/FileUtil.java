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

import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileUtil {

    /**
     * Private constructor.
     */
    private FileUtil() {
    }

    /**
     * Turn an command line argument into a Java File object.
     *
     * @param   anArg   Command line argument to turn into a Java File object.
     * @return  Java File object.
     */
    public static File argToFile(String anArg) {

        try {

            File tmpFile;

            if (anArg.startsWith("file:/")) {

                tmpFile = new File(new URI(anArg));
            }
            else {

                tmpFile = new File(anArg);
            }

            // Will throw an exception if path is faulty.
            tmpFile.getCanonicalPath();

            return tmpFile;
        }
        catch (IOException | URISyntaxException e) {

            throw (new RuntimeException("Could not create File object for '" + anArg + "': " + e.getMessage()));
        }
    }

    public static String getFileNameWithoutExtension(File aFile) {

        // Get file Name first
        String tmpFileName = aFile.getName();
        final int tmpLastIndexOfDot = tmpFileName.lastIndexOf(".");

        if (tmpLastIndexOfDot >= 1) {

            return tmpFileName.substring(0, tmpLastIndexOfDot);
        }

        return tmpFileName;
    }

    public static String getFileExtension(String aFilename) {

        String tmpFileExtension = "";

        final int tmpLastIndexOfDot = aFilename.lastIndexOf(".");

        // If fileName do not contain "." or starts with "." then it is not a valid file
        if (tmpLastIndexOfDot >= 1) {

            tmpFileExtension = aFilename.substring(tmpLastIndexOfDot + 1);
        }

        return tmpFileExtension;
    }

    public static String getFileExtension(File aFile) {

        return getFileExtension(aFile.getName());
    }

    public static String getFileExtension(Path aPath) {

        return getFileExtension(aPath.getFileName().toString());
    }

    public static FileTypeEnum getFiletype(File aFile) {

        return FileTypeEnum.byFileExtension(getFileExtension(aFile), true);
    }

}
