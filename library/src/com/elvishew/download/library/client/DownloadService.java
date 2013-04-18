
package com.elvishew.download.library.client;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.elvishew.download.library.DownloadListener;
import com.elvishew.download.library.server.DownloadClient;
import com.elvishew.download.library.server.DownloadManager;
import com.elvishew.download.library.server.DownloadManager.ServiceConnectionListener;
import com.elvishew.download.library.server.DownloadProgressData;
import com.elvishew.download.library.server.DownloadRequest;
import com.elvishew.download.library.server.DownloadableItem;
import com.elvishew.download.library.server.DownloadedItem;
import com.elvishew.download.library.server.DownloadingItem;

public class DownloadService {

    public static final String DOWNLOAD_SERVICE = "download_qpx";

    private Context mContext;
    private DownloadManager mDownloadManager;
    private DownloadClient mDownloadClient;

    private String mRequester;

    private boolean mDownloadServiceAlive;

    private List<DownloadListener> mDownloadListeners;

    public interface OnServiceStateChangedListener {
        void onServiceStateChanged(boolean alive);
    }

    public DownloadService(Context context, String requester, final OnServiceStateChangedListener l) {
        mContext = context;
        mRequester = requester;
        mDownloadClient = new DefaultDownloadClient();

        DownloadManager.bindService(mContext, new ServiceConnectionListener() {

            @Override
            public void onServiceConnectionSuccess(DownloadManager downloadManager) {
                mDownloadManager = downloadManager;
                mDownloadManager.registerClient(mRequester, mDownloadClient.getIDownloadClient());
                mDownloadServiceAlive = true;
                l.onServiceStateChanged(true);
            }

            @Override
            public void onServiceConnectionDisconnected() {
                mDownloadManager = null;
                mDownloadServiceAlive = false;
                l.onServiceStateChanged(false);
            }

            @Override
            public void onServiceConnectionFailed() {
                mDownloadManager = null;
                mDownloadServiceAlive = false;
                l.onServiceStateChanged(false);
            }
        });

        mDownloadListeners = new ArrayList<DownloadListener>();
    }

    public List<DownloadingItem> getAllDownloadings() {
        ensureDownloadManager();
        return mDownloadManager.getAllDownloadings(mRequester);
    }

    public List<DownloadedItem> getAllDownloadeds() {
        ensureDownloadManager();
        return mDownloadManager.getAllDownloadeds(mRequester);
    }

    public boolean hasDownloading(String url) {
        ensureDownloadManager();
        return mDownloadManager.hasDownloading(mRequester, url);
    }

    public boolean hasDownloadings() {
        ensureDownloadManager();
        return mDownloadManager.hasDownloadings(mRequester);
    }

    public boolean hasDownloaded(String url) {
        ensureDownloadManager();
        return mDownloadManager.hasDownloaded(mRequester, url);
    }

    public boolean hasDownloadeds() {
        ensureDownloadManager();
        return mDownloadManager.hasDownloadeds(mRequester);
    }

    public int addDownloading(DownloadableItem video, String savePath) {
        ensureDownloadManager();
        return mDownloadManager.addDownloading(new DownloadRequest(video, mRequester, savePath));
    }

    public int resumeDownloading(String url) {
        ensureDownloadManager();
        return mDownloadManager.resumeDownloading(mRequester, url);
    }

    public int deleteDownloading(String url) {
        ensureDownloadManager();
        return mDownloadManager.deleteDownloading(mRequester, url);
    }

    public int deleteAllDownloadings() {
        ensureDownloadManager();
        return mDownloadManager.deleteAllDownloadings(mRequester);
    }

    public int pauseDownloading(String url) {
        ensureDownloadManager();
        return mDownloadManager.pauseDownloading(mRequester, url);
    }

    public int pauseAllDownloadings() {
        ensureDownloadManager();
        return mDownloadManager.pauseAllDownloadings(mRequester);
    }

    public int deleteDownloaded(String url) {
        ensureDownloadManager();
        return mDownloadManager.deleteDownloaded(mRequester, url);
    }

    public int deleteAllDownloadeds() {
        ensureDownloadManager();
        return mDownloadManager.deleteAllDownloadeds(mRequester);
    }

    public String getLocalUrlIfDownloaded(String url) {
        ensureDownloadManager();
        return mDownloadManager.getLocalUrlIfDownloaded(mRequester, url);
    }

    public void registerDownloadListener(DownloadListener listener) {
        mDownloadListeners.add(listener);
    }

    public void unregisterDownloadListener(DownloadListener listener) {
        mDownloadListeners.remove(listener);
    }

    public boolean isDownloadServiceAvailable() {
        return mDownloadServiceAlive;
    }

    private void ensureDownloadManager() {
        if (!mDownloadServiceAlive) {
            throw new IllegalStateException("Download service is not alive, are you sure that" +
                    " you have receive #onServiceConnectionSuccess ?");
        }
    }

    private class DefaultDownloadClient extends DownloadClient {

        @Override
        public void onDownloadAddingFail(DownloadRequest request, int errorCode) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadAddingFail(request, errorCode);
            }
        }

        @Override
        public void onDownloadingAdded(DownloadingItem download) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingAdded(download);
            }
        }

        @Override
        public void onDownloadingStateChanged(DownloadingItem download) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingStateChanged(download);
            }
        }

        @Override
        public void onDownloadingsStateChanged(List<DownloadingItem> downloads) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingsStateChanged(downloads);
            }
        }

        @Override
        public void onDownloadingDeleted(DownloadingItem download) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingDeleted(download);
            }
        }

        @Override
        public void onDownloadingsDeleted(List<DownloadingItem> downloads) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingsDeleted(downloads);
            }
        }

        @Override
        public void onDownloadingProgressUpdate(DownloadingItem download,
                DownloadProgressData progress) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingProgressUpdate(download, progress);
            }
        }

        @Override
        public void onDownloadingError(DownloadingItem download, int errorCode) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadingError(download, errorCode);
            }
        }

        @Override
        public void onDownloadedAdded(DownloadedItem download) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadedAdded(download);
            }
        }

        @Override
        public void onDownloadedDeleted(DownloadedItem download) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadedDeleted(download);
            }
        }

        @Override
        public void onDownloadedsDeleted(List<DownloadedItem> downloads) {
            for (DownloadListener l : mDownloadListeners) {
                l.onDownloadedsDeleted(downloads);
            }
        }
    }
}
