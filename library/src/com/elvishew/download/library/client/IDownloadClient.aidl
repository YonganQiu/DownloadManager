package com.elvishew.download.library.client;

import com.elvishew.download.library.model.DownloadingItem;
import com.elvishew.download.library.model.DownloadedItem;
import com.elvishew.download.library.model.DownloadRequest;
import com.elvishew.download.library.model.DownloadProgressData;

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
