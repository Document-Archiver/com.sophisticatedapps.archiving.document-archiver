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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.type.DefinedFileProperties;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;
import com.sophisticatedapps.archiving.documentarchiver.util.DirectoryUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveTest extends BaseTest {

    @TempDir
    File tempDir;

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

        Archive.moveFileToArchive(tmpDocument, tmpDfp);

        // Did it arrive?
        File tmpArchivingFolder =
                DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, tmpDateTime.getYear());
        File tmpArchivedFile = new File(tmpArchivingFolder,
                (GlobalConstants.FILENAME_DATE_TIME_FORMATTER.format(tmpDateTime) + "--cool-doc__Java_Swift.txt"));

        assertTrue(tmpArchivedFile.exists());

        // Cleanup
        assertTrue(tmpArchivedFile.delete());
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
    }

    /**
     * Test if the move to archive fails when target directory cannot be created.
     */
    @Test
    void testMoveFileToArchive_with_exception_while_creating_target_directory() {

        // This should cause a crash when used to create the target directory.
        DirectoryUtil.setArchivingRootFolder(new File("/:" + NUL_CHARACTER_STRING + "/:"));

        // Create temp file
        File tmpDocument = new File(tempDir, "file.txt");

        // We need the defined file properties
        LocalDate tmpDate = LocalDate.now();
        DefinedFileProperties tmpDfp = new DefinedFileProperties(
                tmpDate,false, "12:12:12", "test", null);

        // Move shouldn't work.
        Throwable tmpException = assertThrows(IOException.class, () -> Archive.moveFileToArchive(tmpDocument, tmpDfp));
        assertEquals("Target folder could not be created.", tmpException.getMessage());

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
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
        Throwable tmpException = assertThrows(IOException.class, () -> Archive.moveFileToArchive(tmpDocument, tmpDfp));

        File tmpArchivingFolder = DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.TEXTS, 2021);
        File tmpExistingFile = new File(tmpArchivingFolder, "2021-07-01--test__sna_fu.txt");

        assertEquals("File with name '" + tmpExistingFile.getPath() + "' exists!", tmpException.getMessage());

        // Cleanup
        DirectoryUtil.setArchivingRootFolder(PropertiesUtil.ARCHIVING_ROOT_FOLDER);
    }

}
