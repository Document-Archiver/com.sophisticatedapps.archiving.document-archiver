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

import javafx.util.Pair;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesUtilTest {

    @TempDir
    File tempDir;

    /**
     * Test if properties are being read correctly.
     */
    @Test
    void testReadProperties() throws IOException {

        Properties tmpProperties = PropertiesUtil.readProperties("document-archiver.properties");

        assertNotNull(tmpProperties);
        assertEquals("~/Documents/DocumentArchiver", tmpProperties.getProperty("archiving.path"));
    }

    @Test
    void testUpdateApplicationLanguageLocale() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        PropertiesUtil.updateApplicationLanguageLocale(Locale.KOREA);

        // Read them in again
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("ko-KR", tmpReadProperties.getProperty(PropertiesUtil.KEY_LANGUAGE_LOCALE));

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    @Test
    void testUpdateApplicationProperties() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        Pair<String, String> tmpArchivingPathPropertiesPair =
                new Pair<>(PropertiesUtil.KEY_ARCHIVING_PATH, "/foo/bar/snafu");
        Pair<String, String> tmpQuickDescriptionWordsPropertiesPair =
                new Pair<>(PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS, "ha,hi,ho");

        PropertiesUtil.updateApplicationProperties(tmpArchivingPathPropertiesPair,
                tmpQuickDescriptionWordsPropertiesPair);

        // Read them in again
        Properties tmpReadProperties = PropertiesUtil.readProperties("document-archiver.properties");

        // Check
        assertEquals("/foo/bar/snafu", tmpReadProperties.getProperty(PropertiesUtil.KEY_ARCHIVING_PATH));
        assertEquals("ha,hi,ho", tmpReadProperties.getProperty(PropertiesUtil.KEY_QUICK_DESCRIPTION_WORDS));

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    /**
     * Test if properties are being written correctly.
     */
    @Test
    void testWriteProperties() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        // Create Properties
        Properties tmpProperties = new Properties();
        tmpProperties.setProperty("foo", "bar");

        // Write Properties
        PropertiesUtil.writeProperties("test.properties", tmpProperties);

        // Read them in again
        Properties tmpReadProperties = PropertiesUtil.readProperties("test.properties");

        // Check
        assertEquals(tmpProperties, tmpReadProperties);

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    /**
     * Test if the local properties directory is retrieved correctly.
     */
    @Test
    void testRetrieveLocalPropertiesDirectory() {

        assertEquals(AppDirUtil.getUserDataDir(), PropertiesUtil.retrieveLocalPropertiesDirectory(false));
    }

}
