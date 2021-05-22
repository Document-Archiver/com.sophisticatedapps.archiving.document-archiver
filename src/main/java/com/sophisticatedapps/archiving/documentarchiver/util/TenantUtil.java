package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;
import com.sophisticatedapps.archiving.documentarchiver.type.Tenant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TenantUtil {

    private static final FileFilter TENANT_DIRECTORIES_FILE_FILTER =
            (aFile -> (aFile.isDirectory() && aFile.getName().startsWith(GlobalConstants.TENANT_FOLDER_PREFIX)));

    /**
     * Private constructor.
     */
    private TenantUtil() {
    }

    public static List<String> getAvailableTenantNames() {

        List<String> tmpResultList = new ArrayList<>();
        File tmpCoreArchivingFolder = DirectoryUtil.getCoreArchivingFolder();

        if (tmpCoreArchivingFolder.exists()) {

            File[] tmpTenantDirectories =
                    Objects.requireNonNull(tmpCoreArchivingFolder.listFiles(TENANT_DIRECTORIES_FILE_FILTER));

            for (File tmpCurrentDirectory : tmpTenantDirectories) {

                tmpResultList.add(tmpCurrentDirectory.getName().substring(1));
            }
        }

        tmpResultList.sort(Comparator.naturalOrder());
        return tmpResultList;
    }

    @SuppressWarnings("unchecked")
    public static List<Tenant> getAvailableTenants() {

        List<Tenant> tmpResultList = new ArrayList<>();
        File tmpCoreArchivingFolder = DirectoryUtil.getCoreArchivingFolder();

        if (tmpCoreArchivingFolder.exists()) {

            File[] tmpTenantDirectories =
                    Objects.requireNonNull(tmpCoreArchivingFolder.listFiles(TENANT_DIRECTORIES_FILE_FILTER));

            for (File tmpCurrentDirectory : tmpTenantDirectories) {

                tmpResultList.add(new Tenant(tmpCurrentDirectory.getName().substring(1)));
            }
        }

        tmpResultList.sort(Comparator.naturalOrder());
        return tmpResultList;
    }

    public static void persistTenant(Tenant aTenant) throws IOException {

        Files.createDirectories((new File(DirectoryUtil.getCoreArchivingFolder(), aTenant.getFolderName())).toPath());
    }

    public static void deleteTenant(Tenant aTenant) throws IOException {

        DirectoryUtil.deleteRecursively(new File(DirectoryUtil.getCoreArchivingFolder(), aTenant.getFolderName()));
    }

}
