package com.sophisticatedapps.archiving.documentarchiver.type;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenantTest {

    @Test
    void getFolderName() {

        assertEquals("", (new Tenant(GlobalConstants.DEFAULT_TENANT_NAME)).getFolderName());
        assertEquals("@FooBar", (new Tenant("FooBar")).getFolderName());
    }

}
