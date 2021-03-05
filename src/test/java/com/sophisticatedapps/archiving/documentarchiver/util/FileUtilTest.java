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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.util.FileUtil".
 */
class FileUtilTest {

    /**
     * Test if properties are being read correctly.
     */
    @Test
    void testReadProperties() throws IOException {

        Properties tmpProperties = FileUtil.readProperties("document-archiver.properties");

        assertNotNull(tmpProperties);
        assertEquals("~/Documents/DocumentArchiver", tmpProperties.getProperty("archiving.path"));
    }

}
