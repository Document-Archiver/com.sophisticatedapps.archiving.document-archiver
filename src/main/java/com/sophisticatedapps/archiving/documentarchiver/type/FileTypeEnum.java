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

package com.sophisticatedapps.archiving.documentarchiver.type;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FileTypeEnum {

    PDF("pdf", "pdfs",false),
    TXT("txt", "texts", false),
    JPG("jpg", "images", true),
    PNG("png", "images", true),
    GIF("gif", "images", true),
    HEIC("heic", "images", true),
    XML("xml", "texts", false),
    UNSUPPORTED("misc", "misc", false);

    private static final Map<String, FileTypeEnum> LOOKUP = new HashMap<>();
    private final String fileExtension;
    private final String groupingFolder;
    private final boolean utilizeTimeInformationDefault;

    static {

        for (FileTypeEnum tmpCurrentType : EnumSet.allOf(FileTypeEnum.class)) {

            LOOKUP.put(tmpCurrentType.getFileExtension(), tmpCurrentType);
        }
    }

    /**
     * Initializes a FileType with a given file extension.
     *
     * @param   aFileExtension                      File extension to apply to the instance.
     * @param   aGroupingFolder                     Name of the subfolder within the archiving folder.
     * @param   anUtilizeTimeInformationDefault     If time information should be utilized by default.
     */
    FileTypeEnum(String aFileExtension, String aGroupingFolder, boolean anUtilizeTimeInformationDefault) {

        this.fileExtension = aFileExtension;
        this.groupingFolder = aGroupingFolder;
        this.utilizeTimeInformationDefault = anUtilizeTimeInformationDefault;
    }

    /**
     * Get the file extension of the enum instance.
     *
     * @return  File extension of the enum instance.
     */
    public String getFileExtension() {

        return fileExtension;
    }

    /**
     * Get the grouping folder of the enum instance.
     *
     * @return  Grouping folder of the enum instance.
     */
    public String getGroupingFolder() {

        return groupingFolder;
    }

    /**
     * If time information should be used by default for the file type.
     *
     * @return  TRUE, if time information should be used by default; FALSE, if not.
     */
    public boolean isUtilizeTimeInformationDefault() {

        return utilizeTimeInformationDefault;
    }

    /**
     * Get an FileTypeEnum by a given file extension.
     *
     * @param   aFileExtension to get the FileTypeEnum for.
     * @param   aReturnUnsupportedIfNotFound  If the UNSUPPORTED FileType should be returned if no match found.
     * @return  {@link FileTypeEnum} matching the given file extension.
     * @throws  IllegalArgumentException, if an invalid (unknown) file extension was given.
     */
    public static FileTypeEnum byFileExtension(String aFileExtension, boolean aReturnUnsupportedIfNotFound)
            throws IllegalArgumentException {

        FileTypeEnum tmpResult = LOOKUP.get(aFileExtension.toLowerCase());

        if (tmpResult == null) {

            if (aReturnUnsupportedIfNotFound) {

                return FileTypeEnum.UNSUPPORTED;
            }

            throw new IllegalArgumentException("No FileTypeEnum for file extension: ".concat(aFileExtension));
        }

        return tmpResult;
    }

}
