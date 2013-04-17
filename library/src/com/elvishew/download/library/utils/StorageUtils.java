
package com.elvishew.download.library.utils;

import java.io.File;

import android.os.Environment;

public class StorageUtils {

    public static boolean isSdCardWrittenable() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static long getAvailableStorage() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    public static long getTotalStorage() {
        return Environment.getExternalStorageDirectory().getTotalSpace();
    }

    public static long getQipaoxianStorage() {
        return getFileSize(new File(PathUtil.getDataDirectoryPath()));
    }

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Get the total size of a file or folder.
     */
    private static long getFileSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        long size = 0;
        if (!file.isDirectory()) {
            size = file.length();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        size += getFileSize(f);
                    } else {
                        size += f.length();
                    }
                }
            }
        }
        return size;
    }

}
