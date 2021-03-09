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
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Properties;

public class FileUtil {

    /**
     * Private constructor.
     */
    private FileUtil() {
    }

    /**
     * Read the given properties file and return the contained properties as object.
     *
     * @param   aFilename  Filename of the properties file
     * @return  {@link Properties} object
     * @throws  IOException in case there is a problem with reading the properties file.
     */
    public static Properties readProperties(String aFilename) throws IOException {

        Properties tmpProperties = new Properties();

        // Do we have an existing local properties directory?
        File tmpLocalPropertiesDirectory = retrieveLocalPropertiesDirectory(false);

        if (tmpLocalPropertiesDirectory.exists()) {

            // Do we have a local properties file?
            File tmpPropertiesFile = new File(tmpLocalPropertiesDirectory.getPath() + "/" + aFilename);

            if (tmpPropertiesFile.exists()) {

                try (BufferedInputStream tmpInputStream =
                             new BufferedInputStream(new FileInputStream(tmpPropertiesFile))) {

                    tmpProperties.load(tmpInputStream);
                    return tmpProperties;
                }
            }
        }

        try (BufferedInputStream tmpInputStream = new BufferedInputStream(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(aFilename)))) {

            tmpProperties.load(tmpInputStream);
            return tmpProperties;
        }
    }

    public static void writeProperties(String aFilename, Properties aProperties) throws IOException {

        File tmpPropertiesFile = new File(
                retrieveLocalPropertiesDirectory(true).getPath() + "/" + aFilename);

        try (FileOutputStream tmpOutputStream = new FileOutputStream(tmpPropertiesFile)) {

            aProperties.store(tmpOutputStream, null);
        }
    }

    private static File retrieveLocalPropertiesDirectory(boolean aCreateIfNotExisting) {

        File tmpPropertiesDirectory = new File(System.getProperty("user.home").concat("/.documentarchiver"));

        if ((!tmpPropertiesDirectory.exists()) && aCreateIfNotExisting) {

            tmpPropertiesDirectory.mkdirs();
        }

        return tmpPropertiesDirectory;
    }

    public static void moveFileToArchive(File aFileToMove, DefinedFileProperties aDfp) throws Exception {

        String tmpFileExtension = getFileExtension(aFileToMove);
        FileTypeEnum tmpFileType = FileTypeEnum.byFileExtension(tmpFileExtension, true);

        String tmpSubDirectory = "/" + tmpFileType.getGroupingFolder() + "/" + aDfp.getDate().getYear();
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
        tmpFilename.append(StringUtil.retrieveFilenameSafeString(aDfp.getDescription()));
        tmpFilename.append("__");
        tmpFilename.append(String.join("_", aDfp.getTags()));
        tmpFilename.append(".");
        tmpFilename.append(tmpFileExtension);

        File tmpNewFile = new File(tmpTargetDirectory.getPath().concat(tmpFilename.toString()));
        if (tmpNewFile.exists()) {
            throw new Exception("File with name '" + tmpNewFile.getPath() + "' exists!");
        }

        Path tmpSource = Paths.get(aFileToMove.getPath());
        Path tmpTarget = Paths.get(tmpNewFile.getPath());
        Files.move(tmpSource, tmpTarget);
    }

    public static String getFileExtension(File aFile) {

        String tmpFileExtension = "";

        // Get file Name first
        String tmpFileName = aFile.getName();
        final int tmpListIndexOfDot = tmpFileName.lastIndexOf(".");

        // If fileName do not contain "." or starts with "." then it is not a valid file
        if (tmpListIndexOfDot >= 1) {

            tmpFileExtension = tmpFileName.substring(tmpListIndexOfDot + 1);
        }

        return tmpFileExtension;
    }

    public static FileTypeEnum getFiletype(File aFile) {

        return FileTypeEnum.byFileExtension(getFileExtension(aFile), true);
    }

}
