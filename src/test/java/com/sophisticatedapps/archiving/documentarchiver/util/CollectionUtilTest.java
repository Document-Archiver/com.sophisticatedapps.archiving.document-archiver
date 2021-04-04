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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilTest {

    @Test
    void testIsNullOrEmpty() {

        assertTrue(CollectionUtil.isNullOrEmpty(null));
        assertTrue(CollectionUtil.isNullOrEmpty(new ArrayList<>()));
        assertFalse(CollectionUtil.isNullOrEmpty(Set.of("Hello")));
    }

    @Test
    void testAddToListIfNotContainedYet() {

        String tmpFoo = "foo";
        List<String> tmpList = new ArrayList<>();
        tmpList.add(tmpFoo);

        CollectionUtil.addToListIfNotContainedYet(tmpList, tmpFoo);
        assertEquals(1, tmpList.size());

        CollectionUtil.addToListIfNotContainedYet(tmpList, "bar");
        assertEquals(2, tmpList.size());
    }

}
