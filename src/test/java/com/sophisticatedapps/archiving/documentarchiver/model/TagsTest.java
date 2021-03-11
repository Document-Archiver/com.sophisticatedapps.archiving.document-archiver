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

import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeEnum;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for "com.sophisticatedapps.archiving.documentarchiver.model.Tags".
 */
class TagsTest {

    /**
     * Test if existing tags are being delivered correctly.
     */
    @Test
    void testGetExistingTags() {

        ClassLoader classLoader = getClass().getClassLoader();
        File tmpDirectory = new File(Objects.requireNonNull(classLoader
                .getResource("test-archiving-folder")).getFile());

        SortedSet<String> tmpResult = Tags.getExistingTags(tmpDirectory, FileTypeEnum.TXT);

        assertEquals("[bar, foo, fu, sna]", tmpResult.toString());
    }

}
