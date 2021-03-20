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

package com.sophisticatedapps.archiving.documentarchiver.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileTypeEnumTest {

    @Test
    void getFileExtension() {

        assertEquals("pdf", FileTypeEnum.PDF.getFileExtension());
    }

    @Test
    void getFileTypeGroup() {

        assertEquals(FileTypeGroupEnum.PDFS, FileTypeEnum.PDF.getFileTypeGroup());
    }

    @Test
    void isUtilizeTimeInformationDefault() {

        assertTrue(FileTypeEnum.JPG.isUtilizeTimeInformationDefault());
    }

    @Test
    void byFileExtension() {

        assertEquals(FileTypeEnum.PDF, FileTypeEnum.byFileExtension("pdf", false));
    }

    @Test
    void byFileExtension_unsupported_with_return_unsupported_type() {

        assertEquals(FileTypeEnum.UNSUPPORTED, FileTypeEnum.byFileExtension("mov", true));
    }

    @Test
    void byFileExtension_unsupported_with_exception() {


        Throwable tmpException = assertThrows(IllegalArgumentException.class, () -> {
            FileTypeEnum.byFileExtension("mov", false);
        });
        assertEquals("No FileTypeEnum for file extension: mov", tmpException.getMessage());
    }

}
