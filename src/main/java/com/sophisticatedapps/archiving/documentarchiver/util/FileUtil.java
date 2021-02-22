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
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Properties;

public class FileUtil {

    /**
     * Read the given properties file and return the contained properties as object.
     *
     * @param   aFilename  Filename of the properties file
     * @return  {@link Properties} object
     * @throws  IOException in case there is a problem with reading the properties file.
     */
    public static Properties readProperties(String aFilename) throws IOException {

        try (BufferedInputStream tmpInputStream = new BufferedInputStream(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(aFilename)))) {

            Properties tmpProperties = new Properties();
            tmpProperties.load(tmpInputStream);
            return tmpProperties;
        }
    }

    public static void moveFileToArchive(File aFileToMove, DefinedFileProperties aDfp) throws Exception {

        String tmpSubDirectory = "/" + aDfp.getFileType().getFileExtension() + "/" + aDfp.getDate().getYear();
        File tmpTargetDirectory = new File(GlobalConstants.ARCHIVING_FOLDER.getPath().concat(tmpSubDirectory));

        if (!tmpTargetDirectory.exists()) {
            if (!tmpTargetDirectory.mkdirs()) {
                throw (new Exception("Target folder could not be created."));
            }
        }

        StringBuilder tmpFilename = new StringBuilder();
        tmpFilename.append("/");

        if (aDfp.isUtilizeTimeInformation()) {

            LocalTime tmpLocalTime = LocalTime.from(
                    GlobalConstants.ONLY_TIME_DATE_TIME_FORMATTER.parse(aDfp.getTimeInformation()));
            LocalDateTime tmpLocalDateTime = LocalDateTime.of(aDfp.getDate(), tmpLocalTime);

            tmpFilename.append(GlobalConstants.FILENAME_DATE_TIME_FORMATTER.format(tmpLocalDateTime));
        }
        else {

            tmpFilename.append(GlobalConstants.FILENAME_ONLY_DATE_DATE_TIME_FORMATTER.format(aDfp.getDate()));
        }

        tmpFilename.append("--");
        tmpFilename.append(aDfp.getDescription().replaceAll("[^A-Za-z0-9]+", "-"));
        tmpFilename.append("__");
        tmpFilename.append(String.join("_", aDfp.getTags()));
        tmpFilename.append(".");
        tmpFilename.append(aDfp.getFileType().getFileExtension());

        File tmpNewFile = new File(tmpTargetDirectory.getPath().concat(tmpFilename.toString()));
        if (tmpNewFile.exists()) {
            throw new Exception("File with name '" + tmpNewFile.getPath() + "' exists!");
        }

        Path tmpSource = Paths.get(aFileToMove.getPath());
        Path tmpTarget = Paths.get(tmpNewFile.getPath());
        Files.move(tmpSource, tmpTarget);
    }

}
