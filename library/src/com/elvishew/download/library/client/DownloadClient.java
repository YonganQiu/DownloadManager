
package com.elvishew.download.library.client;

import java.util.List;

import android.os.RemoteException;

import com.elvishew.download.library.model.DownloadProgressData;
import com.elvishew.download.library.model.DownloadRequest;
import com.elvishew.download.library.model.DownloadedItem;
import com.elvishew.download.library.model.DownloadingItem;

public abstract class DownloadClient implements DownloadListener {

    private Transport mTransport = new Transport();

    public IDownloadClient getIDownloadClient() {
        return mTransport;
    }

    class Transport extends IDownloadClient.Stub {

        @Override
        public void onDownloadAddingFail(DownloadRequest request, int errorCode) throws RemoteException {
            DownloadClient.this.onDownloadAddingFail(request, errorCode);
        }

        @Override
        public void onDownloadingAdded(DownloadingItem download) {
            DownloadClient.this.onDownloadingAdded(download);
        }

        @Override
        public void onDownloadingStateChanged(DownloadingItem download) {
            DownloadClient.this.onDownloadingStateChanged(download);
        }

        @Override
        public void onDownloadingsStateChanged(List<DownloadingItem> downloads) {
            DownloadClient.this.onDownloadingsStateChanged(downloads);
        }

        @Override
        public void onDownloadingDeleted(DownloadingItem download) {
            DownloadClient.this.onDownloadingDeleted(download);
        }

        @Override
        public void onDownloadingsDeleted(List<DownloadingItem> downloads) throws RemoteException {
            DownloadClient.this.onDownloadingsDeleted(downloads);
        }

        @Override
        public void onDownloadingProgressUpdate(DownloadingItem download,
                DownloadProgressData progress) {
            DownloadClient.this.onDownloadingProgressUpdate(download, progress);
        }

        @Override
        public void onDownloadingError(DownloadingItem download, int errorCode)
                throws RemoteException {
            DownloadClient.this.onDownloadingError(download, errorCode);
        }

        @Override
        public void onDownloadedAdded(DownloadedItem download) {
            DownloadClient.this.onDownloadedAdded(download);
        }

        @Override
        public void onDownloadedDeleted(DownloadedItem download) {
            DownloadClient.this.onDownloadedDeleted(download);
        }

        @Override
        public void onDownloadedsDeleted(List<DownloadedItem> downloads) throws RemoteException {
            DownloadClient.this.onDownloadedsDeleted(downloads);
        }
    }

}
