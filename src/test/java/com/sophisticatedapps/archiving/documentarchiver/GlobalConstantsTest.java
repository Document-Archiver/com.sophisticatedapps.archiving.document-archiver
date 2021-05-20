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

import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.GlobalConstants".
 */
class GlobalConstantsTest {

    /**
     * Test if the properties are being loaded correctly.
     */
    @Test
    void testIfPropertiesCorrectlyLoaded() {

        String tmpExpectedPath =
                System.getProperty("user.home").concat("/Documents/DocumentArchiver");
        assertEquals(tmpExpectedPath, PropertiesUtil.CORE_ARCHIVING_FOLDER.getPath());

        assertEquals("please set in preferences", PropertiesUtil.QUICK_DESCRIPTION_WORDS);
    }

}
