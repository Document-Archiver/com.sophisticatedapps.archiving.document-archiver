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

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tags {

    /**
     * Private constructor.
     */
    private Tags() {
    }

    private static final Pattern FIND_TAGS_IN_FILENAME_PATTERN =
            Pattern.compile(".*__(.+)\\..+");

    public static SortedSet<String> getExistingTags(File anArchivingFolder, FileTypeEnum aFileType) {

        SortedSet<String> tmpReturn = new TreeSet<>();

        File tmpFileTypeDirectory = new File(
                anArchivingFolder.getPath() + "/" + aFileType.getFileExtension());

        // Check if folder is created yet (may not be the case before first archiving)
        if (tmpFileTypeDirectory.exists()) {

            getTagsFromDirectory(tmpFileTypeDirectory, tmpReturn);
        }

        return tmpReturn;
    }

    private static void getTagsFromDirectory(File aDirectoryPath, SortedSet<String> aTagSet) {

        File[] tmpFilesList = Objects.requireNonNull(aDirectoryPath.listFiles());

        for (File tmpCurrentFile : tmpFilesList) {

            if (tmpCurrentFile.isDirectory()) {

                getTagsFromDirectory(tmpCurrentFile, aTagSet);
            }
            else if (tmpCurrentFile.isFile()) {

                Matcher tmpMatcher = FIND_TAGS_IN_FILENAME_PATTERN.matcher(tmpCurrentFile.getName());

                if (tmpMatcher.find()) {

                    String[] tmpFoundTags = tmpMatcher.group(1).split("_");
                    Collections.addAll(aTagSet, tmpFoundTags);
                }
            }
        }
    }

}
