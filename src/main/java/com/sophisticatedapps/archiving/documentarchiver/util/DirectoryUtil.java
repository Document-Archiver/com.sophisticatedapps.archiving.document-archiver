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
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectoryUtil {

    public static final FileFilter NO_HIDDEN_FILES_FILE_FILTER =
            (aFile -> ((!aFile.isHidden()) && (!aFile.getName().startsWith(GlobalConstants.TENANT_FOLDER_PREFIX))));
    private static File coreArchivingFolder = PropertiesUtil.CORE_ARCHIVING_FOLDER;
    private static File archivingRootFolder =
            new File(coreArchivingFolder, PropertiesUtil.ACTIVE_TENANT.getFolderName());

    /**
     * Private constructor.
     */
    private DirectoryUtil() {
    }

    /**
     * Get the core archiving folder.
     *
     * @return  Core archiving folder as File object.
     */
    public static File getCoreArchivingFolder() {

        return coreArchivingFolder;
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

    /**
     * Read a directory recursive and put all matching file into the given List.
     *
     * @param   aDirectoryPath  Path to read.
     * @param   aFileList       List to put the files in.
     * @param   aFileFilter     A FileFilter to apply.
     */
    @SuppressWarnings("RedundantCollectionOperation")
    public static void readDirectoryRecursive(File aDirectoryPath, List<File> aFileList, FileFilter aFileFilter) {

        List<File> tmpFilesList = Arrays.asList(Objects.requireNonNull(aDirectoryPath.listFiles(aFileFilter)));

        for (File tmpCurrentFile : tmpFilesList) {

            if (tmpCurrentFile.isDirectory()) {

                readDirectoryRecursive(tmpCurrentFile, aFileList, aFileFilter);
            }
            else if (tmpCurrentFile.isFile()) {

                aFileList.add(tmpCurrentFile);
            }
        }
    }

    /**
     * Deletes a file recursively.
     *
     * @param   aFileToBeDeleted   File to delete.
     */
    public static void deleteRecursively(File aFileToBeDeleted) throws IOException {

        File[] tmpAllContents = aFileToBeDeleted.listFiles();

        if (tmpAllContents != null) {

            for (File aCurrentFile : tmpAllContents) {

                deleteRecursively(aCurrentFile);
            }
        }

        Files.delete(aFileToBeDeleted.toPath());
    }

}
