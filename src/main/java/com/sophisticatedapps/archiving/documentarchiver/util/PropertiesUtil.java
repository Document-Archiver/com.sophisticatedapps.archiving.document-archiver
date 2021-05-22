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
import com.sophisticatedapps.archiving.documentarchiver.type.Tenant;
import javafx.util.Pair;

import java.io.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesUtil {

    public static final Tenant ACTIVE_TENANT;
    public static final File CORE_ARCHIVING_FOLDER;
    public static final String QUICK_DESCRIPTION_WORDS;
    public static final Locale LANGUAGE_LOCALE;
    public static final ThemeUtil.ThemeEnum APPEARANCE_THEME;

    public static final String KEY_ACTIVE_TENANT = "active.tenant";
    public static final String KEY_ARCHIVING_PATH = "archiving.path";
    public static final String KEY_QUICK_DESCRIPTION_WORDS = "quick.description.words";
    public static final String KEY_LANGUAGE_LOCALE = "language.locale";
    public static final String KEY_APPEARANCE_THEME = "appearance.theme";

    private static final String PROPERTIES_FILE = "document-archiver.properties";
    private static final Properties APPLICATION_PROPERTIES;

    private static final Pattern HOME_DIR_PATTERN = Pattern.compile("^~(.*)$");

    private static File localPropertiesDirectory = AppDirUtil.getUserDataDir();

    static {

        try {

            APPLICATION_PROPERTIES = PropertiesUtil.readProperties(PROPERTIES_FILE);

            String tmpCoreArchivingFolderPath = APPLICATION_PROPERTIES.getProperty(KEY_ARCHIVING_PATH);
            Matcher tmpMatcher = HOME_DIR_PATTERN.matcher(tmpCoreArchivingFolderPath);
            if (tmpMatcher.find()) {
                tmpCoreArchivingFolderPath = System.getProperty("user.home").concat(tmpMatcher.group(1));
            }

            ACTIVE_TENANT = new Tenant(
                    APPLICATION_PROPERTIES.getProperty(KEY_ACTIVE_TENANT, GlobalConstants.DEFAULT_TENANT_NAME));
            CORE_ARCHIVING_FOLDER = new File(tmpCoreArchivingFolderPath);
            QUICK_DESCRIPTION_WORDS = APPLICATION_PROPERTIES.getProperty(KEY_QUICK_DESCRIPTION_WORDS);
            LANGUAGE_LOCALE = Locale.forLanguageTag(APPLICATION_PROPERTIES.getProperty(KEY_LANGUAGE_LOCALE));
            APPEARANCE_THEME = ThemeUtil.ThemeEnum.valueOf(APPLICATION_PROPERTIES.getProperty(KEY_APPEARANCE_THEME));
        }
        catch (IOException e) {

            throw (new RuntimeException("Properties could not be loaded: ".concat(e.getMessage())));
        }
    }

    /**
     * Private constructor.
     */
    private PropertiesUtil() {
    }

    /**
     * Read the given properties file and return the contained properties as object.
     *
     * @param   aFilename  Filename of the properties file
     * @return  {@link Properties} object
     * @throws IOException in case there is a problem with reading the properties file.
     */
    public static Properties readProperties(String aFilename) throws IOException {

        Properties tmpProperties = new Properties();

        // Do we have an existing local properties directory?
        File tmpLocalPropertiesDirectory = retrieveLocalPropertiesDirectory(false);

        if (tmpLocalPropertiesDirectory.exists()) {

            // Do we have a local properties file?
            File tmpPropertiesFile = new File(tmpLocalPropertiesDirectory, aFilename);

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

    public static void updateActiveTenant(String aNewActiveTenant) throws IOException {

        APPLICATION_PROPERTIES.setProperty(KEY_ACTIVE_TENANT, aNewActiveTenant);

        writeProperties(PROPERTIES_FILE, APPLICATION_PROPERTIES);
    }

    public static void updateApplicationLanguageLocale(Locale aNewLanguageLocale) throws IOException {

        APPLICATION_PROPERTIES.setProperty(KEY_LANGUAGE_LOCALE, aNewLanguageLocale.toLanguageTag());

        writeProperties(PROPERTIES_FILE, APPLICATION_PROPERTIES);
    }

    public static void updateApplicationProperties(Pair<String, String>... aNewPropertiesPairs) throws IOException {

        for (Pair<String, String> tmpCurrentPair : aNewPropertiesPairs) {

            APPLICATION_PROPERTIES.setProperty(tmpCurrentPair.getKey(), tmpCurrentPair.getValue());
        }

        writeProperties(PROPERTIES_FILE, APPLICATION_PROPERTIES);
    }

    public static void updateApplicationAppearanceTheme(ThemeUtil.ThemeEnum aNewTheme) throws IOException {

        APPLICATION_PROPERTIES.setProperty(KEY_APPEARANCE_THEME, aNewTheme.name());

        writeProperties(PROPERTIES_FILE, APPLICATION_PROPERTIES);
    }

    public static void writeProperties(String aFilename, Properties aProperties) throws IOException {

        File tmpPropertiesFile = new File(retrieveLocalPropertiesDirectory(true), aFilename);

        try (FileOutputStream tmpOutputStream = new FileOutputStream(tmpPropertiesFile)) {

            aProperties.store(tmpOutputStream, null);
        }
    }

    protected static File retrieveLocalPropertiesDirectory(boolean aCreateIfNotExisting) {

        if ((!localPropertiesDirectory.exists()) && aCreateIfNotExisting) {

            localPropertiesDirectory.mkdirs();
        }

        return localPropertiesDirectory;
    }

}
