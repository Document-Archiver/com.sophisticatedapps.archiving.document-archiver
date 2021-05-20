package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.BaseTest;
import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantUtilTest extends BaseTest {

    @Test
    void testGetAvailableTenants() throws IllegalAccessException {

        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", TEST_ARCHIVING_FOLDER, true);

        List<String> tmpTenantList = TenantUtil.getAvailableTenants();
        assertEquals(1, tmpTenantList.size());
        assertTrue(tmpTenantList.contains("MyTenant"));

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void testGetAvailableTenants_archiving_folder_not_existing_yet() throws IllegalAccessException {

        FieldUtils.writeStaticField(DirectoryUtil.class, "coreArchivingFolder", new File("/foo"), true);

        List<String> tmpTenantList = TenantUtil.getAvailableTenants();
        assertEquals(0, tmpTenantList.size());

        FieldUtils.writeStaticField(
                DirectoryUtil.class, "coreArchivingFolder", PropertiesUtil.CORE_ARCHIVING_FOLDER, true);
    }

    @Test
    void testGetTenantFolderName() {

        assertEquals("", TenantUtil.getTenantFolderName(GlobalConstants.DEFAULT_TENANT_NAME));
        assertEquals("@FooBar", TenantUtil.getTenantFolderName("FooBar"));
    }

}
