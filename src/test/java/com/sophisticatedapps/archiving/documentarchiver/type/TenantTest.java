package com.sophisticatedapps.archiving.documentarchiver.type;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TenantTest {

    private static final Tenant DEFAULT_TENANT = new Tenant(GlobalConstants.DEFAULT_TENANT_NAME);
    private static final Tenant FOO_BAR_TENANT = new Tenant("FooBar");

    @Test
    void testGetFolderName() {

        assertEquals("", DEFAULT_TENANT.getFolderName());
        assertEquals("@FooBar", FOO_BAR_TENANT.getFolderName());
    }

    @Test
    void testGetDisplayName() {

        assertEquals("Default Tenant", DEFAULT_TENANT.getDisplayName());
        assertEquals("FooBar", FOO_BAR_TENANT.getDisplayName());
    }

    @Test
    void testCompareTo() {

        assertEquals(
                DEFAULT_TENANT.getName().compareTo(FOO_BAR_TENANT.getName()), DEFAULT_TENANT.compareTo(FOO_BAR_TENANT));
    }

    @Test
    void testEquals() {

        assertEquals(DEFAULT_TENANT, DEFAULT_TENANT);
        assertNotEquals(null, DEFAULT_TENANT);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals("_", DEFAULT_TENANT);
        assertEquals(FOO_BAR_TENANT, new Tenant("FooBar"));
    }

    @Test
    void testHashCode() {

        assertEquals(Objects.hash(DEFAULT_TENANT.getName()), DEFAULT_TENANT.hashCode());
    }

}
