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

import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;

import java.io.File;

public class DirectoryUtil {

    private static File archivingRootFolder = PropertiesUtil.ARCHIVING_ROOT_FOLDER;

    /**
     * Private constructor.
     */
    private DirectoryUtil() {
    }

    /**
     * Get the archiving (root) folder.
     *
     * @return  Archiving (root) folder as File object.
     */
    public static File getArchivingRootFolder() {

        return archivingRootFolder;
    }

    /**
     * Set a new archiving (root) folder.
     *
     * @param   anArchivingFolder   A new archiving (root) folder.
     */
    public static void setArchivingRootFolder(File anArchivingFolder) {

        archivingRootFolder = anArchivingFolder;
    }

    public static File getGroupingFolder(FileTypeGroupEnum aFileTypeGroup) {

        return (new File(archivingRootFolder, aFileTypeGroup.getGroupingFolder()));
    }

    public static File getArchivingFolder(FileTypeGroupEnum aFileTypeGroup, int aYear) {

        return (new File(getGroupingFolder(aFileTypeGroup), String.valueOf(aYear)));
    }

}
