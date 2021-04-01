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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LanguageUtil {

    private static final String I18N_BUNDLE_BASENAME = "document-archiver-i18n";
    private static final Map<Locale, ResourceBundle> RESOURCE_BUNDLE_CACHE = new HashMap<>();
    private static final Locale CURRENT_LANGUAGE_LOCALE = PropertiesUtil.LANGUAGE_LOCALE;

    /**
     * Private constructor.
     */
    private LanguageUtil() {
    }

    /**
     * Get the currently active language Locale.
     *
     * @return  Currently active language Locale.
     */
    public static Locale getCurrentLanguageLocale() {

        return CURRENT_LANGUAGE_LOCALE;
    }

    /**
     * Set a new current Locale.
     *
     * @param   aLanguageLocale   Locale to set.
     */
    public static void setNewLanguage(Locale aLanguageLocale) {

        // Write to properties (user has to restart app to take effect).
        try {

            PropertiesUtil.updateApplicationLanguageLocale(aLanguageLocale);
        }
        catch (IOException e) {

            throw (new RuntimeException("New language could not be persisted: " + e.getMessage()));
        }
    }

    public static ResourceBundle getResourceBundleForCurrentLanguage() {

        return getResourceBundleForLanguageLocale(CURRENT_LANGUAGE_LOCALE);
    }

    public static ResourceBundle getResourceBundleForLanguageLocale(Locale aLanguageLocale) {

        return RESOURCE_BUNDLE_CACHE.computeIfAbsent(aLanguageLocale,
                (aLocale -> ResourceBundle.getBundle(I18N_BUNDLE_BASENAME, aLanguageLocale)));
    }

    public static String i18n(String aPropertyKey, Object... aParams) {

        return i18n(aPropertyKey, CURRENT_LANGUAGE_LOCALE, aParams);
    }

    public static String i18n(String aPropertyKey, Locale aLanguageLocale, Object... aParams) {

        if (aParams.length < 1) {

            return getResourceBundleForLanguageLocale(aLanguageLocale).getString(aPropertyKey);
        }
        else {

            return MessageFormat.format(
                    getResourceBundleForLanguageLocale(aLanguageLocale).getString(aPropertyKey), aParams);
        }
    }

}
