package com.elvishew.download.library.server;

import com.elvishew.download.library.server.DownloadingItem;
import com.elvishew.download.library.server.DownloadedItem;
import com.elvishew.download.library.server.DownloadRequest;
import com.elvishew.download.library.server.IDownloadManager;
import com.elvishew.download.library.server.IDownloadClient;

interface IDownloadManager {

    void registerClient(String requester, IDownloadClient client);
    void unregisterClient(String requester);

    List<DownloadingItem> getAllDownloadings(String requester);
    List<DownloadedItem> getAllDownloadeds(String requester);
    boolean hasDownloading(String requester, String url);
    boolean hasDownloadings(String requester);
    boolean hasDownloaded(String requester, String url);
    boolean hasDownloadeds(String requester);

    int addDownloading(in DownloadRequest request);
    int resumeDownloading(String requester, String url);
    int deleteDownloading(String requester, String url);
    int deleteAllDownloadings(String requester);
    int pauseDownloading(String requester, String url);
    int pauseAllDownloadings(String requester);

    int deleteDownloaded(String requester, String url);
    int deleteAllDownloadeds(String requester);

    String getLocalUrlIfDownloaded(String requester, String url);
}
