
package com.elvishew.download.library.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.elvishew.download.library.server.DownloadModel.Downloaded;
import com.elvishew.download.library.server.DownloadModel.Downloading;
import com.elvishew.download.library.server.DownloadTask.DownloadCallbacks;
import com.elvishew.download.library.utils.NetworkUtils;
import com.elvishew.download.library.utils.PathUtil;
import com.elvishew.download.library.utils.StorageUtils;

class DownloadManagerImpl extends IDownloadManager.Stub implements DownloadCallbacks {

    private static final boolean DEBUG = true;

    protected final String TAG = "DownloadManager";

    private static final int MAX_DOWNLOADING = 3;

    private static final int MAX_DOWNLOAD = 4;

    private Context mContext;

    private Map<String, IDownloadClient> mDownloadClients;

    private List<DownloadTask> mDownloadingTasks;

    private List<String> mPendingRequest;

    private List<DownloadingItem> mDownloadingItems;
    private List<DownloadingItem> mPausedItems;
    private List<DownloadingItem> mPendingItems;

    private static DownloadManagerImpl sInstance;

    static DownloadManagerImpl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DownloadManagerImpl(context);
        }
        return sInstance;
    }

    private DownloadManagerImpl(Context context) {
        super();
        mContext = context;

        mDownloadClients = new HashMap<String, IDownloadClient>();

        mPendingRequest = new ArrayList<String>();

        mDownloadingItems = new ArrayList<DownloadingItem>();
        mPausedItems = new ArrayList<DownloadingItem>();
        mPendingItems = new ArrayList<DownloadingItem>();

        // Load all downloadings here.
        List<DownloadingItem> allDownloadingItems = DownloadModelUtil.loadDownloadings(mContext, Downloading.START_TIME, true);
        for (DownloadingItem item : allDownloadingItems) {
//            switch (item.getState()) {
//                case DownloadingItem.STATE_DOWNLOADING:
//                    mDownloadingItems.add(item);
//                    break;
//                case DownloadingItem.STATE_PAUSED:
//                    mPausedItems.add(item);
//                    break;
//                case DownloadingItem.STATE_PENDING:
//                    mPendingItems.add(item);
//                    break;
//            }

            // Init all to be paused.
            // TODO What about the origin state items?
            item.updateState(DownloadingItem.STATE_PAUSED);
            mPausedItems.add(item);
        }
        DownloadModelUtil.updataAllDownloadingState(mContext, DownloadingItem.STATE_DOWNLOADING,
                DownloadingItem.STATE_PAUSED);
        DownloadModelUtil.updataAllDownloadingState(mContext, DownloadingItem.STATE_PENDING,
                DownloadingItem.STATE_PAUSED);

        mDownloadingTasks = new ArrayList<DownloadTask>();
    }

    @Override
    public void registerClient(String requester, IDownloadClient client) throws RemoteException {
        mDownloadClients.put(requester, client);
    }

    @Override
    public void unregisterClient(String requester) throws RemoteException {
        mDownloadClients.remove(requester);
    }

    @Override
    public List<DownloadingItem> getAllDownloadings(String requester) {
        return DownloadModelUtil.loadDownloadings(mContext, Downloading.START_TIME, true);
    }

    @Override
    public List<DownloadedItem> getAllDownloadeds(String requester) {
        return DownloadModelUtil.loadDownloadeds(mContext, Downloaded.FINISH_TIME, true);
    }

    @Override
    public boolean hasDownloading(String requester, String url) throws RemoteException {
        return DownloadModelUtil.hasDownloading(mContext, url);
    }

    @Override
    public boolean hasDownloadings(String requester) throws RemoteException {
        return DownloadModelUtil.hasDownloadings(mContext);
    }

    @Override
    public boolean hasDownloaded(String requester, String url) throws RemoteException {
        return DownloadModelUtil.hasDownloaded(mContext, url);
    }

    @Override
    public boolean hasDownloadeds(String requester) throws RemoteException {
        return DownloadModelUtil.hasDownloadeds(mContext);
    }

    @Override
    public int addDownloading(final DownloadRequest request) {
        if (DEBUG) {
            Log.i(TAG, "addDownload: " + request.downloadable);
        }

        final String url = request.downloadable.getUrl();

        for (String requestUrl : mPendingRequest) {
            if (requestUrl.equals(url)) {
                return DownloadManager.ERROR_DUPLICATE_DOWNLOAD_REQUEST;
            }
        }

        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            return DownloadManager.ERROR_NETWORK_NOT_AVAILABLE;
        }

        // Check if is downloaded or file exists.
        File downloadableFile = new File(request.savePath);
        if (downloadableFile.exists()) {
            if (DownloadModelUtil.downloadedExists(mContext, url)) {
                return DownloadManager.ERROR_ALREADY_DOWNLOADED;
            } else {
                DownloadedItem downloaded = new DownloadedItem(request.downloadable.getName(),
                        request.downloadable.getUrl(), request.requester, request.savePath,
                        (int) downloadableFile.length(), System.currentTimeMillis());
                DownloadModelUtil.addOrUpdateDownloaded(mContext, downloaded);

                IDownloadClient client = mDownloadClients.get(request.requester);
                if (client != null) {
                    try {
                        client.onDownloadedAdded(downloaded);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return DownloadManager.ERROR_NO_ERROR;
            }
        } else {
            if (DownloadModelUtil.downloadedExists(mContext, url)) {
                DownloadModelUtil.deleteDownloaded(mContext, url);
            }
        }

        if (DownloadModelUtil.downloadingExists(mContext, url)) {
            return DownloadManager.ERROR_ALREADY_ADDED;
        }

        if (!StorageUtils.isSDCardPresent()) {
            return DownloadManager.ERROR_SDCARD_NOT_FOUND;
        }

        if (!StorageUtils.isSdCardWrittenable()) {
            return DownloadManager.ERROR_SDCARD_NOT_WRITABLE;
        }

        if (DownloadModelUtil.getDownloadingsCount(mContext) >= MAX_DOWNLOAD) {
            return DownloadManager.ERROR_DOWNLOADING_LIST_FULL;
        }

        final IDownloadClient client = mDownloadClients.get(request.requester);

        mPendingRequest.add(url);

        new CreateLoaclFileTask(new CreateLoaclFileTask.Callback() {
            
            @Override
            public void onError(DownloadException exception) {
                mPendingRequest.remove(url);

                if (client != null) {
                    try {
                        client.onDownloadAddingFail(request, exception.errorCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onDone(DownloadingItem result) {
                int state;
                if (mDownloadingItems.size() < MAX_DOWNLOADING) {
                    state = DownloadingItem.STATE_DOWNLOADING;
                } else {
                    state = DownloadingItem.STATE_PENDING;
                }
                result.updateState(state);

                // Add to DB.
                DownloadModelUtil.addOrUpdateDownloading(mContext, result);

                if (state == DownloadingItem.STATE_DOWNLOADING) {
                    // Start a download task.
                    DownloadTask task = createDownloadTask(result);
                    mDownloadingTasks.add(task);
                    task.execute((Void) null);
                    mDownloadingItems.add(result);
                } else { // Pending.
                    mPendingItems.add(result);
                }

                mPendingRequest.remove(url);

                if (client != null) {
                    try {
                        client.onDownloadingAdded(result.copy());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(request);
        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public int pauseDownloading(String requester, String url) {
        if (DEBUG) {
            Log.i(TAG, "pauseDownload: " + url);
        }

        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            int errorCode = pauseDownloadInner(client, url);
            if (errorCode == DownloadManager.ERROR_NO_ERROR) {
                // Change a pending to downloading.
                moveFirstPendingToDownloading();
            }
            return errorCode;
        } else {
            return DownloadManager.ERROR_DOWNLOAD_CLIENT_NOT_FOUND;
        }
    }

    @Override
    public int pauseAllDownloadings(String requester) {
        if (DEBUG) {
            Log.i(TAG, "pauseAllDownloads.");
        }

        // Cancel all downloading task and clear tasks list.
        for (DownloadTask task : mDownloadingTasks) {
            task.onCancelled();
        }
        mDownloadingTasks.clear();

        List<DownloadingItem> changedItems = new ArrayList<DownloadingItem>();

        // Update all downloading downloads to paused state,
        // and transfer them to paused list.
        for (DownloadingItem item : mDownloadingItems) {
            item.updateState(DownloadingItem.STATE_PAUSED);
            changedItems.add(item.copy());
        }
        mPausedItems.addAll(mDownloadingItems);
        mDownloadingItems.clear();
        DownloadModelUtil.updataAllDownloadingState(mContext, DownloadingItem.STATE_DOWNLOADING, DownloadingItem.STATE_PAUSED);

        // Update all pending downloads to paused state,
        // and transfer them to paused list.
        for (DownloadingItem item : mPendingItems) {
            item.updateState(DownloadingItem.STATE_PAUSED);
            changedItems.add(item.copy());
        }
        mPausedItems.addAll(mPendingItems);
        mPendingItems.clear();
        DownloadModelUtil.updataAllDownloadingState(mContext, DownloadingItem.STATE_PENDING, DownloadingItem.STATE_PAUSED);

        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            try {
                client.onDownloadingsStateChanged(changedItems);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public int resumeDownloading(String requester, String url) {
        if (DEBUG) {
            Log.i(TAG, "resumeDownload: " + url);
        }

        // Can only resume paused download.
        DownloadingItem itemInServer = findInPaused(url);
        if (itemInServer == null) {
            return DownloadManager.ERROR_ALREADY_RESUMED;
        }

        ensureDownloadTaskRemoved(url);
        removeFrom(url, mPausedItems);

        if (mDownloadingItems.size() < MAX_DOWNLOADING) {
            itemInServer.updateState(DownloadingItem.STATE_DOWNLOADING);
            mDownloadingItems.add(itemInServer);
            DownloadModelUtil.updataDownloadingState(mContext, DownloadingItem.STATE_DOWNLOADING, url);

            // Start a download task.
            DownloadTask task = createDownloadTask(itemInServer);
            mDownloadingTasks.add(task);
            task.execute((Void)null);
        } else { // Pending.
            itemInServer.updateState(DownloadingItem.STATE_PENDING);
            mPendingItems.add(itemInServer);
            DownloadModelUtil.updataDownloadingState(mContext, DownloadingItem.STATE_PENDING, url);
        }

        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            try {
                client.onDownloadingStateChanged(itemInServer.copy());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public int deleteDownloading(String requester, String url) {
        if (DEBUG) {
            Log.i(TAG, "deleteDownload: " + url);
        }

        DownloadingItem itemInServer = findInDownloading(url);
        if (itemInServer == null) {
            itemInServer = findInPaused(url);
            if (itemInServer == null) {
                itemInServer = findInPending(url);
                if (itemInServer == null) {
                    return DownloadManager.ERROR_DOWNLOAD_NOT_FOUND;
                }
            }
        }

        deleteDownloadingInnerSilently(itemInServer);

        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            try {
                client.onDownloadingDeleted(itemInServer.copy());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        moveFirstPendingToDownloading();

        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public int deleteAllDownloadings(String requester) throws RemoteException {
        List<DownloadingItem> downloads = getAllDownloadings(requester);
        for (DownloadingItem download : downloads) {
            deleteDownloadingInnerSilently(download);
        }

        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            try {
                client.onDownloadingsDeleted(downloads);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return DownloadManager.ERROR_NO_ERROR;
    }

    private void deleteDownloadingInnerSilently(DownloadingItem itemInServer) {
        String url = itemInServer.getUrl();
        if (itemInServer.getState() == DownloadingItem.STATE_DOWNLOADING) {
            ensureDownloadTaskRemoved(url);
            removeFrom(url, mDownloadingItems);
        } else if (itemInServer.getState() == DownloadingItem.STATE_PAUSED) {
            removeFrom(url, mPausedItems);
        } else { // Pending.
            removeFrom(url, mPendingItems);
        }

        // Delete temp file.
        File file = new File(PathUtil.getVideoTempFilePath(itemInServer.getName(), url));
        if (file.exists()) {
            file.delete();
        }

        // Totally remove this download.
        DownloadModelUtil.deleteDownloading(mContext, url);

    }

    private int pauseDownloadInner(IDownloadClient client, String url) {
        DownloadingItem itemInServer = findInDownloading(url);
        if (itemInServer == null) {
            itemInServer = findInPending(url);
        }
        if (itemInServer == null) {
            // Can only pause downloading or pending download.
            return DownloadManager.ERROR_ALREADY_PAUSED;
        }

        // Cancel task if downloading, and remove from old.
        if (itemInServer.getState() == DownloadingItem.STATE_DOWNLOADING) {
            ensureDownloadTaskRemoved(url);
            removeFrom(url, mDownloadingItems);
        } else { // Pending.
            removeFrom(url, mPendingItems);
        }

        // Update state and add to new.
        itemInServer.updateState(DownloadingItem.STATE_PAUSED);
        mPausedItems.add(itemInServer);
        DownloadModelUtil.updataDownloadingState(mContext, DownloadingItem.STATE_PAUSED, url);

        try {
            client.onDownloadingStateChanged(itemInServer.copy());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public int deleteDownloaded(String requester, String url) {
        if (DownloadModelUtil.downloadedExists(mContext, url)) {
            DownloadedItem downloaded = DownloadModelUtil.loadDownloaded(mContext, url);
            File downloadableFile = new File(PathUtil.getVideoFilePath(downloaded.getName(), url));
            if (downloadableFile.exists() && !downloadableFile.delete()) {
                return DownloadManager.ERROR_DOWNLOADED_DELETE_FAIL;
            } else {
                DownloadModelUtil.deleteDownloaded(mContext, url);
                IDownloadClient client = mDownloadClients.get(requester);
                if (client != null) {
                    try {
                        client.onDownloadedDeleted(downloaded);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return DownloadManager.ERROR_NO_ERROR;
            }
        } else {
            return DownloadManager.ERROR_DOWNLOADED_NOT_FOUND;
        }
    }

    @Override
    public int deleteAllDownloadeds(String requester) throws RemoteException {
        List<DownloadedItem> downloads = getAllDownloadeds(requester);
        for (DownloadedItem download: downloads) {
            File downloadableFile = new File(PathUtil.getVideoFilePath(download.getName(), download.getUrl()));
            if (downloadableFile.exists() && !downloadableFile.delete()) {
                return DownloadManager.ERROR_DOWNLOADED_DELETE_FAIL;
            } else {
                DownloadModelUtil.deleteDownloaded(mContext, download.getUrl());
            }
        }
        IDownloadClient client = mDownloadClients.get(requester);
        if (client != null) {
            try {
                client.onDownloadedsDeleted(downloads);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return DownloadManager.ERROR_NO_ERROR;
    }

    @Override
    public String getLocalUrlIfDownloaded(String requester, String url) throws RemoteException {
        if (DownloadModelUtil.downloadedExists(mContext, url)) {
            DownloadedItem downloaded = DownloadModelUtil.loadDownloaded(mContext, url);
            File downloadableFile = new File(PathUtil.getVideoFilePath(downloaded.getName(), url));
            if (downloadableFile.exists()) {
                return Uri.fromFile(downloadableFile).toString();
            } else {
                DownloadModelUtil.deleteDownloaded(mContext, url);
                IDownloadClient client = mDownloadClients.get(requester);
                if (client != null) {
                    try {
                        client.onDownloadedDeleted(downloaded);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private DownloadingItem findInDownloading(String url) {
        return findInList(url, mDownloadingItems);
    }

    private DownloadingItem findInPaused(String url) {
        return findInList(url, mPausedItems);
    }

    private DownloadingItem findInPending(String url) {
        return findInList(url, mPendingItems);
    }

    private DownloadingItem findInList(String url, List<DownloadingItem> items) {
        for (DownloadingItem item : items) {
            if (item.getUrl().equals(url)) {
                return item;
            }
        }
        return null;
    }

    private void removeFrom(String url, List<DownloadingItem> from) {
        for (DownloadingItem item : from) {
            if (item.getUrl().equals(url)) {
                from.remove(item);
                return;
            }
        }
    }

    private void ensureDownloadTaskRemoved(String url) {
        DownloadTask oldTask = findDownloadTask(url);
        if (oldTask != null) {
            oldTask.onCancelled();
            mDownloadingTasks.remove(oldTask);
        }
    }

    private DownloadTask findDownloadTask(String url) {
        if (url == null) {
            return null;
        }
        for (DownloadTask task : mDownloadingTasks) {
            if (url.equals(task.getUrl())) {
                return task;
            }
        }
        return null;
    }

    /**
     * Create a new download task with default config
     */
    private DownloadTask createDownloadTask(DownloadingItem item) {
        return new DownloadTask(mContext, item, this);
    }

    @Override
    public void onDownloadProgressUpdate(DownloadTask task, long completedLength, long averageSpeed) {
        // Update database.
        DownloadModelUtil.updataDownloading(mContext, (int) completedLength, task.getUrl());

        IDownloadClient client = mDownloadClients.get(task.getRequester());
        if (client != null) {
            try {
                client.onDownloadingProgressUpdate(task.getDownloadingItem(),
                        new DownloadProgressData(completedLength, averageSpeed));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreDownload(DownloadTask task) {
    }

    @Override
    public void onPostDownload(DownloadTask task) {
        DownloadingItem item = task.getDownloadingItem();

        if (DEBUG) {
            Log.i(TAG, "onFinishDownload: " + item);
        }

        // Remove task from list.
        mDownloadingTasks.remove(task);

        // Remove download totally.
        removeFrom(item.getUrl(), mDownloadingItems);
        DownloadModelUtil.deleteDownloading(mContext, item.getUrl());

        IDownloadClient client = mDownloadClients.get(task.getRequester());
        if (client != null) {
            try {
                client.onDownloadingDeleted(item);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        DownloadedItem downloadedItem = new DownloadedItem(item.getName(), item.getUrl(),
                task.getRequester(), item.getSavePath(), (int) item.getFileLength(),
                System.currentTimeMillis());

        // Add to DB as downloaded.
        DownloadModelUtil.addOrUpdateDownloaded(mContext, downloadedItem);

        if (client != null) {
            try {
                client.onDownloadedAdded(downloadedItem);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        // Change a pending to downloading.
        moveFirstPendingToDownloading();
    }

    @Override
    public void onDownloadError(DownloadTask task, DownloadException error) {
        IDownloadClient client = mDownloadClients.get(task.getRequester());
        boolean needPause = true;
        if (error != null) {
            Log.e(TAG, "onDownloadError: errorCode = " + error.errorCode);
            if (client != null) {
                try {
                    client.onDownloadingError(task.getDownloadingItem(), error.errorCode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (error.errorCode == DownloadManager.ERROR_TEMP_FILE_LOST) {
                deleteDownloadingInnerSilently(task.getDownloadingItem());
                if (client != null) {
                    try {
                        client.onDownloadingDeleted(task.getDownloadingItem().copy());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                needPause = false;
            }
        }

        if (needPause) {
            if (client != null) {
                pauseDownloadInner(client, task.getUrl());
            }
        }

        // Change a pending to downloading.
        moveFirstPendingToDownloading();
    }

    private void moveFirstPendingToDownloading() {
        if (mPendingItems.size() > 0 && mDownloadingItems.size() < MAX_DOWNLOADING) {
            DownloadingItem pendingItem = mPendingItems.remove(mPendingItems.size() - 1);
            pendingItem.updateState(DownloadingItem.STATE_DOWNLOADING);
            DownloadModelUtil.updataDownloadingState(mContext, DownloadingItem.STATE_DOWNLOADING,
                    pendingItem.getUrl());
            mDownloadingItems.add(pendingItem);

            IDownloadClient client = mDownloadClients.get(pendingItem.getRequester());
            if (client != null) {
                try {
                    client.onDownloadingStateChanged(pendingItem.copy());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Start a download task.
            DownloadTask newTask = createDownloadTask(pendingItem);
            mDownloadingTasks.add(newTask);
            newTask.execute((Void) null);
        }
    }

}
