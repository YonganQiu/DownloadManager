package com.elvishew.download.library.server;

import com.elvishew.download.library.server.DownloadingItem;
import com.elvishew.download.library.server.DownloadedItem;
import com.elvishew.download.library.server.DownloadRequest;
import com.elvishew.download.library.server.DownloadProgressData;

interface IDownloadClient {
    void onDownloadAddingFail(in DownloadRequest request, int errorCode);
    void onDownloadingAdded(in DownloadingItem download);
    void onDownloadingStateChanged(in DownloadingItem download);
    void onDownloadingsStateChanged(in List<DownloadingItem> downloads);
    void onDownloadingDeleted(in DownloadingItem download);
    void onDownloadingsDeleted(in List<DownloadingItem> downloads);
    void onDownloadingProgressUpdate(in DownloadingItem download, in DownloadProgressData progress);
    void onDownloadingError(in DownloadingItem download, int errorCode);

    void onDownloadedAdded(in DownloadedItem download);
    void onDownloadedDeleted(in DownloadedItem download);
    void onDownloadedsDeleted(in List<DownloadedItem> downloads);
}
