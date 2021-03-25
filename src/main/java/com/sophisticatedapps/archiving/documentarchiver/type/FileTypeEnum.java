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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum FileTypeEnum {

    PDF(FileTypeGroupEnum.PDFS, false, "pdf"),
    TXT(FileTypeGroupEnum.TEXTS, false, "txt"),
    JPG(FileTypeGroupEnum.IMAGES, true, "jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
    PNG(FileTypeGroupEnum.IMAGES, true, "png"),
    GIF(FileTypeGroupEnum.IMAGES, true, "gif"),
    HEIC(FileTypeGroupEnum.IMAGES, true, "heic"),
    XML(FileTypeGroupEnum.TEXTS, false, "xml"),
    DOC(FileTypeGroupEnum.OFFICE, false, "doc", "docx"),
    MP3(FileTypeGroupEnum.AUDIO, false, "mp3"),
    M4A(FileTypeGroupEnum.AUDIO, false, "m4a"),
    WAV(FileTypeGroupEnum.AUDIO, false, "wav", "wave"),
    UNSUPPORTED(FileTypeGroupEnum.MISC, false);

    private static final Map<String, FileTypeEnum> LOOKUP = new HashMap<>();
    private final Set<String> fileExtensions;
    private final FileTypeGroupEnum fileTypeGroup;
    private final boolean utilizeTimeInformationDefault;

    static {

        for (FileTypeEnum tmpCurrentType : EnumSet.allOf(FileTypeEnum.class)) {

            for (String tmpCurrentExtension : tmpCurrentType.getFileExtensions()) {

                LOOKUP.put(tmpCurrentExtension, tmpCurrentType);
            }
        }
    }

    /**
     * Initializes a FileType with a given file extension.
     *
     * @param   aFileTypeGroup                      File type group to apply to the instance.
     * @param   anUtilizeTimeInformationDefault     If time information should be utilized by default.
     * @param   aFileExtensions                     File extension(s) to apply to the instance.
     */
    FileTypeEnum(FileTypeGroupEnum aFileTypeGroup, boolean anUtilizeTimeInformationDefault, String... aFileExtensions) {

        this.fileTypeGroup = aFileTypeGroup;
        this.utilizeTimeInformationDefault = anUtilizeTimeInformationDefault;
        this.fileExtensions = Set.of(aFileExtensions);
    }

    /**
     * Get the file extensions of the enum instance.
     *
     * @return  File extensions of the enum instance.
     */
    public Set<String> getFileExtensions() {

        return fileExtensions;
    }

    /**
     * Get the file type group of the enum instance.
     *
     * @return  File type group of the enum instance.
     */
    public FileTypeGroupEnum getFileTypeGroup() {

        return fileTypeGroup;
    }

    /**
     * If time information should be used by default for the file type.
     *
     * @return  TRUE, if time information should be used by default; FALSE, if not.
     */
    public boolean isUtilizeTimeInformationDefault() {

        return utilizeTimeInformationDefault;
    }

    /**
     * Get an FileTypeEnum by a given file extension.
     *
     * @param   aFileExtension to get the FileTypeEnum for.
     * @param   aReturnUnsupportedIfNotFound  If the UNSUPPORTED FileType should be returned if no match found.
     * @return  {@link FileTypeEnum} matching the given file extension.
     * @throws  IllegalArgumentException, if an invalid (unknown) file extension was given.
     */
    public static FileTypeEnum byFileExtension(String aFileExtension, boolean aReturnUnsupportedIfNotFound)
            throws IllegalArgumentException {

        FileTypeEnum tmpResult = LOOKUP.get(aFileExtension.toLowerCase());

        if (tmpResult == null) {

            if (aReturnUnsupportedIfNotFound) {

                return FileTypeEnum.UNSUPPORTED;
            }

            throw new IllegalArgumentException("No FileTypeEnum for file extension: ".concat(aFileExtension));
        }

        return tmpResult;
    }

}
