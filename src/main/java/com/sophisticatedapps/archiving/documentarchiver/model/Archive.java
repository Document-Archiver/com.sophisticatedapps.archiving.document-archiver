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

package com.sophisticatedapps.archiving.documentarchiver.model;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Archive {

    /**
     * Private constructor.
     */
    private Archive() {
    }

    public static void moveFileToArchive(File aFileToMove, DefinedFileProperties aDfp) throws IOException {

        String tmpFileExtension = FileUtil.getFileExtension(aFileToMove);
        FileTypeEnum tmpFileType = FileTypeEnum.byFileExtension(tmpFileExtension, true);

        File tmpTargetDirectory =
                DirectoryUtil.getArchivingFolder(tmpFileType.getFileTypeGroup(), aDfp.getDate().getYear());

        if (!tmpTargetDirectory.exists() && (!tmpTargetDirectory.mkdirs())) {

            throw (new IOException("Target folder could not be created."));
        }

        StringBuilder tmpFilename = new StringBuilder();

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

        File tmpNewFile = new File(tmpTargetDirectory, tmpFilename.toString());
        if (tmpNewFile.exists()) {
            throw new IOException("File with name '" + tmpNewFile.getPath() + "' exists!");
        }

        Path tmpSource = Paths.get(aFileToMove.getPath());
        Path tmpTarget = Paths.get(tmpNewFile.getPath());
        Files.move(tmpSource, tmpTarget);
    }

}
