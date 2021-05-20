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

import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class Tags {

    /**
     * Private constructor.
     */
    private Tags() {
    }

    public static SortedSet<String> getExistingTags() {

        return getExistingTagsFromFolder(DirectoryUtil.getArchivingRootFolder());
    }

    public static SortedSet<String> getExistingTags(FileTypeEnum aFileType) {

        return getExistingTagsFromFolder(DirectoryUtil.getGroupingFolder(aFileType.getFileTypeGroup()));
    }

    private static SortedSet<String> getExistingTagsFromFolder(File aFolder) {

        SortedSet<String> tmpReturn = new TreeSet<>();

        // Check if folder is created yet (may not be the case before first archiving)
        if (aFolder.exists()) {

            getTagsFromDirectory(aFolder, tmpReturn, DirectoryUtil.NO_HIDDEN_FILES_FILE_FILTER);
        }

        return tmpReturn;
    }

    private static void getTagsFromDirectory(File aDirectoryPath, SortedSet<String> aTagSet, FileFilter aFileFilter) {

        File[] tmpFilesList = Objects.requireNonNull(aDirectoryPath.listFiles(aFileFilter));

        for (File tmpCurrentFile : tmpFilesList) {

            if (tmpCurrentFile.isDirectory()) {

                getTagsFromDirectory(tmpCurrentFile, aTagSet, aFileFilter);
            }
            else if (tmpCurrentFile.isFile()) {

                Set<String> tmpFoundTags = getTagsFromFile(tmpCurrentFile);

                if (!tmpFoundTags.isEmpty()) {

                    aTagSet.addAll(tmpFoundTags);
                }
            }
        }
    }

    public static Set<String> getTagsFromFile(File aFile) {

        String tmpFileName = aFile.getName();
        int tmpStartTagsIndex = tmpFileName.lastIndexOf("__");
        int tmpStopTagsIndex = tmpFileName.lastIndexOf('.');

        if ((tmpStartTagsIndex > 0) && (tmpStopTagsIndex > (tmpStartTagsIndex + 2))) {

            return Set.of(tmpFileName.substring((tmpStartTagsIndex + 2), tmpStopTagsIndex).split("_"));
        }
        else {

            return Collections.emptySet();
        }
    }

}
