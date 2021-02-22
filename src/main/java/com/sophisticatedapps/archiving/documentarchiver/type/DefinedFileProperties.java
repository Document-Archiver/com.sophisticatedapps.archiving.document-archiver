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

import java.time.LocalDate;
import java.util.List;

public class DefinedFileProperties {

    LocalDate date;
    boolean utilizeTimeInformation;
    String timeInformation;
    String description;
    List<String> tags;
    FileTypeEnum fileType;

    public DefinedFileProperties(LocalDate aDate, boolean anUtilizeTimeInformation, String aTimeInformation,
                                 String aDescription, List<String> aTagsList, FileTypeEnum aFileType) {

        this.date = aDate;
        this.utilizeTimeInformation = anUtilizeTimeInformation;
        this.timeInformation = aTimeInformation;
        this.description = aDescription;
        this.tags = aTagsList;
        this.fileType = aFileType;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isUtilizeTimeInformation() {
        return utilizeTimeInformation;
    }

    public String getTimeInformation() {
        return timeInformation;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

}
