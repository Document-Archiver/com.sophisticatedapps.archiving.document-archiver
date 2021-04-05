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

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DatePickerStringConverterTest {

    @Test
    void testToString() {

        DatePickerStringConverter tmpDatePickerStringConverter = new DatePickerStringConverter();

        LocalDate tmpLocalDate = LocalDate.now();

        assertEquals(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.format(tmpLocalDate),
                tmpDatePickerStringConverter.toString(tmpLocalDate));
        assertEquals(StringUtil.EMPTY_STRING, tmpDatePickerStringConverter.toString(null));    }

    @Test
    void testFromString() {

        DatePickerStringConverter tmpDatePickerStringConverter = new DatePickerStringConverter();

        LocalDate tmpLocalDate = LocalDate.now();

        assertEquals(tmpLocalDate, tmpDatePickerStringConverter
                .fromString(GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.format(tmpLocalDate)));
        assertNull(tmpDatePickerStringConverter.fromString(null));
        assertNull(tmpDatePickerStringConverter.fromString(StringUtil.EMPTY_STRING));
        assertNull(tmpDatePickerStringConverter.fromString("31.01"));
    }

}
