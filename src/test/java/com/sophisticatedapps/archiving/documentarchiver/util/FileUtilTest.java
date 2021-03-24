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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.util.FileUtil".
 */
class FileUtilTest extends BaseTest {

    @TempDir
    File tempDir;

    /**
     * Test if an command line argument is correctly turned into a file.
     */
    @Test
    void testArgToFile() {

        String tmpPathInput =
                "/Users/stephansann/Library/Mobile Documents/com~apple~CloudDocs/lotk/Music/The Hit List Vol 5.mp3";
        File tmpFile = FileUtil.argToFile(tmpPathInput);
        assertEquals(tmpPathInput, tmpFile.getPath());

        String tmpURIInput1 =
                "file:/Users/stephansann/Library/Mobile%20Documents/com~apple~CloudDocs/lotk/Music/The%20Hit%20List%20Vol%205.mp3";
        File tmpFile2 = FileUtil.argToFile(tmpURIInput1);
        assertEquals(tmpPathInput, tmpFile2.getPath());

        String tmpURIInput2 =
                "file:///Users/stephansann/Library/Mobile%20Documents/com~apple~CloudDocs/lotk/Music/The%20Hit%20List%20Vol%205.mp3";
        File tmpFile3 = FileUtil.argToFile(tmpURIInput2);
        assertEquals(tmpPathInput, tmpFile3.getPath());

        String tmpDirectoryInput = "/path/to/foo/";
        File tmpFile4 = FileUtil.argToFile(tmpDirectoryInput);
        assertEquals("/path/to/foo", tmpFile4.getPath());

        Throwable tmpException =
                assertThrows(RuntimeException.class, () -> FileUtil.argToFile(String.valueOf('\0')));
        assertEquals("Could not create File object for '\u0000': Invalid file path", tmpException.getMessage());
    }

    /**
     * Test if properties are being read correctly.
     */
    @Test
    void testReadProperties() throws IOException {

        Properties tmpProperties = FileUtil.readProperties("document-archiver.properties");

        assertNotNull(tmpProperties);
        assertEquals("~/Documents/DocumentArchiver", tmpProperties.getProperty("archiving.path"));
    }

    /**
     * Test if properties are being written correctly.
     */
    @Test
    void testWriteProperties() throws IllegalAccessException, IOException {

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File)FieldUtils.readStaticField(
                FileUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(FileUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        // Create Properties
        Properties tmpProperties = new Properties();
        tmpProperties.setProperty("foo", "bar");

        // Write Properties
        FileUtil.writeProperties("test.properties", tmpProperties);

        // Read them in again
        Properties tmpReadProperties = FileUtil.readProperties("test.properties");

        // Check
        assertEquals(tmpProperties, tmpReadProperties);

        // Change local properties directory back
        FieldUtils.writeStaticField(FileUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

    /**
     * Test if the local properties directory is retrieved correctly.
     */
    @Test
    void testRetrieveLocalPropertiesDirectory() {

        File tmpExpectedFile = new File(System.getProperty("user.home").concat("/.documentarchiver"));

        assertEquals(tmpExpectedFile, FileUtil.retrieveLocalPropertiesDirectory(false));
    }

    /**
     * Test if the move to archive works correctly.
     */
    @Test
    void testMoveFileToArchive() throws IOException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);

        // Create temp file and write some stuff to it
        File tmpDocument = new File(tempDir, "snafu.txt");
        try (FileWriter tmpFileWriter = new FileWriter(tmpDocument)) {
            tmpFileWriter.write("snafu");
        }

        // We need the defined file properties
        LocalDateTime tmpDateTime = LocalDateTime.now();
        String tmpTimeInformation = GlobalConstants.ONLY_TIME_DATE_TIME_FORMATTER.format(tmpDateTime);
        List<String> tmpTagsList = Arrays.asList("Java", "Swift");
        DefinedFileProperties tmpDfp = new DefinedFileProperties(tmpDateTime.toLocalDate(),
                true, tmpTimeInformation, "cool doc", tmpTagsList);

        FileUtil.moveFileToArchive(tmpDocument, tmpDfp);

        // Did it arrive?
        File tmpArchivingFolder =
                DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, tmpDateTime.getYear());
        File tmpArchivedFile = new File(tmpArchivingFolder,
                (GlobalConstants.FILENAME_DATE_TIME_FORMATTER.format(tmpDateTime) + "--cool-doc__Java_Swift.txt"));

        assertTrue(tmpArchivedFile.exists());

        // Cleanup
        assertTrue(tmpArchivedFile.delete());
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
    }

    /**
     * Test if the move to archive fails when target directory cannot be created.
     */
    @Test
    void testMoveFileToArchive_with_exception_while_creating_target_directory() {

        // This should cause a crash when used to create the target directory.
        DirectoryUtil.setArchivingRootFolder(new File("/:" + '\0' + "/:"));

        // Create temp file
        File tmpDocument = new File(tempDir, "file.txt");

        // We need the defined file properties
        LocalDate tmpDate = LocalDate.now();
        DefinedFileProperties tmpDfp = new DefinedFileProperties(
                tmpDate,false, "12:12:12", "test", null);

        // Move shouldn't work.
        Throwable tmpException = assertThrows(IOException.class, () -> FileUtil.moveFileToArchive(tmpDocument, tmpDfp));
        assertEquals("Target folder could not be created.", tmpException.getMessage());

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
    }

    /**
     * Test if the move to archive is denied if file with same properties exists.
     */
    @Test
    void testMoveFileToArchive_file_exists_in_archive() throws IOException {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);

        // Create temp file and write some stuff to it
        File tmpDocument = new File(tempDir, "file.txt");
        try (FileWriter tmpFileWriter = new FileWriter(tmpDocument)) {
            tmpFileWriter.write("snafu");
        }

        // We need the defined file properties
        LocalDate tmpDate = LocalDate.from(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.parse("01.07.2021"));
        List<String> tmpTagsList = Arrays.asList("sna", "fu");
        DefinedFileProperties tmpDfp = new DefinedFileProperties(
                tmpDate,false, "12:12:12", "test", tmpTagsList);

        // Move shouldn't work.
        Throwable tmpException = assertThrows(IOException.class, () -> FileUtil.moveFileToArchive(tmpDocument, tmpDfp));

        File tmpArchivingFolder = DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, 2021);
        File tmpExistingFile = new File(tmpArchivingFolder, "2021-07-01--test__sna_fu.txt");

        assertEquals("File with name '" + tmpExistingFile.getPath() + "' exists!", tmpException.getMessage());

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
    }

    /**
     * Test "getFileNameWithoutExtension".
     */
    @Test
    void testGetFileNameWithoutExtension() {

        File tmpFile = new File(TEST_RESOURCES_DIRECTORY, "Screen Shot 2021-02-25 at 16.22.44.png");
        assertEquals("Screen Shot 2021-02-25 at 16.22.44", FileUtil.getFileNameWithoutExtension(tmpFile));
    }

    /**
     * Test "getFileNameWithoutExtension" with a file that doesn't have an extension.
     */
    @Test
    void testGetFileNameWithoutExtension_with_file_without_extension() {

        File tmpFile = new File(TEST_RESOURCES_DIRECTORY, "FooBar");
        assertEquals("FooBar", FileUtil.getFileNameWithoutExtension(tmpFile));
    }

    /**
     * Test "getFileExtension".
     */
    @Test
    void testGetFileExtension() {

        assertEquals("txt", FileUtil.getFileExtension(TEST_TEXT_FILE));
        assertEquals("pdf", FileUtil.getFileExtension(TEST_PDF_FILE));
        assertEquals("png", FileUtil.getFileExtension(TEST_PNG_FILE));
    }

    /**
     * Test "getFileExtension" with a file that doesn't have an extension.
     */
    @Test
    void testGetFileExtension_with_file_without_extension() {

        File tmpFile = new File(TEST_RESOURCES_DIRECTORY, "FooBar");
        assertEquals("", FileUtil.getFileExtension(tmpFile));
    }

}
