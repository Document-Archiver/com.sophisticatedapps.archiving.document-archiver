package com.sophisticatedapps.archiving.documentarchiver.type;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;

import java.util.Objects;

public class Tenant implements Comparable<Tenant> {

    private String name;

    public Tenant(String aName) {

        this.name = aName;
    }

    public String getName() {

        return name;
    }

    public String getFolderName() {

        return (GlobalConstants.DEFAULT_TENANT_NAME.equals(name) ?
                StringUtil.EMPTY_STRING : GlobalConstants.TENANT_FOLDER_PREFIX.concat(name));
    }

    @Override
    public int compareTo(Tenant anOtherTenant) {

        return this.name.compareTo(anOtherTenant.name);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(name, tenant.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

}
