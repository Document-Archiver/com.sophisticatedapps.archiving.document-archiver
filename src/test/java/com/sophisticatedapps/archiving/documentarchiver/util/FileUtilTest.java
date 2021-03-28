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
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.util.FileUtil".
 */
class FileUtilTest extends BaseTest {

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
                assertThrows(RuntimeException.class, () -> FileUtil.argToFile(NUL_CHARACTER_STRING));
        assertEquals("Could not create File object for '\u0000': Invalid file path", tmpException.getMessage());
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
