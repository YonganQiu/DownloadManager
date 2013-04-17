package com.elvishew.download.library.server;

import java.util.List;

import android.content.Context;
import android.os.RemoteException;

public class DownloadManager {

    protected final String TAG = "DownloadManager";

    public static final String DOWNLOAD_MANAGER_SERVICE = "download_manager_qpx";

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ALREADY_DOWNLOADED = 1;
    public static final int ERROR_ALREADY_ADDED = 2;
    public static final int ERROR_SDCARD_NOT_FOUND = 3;
    public static final int ERROR_SDCARD_NOT_WRITABLE = 4;
    public static final int ERROR_STORAGE_NOT_ENOUGH = 5;
    public static final int ERROR_DOWNLOADING_LIST_FULL = 6;
    public static final int ERROR_NETWORK_NOT_AVAILABLE = 7;
    public static final int ERROR_NETWORK_ERROR = 8;
    public static final int ERROR_FETCH_FILE_LENGTH_FAIL = 9;
    public static final int ERROR_CREATE_FILE_FAIL = 10;
    public static final int ERROR_DOWNLOAD_INIT_FAIL = 11;
    public static final int ERROR_DOWNLOAD_PARAMS_ERROR = 12;
    public static final int ERROR_TEMP_FILE_LOST = 13;
    public static final int ERROR_IO_ERROR = 14;
    public static final int ERROR_DUPLICATE_DOWNLOAD_REQUEST = 15;
    public static final int ERROR_UNKNOWN_ERROR = 16;
    public static final int ERROR_NETWORK_TIME_OUT = 17;
    public static final int ERROR_ALREADY_RESUMED = 18;
    public static final int ERROR_ALREADY_PAUSED = 19;
    public static final int ERROR_DOWNLOAD_NOT_FOUND = 19;
    public static final int ERROR_DOWNLOADED_DELETE_FAIL = 20;
    public static final int ERROR_DOWNLOADED_NOT_FOUND = 21;
    public static final int ERROR_DOWNLOAD_CLIENT_NOT_FOUND = 22;

    private static DownloadManager sInstance;

    private IDownloadManager sServices;

    public static DownloadManager getDefault(Context context) {
        if (sInstance == null) {
            sInstance = new DownloadManager(context, (IDownloadManager) context
                    .getApplicationContext().getSystemService(DOWNLOAD_MANAGER_SERVICE));
        }
        return sInstance;
    }

    private DownloadManager(Context context, IDownloadManager service) {
        sServices = service;
    }

    public static String errorToString(int errorCode) {
        return String.valueOf(errorCode);
    }

    public void registerClient(String requester, IDownloadClient client) {
        try {
            sServices.registerClient(requester, client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterClient(String requester) {
        try {
            sServices.unregisterClient(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<DownloadingItem> getAllDownloadings(String requester) {
        try {
            return sServices.getAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DownloadedItem> getAllDownloadeds(String requester) {
        try {
            return sServices.getAllDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasDownloading(String requester, String url) {
        try {
            return sServices.hasDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloadings(String requester) {
        try {
            return sServices.hasDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloaded(String requester, String url) {
        try {
            return sServices.hasDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasDownloadeds(String requester) {
        try {
            return sServices.hasDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addDownloading(DownloadRequest request) {
        try {
            return sServices.addDownloading(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int resumeDownloading(String requester, String url) {
        try {
            return sServices.resumeDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteDownloading(String requester, String url) {
        try {
            return sServices.deleteDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteAllDownloadings(String requester) {
        try {
            return sServices.deleteAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int pauseDownloading(String requester, String url) {
        try {
            return sServices.pauseDownloading(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int pauseAllDownloadings(String requester) {
        try {
            return sServices.pauseAllDownloadings(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteDownloaded(String requester, String url) {
        try {
            return sServices.deleteDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public int deleteAllDownloadeds(String requester) {
        try {
            return sServices.deleteAllDownloadeds(requester);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ERROR_UNKNOWN_ERROR;
    }

    public String getLocalUrlIfDownloaded(String requester, String url) {
        try {
            return sServices.getLocalUrlIfDownloaded(requester, url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
