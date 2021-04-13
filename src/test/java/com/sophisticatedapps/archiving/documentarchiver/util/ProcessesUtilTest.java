package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

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

        // Exchange the local properties directory
        File tmpOriginalLocalPropertiesDirectory = (File) FieldUtils.readStaticField(
                PropertiesUtil.class, "localPropertiesDirectory", true);
        File tmpTempLocalPropertiesDirectory = new File(tempDir, ".documentarchiver");
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpTempLocalPropertiesDirectory, true);

        File tmpImg2JpgBinary = new File(tmpTempLocalPropertiesDirectory, "img2jpg");

        // First time -> Copy binary
        String tmpResult = ProcessesUtil.getImg2JpgPath();
        assertEquals(tmpImg2JpgBinary.getPath(), tmpResult);
        assertTrue(tmpImg2JpgBinary.exists());

        // Second time -> binary exists
        tmpResult = ProcessesUtil.getImg2JpgPath();
        assertEquals(tmpImg2JpgBinary.getPath(), tmpResult);
        assertTrue(tmpImg2JpgBinary.exists());

        // Change local properties directory back
        FieldUtils.writeStaticField(PropertiesUtil.class,"localPropertiesDirectory",
                tmpOriginalLocalPropertiesDirectory, true);
    }

}
