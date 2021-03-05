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

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void testRetrieveDescriptionSafeString() {

        assertEquals("Hallo Welt", StringUtil.retrieveDescriptionSafeString(" Hallo=Welt "));
        assertEquals("Schweizer Käse", StringUtil.retrieveDescriptionSafeString("Schweizer Käse"));
        assertEquals("لوحة المفاتيح العربية",
                StringUtil.retrieveDescriptionSafeString("لوحة المفاتيح العربية"));
        assertEquals("på rött hår", StringUtil.retrieveDescriptionSafeString("på rött hår"));
        assertEquals("中文文本", StringUtil.retrieveDescriptionSafeString("中文文本"));
        assertEquals("Aide-toi et le ciel t aidera",
                StringUtil.retrieveDescriptionSafeString("Aide-toi et le ciel t’aidera"));
        assertEquals("foo bar",
                StringUtil.retrieveDescriptionSafeString("foo\\,/,:,*,\",<,>,|bar.!;"));
        assertEquals("A---M", StringUtil.retrieveDescriptionSafeString("A---M"));
        assertEquals("N Z", StringUtil.retrieveDescriptionSafeString("N___Z"));
        assertEquals("null", StringUtil.retrieveDescriptionSafeString(null));
    }

    @Test
    void testRetrieveTagNameSafeString() {

        assertEquals("Hallo=Welt", StringUtil.retrieveTagNameSafeString(" Hallo=Welt "));
        assertEquals("Schweizer-Käse", StringUtil.retrieveTagNameSafeString("Schweizer Käse"));
        assertEquals("لوحة-المفاتيح-العربية",
                StringUtil.retrieveTagNameSafeString("لوحة المفاتيح العربية"));
        assertEquals("på-rött-hår", StringUtil.retrieveTagNameSafeString("på rött hår"));
        assertEquals("中文文本", StringUtil.retrieveTagNameSafeString("中文文本"));
        assertEquals("Aide-toi-et-le-ciel-t-aidera",
                StringUtil.retrieveTagNameSafeString("Aide-toi et le ciel t’aidera"));
        assertEquals("foo-bar-",
                StringUtil.retrieveTagNameSafeString("foo\\,/,:,*,\",<,>,|bar.!;"));
        assertEquals("A---M", StringUtil.retrieveTagNameSafeString("A---M"));
        assertEquals("N-Z", StringUtil.retrieveTagNameSafeString("N___Z"));
        assertEquals("null", StringUtil.retrieveTagNameSafeString(null));
    }

    @Test
    void testRetrieveFilenameSafeString() {

        assertEquals("Hallo-Welt", StringUtil.retrieveFilenameSafeString(" Hallo=Welt "));
        assertEquals("Schweizer-Käse", StringUtil.retrieveFilenameSafeString("Schweizer Käse"));
        assertEquals("لوحة-المفاتيح-العربية",
                StringUtil.retrieveFilenameSafeString("لوحة المفاتيح العربية"));
        assertEquals("på-rött-hår", StringUtil.retrieveFilenameSafeString("på rött hår"));
        assertEquals("中文文本", StringUtil.retrieveFilenameSafeString("中文文本"));
        assertEquals("Aide-toi-et-le-ciel-t-aidera",
                StringUtil.retrieveFilenameSafeString("Aide-toi et le ciel t’aidera"));
        assertEquals("foo-bar-",
                StringUtil.retrieveFilenameSafeString("foo\\,/,:,*,\",<,>,|bar.!;"));
        assertEquals("A-M", StringUtil.retrieveFilenameSafeString("A---M"));
        assertEquals("N-Z", StringUtil.retrieveFilenameSafeString("N___Z"));
        assertEquals("null", StringUtil.retrieveFilenameSafeString(null));
    }

    @Test
    void testCleanQuickDescriptionWordsString() {

        assertEquals("Hallo Welt,Ganz toll,Los-geht s",
                StringUtil.cleanQuickDescriptionWordsString("Hallo\r\nWelt, Ganz!toll, Los-geht's"));
    }

}
