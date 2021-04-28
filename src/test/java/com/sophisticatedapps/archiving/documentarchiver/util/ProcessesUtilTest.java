package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ProcessesUtilTest extends BaseTest {

    @TempDir
    File tempDir;

    @Test
    void createTempJpg_with_exception() {

        File tmpBrokenFile = new File(NUL_CHARACTER_STRING);

        assertThrows(IOException.class, () -> ProcessesUtil.createTempJpg(tmpBrokenFile));
    }

    @Test
    void testGetImg2JpgPath() throws IllegalAccessException, IOException {

        // Exchange the user data directory
        File tmpOriginalUserDataDirectory = (File) FieldUtils.readStaticField(
                AppDirUtil.class, "userDataDir", true);
        File tmpTempUserDataDirectory = new File(tempDir, ".documentarchiver");
        Files.createDirectories(tmpTempUserDataDirectory.toPath());
        FieldUtils.writeStaticField(AppDirUtil.class,"userDataDir",
                tmpTempUserDataDirectory, true);

        File tmpImg2JpgBinary = new File(tmpTempUserDataDirectory, "img2jpg");

        // First time -> Copy binary
        String tmpResult = ProcessesUtil.getImg2JpgPath();
        assertEquals(tmpImg2JpgBinary.getPath(), tmpResult);
        assertTrue(tmpImg2JpgBinary.exists());

        // Second time -> binary exists
        tmpResult = ProcessesUtil.getImg2JpgPath();
        assertEquals(tmpImg2JpgBinary.getPath(), tmpResult);
        assertTrue(tmpImg2JpgBinary.exists());

        // Change local properties directory back
        FieldUtils.writeStaticField(AppDirUtil.class,"userDataDir",
                tmpOriginalUserDataDirectory, true);
    }

}
