package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.GlobalConstants;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
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

    public static List<String> getAvailableTenants() {

        List<String> tmpResultList = new ArrayList<>();
        File tmpCoreArchivingFolder = DirectoryUtil.getCoreArchivingFolder();

        if (tmpCoreArchivingFolder.exists()) {

            File[] tmpTenantDirectories =
                    Objects.requireNonNull(tmpCoreArchivingFolder.listFiles(TENANT_DIRECTORIES_FILE_FILTER));

            for (File tmpCurrentDirectory : tmpTenantDirectories) {

                tmpResultList.add(tmpCurrentDirectory.getName().substring(1));
            }
        }

        return tmpResultList;
    }

    public static String getTenantFolderName(String aTenant) {

        return (GlobalConstants.DEFAULT_TENANT_NAME.equals(aTenant) ?
                StringUtil.EMPTY_STRING : GlobalConstants.TENANT_FOLDER_PREFIX.concat(aTenant));
    }

}
