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

import com.sophisticatedapps.archiving.documentarchiver.util.ResourceLoadContext;
import io.github.g00fy2.versioncompare.Version;
import javafx.scene.image.Image;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GlobalConstants {

    /**
     * Private constructor.
     */
    private GlobalConstants() {
    }

    public static final Image APP_ICON = new Image(Objects.requireNonNull(
            Thread.currentThread().getContextClassLoader().getResourceAsStream("binder-icon.png")));

    public static final ResourceLoadContext DEFAULT_RESOURCE_LOAD_CONTEXT =
            new ResourceLoadContext(App.class, "document-archiver-i18n");

    public static final DateTimeFormatter DD_MM_YYYY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter ONLY_TIME_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FILENAME_ONLY_DATE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static final String ALL_DOCUMENTS_PROPERTY_KEY = "allDocuments";
    public static final String CURRENT_DOCUMENT_PROPERTY_KEY = "currentDocument";

    public static final String LATEST_RELEASE_REST_URL =
            "http://api.sophisticatedapps.com/githubApiWrapper/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/latestReleaseTag";
    public static final String DOWNLOAD_SITE_URL = "https://document-archiver.github.io/index.html#download";
    public static final Version ARCHIVE_BROWSER_PLUGIN_CURRENT_VERSION = new Version("1.0.0");
    public static final String ARCHIVE_BROWSER_PLUGIN_URL =
            "https://github.com/Document-Archiver/com.sophisticatedapps.archiving.archive-browser/releases/download/v1.0.0/archive-browser-1.0.0.jar";
    public static final String WIKI_URL =
            "https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/wiki/Quick-Start";

    public static final String DEFAULT_TENANT_NAME = "_";
    public static final String TENANT_FOLDER_PREFIX = "@";

}
