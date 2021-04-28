package com.sophisticatedapps.archiving.documentarchiver.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppDirUtilTest {

    @Test
    void testGetUserDataDir() {

        String tmpUserDataDirPath = AppDirUtil.getUserDataDir().getPath();

        assertTrue(tmpUserDataDirPath.startsWith(System.getProperty("user.home")));
        assertTrue(tmpUserDataDirPath.endsWith("/DocumentArchiver"));
    }

}
