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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * StringConverter for DatePickers.
 */
public class DatePickerStringConverter extends StringConverter<LocalDate> {

    @Override
    public String toString(LocalDate aLocalDate) {

        if (Objects.isNull(aLocalDate)) {

            return StringUtil.EMPTY_STRING;
        }

        return GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER.format(aLocalDate);
    }

    @Override
    public LocalDate fromString(String aDateString) {

        if (Objects.isNull(aDateString) || (aDateString.trim().isEmpty())) {

            return null;
        }

        try {

            return LocalDate.parse(aDateString, GlobalConstants.DD_MM_YYYY_DATE_TIME_FORMATTER);
        }
        catch (DateTimeParseException e) {

            return null;
        }
    }

}
