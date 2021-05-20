package com.sophisticatedapps.archiving.documentarchiver.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ProcessesUtil {

    private static final File IMG2JPG_FILE = new File(AppDirUtil.getUserDataDir(), "img2jpg");

    /**
     * Private constructor.
     */
    private ProcessesUtil() {
    }

    public static File createTempJpg(File aSourceFile) throws IOException {

        // Create JPG from HEIC
        File tmpTempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", PropertiesUtil.CORE_ARCHIVING_FOLDER);

        try {

            ProcessBuilder tmpProcessBuilder = new ProcessBuilder(getImg2JpgPath(), aSourceFile.getPath(), tmpTempFile.getPath());
            Process tmpProcess = tmpProcessBuilder.start();
            tmpProcess.waitFor();

            return tmpTempFile;
        }
        catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            Files.delete(tmpTempFile.toPath());
            throw (new IOException("Could not create temporary JPG file: " + e.getMessage()));
        }
        catch (IOException e) {

            Files.delete(tmpTempFile.toPath());
            throw e;
        }
    }

    protected static String getImg2JpgPath() throws IOException {

        // Is binary already copied? If not, copy and chmod.
        if (!IMG2JPG_FILE.exists()) {

            Path tmpTarget = IMG2JPG_FILE.toPath();
            Files.copy(Objects.requireNonNull(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("img2jpg")), tmpTarget);

            Set<PosixFilePermission> tmpPerms = new HashSet<>();
            //add owners permission
            tmpPerms.add(PosixFilePermission.OWNER_READ);
            tmpPerms.add(PosixFilePermission.OWNER_WRITE);
            tmpPerms.add(PosixFilePermission.OWNER_EXECUTE);
            //add group permissions
            tmpPerms.add(PosixFilePermission.GROUP_READ);
            tmpPerms.add(PosixFilePermission.GROUP_EXECUTE);

            Files.setPosixFilePermissions(tmpTarget, tmpPerms);
        }

        return IMG2JPG_FILE.getPath();
    }

}
