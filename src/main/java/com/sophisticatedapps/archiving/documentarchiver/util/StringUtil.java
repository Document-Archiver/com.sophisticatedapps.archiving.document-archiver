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

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtil {

    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("[^\\p{Sc}\\p{So}\\p{Mn}ء-يÀ-ÿa-zA-Z0-9\\p{script=Han}-]+");
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("[^\\p{Sc}\\p{So}\\p{Mn}ء-يÀ-ÿa-zA-Z0-9\\p{script=Han}=-]+");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("[^\\p{Sc}\\p{So}\\p{Mn}ء-يÀ-ÿa-zA-Z0-9\\p{script=Han}]+");
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public static String retrieveDescriptionSafeString(String aString) {

        return DESCRIPTION_PATTERN.matcher(String.valueOf(aString)).replaceAll(" ").trim();
    }

    public static String retrieveTagNameSafeString(String aString) {

        return TAG_NAME_PATTERN.matcher(String.valueOf(aString).trim()).replaceAll("-");
    }

    public static String retrieveFilenameSafeString(String aString) {

        return FILENAME_PATTERN.matcher(String.valueOf(aString).trim()).replaceAll("-");
    }

    public static String cleanQuickDescriptionWordsString(String aString) {

        return COMMA_PATTERN.splitAsStream(aString)
                .map(StringUtil::retrieveDescriptionSafeString) // .map(aChunk -> retrieveDescriptionSafeString(aChunk))
                .collect(Collectors.joining(","));
    }

}
