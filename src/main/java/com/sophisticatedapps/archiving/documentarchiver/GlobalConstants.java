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

package com.sophisticatedapps.archiving.documentarchiver;

import com.sophisticatedapps.archiving.documentarchiver.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalConstants {

    /**
     * Private constructor.
     */
    private GlobalConstants() {
    }

    public static final DateTimeFormatter DD_MM_YYYY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter ONLY_TIME_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FILENAME_ONLY_DATE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static final String HOST_SERVICES_PROPERTY_KEY = "hostServices";
    public static final String ALL_DOCUMENTS_PROPERTY_KEY = "allDocuments";
    public static final String CURRENT_DOCUMENT_PROPERTY_KEY = "currentDocument";

    public static final String WIKI_URL =
            "https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/wiki/Quick-Start";

    public static final String PROPERTIES_FILE = "document-archiver.properties";
    public static final File ARCHIVING_ROOT_FOLDER;
    public static final String QUICK_DESCRIPTION_WORDS;

    private static final Pattern HOME_DIR_PATTERN = Pattern.compile("^~(.*)$");

    static {

        try {

            Properties tmpProperties = FileUtil.readProperties(PROPERTIES_FILE);

            String tmpArchivingFolderPath = tmpProperties.getProperty("archiving.path");
            Matcher tmpMatcher = HOME_DIR_PATTERN.matcher(tmpArchivingFolderPath);
            if (tmpMatcher.find()) {
                tmpArchivingFolderPath = System.getProperty("user.home").concat(tmpMatcher.group(1));
            }
            ARCHIVING_ROOT_FOLDER = new File(tmpArchivingFolderPath);

            QUICK_DESCRIPTION_WORDS = tmpProperties.getProperty("quick.description.words");
        }
        catch (IOException e) {

            throw (new RuntimeException("Properties could not be loaded: ".concat(e.getMessage())));
        }
    }

}
