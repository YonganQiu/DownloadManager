package com.elvishew.download.library;

import java.util.List;

import com.elvishew.download.library.server.DownloadProgressData;
import com.elvishew.download.library.server.DownloadRequest;
import com.elvishew.download.library.server.DownloadedItem;
import com.elvishew.download.library.server.DownloadingItem;

public interface DownloadListener {

    public void onDownloadAddingFail(DownloadRequest request, int errorCode);

    public void onDownloadingAdded(DownloadingItem download);

    public void onDownloadingStateChanged(DownloadingItem download);

    public void onDownloadingsStateChanged(List<DownloadingItem> downloads);

    public void onDownloadingDeleted(DownloadingItem download);

    public void onDownloadingsDeleted(List<DownloadingItem> downloads);

    public void onDownloadingProgressUpdate(DownloadingItem download,
            DownloadProgressData progress);

    public void onDownloadingError(DownloadingItem download, int errorCode);

    public void onDownloadedAdded(DownloadedItem download);

    public void onDownloadedDeleted(DownloadedItem download);

    public void onDownloadedsDeleted(List<DownloadedItem> downloads);
}
