package com.elvishew.download.library.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.elvishew.download.library.DownloadException;
import com.elvishew.download.library.client.DownloadManager;



public class HttpUtil {

    public static long getRemoteFileLength(String urlString) throws DownloadException {
        try {
            URL url = new URL(urlString);
            int remoteFileLength;
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            remoteFileLength = connection.getContentLength();
            connection.disconnect();
            return remoteFileLength;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException(DownloadManager.ERROR_FETCH_FILE_LENGTH_FAIL);
        }
    }

    /**
     * Create a local file.<br>
     * If file already exists, just delete it first.<br>
     * 
     * @param savePath the path that local file should be saved
     * @param fileLength the length of local file
     * @throws FileNotCreatedException
     *             If new file not created successfully
     */
    public static void createLocalTempFile(String savePath, long fileLength) throws DownloadException {
        File file = new File(savePath);

        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        // Cache file? Just delete it.
        if (file.exists()) {
            file.delete();
        }

        try {
            if (StorageUtils.getAvailableStorage() > fileLength && file.createNewFile()) {
                // Initialize file length, should be set to the length of remote
                // file.
                RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
                accessFile.setLength(fileLength);
                accessFile.close();
            } else {
                throw new DownloadException(DownloadManager.ERROR_CREATE_FILE_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException(DownloadManager.ERROR_CREATE_FILE_FAIL);
        }
    }

}
