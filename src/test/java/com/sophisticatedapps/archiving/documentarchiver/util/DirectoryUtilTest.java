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

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.type.FileTypeGroupEnum;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryUtilTest extends BaseTest {

    @Test
    void getArchivingRootFolder() {

        assertSame(GlobalConstants.ARCHIVING_ROOT_FOLDER, DirectoryUtil.getArchivingRootFolder());
    }

    @Test
    void setArchivingRootFolder() {

        DirectoryUtil.setArchivingRootFolder(TEST_ARCHIVING_FOLDER);
        assertSame(TEST_ARCHIVING_FOLDER, DirectoryUtil.getArchivingRootFolder());
        DirectoryUtil.setArchivingRootFolder(GlobalConstants.ARCHIVING_ROOT_FOLDER);
    }

    @Test
    void getGroupingFolder() {

        File tmpPDFsGroupingFolder = DirectoryUtil.getGroupingFolder(FileTypeGroupEnum.PDFS);

        assertEquals((new File(GlobalConstants.ARCHIVING_ROOT_FOLDER.getPath() + "/pdfs")),
                tmpPDFsGroupingFolder);
    }

    @Test
    void getArchivingFolder() {

        File tmpArchivingFolder = DirectoryUtil.getArchivingFolder(FileTypeGroupEnum.PDFS, 1975);

        assertEquals((new File(GlobalConstants.ARCHIVING_ROOT_FOLDER.getPath() + "/pdfs/1975")),
                tmpArchivingFolder);
    }

}
