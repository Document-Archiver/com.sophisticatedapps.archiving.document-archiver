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

public enum FileTypeGroupEnum {

    PDFS("pdfs"),
    TEXTS("texts"),
    IMAGES("images"),
    OFFICE("office"),
    AUDIO("audio"),
    VIDEOS("videos"),
    MISC("misc");

    private final String groupingFolder;

    /**
     * Initializes a FileTypeGroup with a given grouping folder.
     *
     * @param   aGroupingFolder Name of the subfolder within the archiving folder.
     */
    FileTypeGroupEnum(String aGroupingFolder) {

        this.groupingFolder = aGroupingFolder;
    }

    /**
     * Get the grouping folder of the enum instance.
     *
     * @return  Grouping folder of the enum instance.
     */
    public String getGroupingFolder() {

        return groupingFolder;
    }

}
